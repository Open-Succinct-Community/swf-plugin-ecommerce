package in.succinct.plugins.ecommerce.extensions.order.line;

import com.venky.core.date.DateUtils;
import com.venky.swf.db.extensions.BeforeModelDestroyExtension;
import com.venky.swf.plugins.background.core.Task;
import com.venky.swf.plugins.background.core.TaskManager;
import in.succinct.plugins.ecommerce.agents.demand.OpendDemandIncrementor;
import in.succinct.plugins.ecommerce.agents.order.tasks.OrderStatusMonitor;
import in.succinct.plugins.ecommerce.db.model.order.OrderLine;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class BeforeDestroyOrderLine extends BeforeModelDestroyExtension<OrderLine> {
    static {
        registerExtension(new BeforeDestroyOrderLine());
    }
    @Override
    public void beforeDestroy(OrderLine orderLine) {
        List<Task> tasks = new ArrayList<>();
        if (orderLine.getAcknowledgedQuantity() > 0 ){

            Timestamp demandDate = orderLine.getDeliveryExpectedNoEarlierThan() == null ? null : new Timestamp(DateUtils.getStartOfDay(orderLine.getDeliveryExpectedNoEarlierThan().getTime()));

            tasks.add(new OpendDemandIncrementor(orderLine.getInventory(false).getId(),-orderLine.getAcknowledgedQuantity(),demandDate,orderLine.getWorkSlot()));
            tasks.add(new OrderStatusMonitor(orderLine.getOrderId()));
        }
        TaskManager.instance().executeAsync(tasks,false);
    }
}
