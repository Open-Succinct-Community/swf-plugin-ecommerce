package in.succinct.plugins.ecommerce.db.model.attributes;

import com.venky.swf.db.annotations.column.IS_NULLABLE;
import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.validations.Enumeration;
import com.venky.swf.db.model.Model;

public interface AssetCodeAttribute extends Model {
    @UNIQUE_KEY
    public long getAssetCodeId();
    public void setAssetCodeId(long assetCodeAttributeId);
    public AssetCode getAssetCode();

    public static  final String ATTRIBUTE_TYPE_CATALOG = "Catalog";
    public static  final String ATTRIBUTE_TYPE_INVENTORY = "Inventory";

    @Enumeration(ATTRIBUTE_TYPE_CATALOG +"," + ATTRIBUTE_TYPE_INVENTORY)
    public String getAttributeType();
    public void setAttributeType(String attributeType);


    @IS_NULLABLE(false)
    public long getAttributeId();
    public void setAttributeId(long AttributeId);
    public Attribute getAttribute();

}
