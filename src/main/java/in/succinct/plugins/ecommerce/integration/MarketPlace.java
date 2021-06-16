package in.succinct.plugins.ecommerce.integration;

import com.venky.core.util.ObjectUtil;
import com.venky.swf.db.Database;
import com.venky.swf.sql.Conjunction;
import com.venky.swf.sql.Expression;
import com.venky.swf.sql.Operator;
import com.venky.swf.sql.Select;
import in.succinct.plugins.ecommerce.db.model.inventory.Inventory;
import in.succinct.plugins.ecommerce.db.model.order.Order;
import in.succinct.plugins.ecommerce.db.model.order.OrderLine;
import in.succinct.plugins.ecommerce.db.model.participation.Facility;
import in.succinct.plugins.ecommerce.db.model.participation.MarketPlaceIntegration;
import in.succinct.plugins.ecommerce.integration.humbhionline.HumBhiOnline;
import in.succinct.plugins.ecommerce.integration.unicommerce.UniCommerce;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public interface MarketPlace {
    public long getFacilityId();
    public String getOrderPrefix();

    default void pullOrders() {
        getWarehouseActionHandler().pullOrders(getLastOrderDownloaded());
    }

    default Order getLastOrderDownloaded() {
        Select select = new Select().from(Order.class);
        Expression where = new Expression(select.getPool(), Conjunction.AND);
        where.add(new Expression(select.getPool(),"REFERENCE", Operator.LK,getOrderPrefix()+"%"));
        select.where(where).add(String.format(" and exists (select 1 from order_lines where order_id = orders.id and ship_from_id = %d )",getFacilityId()));
        List<Order> orders = select.orderBy("ID DESC").execute(1);
        Order order = null;
        if (!orders.isEmpty()){
            order = orders.get(0);
        }
        return order;
    }
    public static List<MarketPlace> get(long facilityId){
        return get(facilityId,null);
    }
    public static List<MarketPlace> get(long facilityId,String integrationName){
        Facility facility = Database.getTable(Facility.class).get(facilityId);
        List<MarketPlace> marketPlaces = new ArrayList<>();
        if (facility == null || facility.getPreferredMarketPlaceIntegrations().isEmpty()){
            return marketPlaces;
        }
        for (MarketPlaceIntegration integration : facility.getPreferredMarketPlaceIntegrations()){
            switch (integration.getName()) {
                case "HumBhiOnline":
                    if (integrationName == null || ObjectUtil.equals(integrationName,integration.getName())) {
                        marketPlaces.add(HumBhiOnline.getInstance(integration));
                    }
                    break;
                case "UniCommerce":
                    if (integrationName == null || ObjectUtil.equals(integrationName,integration.getName())) {
                        marketPlaces.add(UniCommerce.getInstance(integration));
                    }
                    break;
                default:
                    throw new RuntimeException(String.format("% not a valid market place integrator!",integration.getName()));
            }
        }
        return marketPlaces;
    }

    public interface WarehouseActionHandler {
        public void sync(Inventory inventory);
        public void pullOrders(Order lastOrder);
        public void pack(Order order);
        public void ship(Order order);
        public void deliver(Order order);
        public void reject(OrderLine orderLine);
        public void startCount();
    }
    public interface UserActionHandler {
        public void book(JSONObject orderJson);
        public void cancel_line(JSONObject orderLineJson);
        public void confirm_delivery(JSONObject orderJson);
    }

    public WarehouseActionHandler getWarehouseActionHandler();
    public UserActionHandler getUserActionHandler();


}
