package in.succinct.plugins.ecommerce.db.model.order;

import com.venky.core.util.ObjectUtil;
import com.venky.swf.db.annotations.column.IS_VIRTUAL;
import com.venky.swf.db.table.ModelImpl;
import com.venky.swf.plugins.background.core.TaskManager;
import com.venky.swf.sql.Conjunction;
import com.venky.swf.sql.Expression;
import com.venky.swf.sql.Operator;
import com.venky.swf.sql.Select;
import com.venky.swf.sql.parser.SQLExpressionParser.Or;
import in.succinct.plugins.ecommerce.agents.order.tasks.ship.ManifestShippingUpdatesTask;
import in.succinct.plugins.ecommerce.integration.fedex.TrackWebServiceClient;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ManifestImpl extends ModelImpl<Manifest>{
    public ManifestImpl(Manifest proxy){
        super(proxy);
    }

    public void close(){
        Manifest manifest = getProxy();
        if (!manifest.isClosed()){
            manifest.setClosed(true);
            manifest.save();
        }else {
            TaskManager.instance().executeAsync(new ManifestShippingUpdatesTask(manifest.getId()), false);
        }

    }
    public void track(){
        if (getNumOrdersPendingDelivery() > 0) {
            TrackWebServiceClient client = new TrackWebServiceClient(getProxy());
            client.track();
        }
    }



    Set<Long> orderIds = null;
    public Set<Long> getOrderIdsPendingDelivery(){
        if (orderIds == null){
            orderIds = new HashSet<>();
            Manifest manifest = getProxy();
            Select s = new Select().from(OrderAttribute.class);
            Expression where = new Expression(s.getPool(), Conjunction.AND);
            where.add(new Expression(s.getPool(), "NAME", Operator.EQ, "manifest_number"));
            where.add(new Expression(s.getPool(), "VALUE", Operator.EQ, manifest.getManifestNumber()));
            s.where(where).add(" and exists ( select 1 from order_statuses where order_id = order_attributes.order_id and fulfillment_status = 'MANIFESTED' ) ");
            List<OrderAttribute> oas = s.execute();
            oas.forEach(oa -> {
                orderIds.add(oa.getOrderId());
            });
        }
        return orderIds;

    }

    List<Order> orders = null;
    public List<Order> getOrders(){
        if (orders == null){
            Set<Long> orderIds = getOrderIdsPendingDelivery();
            if (orderIds.isEmpty()){
                orders = new ArrayList<>();
            }else {
                orders = new Select().from(Order.class).where(new Expression(getReflector().getPool(), "ID", Operator.IN, orderIds.toArray())).execute();
            }
        }
        return orders;
    }

    public int getNumOrdersPendingDelivery() {
        return getOrderIdsPendingDelivery().size();
    }

}
