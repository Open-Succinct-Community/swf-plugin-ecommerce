package in.succinct.plugins.ecommerce.db.model.demand;

import in.succinct.plugins.ecommerce.db.model.inventory.Inventory;
import com.venky.core.util.Bucket;
import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.model.Model;

public interface Demand extends Model{
    @UNIQUE_KEY
    public long getInventoryId();
    public void setInventoryId(long id);
    public Inventory getInventory();

    public Bucket getQuantity();
    public void setQuantity(Bucket demand);

}
