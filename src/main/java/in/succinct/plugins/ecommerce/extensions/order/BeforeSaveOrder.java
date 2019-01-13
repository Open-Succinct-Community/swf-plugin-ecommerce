package in.succinct.plugins.ecommerce.extensions.order;

import com.venky.swf.db.model.reflection.ModelReflector;
import com.venky.swf.sql.Expression;
import com.venky.swf.sql.Operator;
import com.venky.swf.sql.Select;
import in.succinct.plugins.ecommerce.agents.order.tasks.pack.PacklistPrintTask;
import in.succinct.plugins.ecommerce.agents.order.tasks.ship.CreateManifestTask;
import in.succinct.plugins.ecommerce.db.model.order.Manifest;
import in.succinct.plugins.ecommerce.db.model.order.Order;
import com.venky.core.collections.SequenceSet;
import com.venky.swf.db.extensions.BeforeModelSaveExtension;
import com.venky.swf.plugins.background.core.TaskManager;
import in.succinct.plugins.ecommerce.db.model.order.OrderLine;
import in.succinct.plugins.ecommerce.db.model.participation.Facility;
import in.succinct.plugins.ecommerce.db.model.sequence.SequentialNumber;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BeforeSaveOrder extends BeforeModelSaveExtension<Order> {
	static { 
		registerExtension(new BeforeSaveOrder());
	}



	@Override
	public void beforeSave(Order order) {
	    if (order.getFulfillmentStatus().equals(Order.FULFILLMENT_STATUS_MANIFESTED) && order.getRawRecord().isFieldDirty("FULFILLMENT_STATUS")){
            TaskManager.instance().executeAsync(new PacklistPrintTask(order.getId()));
        }else if (order.getFulfillmentStatus().equals(Order.FULFILLMENT_STATUS_PACKED) && order.getRawRecord().isFieldDirty("FULFILLMENT_STATUS")){
			Set<Long> facilityIds = new HashSet<>();
			for (OrderLine orderLine : order.getOrderLines()) {
				facilityIds.add(orderLine.getShipFromId());
			}
			List<Facility> facilities = new Select().from(Facility.class).where(new Expression(ModelReflector.instance(Facility.class).getPool(),"ID", Operator.IN,facilityIds.toArray())).execute();
			facilities.forEach(f->{
				f.getPreferredCarriers().forEach(preferredCarrier->{
					TaskManager.instance().executeAsync(new CreateManifestTask(preferredCarrier.getId()));
				});
			});
		}
    }



}
