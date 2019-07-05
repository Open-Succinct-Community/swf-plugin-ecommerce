package in.succinct.plugins.ecommerce.agents.order.tasks.ship;

import com.venky.core.util.ObjectUtil;
import com.venky.swf.db.model.reflection.ModelReflector;
import com.venky.swf.plugins.background.core.TaskManager;
import in.succinct.plugins.ecommerce.agents.order.tasks.manifest.ManifestOrderTask;
import in.succinct.plugins.ecommerce.db.model.order.Manifest;
import in.succinct.plugins.ecommerce.db.model.order.Order;
import in.succinct.plugins.ecommerce.db.model.order.OrderLine;
import in.succinct.plugins.ecommerce.db.model.participation.Facility;
import com.venky.core.string.StringUtil;
import com.venky.swf.db.Database;
import com.venky.swf.plugins.background.core.Task;
import com.venky.swf.sql.Conjunction;
import com.venky.swf.sql.Expression;
import com.venky.swf.sql.Operator;
import com.venky.swf.sql.Select;
import in.succinct.plugins.ecommerce.db.model.participation.PreferredCarrier;
import in.succinct.plugins.ecommerce.db.model.sequence.SequentialNumber;

import java.security.cert.Extension;
import java.util.List;


public class CreateManifestTask implements Task{

	private static final long serialVersionUID = -2079771132674955414L;
	@Deprecated
	public CreateManifestTask() {
	}
	
	private Priority priority = Priority.DEFAULT;

	long preferredCarrierId = -1;
	public CreateManifestTask(long preferredCarrierId) {
		this.preferredCarrierId = preferredCarrierId;
	}

	public void setTaskPriority(Priority priority){
		this.priority = priority;
	}
	@Override
	public Priority getTaskPriority(){
		return priority;
	}

	@Override
	public void execute() {
	    PreferredCarrier preferredCarrier = Database.getTable(PreferredCarrier.class).lock(preferredCarrierId);

		Select manifestSelect = new Select().from(Manifest.class);
		Expression where = new Expression(manifestSelect.getPool(),Conjunction.AND);
        where.add(new Expression(manifestSelect.getPool(),"PREFERRED_CARRIER_ID",Operator.EQ, preferredCarrier.getId()));
        where.add(new Expression(manifestSelect.getPool(),"CLOSED",Operator.EQ, false));

        List<Manifest> manifests = manifestSelect.where(where).orderBy("ID").execute();
		Manifest manifest = null;
		if (manifests.isEmpty()) {
			manifest = Database.getTable(Manifest.class).newRecord();
			manifest.setManifestNumber(SequentialNumber.get(preferredCarrier.getName()+".Manifest").next());
			manifest.setPreferredCarrierId(preferredCarrier.getId());
			manifest.save();
		}else {
			manifest = manifests.get(0);
		}
		addOrdersToManifest(manifest,preferredCarrier);
	}

	private void addOrdersToManifest(Manifest manifest,PreferredCarrier preferredCarrier) {
		Select orderSelect = new Select().from(Order.class);
		Expression where = new Expression(orderSelect.getPool(), Conjunction.AND);
		where.add(new Expression(orderSelect.getPool(),"FULFILLMENT_STATUS" , Operator.EQ, Order.FULFILLMENT_STATUS_PACKED));
		orderSelect.where(where).add(" and exists ( select 1 from " + ModelReflector.instance(OrderLine.class).getTableName() + " ol  where ol.order_id = " +
				ModelReflector.instance(Order.class).getTableName() +".id and ol.ship_from_id = " + manifest.getPreferredCarrier().getFacilityId() + ")");

		List<Order> packedOrders = orderSelect.orderBy("ID").execute();
		packedOrders.forEach(o-> {
			Double estimatedCharges = preferredCarrier.getEstimatedCharges(o);
			Double maxShippingCharges = preferredCarrier.getMaxShippingCharges();
			if (estimatedCharges == null || maxShippingCharges == null || estimatedCharges < maxShippingCharges ) {
				TaskManager.instance().executeAsync(new ManifestOrderTask(o.getId(),manifest.getId()),false);
			}else {
				StringBuilder holdReason = new StringBuilder();
				holdReason.append(StringUtil.valueOf(o.getHoldReason()));
				holdReason.append("Shipping Charges > " + maxShippingCharges + " for " + preferredCarrier.getName()).append("<br/>") ;
				o.setHoldReason(holdReason.toString());
				o.save();
			}
		});
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [preferredCarrierId=" + preferredCarrierId + "]";
	}
	@Override
	public int hashCode() {
		return toString().hashCode();
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CreateManifestTask that = (CreateManifestTask) o;

        return (preferredCarrierId == that.preferredCarrierId);
    }
}
