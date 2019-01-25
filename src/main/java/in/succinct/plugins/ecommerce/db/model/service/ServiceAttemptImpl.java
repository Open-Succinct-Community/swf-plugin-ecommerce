package in.succinct.plugins.ecommerce.db.model.service;

import com.venky.swf.db.table.ModelImpl;
import in.succinct.plugins.ecommerce.db.model.service.ServiceAttempt.AttemptStatus;

public class ServiceAttemptImpl extends ModelImpl<ServiceAttempt> {
    public ServiceAttemptImpl(ServiceAttempt proxy){
        super(proxy);
    }
    public ServiceAttemptImpl(){
        super();
    }
    public boolean isPending(){
        ServiceAttempt attempt = getProxy();
        return AttemptStatus.valueOf(attempt.getStatus()) == AttemptStatus.PENDING;
    }
    public void success(){
        ServiceAttempt attempt = getProxy();
        attempt.setStatus(AttemptStatus.SUCCESS.toString());
        attempt.save();

        ServiceOrder order = attempt.getServiceOrder();
        order.setFulfillmentStatus(ServiceOrder.FULFILLMENT_STATUS_COMPLETE);
        order.save();

    }
    public void failed(){
        ServiceAttempt attempt = getProxy();
        attempt.setStatus(AttemptStatus.FAIL.toString());
        attempt.save();

        ServiceOrder order = attempt.getServiceOrder();
        order.setFulfillmentStatus(ServiceOrder.FULFILLMENT_STATUS_SERVICE_ATTEMPTED);
        order.save();
    }

}
