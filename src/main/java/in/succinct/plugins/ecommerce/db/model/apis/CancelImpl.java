package in.succinct.plugins.ecommerce.db.model.apis;

import in.succinct.plugins.ecommerce.db.model.order.OrderLine;
import com.venky.swf.db.table.ModelImpl;

public class CancelImpl extends ModelImpl<Cancel>{
    public CancelImpl(Cancel cancel){
        super(cancel);
    }
    public void cancel() throws Cancel.OrderCancellationException {
        cancel("User Cancellation", OrderLine.CANCELLATION_INITIATOR_USER);
    }
    public void reject() throws Cancel.OrderCancellationException {
        cancel(OrderLine.CANCELLATION_REASON_OUT_OF_STOCK,OrderLine.CANCELLATION_INITIATOR_COMPANY);
    }

    public void cancel(String reason, String initiator) throws Cancel.OrderCancellationException {
        Cancel cancel = getProxy();
        try {
            OrderLine ol = cancel.getOrderLine();
            if (ol == null) {
                throw new Cancel.OrderCancellationException("Invalid Order Line id " + cancel.getOrderLineId());
            }
            if (cancel.getQuantity() == null){
                ol.cancel(reason,initiator);
            }else {
                ol.cancel(reason, initiator, cancel.getQuantity());
            }
            cancel.setSuccess(true);
        }catch (RuntimeException ex){
            cancel.setError(ex.getMessage());
            cancel.setSuccess(false);
        }
    }

}
