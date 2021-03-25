package in.succinct.plugins.ecommerce.controller;

import com.venky.swf.controller.ModelController;
import com.venky.swf.controller.annotations.SingleRecordAction;
import com.venky.swf.db.Database;
import com.venky.swf.path.Path;
import com.venky.swf.plugins.background.core.TaskManager;
import com.venky.swf.views.View;
import in.succinct.plugins.ecommerce.agents.order.tasks.OrderStatusMonitor;
import in.succinct.plugins.ecommerce.db.model.order.OrderLine;

public class OrderLinesController extends ModelController<OrderLine> {

	public OrderLinesController(Path path) {
		super(path);
	}
	
	@SingleRecordAction(icon="glyphicon-thumbs-up",tooltip="Accept Order for Fulfillment")
	public View acknowledge(int orderLineId) {
		OrderLine ol = Database.getTable(OrderLine.class).get(orderLineId);
		ol.acknowledge();
        if (getIntegrationAdaptor() != null) {
            return getIntegrationAdaptor().createResponse(getPath(), ol,null,getIgnoredParentModels(), getIncludedModelFields());
        }else {
            return back();
        }
	}
	
	@SingleRecordAction(icon="glyphicon-thumbs-down",tooltip="Reject order fulfillment")
	public View reject(int orderLineId) {
		OrderLine orderLine = Database.getTable(OrderLine.class).get(orderLineId);
		orderLine.reject(OrderLine.CANCELLATION_REASON_OUT_OF_STOCK);
        if (getIntegrationAdaptor() != null) {
            return getIntegrationAdaptor().createResponse(getPath(), orderLine,null,getIgnoredParentModels(), getIncludedModelFields());
        }else {
            return back();
        }
	}
	@SingleRecordAction(icon="glyphicon-thumbs-down",tooltip="Cancel Order Line")
	public View cancel(int orderLineId) {
		OrderLine orderLine = Database.getTable(OrderLine.class).get(orderLineId);
		orderLine.cancel("No Longer Required",OrderLine.CANCELLATION_INITIATOR_USER);
		if (getIntegrationAdaptor() != null) {
			return getIntegrationAdaptor().createResponse(getPath(), orderLine,null,getIgnoredParentModels(), getIncludedModelFields());
		}else {
			return back();
		}
	}

	@SingleRecordAction(icon="glyphicon-gift", tooltip="Pack")
	public View pack(int orderLineId) {
		OrderLine orderLine = Database.getTable(OrderLine.class).get(orderLineId);
		orderLine.pack(orderLine.getToPackQuantity());
        TaskManager.instance().executeAsync(new OrderStatusMonitor(orderLine.getOrderId()),false);
        if (getIntegrationAdaptor() != null) {
            return getIntegrationAdaptor().createResponse(getPath(), orderLine,null,getIgnoredParentModels(), getIncludedModelFields());
        }else {
            return back();
        }
	}


}
