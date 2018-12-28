package in.succinct.plugins.ecommerce.agents.order.tasks.pack;

import in.succinct.plugins.ecommerce.db.model.apis.Pack;
import com.venky.swf.db.Database;
import com.venky.swf.plugins.background.core.Task;

public class PackOrderLineTask implements Task {

	private static final long serialVersionUID = -5368745021643408509L;


    private int orderLineId = -1;
    private Double quantity = null;
    private String unitNumber = null;
	public PackOrderLineTask() {
        this(-1,null,null);
	}
    public PackOrderLineTask(int orderLineId){
        this(orderLineId,null,null);
    }
	public PackOrderLineTask(int orderLineId, Double quantity,String unitNumber) {
        this.orderLineId = orderLineId;
		this.quantity = quantity;
		this.unitNumber = unitNumber;
	}

    public void execute() {
        Pack pack = Database.getTable(Pack.class).newRecord();
        pack.setOrderLineId(orderLineId);
        pack.setPackedQuantity(quantity);
        pack.setUnitNumber(unitNumber);
        pack.pack();
	}



}
