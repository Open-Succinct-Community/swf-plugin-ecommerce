package in.succinct.plugins.ecommerce.extensions.collab;

import com.venky.swf.db.extensions.BeforeModelDestroyExtension;
import com.venky.swf.db.model.reflection.ModelReflector;
import com.venky.swf.sql.Conjunction;
import com.venky.swf.sql.Expression;
import com.venky.swf.sql.Operator;
import com.venky.swf.sql.Select;
import in.succinct.plugins.ecommerce.db.model.inventory.Inventory;
import in.succinct.plugins.ecommerce.db.model.order.OrderLine;
import in.succinct.plugins.ecommerce.db.model.participation.Facility;

public class BeforeDestroyFacility extends BeforeModelDestroyExtension<Facility> {
    static {
        registerExtension(new BeforeDestroyFacility());
    }
    @Override
    public void beforeDestroy(Facility model) {
        boolean canDestroy = true;
        for (Inventory inventory : model.getInventoryList()){
            if (!inventory.isInfinite() && inventory.getQuantity() > 0 ){
                canDestroy = false;
            }
        }
        if (canDestroy){
            ModelReflector<OrderLine> ref = ModelReflector.instance(OrderLine.class);
            Expression where = new Expression(ref.getPool(), Conjunction.AND);
            where.add(new Expression(ref.getPool(), "SHIP_FROM_ID", Operator.EQ, model.getId()));
            where.add(new Expression(ref.getPool(), "ORDERED_QUANTITY", Operator.GT, 0));
            canDestroy = new Select().from(OrderLine.class).where(where).execute(1).isEmpty();
        }
        if (!canDestroy){
            throw new RuntimeException("Cannot delete facility with inventory or orders placed." );
        }
    }
}
