package in.succinct.plugins.ecommerce.extensions.service;

import com.venky.core.util.ObjectUtil;
import com.venky.swf.db.Database;
import com.venky.swf.db.extensions.BeforeModelSaveExtension;
import com.venky.swf.plugins.background.core.Task;
import com.venky.swf.plugins.background.core.TaskManager;
import in.succinct.plugins.ecommerce.db.model.service.ServiceAppointment;
import in.succinct.plugins.ecommerce.db.model.service.ServiceAppointment.AttemptStatus;
import in.succinct.plugins.ecommerce.db.model.service.ServiceOrder;

import java.util.Comparator;

public class BeforeSaveServiceAppointment extends BeforeModelSaveExtension<ServiceAppointment> {
    static {
        registerExtension(new BeforeSaveServiceAppointment());
    }
    @Override
    public void beforeSave(ServiceAppointment appointment) {
        if (appointment.getRawRecord().isFieldDirty("STATUS")){
            ServiceOrder order = appointment.getServiceOrder();
            boolean requiresPropagationToOrder = false;
            if (AttemptStatus.valueOf(appointment.getStatus()) == AttemptStatus.SUCCESS &&
                    order.isOpen()){
                order.setFulfillmentStatus(ServiceOrder.FULFILLMENT_STATUS_COMPLETE);
                requiresPropagationToOrder = true;
            }
            if (AttemptStatus.valueOf(appointment.getStatus()) == AttemptStatus.FAIL && order.isOpen()){
                order.setFulfillmentStatus(ServiceOrder.FULFILLMENT_STATUS_SERVICE_ATTEMPTED);
                requiresPropagationToOrder = true;
            }
            if (requiresPropagationToOrder) {
                order.save();
            }
        }
    }


}
