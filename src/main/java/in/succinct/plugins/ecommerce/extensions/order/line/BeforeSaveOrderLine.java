package in.succinct.plugins.ecommerce.extensions.order.line;

import com.venky.core.collections.SequenceSet;
import com.venky.core.math.DoubleUtils;
import com.venky.swf.db.JdbcTypeHelper.TypeConverter;
import com.venky.swf.db.extensions.BeforeModelSaveExtension;
import com.venky.swf.plugins.background.core.Task;
import com.venky.swf.plugins.background.core.TaskManager;
import in.succinct.plugins.ecommerce.agents.demand.OpendDemandIncrementor;
import in.succinct.plugins.ecommerce.agents.order.tasks.OrderStatusMonitor;
import in.succinct.plugins.ecommerce.agents.order.tasks.cancel.CancelOrderTask;
import in.succinct.plugins.ecommerce.db.model.inventory.Inventory;
import in.succinct.plugins.ecommerce.db.model.order.OrderLine;

import java.sql.Timestamp;

public class BeforeSaveOrderLine extends BeforeModelSaveExtension<OrderLine>{
	static { 
		registerExtension(new BeforeSaveOrderLine());
	}

	/** TODO Cancel Test */

	@Override
	public void beforeSave(OrderLine orderLine) {
		SequenceSet<Task> tasks = new SequenceSet<>();
        Timestamp now = new Timestamp(System.currentTimeMillis());

		if (orderLine.getOrderedQuantity() > 0 && orderLine.getRawRecord().isFieldDirty("ORDERED_QUANTITY") ){
            orderLine.setOrderedTs(now);
        }
		if (orderLine.getAcknowledgedQuantity() > 0 &&
                orderLine.getRawRecord().isFieldDirty("ACKNOWLEDGED_QUANTITY")) {
            orderLine.setAcknowledgedTs(now);

            TypeConverter<Double> doubleTypeConverter = orderLine.getReflector().getJdbcTypeHelper().getTypeRef(Double.class).getTypeConverter();
            Double oldAcknowledgedQty = doubleTypeConverter.valueOf(orderLine.getRawRecord().getOldValue("ACKNOWLEDGED_QUANTITY"));
            Double newAcknowledgedQty = orderLine.getAcknowledgedQuantity();
            double qtyAcknowledgedNow = newAcknowledgedQty - oldAcknowledgedQty ;
			tasks.add(new OpendDemandIncrementor(orderLine.getInventory(false).getId(),qtyAcknowledgedNow));
            tasks.add(new OrderStatusMonitor(orderLine.getOrderId()));
		}
		

		if (orderLine.getPackedQuantity() > 0 && orderLine.getToPackQuantity() <= 0 &&
                orderLine.getRawRecord().isFieldDirty("PACKED_QUANTITY")) {
		    orderLine.setPackedTs(now);
			tasks.add(new OrderStatusMonitor(orderLine.getOrderId()));
		}
		
		if (orderLine.getManifestedQuantity() > 0 && orderLine.getToManifestQuantity() <=0 &&
                orderLine.getRawRecord().isFieldDirty("MANIFESTED_QUANTITY")) {
		    orderLine.setManifestedTs(now);
			tasks.add(new OrderStatusMonitor(orderLine.getOrderId()));
		}
		if (orderLine.getCancelledQuantity() > 0 && orderLine.getRawRecord().isFieldDirty("CANCELLED_QUANTITY")){
			TypeConverter<Double> dConvertor = orderLine.getReflector().getJdbcTypeHelper().getTypeRef(Double.class).getTypeConverter();
			Double oldCancelledQty = dConvertor.valueOf(orderLine.getRawRecord().getOldValue("CANCELLED_QUANTITY"));
			if (DoubleUtils.equals(oldCancelledQty, 0)) {
				orderLine.setCancelledTs(now);
			}
            Double newCancelledQty = orderLine.getCancelledQuantity();
            double qtyCancelledNow = newCancelledQty - oldCancelledQty ;

			if (!orderLine.getCancellationInitiator().equals(OrderLine.CANCELLATION_INITIATOR_MARKET_PLACE)){
				tasks.add(new CancelOrderTask(orderLine.getOrderId(), OrderLine.CANCELLATION_REASON_PARTIAL_CANCEL_NOT_SUPPORTED,
						OrderLine.CANCELLATION_INITIATOR_MARKET_PLACE));
            }

			if (qtyCancelledNow > 0 && orderLine.getShipFromId() != null) {
			    Inventory inventory = orderLine.getInventory(false);
			    if (inventory != null) {
                    tasks.add(new OpendDemandIncrementor(inventory.getId(), -1 * qtyCancelledNow));
                }
            }
            tasks.add(new OrderStatusMonitor(orderLine.getOrderId()));
        }
		
		if (orderLine.getShippedQuantity() > 0 && orderLine.getRawRecord().isFieldDirty("SHIPPED_QUANTITY")) {
            TypeConverter<Double> dConvertor = orderLine.getReflector().getJdbcTypeHelper().getTypeRef(Double.class).getTypeConverter();
            Double oldShippedQty = dConvertor.valueOf(orderLine.getRawRecord().getOldValue("SHIPPED_QUANTITY"));
            if (DoubleUtils.equals(oldShippedQty, 0)){
                orderLine.setShippedTs(now);
            }
            double newShippedQty = orderLine.getShippedQuantity();
            double qtyShippedNow = newShippedQty - oldShippedQty;
			Inventory inventory = orderLine.getInventory(true);
			if (inventory != null ) { 
				inventory.setQuantity(inventory.getQuantity() - qtyShippedNow);
				inventory.save();
			}
            tasks.add(new OpendDemandIncrementor(inventory.getId(),-1*qtyShippedNow));
            tasks.add(new OrderStatusMonitor(orderLine.getOrderId()));
		}
		if (orderLine.getDeliveredQuantity() > 0  && orderLine.getRawRecord().isFieldDirty("DELIVERED_QUANTITY")) {
			TypeConverter<Double> dConvertor = orderLine.getReflector().getJdbcTypeHelper().getTypeRef(Double.class).getTypeConverter();
			Double oldDeliveredQuantity = dConvertor.valueOf(orderLine.getRawRecord().getOldValue("DELIVERED_QUANTITY"));
			if (DoubleUtils.equals(oldDeliveredQuantity,0)) {
				orderLine.setDeliveredTs(now);
                tasks.add(new OrderStatusMonitor(orderLine.getOrderId()));
			}
		}
		if (orderLine.getReturnedQuantity() > 0 && orderLine.getRawRecord().isFieldDirty("RETURNED_QUANTITY")) {

            TypeConverter<Double> dConvertor = orderLine.getReflector().getJdbcTypeHelper().getTypeRef(Double.class).getTypeConverter();
            Double oldReturnedQuantity = dConvertor.valueOf(orderLine.getRawRecord().getOldValue("RETURNED_QUANTITY"));
            if (DoubleUtils.equals(oldReturnedQuantity,0)) {
                orderLine.setReturnedTs(now);
                if ( orderLine.getCancellationInitiator().equals(OrderLine.CANCELLATION_INITIATOR_COMPANY) ){
                    tasks.add(new CancelOrderTask(orderLine.getOrderId(),OrderLine.CANCELLATION_REASON_PARTIAL_CANCEL_NOT_SUPPORTED,OrderLine.CANCELLATION_INITIATOR_MARKET_PLACE));
                }
                tasks.add(new OrderStatusMonitor(orderLine.getOrderId()));
            }
        }

		TaskManager.instance().executeAsync(tasks,false);
	}

}
