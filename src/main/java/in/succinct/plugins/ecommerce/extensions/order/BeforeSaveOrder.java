package in.succinct.plugins.ecommerce.extensions.order;

import com.venky.cache.Cache;
import com.venky.swf.db.Database;
import com.venky.swf.db.model.reflection.ModelReflector;
import com.venky.swf.sql.Expression;
import com.venky.swf.sql.Operator;
import com.venky.swf.sql.Select;
import in.succinct.plugins.ecommerce.agents.order.tasks.pack.PacklistPrintTask;
import in.succinct.plugins.ecommerce.agents.order.tasks.ship.CreateManifestTask;
import in.succinct.plugins.ecommerce.db.model.order.Order;
import com.venky.swf.db.extensions.BeforeModelSaveExtension;
import com.venky.swf.plugins.background.core.TaskManager;
import in.succinct.plugins.ecommerce.db.model.order.OrderLine;
import in.succinct.plugins.ecommerce.db.model.order.OrderStatus;
import in.succinct.plugins.ecommerce.db.model.participation.Facility;
import in.succinct.plugins.ecommerce.db.model.participation.MarketPlaceIntegration;
import in.succinct.plugins.ecommerce.integration.MarketPlace;
import in.succinct.plugins.ecommerce.integration.unicommerce.UniCommerce;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BeforeSaveOrder extends BeforeModelSaveExtension<Order> {
	static { 
		registerExtension(new BeforeSaveOrder());
	}



	@Override
	public void beforeSave(Order order) {
		List<OrderLine> lines = order.getRawRecord().isNewRecord() ? new ArrayList<>() : order.getOrderLines();

		Facility facility = lines.isEmpty()? null : lines.get(0).getShipFrom();
		List<MarketPlace> marketPlaceIntegrations;

		if (facility != null && !facility.getPreferredMarketPlaceIntegrations().isEmpty()){
			 marketPlaceIntegrations = MarketPlace.get(facility.getId());
		}else {
			marketPlaceIntegrations = new ArrayList<>();
		}
		if (order.getFulfillmentStatus().equals(Order.FULFILLMENT_STATUS_DELIVERED) && order.getRawRecord().isFieldDirty("FULFILLMENT_STATUS")) {
			for (MarketPlace mp : marketPlaceIntegrations){
				mp.getWarehouseActionHandler().deliver(order);
			}
		}else if (order.getFulfillmentStatus().equals(Order.FULFILLMENT_STATUS_SHIPPED) && order.getRawRecord().isFieldDirty("FULFILLMENT_STATUS")) {
			for (MarketPlace mp : marketPlaceIntegrations){
				mp.getWarehouseActionHandler().ship(order);
			}
		}else if (order.getFulfillmentStatus().equals(Order.FULFILLMENT_STATUS_MANIFESTED) && order.getRawRecord().isFieldDirty("FULFILLMENT_STATUS")){
			TaskManager.instance().executeAsync(new PacklistPrintTask(order.getId()),false);
			for (MarketPlace mp : marketPlaceIntegrations){
				mp.getWarehouseActionHandler().pack(order);
			}
        }else if (order.getFulfillmentStatus().equals(Order.FULFILLMENT_STATUS_PACKED) && order.getRawRecord().isFieldDirty("FULFILLMENT_STATUS")){
			Set<Long> facilityIds = new HashSet<>();
			for (OrderLine orderLine : order.getOrderLines()) {
				Long facilityId = orderLine.getShipFromId();
				if (facilityId != null) {
					facilityIds.add(facilityId);
				}
			}
			List<Facility> facilities = new Select().from(Facility.class).where(new Expression(ModelReflector.instance(Facility.class).getPool(),"ID", Operator.IN,facilityIds.toArray())).execute();
			facilities.forEach(f->{
				f.getPreferredCarriers().forEach(preferredCarrier->{
					TaskManager.instance().executeAsync(new CreateManifestTask(preferredCarrier.getId()),false);
				});
			});
		}
		if (!order.getRawRecord().isNewRecord() && order.getRawRecord().isFieldDirty("FULFILLMENT_STATUS")){
			Cache<String, OrderStatus> cache = new Cache<String, OrderStatus>() {
				@Override
				protected OrderStatus getValue(String status) {
					OrderStatus orderStatus = Database.getTable(OrderStatus.class).newRecord();
					orderStatus.setFulfillmentStatus(status);
					orderStatus.setOrderId(order.getId());
					orderStatus.setStatusDate(new Timestamp(System.currentTimeMillis()));
					return orderStatus;
				}
			};
			order.getOrderStatuses().forEach(orderStatus -> cache.put(orderStatus.getFulfillmentStatus(),orderStatus));
			cache.get(order.getFulfillmentStatus()).save(); //Only fist time order reaches a status it is logged.
		}
    }



}
