package in.succinct.plugins.ecommerce.db.model.inventory;

import com.venky.swf.db.annotations.column.COLUMN_SIZE;
import com.venky.swf.db.annotations.column.ui.HIDDEN;
import com.venky.swf.db.annotations.column.ui.PROTECTION;
import com.venky.swf.db.annotations.column.ui.PROTECTION.Kind;
import com.venky.swf.db.annotations.model.EXPORTABLE;
import com.venky.swf.db.model.Model;
@EXPORTABLE(false)

public interface InventoryAudit extends Model {
    @PROTECTION(Kind.NON_EDITABLE)
    public long getInventoryId();
    public void setInventoryId(long id);
    public Inventory getInventory();

    public double getAuditQuantity();
    public void setAuditQuantity(double auditQuantity);

    @COLUMN_SIZE(2048)
    public String getComment();
    public void setComment (String comments);
}
