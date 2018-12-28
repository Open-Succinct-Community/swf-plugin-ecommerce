package in.succinct.plugins.ecommerce.agents.order.tasks.cancel;

import in.succinct.plugins.ecommerce.db.model.apis.Cancel;
import com.venky.swf.db.Database;
import com.venky.swf.plugins.background.core.Task;

public class CancelApiTask implements Task{
    private int orderLineId;
    private String initiator;
    private String reason;
    private Double quantity;

    public CancelApiTask(int orderLineId, String initiator, String reason){
        this(orderLineId,initiator,reason,(Double)null);
    }
    public CancelApiTask(int orderLineId, String initiator, String reason, Double quantity) {
        this.orderLineId = orderLineId;
        this.initiator = initiator ;
        this.reason = reason;
        this.quantity = quantity;
    }
    @Deprecated
    public CancelApiTask(){
    }

    @Override
    public void execute() {
        Cancel cancel = Database.getTable(Cancel.class).newRecord();
        cancel.setOrderLineId(this.orderLineId);
        cancel.setQuantity(this.quantity);
        cancel.cancel(reason,initiator);
    }
}
