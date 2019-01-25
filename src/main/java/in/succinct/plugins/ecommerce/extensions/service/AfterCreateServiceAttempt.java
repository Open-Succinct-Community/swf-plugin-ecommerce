package in.succinct.plugins.ecommerce.extensions.service;

import com.venky.swf.db.extensions.AfterModelCreateExtension;
import com.venky.swf.db.extensions.BeforeModelSaveExtension;
import in.succinct.plugins.ecommerce.db.model.service.ServiceAttempt;
import in.succinct.plugins.ecommerce.db.model.service.ServiceAttempt.AttemptStatus;
import in.succinct.plugins.ecommerce.db.model.service.ServiceOrder;

public class AfterCreateServiceAttempt extends AfterModelCreateExtension<ServiceAttempt> {
    static {
        registerExtension(new AfterCreateServiceAttempt());
    }
    @Override
    public void afterCreate(ServiceAttempt model) {
        ServiceOrder order = model.getServiceOrder();
        if (ServiceAttempt.AttemptStatus.valueOf(model.getStatus()) == AttemptStatus.PENDING){
            order.setFulfillmentStatus(ServiceOrder.FULFILLMENT_STATUS_APPOINTMENT_PLANNED);
        }
        order.save();
    }
}
