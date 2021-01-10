package in.succinct.plugins.ecommerce.db.model.attributes;

import com.venky.core.collections.SequenceSet;
import com.venky.swf.db.annotations.column.COLUMN_SIZE;
import com.venky.swf.db.annotations.column.IS_NULLABLE;
import com.venky.swf.db.annotations.column.IS_VIRTUAL;
import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.indexing.Index;
import com.venky.swf.db.annotations.column.ui.HIDDEN;
import com.venky.swf.db.annotations.model.HAS_DESCRIPTION_FIELD;
import com.venky.swf.db.annotations.model.MENU;
import com.venky.swf.db.model.Model;
import com.venky.swf.db.model.reflection.ModelReflector;
import com.venky.swf.pm.DataSecurityFilter;
import com.venky.swf.sql.Expression;
import com.venky.swf.sql.Operator;
import com.venky.swf.sql.Select;
import in.succinct.plugins.ecommerce.db.model.catalog.Item;

import java.util.List;

@MENU("Inventory")
@HAS_DESCRIPTION_FIELD("LONG_DESCRIPTION")
public interface AssetCode extends Model {
    @Index
    @IS_NULLABLE(false)
    @UNIQUE_KEY
    public String getCode();
    public void setCode(String code);

    @Index
    @IS_NULLABLE(false)
    @COLUMN_SIZE(4096)
    public String getDescription();
    public void setDescription(String description);


    @IS_NULLABLE
    public Long getRentalAssetCodeId();
    public void setRentalAssetCodeId(Long RentalAssetCodeId);
    public AssetCode getRentalAssetCode();


    @IS_VIRTUAL
    @Index
    public String getLongDescription();

    public Double getGstPct();
    public void setGstPct(Double gstPct);

    @IS_VIRTUAL
    public boolean isHsn();

    @IS_VIRTUAL
    public boolean isSac();

    public List<AssetCodeAttribute> getAssetCodeAttributes();

    @HIDDEN
    public List<Item> getItems();

    public static List<Long> getDeliverySkuIds(){
        List<AssetCode> assetCodes = new Select().from(AssetCode.class).where(new Expression(ModelReflector.instance(AssetCode.class).getPool(),"CODE", Operator.LK,"99681%")).execute();
        List<Long> deliverySkuIds = new SequenceSet<>();
        for (AssetCode ac : assetCodes){
            List<in.succinct.plugins.ecommerce.db.model.catalog.Item> items = ac.getItems();
            for (in.succinct.plugins.ecommerce.db.model.catalog.Item i :items){
                deliverySkuIds.addAll(DataSecurityFilter.getIds(i.getSkus()));
            }
        }
        return deliverySkuIds;
    }
}
