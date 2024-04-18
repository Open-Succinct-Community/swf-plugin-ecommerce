package in.succinct.plugins.ecommerce.db.model.participation;

import com.venky.swf.db.annotations.column.COLUMN_DEF;
import com.venky.swf.db.annotations.column.defaulting.StandardDefault;
import com.venky.swf.db.annotations.column.ui.WATERMARK;

public interface MasterFacilityCategoryValue extends com.venky.swf.plugins.collab.db.model.participants.admin.MasterFacilityCategoryValue {
    @COLUMN_DEF(StandardDefault.BOOLEAN_TRUE)
    @WATERMARK("Can ship products")
    public boolean isCanShip();
    public  void setCanShip(boolean shipping);


    @COLUMN_DEF(StandardDefault.BOOLEAN_TRUE)
    @WATERMARK("Can stock products")
    public boolean isCanStockProducts();
    public  void setCanStockProducts(boolean canStockProducts);

    @WATERMARK("Can process returns")
    @COLUMN_DEF(StandardDefault.BOOLEAN_FALSE)
    public boolean isCanProcessReturns();
    public  void setCanProcessReturns(boolean returns);

    @WATERMARK("Is invoices externally generated.")
    @COLUMN_DEF(StandardDefault.BOOLEAN_FALSE)
    public boolean isCustomerInvoiceExternallyHandled();
    public void setCustomerInvoiceExternallyHandled(boolean customerInvoiceExternallyHandled);

}
