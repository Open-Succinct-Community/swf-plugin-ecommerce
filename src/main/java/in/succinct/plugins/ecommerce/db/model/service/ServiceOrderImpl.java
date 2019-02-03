package in.succinct.plugins.ecommerce.db.model.service;

import in.succinct.plugins.ecommerce.db.model.participation.ExtendedEntityImpl;
import in.succinct.plugins.ecommerce.db.model.service.ServiceOrder.CancelReason;

import java.util.List;
import java.util.stream.Collectors;

public class ServiceOrderImpl extends ExtendedEntityImpl<ServiceOrder,ServiceOrderAttribute,
        ServiceOrderAddress,ServiceOrderPrint> {
    public ServiceOrderImpl(){

    }
    public ServiceOrderImpl(ServiceOrder serviceOrder){
        super(serviceOrder);
    }
    public String getOrderNumber(){
        return String.valueOf(getProxy().getId());
    }


    public void reject() {
        cancel(CancelReason.CANNOT_SERVICE.toString(),getProxy().getCompany().getName());
    }
    public void cancel() {
        cancel(CancelReason.USER_CANCELLATION.toString(),ServiceOrder.CANCELLATION_INITIATOR_USER);
    }
    public void cancel(String reason, String initiator){
        ServiceOrder order = getProxy();
        order.setFulfillmentStatus(ServiceOrder.FULFILLMENT_STATUS_CANCELLED);
        order.setCancellationReason(reason);
        order.setCancellationInitiatedBy(initiator);
        order.save();
    }

    public void complete() {
        ServiceOrder order = getProxy();
        List<ServiceAppointment> pendingAttempts = order.getServiceAttempts().stream().filter(a->a.isPending()).collect(Collectors.toList());
        if (pendingAttempts.size() == 1){
            pendingAttempts.get(0).success();
        }else {
            throw new RuntimeException("No Appointment to close out");
        }
    }
}
