package in.succinct.plugins.ecommerce.db.model.catalog;

import com.venky.swf.db.Database;
import com.venky.swf.db.table.ModelImpl;

import java.util.List;

public class ItemImpl extends ModelImpl<Item>{
    public ItemImpl(Item item){
        super(item);
    }

    public ItemCategory getItemCategory(String categoryName) {
        Item item = getProxy();

        ItemCategory itemCategory = null;
        for (ItemCategory tmp : item.getItemCategories()) {
            if (tmp.getMasterItemCategory().getName().equals(categoryName)){
                itemCategory = tmp;
                break;
            }
        }

        return itemCategory;

    }
    public ItemCategory setItemCategory(String categoryName,String categoryValue){
        Item item = getProxy();
        ItemCategory itemCategory = getItemCategory(categoryName);
        if (itemCategory != null){
            if (itemCategory.getMasterItemCategoryValue().getAllowedValue().equals(categoryValue)) {
                return itemCategory;
            }else {
                MasterItemCategoryValue masterItemCategoryValue = getMasterItemCategoryValue(item.getCompanyId(),categoryName,categoryValue);
                itemCategory.setMasterItemCategoryValueId(masterItemCategoryValue.getId());
            }
        }else {
            itemCategory = Database.getTable(ItemCategory.class).newRecord();
            itemCategory.setItemId(item.getId());
            MasterItemCategoryValue masterItemCategoryValue = getMasterItemCategoryValue(item.getCompanyId(), categoryName, categoryValue);
            itemCategory.setMasterItemCategoryId(masterItemCategoryValue.getMasterItemCategoryId());
            itemCategory.setMasterItemCategoryValueId(masterItemCategoryValue.getId());
        }
        itemCategory.save();
        return  itemCategory;
    }


    private static MasterItemCategoryValue getMasterItemCategoryValue(long companyId, String categoryName, String categoryValue){
        MasterItemCategory category = getMasterCategory(companyId,categoryName);
        List<MasterItemCategoryValue> values = category.getAllowedValues();

        MasterItemCategoryValue matchedMasterItemCategoryValue = null;
        for (MasterItemCategoryValue value : values) {
            if (value.getAllowedValue().equals(categoryValue)){
                matchedMasterItemCategoryValue = value;
                break;
            }
        }
        if (matchedMasterItemCategoryValue == null){
            matchedMasterItemCategoryValue = Database.getTable(MasterItemCategoryValue.class).newRecord();
            matchedMasterItemCategoryValue.setMasterItemCategoryId(category.getId());
            matchedMasterItemCategoryValue.setAllowedValue(categoryValue);
            matchedMasterItemCategoryValue.save();
        }
        return matchedMasterItemCategoryValue;
    }
    private static MasterItemCategory getMasterCategory(long companyId, String categoryName){
        MasterItemCategory category = MasterItemCategory.find(companyId,categoryName,true);
        if (category == null){
            category = Database.getTable(MasterItemCategory.class).newRecord();
            category.setCompanyId(companyId);
            category.setName(categoryName);
            category.save();
        }
        return category;
    }
}
