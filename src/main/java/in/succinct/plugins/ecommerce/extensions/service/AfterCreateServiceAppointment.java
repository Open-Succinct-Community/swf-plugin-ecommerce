package in.succinct.plugins.ecommerce.extensions.service;

import com.venky.swf.db.extensions.AfterModelCreateExtension;
import in.succinct.plugins.ecommerce.db.model.service.ServiceAppointment;
import in.succinct.plugins.ecommerce.db.model.service.ServiceAppointment.AttemptStatus;
import in.succinct.plugins.ecommerce.db.model.service.ServiceOrder;

public class AfterCreateServiceAppointment extends AfterModelCreateExtension<ServiceAppointment> {
    static {
        registerExtension(new AfterCreateServiceAppointment());
    }
    @Override
    public void afterCreate(ServiceAppointment model) {
        ServiceOrder order = model.getServiceOrder();
        if (ServiceAppointment.AttemptStatus.valueOf(model.getStatus()) == AttemptStatus.PENDING){
            order.setFulfillmentStatus(ServiceOrder.FULFILLMENT_STATUS_APPOINTMENT_PLANNED);
        }
        order.save();
    }
}
