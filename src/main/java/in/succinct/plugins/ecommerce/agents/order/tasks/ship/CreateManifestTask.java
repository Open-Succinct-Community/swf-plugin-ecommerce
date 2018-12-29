package in.succinct.plugins.ecommerce.agents.order.tasks.ship;

import com.venky.swf.db.model.reflection.ModelReflector;
import com.venky.swf.plugins.background.core.TaskManager;
import in.succinct.plugins.ecommerce.agents.order.tasks.manifest.ManifestOrderTask;
import in.succinct.plugins.ecommerce.db.model.order.Manifest;
import in.succinct.plugins.ecommerce.db.model.order.Order;
import in.succinct.plugins.ecommerce.db.model.order.OrderLine;
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
	
	private Priority priority = Priority.DEFAULT;
	public void setTaskPriority(Priority priority){
		this.priority = priority;
	}
	@Override
	public Priority getTaskPriority(){
		return priority;
	}

	String manifestNumber = null;
	long facilityId = -1;
	String courier= null;
	public CreateManifestTask(String manifestNumber, long facilityId, String courier) {
        this.manifestNumber = manifestNumber;
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
		if (manifestNumber != null){
            where.add(new Expression(manifestSelect.getPool(),"MANIFEST_NUMBER",Operator.EQ, manifestNumber));
        }else {
            where.add(new Expression(manifestSelect.getPool(),"MANIFEST_NUMBER",Operator.EQ));
        }
        if (courier != null){
            where.add(new Expression(manifestSelect.getPool(),"COURIER",Operator.EQ, courier));
        }else {
            where.add(new Expression(manifestSelect.getPool(),"COURIER",Operator.EQ));
        }

		List<Manifest> manifests = manifestSelect.where(where).orderBy("ID").execute();
		Manifest manifest = null;
		if (manifests.isEmpty()) {
			manifest = Database.getTable(Manifest.class).newRecord();
			manifest.setManifestNumber(manifestNumber);
			manifest.setFacilityId(facilityId);
			manifest.setCourier(courier);
			manifest.save();
		}else {
			manifest = manifests.get(0);
		}
		addOrdersToManifest(manifest);
	}

	private void addOrdersToManifest(Manifest manifest) {
		Select orderSelect = new Select().from(Order.class);
		Expression where = new Expression(orderSelect.getPool(), Conjunction.AND);
		where.add(new Expression(orderSelect.getPool(),"FULFILLMENT_STATUS" , Operator.EQ, Order.FULFILLMENT_STATUS_PACKED));
		orderSelect.where(where).add(" and exists ( select 1 from " + ModelReflector.instance(OrderLine.class).getTableName() + " ol  where ol.order_id = " +
				ModelReflector.instance(Order.class).getTableName() +".id and ol.ship_from_id = " + facilityId + ")");

		List<Order> packedOrders = orderSelect.orderBy("ID").execute();
		packedOrders.forEach(o-> {
			TaskManager.instance().executeAsync(new ManifestOrderTask(o.getId(),manifest.getId()));
		});
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [manifestNumber=" + StringUtil.valueOf(manifestNumber) + ", facilityId=" + facilityId +  ", courierCode=" + courier + "]";
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
        if (manifestNumber != null ? !manifestNumber.equals(that.manifestNumber) : that.manifestNumber != null)
            return false;
        return courier != null ? courier.equals(that.courier) : that.courier == null;
    }
}
