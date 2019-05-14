package in.succinct.plugins.ecommerce.db.model.inventory;

import com.venky.swf.db.annotations.column.IS_NULLABLE;
import com.venky.swf.db.annotations.column.ui.HIDDEN;
import com.venky.swf.db.annotations.column.validations.MinLength;
import com.venky.swf.db.model.Model;

public interface AdjustmentRequest extends Model {
    @HIDDEN
    public long getInventoryId();
    public void setInventoryId(long id);
    public Inventory getInventory();

    public double getAdjustmentQuantity();
    public void setAdjustmentQuantity(double auditQuantity);

    @MinLength(1)
    @IS_NULLABLE(false)
    public String getComment();
    public void setComment (String comment);

}
