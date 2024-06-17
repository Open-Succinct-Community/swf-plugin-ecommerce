package in.succinct.plugins.ecommerce.extensions.participant;

import in.succinct.plugins.ecommerce.db.model.catalog.Item;
import in.succinct.plugins.ecommerce.db.model.catalog.ItemCategory;
import in.succinct.plugins.ecommerce.db.model.catalog.MasterItemCategory;
import com.venky.core.collections.SequenceSet;
import com.venky.swf.db.Database;
import com.venky.swf.db.extensions.ParticipantExtension;
import com.venky.swf.db.model.User;
import com.venky.swf.pm.DataSecurityFilter;

import java.util.List;

public class ItemCategoryParticipantExtension extends ParticipantExtension<ItemCategory>{
	static  {
		registerExtension(new ItemCategoryParticipantExtension());
	}
	@Override
	public List<Long> getAllowedFieldValues(User user, ItemCategory partiallyFilledModel, String fieldName) {
	    List<Long> ret = null;
		if (fieldName.equals("ITEM_ID")){
            ret = new SequenceSet<>();
            if (partiallyFilledModel.getItemId()> 0) {
                if (partiallyFilledModel.getItem().isAccessibleBy(user)){
                    ret.add(partiallyFilledModel.getItemId());
                }
            }else {
                ret = DataSecurityFilter.getIds(DataSecurityFilter.getRecordsAccessible(Item.class, user));
            }
		}else if (fieldName.equals("MASTER_ITEM_CATEGORY_ID")){
			ret = DataSecurityFilter.getIds(DataSecurityFilter.getRecordsAccessible(MasterItemCategory.class, user));
		}else if (fieldName.equals("MASTER_ITEM_CATEGORY_VALUE_ID")){
			if (!Database.getJdbcTypeHelper(getReflector().getPool()).isVoid(partiallyFilledModel.getMasterItemCategoryId())){
				ret = DataSecurityFilter.getIds(partiallyFilledModel.getMasterItemCategory().getAllowedValues());
			}
		}
		return ret;
	}

}
