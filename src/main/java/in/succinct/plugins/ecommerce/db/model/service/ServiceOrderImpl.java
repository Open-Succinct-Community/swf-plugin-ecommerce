package in.succinct.plugins.ecommerce.db.model.service;

import com.venky.core.util.ObjectUtil;
import com.venky.swf.db.annotations.column.IS_VIRTUAL;
import in.succinct.plugins.ecommerce.db.model.order.Order;
import in.succinct.plugins.ecommerce.db.model.participation.ExtendedEntityImpl;
import in.succinct.plugins.ecommerce.db.model.service.ServiceOrder.CancelReason;

import java.sql.Timestamp;
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
            order.setCancellationReason(CancelReason.CANNOT_SERVICE.toString());
        }
        cancel(ServiceOrder.CANCELLATION_INITIATOR_COMPANY);
    }
    public void cancel() {
        ServiceOrder order = getProxy();
        if (ObjectUtil.isVoid(order.getCancellationReason())){
            order.setCancellationReason(CancelReason.USER_CANCELLATION.toString());
        }
        cancel(ServiceOrder.CANCELLATION_INITIATOR_USER);
    }
    public void cancel(String initiator){
        ServiceOrder order = getProxy();
        if (!ObjectUtil.equals(ServiceOrder.FULFILLMENT_STATUS_CANCELLED,order.getFulfillmentStatus())){
            if (ObjectUtil.isVoid(order.getCancellationReason())){
                throw new RuntimeException("Please provide us your reason for cancellation.");
            }
            if (ObjectUtil.equals(order.getCancellationReason(), CancelReason.OTHER.toString())){
                if (ObjectUtil.isVoid(order.getRemarks())){
                    throw new RuntimeException("It is mandatory to provide Remarks if your reason is not in the list.");
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
}
