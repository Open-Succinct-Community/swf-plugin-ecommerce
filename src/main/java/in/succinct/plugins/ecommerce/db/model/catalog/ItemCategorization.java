package in.succinct.plugins.ecommerce.db.model.catalog;

import com.venky.swf.db.model.Model;
import com.venky.swf.db.annotations.column.IS_NULLABLE;
import com.venky.swf.db.annotations.column.IS_VIRTUAL;
import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.pm.PARTICIPANT;
import com.venky.swf.db.annotations.column.ui.OnLookupSelect;

@IS_VIRTUAL
public interface ItemCategorization extends Model{

	@IS_NULLABLE(false)
	@UNIQUE_KEY
	@PARTICIPANT(value="MASTER_ITEM_CATEGORY_ID",redundant=true)
	@OnLookupSelect(processor="in.succinct.plugins.ecommerce.db.model.catalog.MasterItemCategorySelectionProcessor")
	public Long getMasterItemCategoryId();
	public void setMasterItemCategoryId(Long id);
	public MasterItemCategory getMasterItemCategory();
	
	
	@IS_NULLABLE(false)
	@PARTICIPANT(value="MASTER_ITEM_CATEGORY_VALUE_ID",redundant=true)
	@OnLookupSelect(processor="in.succinct.plugins.ecommerce.db.model.catalog.MasterItemCategorySelectionProcessor")
	public Long getMasterItemCategoryValueId();
	public void setMasterItemCategoryValueId(Long id);
	public MasterItemCategoryValue getMasterItemCategoryValue();
}
