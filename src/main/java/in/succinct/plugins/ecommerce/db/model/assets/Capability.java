package in.succinct.plugins.ecommerce.db.model.assets;

import com.venky.swf.db.annotations.column.IS_NULLABLE;
import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.model.Model;
import in.succinct.plugins.ecommerce.db.model.attributes.AssetCode;


public interface Capability extends Model {
    @UNIQUE_KEY
    public Long getAssetId();
    public void setAssetId(Long AssetId);
    public Asset getAsset();

    @UNIQUE_KEY
    @IS_NULLABLE(false)
    public long getAssetCodeId();
    public void setAssetCodeId(long AssetCodeId);
    public AssetCode getAssetCode();


    public String getAssetInventoryHash();
    public void setAssetInventoryHash(String hash);

    public String getAssetCatalogHash();
    public void setAssetCatalogHash(String hash);

}
