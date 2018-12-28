package in.succinct.plugins.ecommerce.agents.order.tasks;

import com.venky.core.util.Bucket;
import in.succinct.plugins.ecommerce.db.model.order.Order;
import in.succinct.plugins.ecommerce.db.model.order.OrderLine;

public class OrderStatusMonitor extends EntityTask<Order> {
    private static final long serialVersionUID = -2307669734287920701L;

    @Deprecated
    public OrderStatusMonitor() {
        this(-1);
    }
    public OrderStatusMonitor(long orderId) {
        super(orderId);
    }
    @Override
    public void execute(Order order) {

        Bucket numLinesToBeAcknowledged = new Bucket();
        Bucket numLinesToBePacked = new Bucket();
        Bucket numLinesToBeManifested = new Bucket();
        Bucket numLinesToBeShipped = new Bucket();

        Bucket numLinesCancelled = new Bucket();
        Bucket numLinesShipped = new Bucket();
        Bucket numLinesDelivered = new Bucket();
        Bucket numLinesReturned = new Bucket();



        for (OrderLine ol : order.getOrderLines()) {
            if (ol.getToAcknowledgeQuantity() > 0) {
                numLinesToBeAcknowledged.increment();
            }

            if (ol.getToPackQuantity() > 0) {
                numLinesToBePacked.increment();
            } else if (ol.isUnitNumberCaptureRequired()) {
                if (ol.getUnitNumbers().size() != ol.getPackedQuantity()) {
                    throw new RuntimeException("Order Line:" + ol.getId() + " is not packed correctly with respect to capturing of serial/imei/.. numbers");
                }
            }
            if (ol.getToManifestQuantity() > 0) {
                numLinesToBeManifested.increment();
            }
            if (ol.getToShipQuantity() > 0){
                numLinesToBeShipped.increment();
            }
            if (ol.getShippedQuantity() > 0) {
                numLinesShipped.increment();
            }
            if (ol.getDeliveredQuantity() > 0) {
                numLinesDelivered.increment();
            }
            if (ol.getReturnedQuantity() > 0 ) {
                numLinesReturned.increment();
            }
            if (ol.getCancelledQuantity() > 0) {
                numLinesCancelled.increment();
            }
        }
        if (numLinesToBeShipped.intValue() == 0){
            if (numLinesReturned.value() > 0) {
                order.setFulfillmentStatus(Order.FULFILLMENT_STATUS_RETURNED);
            }else if (numLinesDelivered.intValue() > 0) {
                order.setFulfillmentStatus(Order.FULFILLMENT_STATUS_DELIVERED);
            }else if (numLinesShipped.intValue() > 0) {
                order.setFulfillmentStatus(Order.FULFILLMENT_STATUS_SHIPPED);
            }else if (numLinesCancelled.intValue()> 0) {
                order.setFulfillmentStatus(Order.FULFILLMENT_STATUS_CANCELLED);
            }else {
                order.setFulfillmentStatus(Order.FULFILLMENT_STATUS_DOWNLOADED);
            }
        }else {
            if (numLinesToBeManifested.intValue()> 0){
                if (numLinesToBePacked.intValue() >0 ) {
                    if (numLinesToBeAcknowledged.intValue() > 0) {
                        order.setFulfillmentStatus(Order.FULFILLMENT_STATUS_DOWNLOADED);
                    }else {
                        order.setFulfillmentStatus(Order.FULFILLMENT_STATUS_ACKNOWLEDGED);
                    }
                }else {
                    order.setFulfillmentStatus(Order.FULFILLMENT_STATUS_PACKED);
                }
            }else {
                order.setFulfillmentStatus(Order.FULFILLMENT_STATUS_MANIFESTED);
            }
        }
        order.save();
    }

}
