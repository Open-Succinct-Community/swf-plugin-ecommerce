package in.succinct.plugins.ecommerce.agents.order.tasks.manifest;

import in.succinct.plugins.ecommerce.agents.order.tasks.EntityTask;
import in.succinct.plugins.ecommerce.db.model.order.Order;
import in.succinct.plugins.ecommerce.db.model.order.OrderLine;

public class ManifestOrderTask extends EntityTask<Order> {
    public ManifestOrderTask(long id) {
        super(id);
    }

    public ManifestOrderTask(){
        this(-1L);
    }
    @Override
    protected void execute(Order order) {
        for (OrderLine orderLine : order.getOrderLines()) {
            orderLine.manifest();
        }
    }
}
