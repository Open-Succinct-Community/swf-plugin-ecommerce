package in.succinct.plugins.ecommerce.extensions.order;

import com.venky.swf.plugins.collab.extensions.beforesave.BeforeSaveAddress;
import in.succinct.plugins.ecommerce.db.model.order.OrderAddress;

public class BeforeSaveOrderAddress extends BeforeSaveAddress<OrderAddress> {
    static {
        registerExtension(new BeforeSaveOrderAddress());
    }
}
