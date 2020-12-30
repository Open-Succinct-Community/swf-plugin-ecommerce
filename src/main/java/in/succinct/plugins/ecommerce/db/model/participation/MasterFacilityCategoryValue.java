package in.succinct.plugins.ecommerce.db.model.participation;

import com.venky.swf.db.annotations.column.COLUMN_DEF;
import com.venky.swf.db.annotations.column.IS_NULLABLE;
import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.defaulting.StandardDefault;
import com.venky.swf.db.annotations.column.pm.PARTICIPANT;
import com.venky.swf.db.annotations.column.relationship.CONNECTED_VIA;
import com.venky.swf.db.annotations.column.ui.PROTECTION;
import com.venky.swf.db.annotations.column.ui.PROTECTION.Kind;
import com.venky.swf.db.annotations.model.HAS_DESCRIPTION_FIELD;
import com.venky.swf.db.model.Model;
import in.succinct.plugins.ecommerce.db.model.catalog.ItemCategory;

import java.util.List;

@HAS_DESCRIPTION_FIELD("ALLOWED_VALUE")
public interface MasterFacilityCategoryValue extends Model{
	@UNIQUE_KEY
	@PROTECTION(Kind.NON_EDITABLE)
	@PARTICIPANT
	@IS_NULLABLE(false)
	public Long getMasterFacilityCategoryId();
	public void setMasterFacilityCategoryId(Long id);
	public MasterFacilityCategory getMasterFacilityCategory();
	
	@UNIQUE_KEY
	public String getAllowedValue();
	public void setAllowedValue(String value);
	
	@COLUMN_DEF(StandardDefault.BOOLEAN_TRUE)
	public boolean isCanShip();
	public  void setCanShip(boolean shipping);
	
	@COLUMN_DEF(StandardDefault.BOOLEAN_FALSE)
	public boolean isCanProcessReturns();
	public  void setCanProcessReturns(boolean returns);
	
	@COLUMN_DEF(StandardDefault.BOOLEAN_TRUE)
	public boolean isCanStockProducts();
	public  void setCanStockProducts(boolean canStockProducts);

	@COLUMN_DEF(StandardDefault.BOOLEAN_FALSE)
	public boolean isCustomerInvoiceExternallyHandled();
	public void setCustomerInvoiceExternallyHandled(boolean customerInvoiceExternallyHandled);

	@CONNECTED_VIA("MASTER_FACILITY_CATEGORY_VALUE_ID")
	List<FacilityCategory> getFacilityCategories();
	
	
}
