package in.succinct.plugins.ecommerce.db.model.inventory;

import com.venky.swf.db.Database;
import com.venky.swf.db.annotations.column.COLUMN_DEF;
import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.defaulting.StandardDefault;
import com.venky.swf.db.annotations.column.indexing.Index;
import com.venky.swf.db.annotations.column.pm.PARTICIPANT;
import com.venky.swf.db.annotations.column.ui.HIDDEN;
import com.venky.swf.db.annotations.model.EXPORTABLE;
import com.venky.swf.db.annotations.model.MENU;
import com.venky.swf.db.model.Model;
import com.venky.swf.plugins.collab.db.model.CompanySpecific;
import com.venky.swf.sql.Conjunction;
import com.venky.swf.sql.Expression;
import com.venky.swf.sql.Operator;
import com.venky.swf.sql.Select;
import in.succinct.plugins.ecommerce.db.model.demand.Demand;
import in.succinct.plugins.ecommerce.db.model.participation.Facility;

import java.util.List;


@MENU("Inventory")
@EXPORTABLE(false)

public interface Inventory extends Model, CompanySpecific {
    @HIDDEN
    public Long getCompanyId();

	@PARTICIPANT(redundant =  true)
	@UNIQUE_KEY
	public long getFacilityId();
	public void setFacilityId(long id);
	public Facility getFacility();
	
	@PARTICIPANT(redundant = true)
	@UNIQUE_KEY
    @Index
	public long getSkuId();
	public void setSkuId(long id);
	public Sku getSku();

	@COLUMN_DEF(StandardDefault.BOOLEAN_FALSE)
	public boolean isInfinite();
	public void setInfinite(boolean infinite);

	@COLUMN_DEF(StandardDefault.ZERO)
	public double getQuantity();
	public void setQuantity(double quantity);

	List<AdjustmentRequest> getAdjustmentRequests();
	List<InventoryAudit> getAudits();
	List<Demand> getDemands();

	public void adjust(double delta,String comment);


	public static void adjust(Facility facility , Sku sku, double signedDelta,String comment){
		Select inventorySelect = new Select().from(Inventory.class);
		Expression where = new Expression(inventorySelect.getPool(), Conjunction.AND);
		where.add(new Expression(inventorySelect.getPool(),"FACILITY_ID", Operator.EQ,facility.getId()));
		where.add(new Expression(inventorySelect.getPool(),"SKU_ID",Operator.EQ,sku.getId()));

		List<Inventory> inventories = inventorySelect.where(where).execute();
		Inventory inventory = null;
		if (inventories.isEmpty()) {
			inventory = Database.getTable(Inventory.class).newRecord();
			inventory.setFacilityId(facility.getId());
			inventory.setSkuId(sku.getId());
		}else if (inventories.size() == 1){
			inventory = inventories.get(0);
		}else {
			throw new RuntimeException("Cannot find inventory record uniquely:" + where.getRealSQL() );
		}
		inventory.adjust(signedDelta,comment);
	}



}
