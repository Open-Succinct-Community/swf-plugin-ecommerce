package in.succinct.plugins.ecommerce.db.model.attributes;

import com.venky.core.collections.SequenceSet;
import com.venky.swf.db.annotations.column.COLUMN_DEF;
import com.venky.swf.db.annotations.column.COLUMN_SIZE;
import com.venky.swf.db.annotations.column.IS_NULLABLE;
import com.venky.swf.db.annotations.column.IS_VIRTUAL;
import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.defaulting.StandardDefault;
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
import in.succinct.plugins.ecommerce.db.model.inventory.Sku;

import java.util.List;

@MENU("Inventory")
@HAS_DESCRIPTION_FIELD("LONG_DESCRIPTION")
public interface AssetCode extends com.venky.swf.plugins.gst.db.model.assets.AssetCode {

    public List<AssetCodeAttribute> getAssetCodeAttributes();

    @HIDDEN
    public List<Item> getItems();


    public static List<Sku> getDeliverySkus(){
        List<AssetCode> assetCodes = new Select().from(AssetCode.class).where(new Expression(ModelReflector.instance(AssetCode.class).getPool(),"CODE", Operator.LK,"99681%")).execute();
        List<Sku> deliverySkus = new SequenceSet<>();
        for (AssetCode ac : assetCodes){
            List<in.succinct.plugins.ecommerce.db.model.catalog.Item> items = ac.getItems();
            for (in.succinct.plugins.ecommerce.db.model.catalog.Item i :items){
                deliverySkus.addAll(i.getSkus());
            }
        }
        return deliverySkus;
    }

    public static List<Long> getDeliverySkuIds(){
        return DataSecurityFilter.getIds(getDeliverySkus());
    }
}
