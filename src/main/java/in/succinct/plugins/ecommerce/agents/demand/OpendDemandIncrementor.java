package in.succinct.plugins.ecommerce.agents.demand;

import in.succinct.plugins.ecommerce.db.model.demand.Demand;
import in.succinct.plugins.ecommerce.db.model.inventory.Inventory;
import com.venky.core.util.Bucket;
import com.venky.swf.db.Database;
import com.venky.swf.db.model.reflection.ModelReflector;
import com.venky.swf.plugins.background.core.Task;
import com.venky.swf.sql.Conjunction;
import com.venky.swf.sql.Expression;
import com.venky.swf.sql.Operator;
import com.venky.swf.sql.Select;

import java.util.List;

public class OpendDemandIncrementor implements Task{

    private static final long serialVersionUID = -71053624205635099L;
	private long inventoryId ;
    private double demandQuantity;
    public OpendDemandIncrementor(long inventoryId, double demandQuantity){
        this.inventoryId= inventoryId;
        this.demandQuantity = demandQuantity;
    }
    @Deprecated
    public OpendDemandIncrementor(){

    }
    @Override
    public void execute() {
        Inventory inventory = Database.getTable(Inventory.class).get(inventoryId);
        if (inventory != null){
            ModelReflector<Demand> reflector = ModelReflector.instance(Demand.class);
            Expression where = new Expression(reflector.getPool(), Conjunction.AND);
            where.add(new Expression(reflector.getPool(), "INVENTORY_ID", Operator.EQ , inventoryId));

            List<Demand> demands = new Select().from(Demand.class).where(where).orderBy("ID").execute();
            double incrementBy = demandQuantity;
            Demand demand = null;
            if (!demands.isEmpty()){
                demand = demands.get(0);
                //Consolidate if multiple demands happened to be created.
                for (int i = 1; i < demands.size() ; i ++ ){
                    Demand d = demands.get(i);
                    incrementBy += d.getQuantity().doubleValue();
                    d.destroy();
                }
            }else {
                demand = Database.getTable(Demand.class).newRecord();
                demand.setInventoryId(inventoryId);
                demand.setQuantity(new Bucket(0.0));
            }
            demand.getQuantity().increment(incrementBy);
            demand.save();


        }
    }

}
