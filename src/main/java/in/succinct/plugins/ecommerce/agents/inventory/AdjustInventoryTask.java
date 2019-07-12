package in.succinct.plugins.ecommerce.agents.inventory;

import in.succinct.plugins.ecommerce.db.model.inventory.Inventory;
import in.succinct.plugins.ecommerce.db.model.inventory.Sku;
import in.succinct.plugins.ecommerce.db.model.participation.Company;
import in.succinct.plugins.ecommerce.db.model.participation.Facility;
import com.venky.swf.db.Database;
import com.venky.swf.plugins.background.core.Task;
import com.venky.swf.sql.Conjunction;
import com.venky.swf.sql.Expression;
import com.venky.swf.sql.Operator;
import com.venky.swf.sql.Select;

import java.util.List;

public class AdjustInventoryTask implements Task{
    public AdjustInventoryTask(){

    }
    long skuId;
    long facilityId;
    private double quantity;
    private String comment;

    public AdjustInventoryTask(Sku  sku , Facility facility, double quantity, String comment){
        this(sku.getId(),facility.getId(),quantity,comment);
    }
    public AdjustInventoryTask(long skuId, long facilityId , double quantity, String comment){
        this.skuId = skuId;
        this.facilityId = facilityId;
        this.quantity = quantity;
        this.comment = comment;
    }
    @Override
    public void execute() {
        Facility facility = Database.getTable(Facility.class).get(facilityId);
        Sku sku = Database.getTable(Sku.class).get(skuId);

        Inventory.adjust(facility,sku,quantity,comment);
    }


}
