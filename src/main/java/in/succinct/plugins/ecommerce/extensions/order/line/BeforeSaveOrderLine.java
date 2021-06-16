package in.succinct.plugins.ecommerce.extensions.order.line;

import com.venky.core.collections.SequenceSet;
import com.venky.core.date.DateUtils;
import com.venky.core.math.DoubleUtils;
import com.venky.core.util.ObjectUtil;
import com.venky.extension.Registry;
import com.venky.swf.db.Database;
import com.venky.swf.db.JdbcTypeHelper.TypeConverter;
import com.venky.swf.db.extensions.BeforeModelSaveExtension;
import com.venky.swf.plugins.background.core.Task;
import com.venky.swf.plugins.background.core.TaskManager;
import in.succinct.plugins.ecommerce.agents.demand.OpendDemandIncrementor;
import in.succinct.plugins.ecommerce.agents.order.tasks.OrderStatusMonitor;
import in.succinct.plugins.ecommerce.agents.order.tasks.cancel.CancelApiTask;
import in.succinct.plugins.ecommerce.agents.order.tasks.cancel.CancelOrderTask;
import in.succinct.plugins.ecommerce.db.model.catalog.Item;
import in.succinct.plugins.ecommerce.db.model.inventory.Inventory;
import in.succinct.plugins.ecommerce.db.model.order.Order;
import in.succinct.plugins.ecommerce.db.model.order.OrderAddress;
import in.succinct.plugins.ecommerce.db.model.order.OrderLine;
import in.succinct.plugins.ecommerce.integration.MarketPlace;
import org.json.simple.JSONObject;

import java.sql.Timestamp;
import java.util.Optional;

public class BeforeSaveOrderLine extends BeforeModelSaveExtension<OrderLine> {
    static {
        registerExtension(new BeforeSaveOrderLine());
    }

    /**
     * TODO Cancel Test
     */

