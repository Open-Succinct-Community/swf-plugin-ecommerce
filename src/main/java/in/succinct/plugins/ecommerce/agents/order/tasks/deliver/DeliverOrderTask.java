package in.succinct.plugins.ecommerce.agents.order.tasks.deliver;

import in.succinct.plugins.ecommerce.agents.order.tasks.EntityTask;
import in.succinct.plugins.ecommerce.db.model.order.Order;

public class DeliverOrderTask extends EntityTask<Order> {

	private static final long serialVersionUID = -5368745021643408509L;

	public DeliverOrderTask() {
        this(-1L);
	}
	public DeliverOrderTask(long orderId) {
		super(orderId);
	}
	
	@Override
	public void execute(Order ol) {
		ol.deliver();
	}

}
