package in.succinct.plugins.ecommerce.agents.order.tasks.ship;

import in.succinct.plugins.ecommerce.db.model.apis.Ship;
import com.venky.swf.db.Database;
import com.venky.swf.plugins.background.core.Task;

public class ShipOrderLineTask implements Task{

	private static final long serialVersionUID = -5368745021643408509L;


    private long orderLineId = -1;
    private Double quantity = null;
	public ShipOrderLineTask() {
        this(-1);
	}
    public ShipOrderLineTask(long orderLineId){
        this(orderLineId,null);
    }
	public ShipOrderLineTask(long orderLineId,Double quantity) {
        this.orderLineId = orderLineId;
		this.quantity = quantity;
	}

    public void execute() {
        Ship ship = Database.getTable(Ship.class).newRecord();
        ship.setOrderLineId(orderLineId);
        ship.setQuantity(quantity);
        ship.ship();
	}



}
