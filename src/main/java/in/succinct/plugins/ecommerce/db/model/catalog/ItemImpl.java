package in.succinct.plugins.ecommerce.db.model.catalog;

import com.venky.core.util.ObjectUtil;
import com.venky.digest.Encryptor;
import com.venky.swf.db.Database;
import com.venky.swf.db.model.reflection.ModelReflector;
import com.venky.swf.db.table.ModelImpl;
import com.venky.swf.sql.Conjunction;
import com.venky.swf.sql.Expression;
import com.venky.swf.sql.Operator;
import com.venky.swf.sql.Select;
import in.succinct.plugins.ecommerce.db.model.assets.Asset;
import in.succinct.plugins.ecommerce.db.model.assets.Capability;
import in.succinct.plugins.ecommerce.db.model.attributes.AssetCode;
import in.succinct.plugins.ecommerce.db.model.attributes.AssetCodeAttribute;
import in.succinct.plugins.ecommerce.db.model.attributes.AttributeValue;
import in.succinct.plugins.ecommerce.db.model.order.OrderLine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class ItemImpl extends ModelImpl<Item>{
    public ItemImpl(){
        super();
    }
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


    public boolean isRentable(){
        /*
        AssetCode code = getProxy().getAssetCode();
        if (code != null){
            return code.isSac();
        }*/
        return false;
    }

    public List<Asset> getAssets(){
        Item item  = getProxy();
        ModelReflector<Capability> ref = ModelReflector.instance(Capability.class);
        List<Asset> assets =  new Select().from(Capability.class).where(new Expression(ref.getPool(), Conjunction.AND).
                add(new Expression(ref.getPool(),"catalog_hash", Operator.EQ, item.getItemHash())).
                add(new Expression(ref.getPool(), "asset_code_id",Operator.EQ,item.getAssetCodeId()))).execute();
        return assets;
    }

    public void computeHash(){
        Item item = getProxy();
        if (item.getReflector().isVoid(item.getAssetCodeId())){
            return;
        }
        List<AttributeValue> attributeValues = new ArrayList<>();
        for (ItemAttributeValue itemAttributeValue : item.getAttributeValues()) {
            attributeValues.add(itemAttributeValue.getAttributeValue());
        }

        item.setItemHash(Item.hash(item.getAssetCode(),attributeValues));
        item.save();
    }

    private String hsn = null;
    public String getHsn(){
        if (hsn == null){
            Item item = getProxy();
            if (item.getAssetCodeId() != null){
                AssetCode assetCode =  item.getAssetCode();
                if (assetCode.isHsn()){
                    return assetCode.getCode();
                }
            }
            ItemCategory category = item.getItemCategory("HSN");
            if (category != null){
                hsn = category.getMasterItemCategoryValue().getAllowedValue();
            }else {
                hsn = "";
            }
        }
        return hsn;
    }

}
