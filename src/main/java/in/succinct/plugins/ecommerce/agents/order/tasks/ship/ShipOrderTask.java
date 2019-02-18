package in.succinct.plugins.ecommerce.agents.order.tasks.ship;

import in.succinct.plugins.ecommerce.agents.order.tasks.EntityTask;
import in.succinct.plugins.ecommerce.db.model.order.Order;

public class ShipOrderTask extends EntityTask<Order> {

	private static final long serialVersionUID = -5368745021643408509L;


	public ShipOrderTask() {
        this(-1L);
	}
    public ShipOrderTask(long orderId){
	    super(orderId);
	}


    @Override
    protected void execute(Order model) {
        model.ship();
    }


}
