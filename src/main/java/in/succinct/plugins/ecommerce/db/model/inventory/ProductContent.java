package in.succinct.plugins.ecommerce.db.model.inventory;

import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.ui.HIDDEN;
import com.venky.swf.db.model.Model;


public interface ProductContent extends Model {
    @UNIQUE_KEY
    public long getSkuId();
    public void setSkuId(long skuId);
    public Sku getSku();

    @UNIQUE_KEY
    public String getName();
    public void setName(String name);

    public String getValue();
    public void setValue(String value);
}
