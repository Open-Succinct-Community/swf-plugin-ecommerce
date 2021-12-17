package in.succinct.plugins.ecommerce.db.model.inventory;

import com.venky.swf.db.annotations.column.COLUMN_SIZE;
import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.indexing.Index;
import com.venky.swf.db.annotations.column.ui.HIDDEN;
import com.venky.swf.db.annotations.model.ORDER_BY;
import com.venky.swf.db.model.Model;

@ORDER_BY("DISPLAY_SEQUENCE")
public interface ProductContent extends Model {
    @UNIQUE_KEY
    public long getSkuId();
    public void setSkuId(long skuId);

    @Index
    public Sku getSku();

    @UNIQUE_KEY
    public String getCategory();
    public void setCategory(String category);


    @UNIQUE_KEY
    public String getName();
    public void setName(String name);

    @UNIQUE_KEY
    public int getDisplaySequence();
    public void setDisplaySequence(int displaySequence);


    @COLUMN_SIZE(4096)
    public String getValue();
    public void setValue(String value);
}
