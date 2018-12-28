package in.succinct.plugins.ecommerce.db.model.inventory;

import in.succinct.plugins.ecommerce.db.model.participation.Company;
import in.succinct.plugins.ecommerce.db.model.participation.Facility;
import com.venky.swf.db.annotations.column.IS_NULLABLE;
import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.indexing.Index;
import com.venky.swf.db.annotations.column.pm.PARTICIPANT;
import com.venky.swf.db.annotations.column.ui.HIDDEN;
import com.venky.swf.db.annotations.model.MENU;
import com.venky.swf.db.model.Model;


@MENU("Inventory")
public interface Inventory extends Model{
    @PARTICIPANT
    @HIDDEN
    @IS_NULLABLE
    public long getCompanyId();
    public void setCompanyId(long id);
    public Company getCompany();

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
	
	
}
