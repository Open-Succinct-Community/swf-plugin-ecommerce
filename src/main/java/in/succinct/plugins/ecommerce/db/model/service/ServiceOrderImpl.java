package in.succinct.plugins.ecommerce.db.model.service;

import com.venky.core.util.ObjectUtil;
import in.succinct.plugins.ecommerce.db.model.participation.ExtendedEntityImpl;

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
        ServiceOrder order = getProxy();
        if (ObjectUtil.isVoid(order.getCancellationReason())){
            for (ServiceCancellationReason reason : order.getService().getServiceCancellationReasons()){
                if (ObjectUtil.equals(reason.getInitiator(),ServiceOrder.CANCELLATION_INITIATOR_COMPANY) && reason.isDefaultReason()){
                    order.setCancellationReasonId(reason.getId());
                    break;
                }
            }
        }
        cancel(ServiceOrder.CANCELLATION_INITIATOR_COMPANY);
    }
    public void cancel() {
        ServiceOrder order = getProxy();
        if (ObjectUtil.isVoid(order.getCancellationReason())){
            for (ServiceCancellationReason reason : order.getService().getServiceCancellationReasons()){
                if (ObjectUtil.equals(reason.getInitiator(),ServiceOrder.CANCELLATION_INITIATOR_USER) && reason.isDefaultReason()){
                    order.setCancellationReasonId(reason.getId());
                    break;
                }
            }
        }
        cancel(ServiceOrder.CANCELLATION_INITIATOR_USER);
    }
    public void cancel(String initiator){
        ServiceOrder order = getProxy();
        if (!ObjectUtil.equals(ServiceOrder.FULFILLMENT_STATUS_CANCELLED,order.getFulfillmentStatus())){
            if (ObjectUtil.isVoid(order.getCancellationReasonId())){
                throw new RuntimeException("Please provide us your reason for cancellation.");
            }
            if (order.getCancellationReason().isRemarksRequired()){
                if (ObjectUtil.isVoid(order.getRemarks())){
                    throw new RuntimeException("Please provide with additional remarks.");
                }
            }
            order.setFulfillmentStatus(ServiceOrder.FULFILLMENT_STATUS_CANCELLED);
            order.setCancellationInitiatedBy(initiator);
            order.save();
        }
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

    public boolean isOpen(){
        switch (getProxy().getFulfillmentStatus()){
            case ServiceOrder.FULFILLMENT_STATUS_CANCELLED:
            case ServiceOrder.FULFILLMENT_STATUS_COMPLETE:
                return true;
            default:
                return false;
        }
    }
}
