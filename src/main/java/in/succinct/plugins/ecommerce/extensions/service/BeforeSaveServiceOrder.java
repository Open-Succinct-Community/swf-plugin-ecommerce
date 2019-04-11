package in.succinct.plugins.ecommerce.extensions.service;

import com.venky.core.util.ObjectUtil;
import com.venky.swf.db.extensions.BeforeModelSaveExtension;
import com.venky.swf.plugins.background.core.TaskManager;
import in.succinct.plugins.ecommerce.db.model.service.ServiceOrder;

import java.util.HashSet;
import java.util.Set;

public class BeforeSaveServiceOrder extends BeforeModelSaveExtension<ServiceOrder> {
    static {
        registerExtension(new BeforeSaveServiceOrder());
    }
    @Override
    public void beforeSave(ServiceOrder model) {
        if (model.getFulfillmentStatus().equals(ServiceOrder.FULFILLMENT_STATUS_COMPLETE)){
            if (ObjectUtil.isVoid(model.getServicedById())){
                throw new RuntimeException("Please enter who serviced the request");
            }

            Set<Long> allowedUsers = new HashSet<>();
            model.getService().getServiceResources().forEach(sr->allowedUsers.add(sr.getUserId()));

            if (!allowedUsers.isEmpty() && !allowedUsers.contains(model.getServicedById())){
                throw new RuntimeException("Connect be serviced by " + model.getServicedBy().getLongName());
            }
        }
    }
}
