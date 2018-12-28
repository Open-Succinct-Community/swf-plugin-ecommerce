package in.succinct.plugins.ecommerce.db.model.catalog;

import com.venky.swf.db.Database;
import com.venky.swf.db.annotations.column.ui.OnLookupSelectionProcessor;

public class MasterItemCategorySelectionProcessor implements OnLookupSelectionProcessor<ItemCategory>  {

	@Override
	public void process(String fieldSelected, ItemCategory partiallyFilledModel) {
		if (fieldSelected.equals("MASTER_ITEM_CATEGORY_VALUE_ID") &&
				!Database.getJdbcTypeHelper(partiallyFilledModel.getReflector().getPool()).isVoid(partiallyFilledModel.getMasterItemCategoryValueId())){
			partiallyFilledModel.setMasterItemCategoryId(partiallyFilledModel.getMasterItemCategoryValue().getMasterItemCategoryId());
		}else if (fieldSelected.equals("MASTER_ITEM_CATEGORY_ID") && 
				!Database.getJdbcTypeHelper(partiallyFilledModel.getReflector().getPool()).isVoid(partiallyFilledModel.getMasterItemCategoryId())){

			if (!Database.getJdbcTypeHelper(partiallyFilledModel.getReflector().getPool()).isVoid(partiallyFilledModel.getMasterItemCategoryValueId())){
				if (partiallyFilledModel.getMasterItemCategoryId() != partiallyFilledModel.getMasterItemCategoryValue().getMasterItemCategoryId()) {
					partiallyFilledModel.setMasterItemCategoryValueId(null);
				}
			}
		}
		
	}

}
