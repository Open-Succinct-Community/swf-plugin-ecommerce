package in.succinct.plugins.ecommerce.db.model.assets;

import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.model.Model;
import in.succinct.plugins.ecommerce.db.model.attributes.AttributeValue;

public interface AssetAttributeValue extends Model {
    @UNIQUE_KEY
    public long getAssetId();
    public void setAssetId(long AssetId);
    public Asset getAsset();

    //Must be an assetCode's atribute of one of the asset's capabilities
    @UNIQUE_KEY
    public long getAttributeValueId();
    public void setAttributeValueId(long AttributeValueId);
    public AttributeValue getAttributeValue();
}
