package in.succinct.plugins.ecommerce.agents.order.tasks.manifest;

import in.succinct.plugins.ecommerce.agents.order.tasks.EntityTask;
import in.succinct.plugins.ecommerce.db.model.order.OrderLine;

public class ManifestOrderLineTask extends EntityTask<OrderLine> {
    public ManifestOrderLineTask(long id) {
        super(id);
    }

    public ManifestOrderLineTask(){
        this(-1L);
    }
    @Override
    protected void execute(OrderLine line) {
        line.manifest();
    }
}