    @Override
    public void beforeSave(OrderLine orderLine) {
        SequenceSet<Task> tasks = new SequenceSet<>();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Order order = orderLine.getOrder();

        Timestamp demandDate = null;

        if (orderLine.getSku().getItem().isRentable()) {
            if (orderLine.getDeliveryExpectedNoEarlierThan() == null) {
                orderLine.setDeliveryExpectedNoEarlierThan(orderLine.getOrder().getShipAfterDate());
            }
            if (orderLine.getDeliveryExpectedNoLaterThan() == null) {
                orderLine.setDeliveryExpectedNoLaterThan(orderLine.getOrder().getShipByDate());
            }
            demandDate = new Timestamp(DateUtils.getStartOfDay(orderLine.getDeliveryExpectedNoEarlierThan().getTime()));
        }

        if (orderLine.getOrderedQuantity() > 0 && orderLine.getRawRecord().isFieldDirty("ORDERED_QUANTITY")) {
            orderLine.setOrderedTs(now);
            TypeConverter<Double> doubleTypeConverter = orderLine.getReflector().getJdbcTypeHelper().getTypeRef(Double.class).getTypeConverter();
            Double oldOrderedQty = doubleTypeConverter.valueOf(orderLine.getRawRecord().getOldValue("ORDERED_QUANTITY"));
            Double newOrderedQty = orderLine.getOrderedQuantity();
            double qtyOrderedNow = newOrderedQty - oldOrderedQty;
            Registry.instance().callExtensions("OrderLine." + Order.FULFILLMENT_STATUS_DOWNLOADED + ".quantity", orderLine, qtyOrderedNow);
        }
        if (!orderLine.getRawRecord().isNewRecord() && orderLine.getRawRecord().isFieldDirty("ACKNOWLEDGED_QUANTITY")) {
            if (orderLine.getAcknowledgedQuantity() > 0) {
                orderLine.setAcknowledgedTs(now);
            } else {
                orderLine.setAcknowledgedTs(null);
            }

            TypeConverter<Double> doubleTypeConverter = orderLine.getReflector().getJdbcTypeHelper().getTypeRef(Double.class).getTypeConverter();
            Double oldAcknowledgedQty = doubleTypeConverter.valueOf(orderLine.getRawRecord().getOldValue("ACKNOWLEDGED_QUANTITY"));
            Double newAcknowledgedQty = orderLine.getAcknowledgedQuantity();
            double qtyAcknowledgedNow = newAcknowledgedQty - oldAcknowledgedQty;

            Item item = orderLine.getSku().getItem();
            if (item.getAssetCodeId() != null) {
                if (item.isRentable()) {
                    if (orderLine.getReflector().isVoid(orderLine.getDeliveryExpectedNoEarlierThan()) || orderLine.getReflector().isVoid(orderLine.getDeliveryExpectedNoEarlierThan())) {
                        throw new RuntimeException("Please specify slot to book appointment.");
                    }
                }
            }
            tasks.add(new OpendDemandIncrementor(orderLine.getInventory(false).getId(), qtyAcknowledgedNow, demandDate, orderLine.getWorkSlot()));
            tasks.add(new OrderStatusMonitor(orderLine.getOrderId()));
            Registry.instance().callExtensions("OrderLine." + Order.FULFILLMENT_STATUS_ACKNOWLEDGED + ".quantity", orderLine, qtyAcknowledgedNow);
        }


        if (orderLine.getPackedQuantity() > 0 && orderLine.getToPackQuantity() <= 0 &&
                orderLine.getRawRecord().isFieldDirty("PACKED_QUANTITY")) {
            orderLine.setPackedTs(now);
            tasks.add(new OrderStatusMonitor(orderLine.getOrderId()));
            Registry.instance().callExtensions("OrderLine." + Order.FULFILLMENT_STATUS_PACKED + ".quantity", orderLine, orderLine.getPackedQuantity());
        }

        if (orderLine.getManifestedQuantity() > 0 && orderLine.getToManifestQuantity() <= 0 &&
                orderLine.getRawRecord().isFieldDirty("MANIFESTED_QUANTITY")) {
            orderLine.setManifestedTs(now);
            tasks.add(new OrderStatusMonitor(orderLine.getOrderId()));
            Registry.instance().callExtensions("OrderLine." + Order.FULFILLMENT_STATUS_MANIFESTED + ".quantity", orderLine, orderLine.getManifestedQuantity());
        }

        if (orderLine.getCancelledQuantity() > 0 && orderLine.getRawRecord().isFieldDirty("CANCELLED_QUANTITY")) {
            TypeConverter<Double> dConvertor = orderLine.getReflector().getJdbcTypeHelper().getTypeRef(Double.class).getTypeConverter();
            Double oldCancelledQty = dConvertor.valueOf(orderLine.getRawRecord().getOldValue("CANCELLED_QUANTITY"));
            if (DoubleUtils.equals(oldCancelledQty, 0)) {
                orderLine.setCancelledTs(now);
            }
            Double newCancelledQty = orderLine.getCancelledQuantity();
            double qtyCancelledNow = newCancelledQty - oldCancelledQty;

            if (!orderLine.getCancellationInitiator().equals(OrderLine.CANCELLATION_INITIATOR_MARKET_PLACE) &&
                    order.getMarketPlaceIntegrationId() != null ) {
                if (orderLine.getRemainingCancellableQuantity() > 0) {
                    //If market place order cancel entire line
                    tasks.add(new CancelApiTask(orderLine.getId(),
                            OrderLine.CANCELLATION_INITIATOR_MARKET_PLACE,
                            OrderLine.CANCELLATION_REASON_PARTIAL_CANCEL_NOT_SUPPORTED));
                }
            }

            if (qtyCancelledNow > 0 && orderLine.getShipFromId() != null) {
                Inventory inventory = orderLine.getInventory(false);
                if (inventory != null) {
                    tasks.add(new OpendDemandIncrementor(inventory.getId(), -1 * qtyCancelledNow, demandDate, orderLine.getWorkSlot()));
                }
            }
            tasks.add(new OrderStatusMonitor(orderLine.getOrderId()));

            Registry.instance().callExtensions("OrderLine." + Order.FULFILLMENT_STATUS_CANCELLED + ".quantity", orderLine, qtyCancelledNow);
        }

        if (orderLine.getShippedQuantity() > 0 && orderLine.getRawRecord().isFieldDirty("SHIPPED_QUANTITY")) {
            TypeConverter<Double> dConvertor = orderLine.getReflector().getJdbcTypeHelper().getTypeRef(Double.class).getTypeConverter();
            Double oldShippedQty = dConvertor.valueOf(orderLine.getRawRecord().getOldValue("SHIPPED_QUANTITY"));
            if (DoubleUtils.equals(oldShippedQty, 0)) {
                orderLine.setShippedTs(now);
            }
            double newShippedQty = orderLine.getShippedQuantity();
            double qtyShippedNow = newShippedQty - oldShippedQty;
            Inventory inventory = orderLine.getInventory(true);
            if (inventory != null) {
                JSONObject object = new JSONObject();
                object.put("OrderLineId", orderLine.getId());
                object.put("OrderId", orderLine.getOrderId());
                inventory.adjust(-1.0D * qtyShippedNow, object.toString());
                inventory.save();
            } else {
                inventory = Database.getTable(Inventory.class).newRecord();
                inventory.setSkuId(orderLine.getSkuId());
                inventory.setFacilityId(orderLine.getShipFromId());
                inventory.setQuantity(0.0);
                inventory.save();
            }
            tasks.add(new OpendDemandIncrementor(inventory.getId(), -1 * qtyShippedNow, demandDate, orderLine.getWorkSlot()));
            tasks.add(new OrderStatusMonitor(orderLine.getOrderId()));
            Registry.instance().callExtensions("OrderLine." + Order.FULFILLMENT_STATUS_SHIPPED + ".quantity", orderLine, qtyShippedNow);
        }
        if (orderLine.getDeliveredQuantity() > 0 && orderLine.getRawRecord().isFieldDirty("DELIVERED_QUANTITY")) {
            TypeConverter<Double> dConvertor = orderLine.getReflector().getJdbcTypeHelper().getTypeRef(Double.class).getTypeConverter();
            Double oldDeliveredQuantity = dConvertor.valueOf(orderLine.getRawRecord().getOldValue("DELIVERED_QUANTITY"));
            if (DoubleUtils.equals(oldDeliveredQuantity, 0)) {
                orderLine.setDeliveredTs(now);
                tasks.add(new OrderStatusMonitor(orderLine.getOrderId()));
            }
            double newDeliveredQty = orderLine.getDeliveredQuantity();
            double qtyDeliveredNow = newDeliveredQty - oldDeliveredQuantity;

            Optional<OrderAddress> possibleShipToFacility = orderLine.getOrder().getAddresses().stream().filter(a -> a.getFacilityId() != null && ObjectUtil.equals(a.getAddressType(), OrderAddress.ADDRESS_TYPE_SHIP_TO)).findFirst();
            if (possibleShipToFacility.isPresent()) {
                OrderAddress shipToFacility = possibleShipToFacility.get();
                Inventory.adjust(shipToFacility.getFacility(), orderLine.getSku(), qtyDeliveredNow, "Receipt into facility " + shipToFacility.getFacility().getName() + " via Order " + orderLine.getOrderId());
            }
            Registry.instance().callExtensions("OrderLine." + Order.FULFILLMENT_STATUS_DELIVERED + ".quantity", orderLine, qtyDeliveredNow);
        }
        if (orderLine.getReturnedQuantity() > 0 && orderLine.getRawRecord().isFieldDirty("RETURNED_QUANTITY")) {
            TypeConverter<Double> dConvertor = orderLine.getReflector().getJdbcTypeHelper().getTypeRef(Double.class).getTypeConverter();
            Double oldReturnedQuantity = dConvertor.valueOf(orderLine.getRawRecord().getOldValue("RETURNED_QUANTITY"));
            if (DoubleUtils.equals(oldReturnedQuantity, 0)) {
                orderLine.setReturnedTs(now);
            }
            if (!orderLine.getCancellationInitiator().equals(OrderLine.CANCELLATION_INITIATOR_MARKET_PLACE) &&
                    order.getMarketPlaceIntegrationId() != null) {
                if (orderLine.getRemainingCancellableQuantity() > 0){
                    tasks.add(new CancelApiTask(orderLine.getId(),
                            OrderLine.CANCELLATION_INITIATOR_MARKET_PLACE, OrderLine.CANCELLATION_REASON_PARTIAL_CANCEL_NOT_SUPPORTED));
                }
            }
            tasks.add(new OrderStatusMonitor(orderLine.getOrderId()));
            double newReturnedQuantity = orderLine.getReturnedQuantity();
            double qtyReturnedNow = newReturnedQuantity - oldReturnedQuantity;

            Registry.instance().callExtensions("OrderLine." + Order.FULFILLMENT_STATUS_RETURNED + ".quantity", orderLine, qtyReturnedNow);
        }
        if (isBeingFullyCancelled(orderLine) && order.getMarketPlaceIntegrationId() != null) {
            MarketPlace.get(orderLine.getShipFromId(),order.getMarketplaceIntegration().getName()).forEach(mp -> mp.getWarehouseActionHandler().reject(orderLine));
        }


        TaskManager.instance().executeAsync(tasks, false);
    }

    private boolean isBeingFullyCancelled(OrderLine model) {
        return (model.getCancelledQuantity() + model.getReturnedQuantity() > 0 && model.getRemainingCancellableQuantity() == 0
                && (model.getRawRecord().isFieldDirty("CANCELLED_QUANTITY") || model.getRawRecord().isFieldDirty("RETURNED_QUANTITY")));
    }

}
