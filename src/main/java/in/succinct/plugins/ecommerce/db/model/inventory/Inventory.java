package in.succinct.plugins.ecommerce.db.model.inventory;

import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.indexing.Index;
import com.venky.swf.db.annotations.column.pm.PARTICIPANT;
import com.venky.swf.db.annotations.column.ui.HIDDEN;
import com.venky.swf.db.annotations.model.MENU;
import com.venky.swf.db.model.Model;
import com.venky.swf.plugins.collab.db.model.CompanySpecific;
import in.succinct.plugins.ecommerce.db.model.participation.Facility;

import java.util.List;


@MENU("Inventory")
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
	
	public double getQuantity();
	public void setQuantity(double quantity);

	List<AdjustmentRequest> getAdjustmentRequests();
	List<InventoryAudit> getAudits();

	public void adjust(double delta,String comment);




}
