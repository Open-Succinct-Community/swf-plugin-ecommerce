package in.succinct.plugins.ecommerce.extensions.service;

import com.venky.core.util.ObjectUtil;
import com.venky.swf.db.Database;
import com.venky.swf.db.extensions.BeforeModelSaveExtension;
import in.succinct.plugins.ecommerce.db.model.service.ServiceOrder;
import in.succinct.plugins.ecommerce.db.model.service.ServiceOrderRemark;

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
        if (model.getRawRecord().isFieldDirty("REMARKS") &&
                !model.getReflector().isVoid(model.getRemarks()) &&
                !model.getRawRecord().isNewRecord() && model.getId() > 0 ){
            ServiceOrderRemark remark = Database.getTable(ServiceOrderRemark.class).newRecord();
            remark.setServiceOrderId(model.getId());
            remark.setRemarks(model.getRemarks());
            remark.save();
        }
    }
}
