package in.succinct.plugins.ecommerce.agents.order.tasks.cancel;

import in.succinct.plugins.ecommerce.agents.order.tasks.EntityTask;
import in.succinct.plugins.ecommerce.db.model.order.Order;
import in.succinct.plugins.ecommerce.db.model.order.OrderLine;

public class CancelOrderTask extends EntityTask<Order> {

    private static final long serialVersionUID = -1763097758544463954L;

    public CancelOrderTask(){
        this(-1);
    }
    public CancelOrderTask(long id) {
        this(id, OrderLine.CANCELLATION_REASON_OUT_OF_STOCK,OrderLine.CANCELLATION_INITIATOR_COMPANY);
    }
    String initiator = null;
    String reason = null;
    public CancelOrderTask(long id, String reason, String initiator) {
        super(id);
        this.reason = reason;
        this.initiator = initiator;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + getModelClass(this).getSimpleName() +"Id=" + id + ", reason = " + reason + ", initiator = " + initiator + "]";
    }


    @Override
    protected void execute(Order model) {
        model.cancel(reason,initiator);
    }
}
