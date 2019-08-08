package in.succinct.plugins.ecommerce.extensions.order;

import com.venky.swf.db.Database;
import com.venky.swf.db.extensions.BeforeModelDestroyExtension;
import in.succinct.plugins.ecommerce.db.model.order.OrderPrint;

public class BeforeDestroyOrderPrint extends BeforeModelDestroyExtension<OrderPrint> {
    static {
        registerExtension(new BeforeDestroyOrderPrint());
    }

    @Override
    public void beforeDestroy(OrderPrint model) {
        if (model.getDocumentType().equals(OrderPrint.DOCUMENT_TYPE_CARRIER_LABEL)){
            if (Database.getInstance().getCurrentUser().getId() != 1){
                throw new RuntimeException("Login as root to delete carrier labels.");
            }
        }
    }
}
