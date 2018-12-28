package in.succinct.plugins.ecommerce.agents.order.tasks.deliver;

import in.succinct.plugins.ecommerce.agents.order.tasks.EntityTask;
import in.succinct.plugins.ecommerce.db.model.order.OrderLine;

public class DeliverOrderLineTask extends EntityTask<OrderLine> {

	private static final long serialVersionUID = -5368745021643408509L;

	public DeliverOrderLineTask() {
        this(-1L);
	}
	public DeliverOrderLineTask(long orderLineId) {
		super(orderLineId);
	}
	
	@Override
	public void execute(OrderLine ol) {
		ol.deliver();
	}

}
