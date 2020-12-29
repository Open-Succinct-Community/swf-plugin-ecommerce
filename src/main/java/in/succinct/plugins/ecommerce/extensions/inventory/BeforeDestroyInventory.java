package in.succinct.plugins.ecommerce.extensions.inventory;

import com.venky.swf.db.extensions.BeforeModelDestroyExtension;
import com.venky.swf.db.extensions.BeforeModelSaveExtension;
import com.venky.swf.db.model.reflection.ModelReflector;
import com.venky.swf.sql.Expression;
import com.venky.swf.sql.Operator;
import com.venky.swf.sql.Select;
import in.succinct.plugins.ecommerce.db.model.inventory.Inventory;
import in.succinct.plugins.ecommerce.db.model.order.OrderLine;
import in.succinct.plugins.ecommerce.db.model.participation.MarketPlaceInventoryUpdateQueue;

import java.util.List;

public class BeforeDestroyInventory extends BeforeModelDestroyExtension<Inventory> {
	static {
		registerExtension(new BeforeDestroyInventory());
	}
	@Override
	public void beforeDestroy(Inventory model) {
        List<OrderLine> orderLineList = new Select().from(OrderLine.class).where(new Expression(ModelReflector.instance(OrderLine.class).getPool(),"INVENTORY_ID", Operator.EQ,model.getId())).execute(1);
        if (!orderLineList.isEmpty()){
        	throw new RuntimeException("Inventory cannot be removed for which orders have already been placed");
		}
	}

}
