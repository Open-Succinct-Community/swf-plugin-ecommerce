package in.succinct.plugins.ecommerce.extensions.order;

import in.succinct.plugins.ecommerce.agents.order.tasks.pack.PacklistPrintTask;
import in.succinct.plugins.ecommerce.db.model.order.Order;
import com.venky.core.collections.SequenceSet;
import com.venky.swf.db.extensions.BeforeModelSaveExtension;
import com.venky.swf.plugins.background.core.TaskManager;

import java.util.List;

public class BeforeSaveOrder extends BeforeModelSaveExtension<Order> {
	static { 
		registerExtension(new BeforeSaveOrder());
	}



	@Override
	public void beforeSave(Order order) {
	    if (order.getFulfillmentStatus().equals(Order.FULFILLMENT_STATUS_PACKED) && order.getRawRecord().isFieldDirty("FULFILLMENT_STATUS")){
            TaskManager.instance().executeAsync(new PacklistPrintTask(order.getId()));
        }
    }

}
