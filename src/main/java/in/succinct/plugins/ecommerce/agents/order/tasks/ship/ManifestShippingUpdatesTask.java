package in.succinct.plugins.ecommerce.agents.order.tasks.ship;

import in.succinct.plugins.ecommerce.agents.order.tasks.EntityTask;
import in.succinct.plugins.ecommerce.db.model.order.Manifest;
import in.succinct.plugins.ecommerce.db.model.order.OrderLineAttribute;
import com.venky.swf.plugins.background.core.Task;
import com.venky.swf.plugins.background.core.TaskManager;
import com.venky.swf.sql.Conjunction;
import com.venky.swf.sql.Expression;
import com.venky.swf.sql.Operator;
import com.venky.swf.sql.Select;

import java.util.LinkedList;
import java.util.List;

public class ManifestShippingUpdatesTask extends EntityTask<Manifest> {
	private static final long serialVersionUID = -6047059931220609017L;

	@Deprecated
	public ManifestShippingUpdatesTask() {
	    this(-1);
	}

	public ManifestShippingUpdatesTask(long manifestId) {
		super(manifestId);
	}
	@Override
	public void execute(Manifest m) {

		Select s = new Select().from(OrderLineAttribute.class);
		
		Expression where = new Expression(s.getPool(),  Conjunction.AND); 
		where.add(new Expression(s.getPool(), "NAME" , Operator.EQ, "manifest_id"));
		where.add(new Expression(s.getPool(), "VALUE" , Operator.EQ, m.getManifestId()));
		List<OrderLineAttribute> olas = s.where(where).execute();
		List<Task> tasks = new LinkedList<>();
		olas.forEach(a->{
			tasks.add(new ShipOrderLineTask(a.getOrderLineId()));
		});
		TaskManager.instance().executeAsync(tasks,true);
	}

}
