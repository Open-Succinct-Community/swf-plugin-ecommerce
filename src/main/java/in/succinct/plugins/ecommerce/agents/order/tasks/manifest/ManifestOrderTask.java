package in.succinct.plugins.ecommerce.agents.order.tasks.manifest;

import com.venky.swf.db.Database;
import in.succinct.plugins.ecommerce.agents.order.tasks.EntityTask;
import in.succinct.plugins.ecommerce.db.model.order.Manifest;
import in.succinct.plugins.ecommerce.db.model.order.Order;
import in.succinct.plugins.ecommerce.db.model.order.OrderAttribute;
import in.succinct.plugins.ecommerce.db.model.order.OrderLine;
import in.succinct.plugins.ecommerce.db.model.order.OrderLineAttribute;

import java.util.Map;

public class ManifestOrderTask extends EntityTask<Order> {
    long manifestId = -1;
    public ManifestOrderTask(long orderId, long manifestId) {
        super(orderId);
        this.manifestId = manifestId;
    }

    public ManifestOrderTask(){
        this(-1L,-1L);
    }
    @Override
    protected void execute(Order order) {
        Manifest manifest = Database.getTable(Manifest.class).get(manifestId);
        for (OrderLine orderLine : order.getOrderLines()) {
            orderLine.manifest();
        }
        Map<String, OrderAttribute> map = order.getAttributeMap();

        map.get("manifest_id").setValue(String.valueOf(manifest.getId()));
        map.get("manifest_number").setValue(manifest.getManifestNumber());
        map.get("courier").setValue(manifest.getPreferredCarrier().getName());
        map.get("preferred_carrier_id").setValue(String.valueOf(manifest.getPreferredCarrierId()));


        order.saveAttributeMap(map);

    }
}
