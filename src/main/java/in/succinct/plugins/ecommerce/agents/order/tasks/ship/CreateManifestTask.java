package in.succinct.plugins.ecommerce.agents.order.tasks.ship;

import in.succinct.plugins.ecommerce.db.model.order.Manifest;
import in.succinct.plugins.ecommerce.db.model.participation.Facility;
import com.venky.core.string.StringUtil;
import com.venky.swf.db.Database;
import com.venky.swf.plugins.background.core.Task;
import com.venky.swf.sql.Conjunction;
import com.venky.swf.sql.Expression;
import com.venky.swf.sql.Operator;
import com.venky.swf.sql.Select;

import java.util.List;


public class CreateManifestTask implements Task{

	private static final long serialVersionUID = -2079771132674955414L;
	@Deprecated
	public CreateManifestTask() {
	}
	
	
	String manifestId = null;
	long facilityId = -1;
	String courier= null;
	public CreateManifestTask(String manifestId,long facilityId,String courier) {
        this.manifestId = manifestId;
		this.facilityId = facilityId;
		this.courier = courier;
	}
	@Override
	public void execute() {
	    Facility lockedFacility = Database.getTable(Facility.class).lock(facilityId);

		Select manifestSelect = new Select().from(Manifest.class);
		Expression where = new Expression(manifestSelect.getPool(),Conjunction.AND);
        where.add(new Expression(manifestSelect.getPool(),"FACILITY_ID",Operator.EQ, facilityId));
        where.add(new Expression(manifestSelect.getPool(),"CLOSED",Operator.EQ, false));
		if (manifestId != null){
            where.add(new Expression(manifestSelect.getPool(),"MANIFEST_ID",Operator.EQ, manifestId));
        }else {
            where.add(new Expression(manifestSelect.getPool(),"MANIFEST_ID",Operator.EQ));
        }
        if (courier != null){
            where.add(new Expression(manifestSelect.getPool(),"COURIER",Operator.EQ, courier));
        }else {
            where.add(new Expression(manifestSelect.getPool(),"COURIER",Operator.EQ));
        }

		List<Manifest> manifests = manifestSelect.where(where).execute();
		if (manifests.isEmpty()) {
			Manifest manifest = Database.getTable(Manifest.class).newRecord();
			manifest.setManifestId(manifestId);
			manifest.setFacilityId(facilityId);
			manifest.setCourier(courier);
			manifest.save();
		}
		
	}
	@Override
	public String toString() {
		return getClass().getSimpleName() + " [manifestId=" + StringUtil.valueOf(manifestId) + ", facilityId=" + facilityId +  ", courierCode=" + courier + "]";
	}
	@Override
	public int hashCode() {
		return toString().hashCode();
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CreateManifestTask that = (CreateManifestTask) o;

        if (facilityId != that.facilityId) return false;
        if (manifestId != null ? !manifestId.equals(that.manifestId) : that.manifestId != null)
            return false;
        return courier != null ? courier.equals(that.courier) : that.courier == null;
    }
}
