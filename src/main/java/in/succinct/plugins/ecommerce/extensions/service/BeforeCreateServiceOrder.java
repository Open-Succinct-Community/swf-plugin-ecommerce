package in.succinct.plugins.ecommerce.extensions.service;

import com.venky.swf.db.extensions.BeforeModelCreateExtension;
import in.succinct.plugins.ecommerce.db.model.service.ServiceOrder;

public class BeforeCreateServiceOrder extends BeforeModelCreateExtension<ServiceOrder> {
    static {
        registerExtension(new BeforeCreateServiceOrder());
    }
    @Override
    public void beforeCreate(ServiceOrder model) {
        model.setSellingPrice(model.getService().getMaxRetailPrice());
        model.setFulfillmentStatus(ServiceOrder.FULFILLMENT_STATUS_OPEN);
    }
}
