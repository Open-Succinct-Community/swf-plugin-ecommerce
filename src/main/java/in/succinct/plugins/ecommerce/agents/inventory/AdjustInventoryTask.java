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
    private String companyName;
    private String skuName;
    private String facilityName;
    private double quantity;
    private String comment;

    public AdjustInventoryTask(String companyName, String skuName, String facilityName, double quantity, String comment){
        this.companyName = companyName;
        this.skuName = skuName;
        this.facilityName = facilityName;
        this.quantity = quantity;
        this.comment = comment;
    }
    @Override
    public void execute() {
        Select cSelect = new Select().from(Company.class);
        List<Company> companyList = cSelect.where(new Expression(cSelect.getPool(),"NAME", Operator.EQ,companyName)).execute();
        if (companyList.size() == 1){
            Company company = companyList.get(0);
            Select fSelect = new Select().from(Facility.class);
            Expression where = new Expression(fSelect.getPool(), Conjunction.AND);
            where.add(new Expression(fSelect.getPool(),"COMPANY_ID",Operator.EQ,company.getId()));
            where.add(new Expression(fSelect.getPool(),"NAME",Operator.EQ,facilityName));
            List<Facility> facilities = fSelect.where(where).execute();
            if (facilities.size() == 1){
                Facility facility = facilities.get(0);
                Select skuSelect = new Select().from(Sku.class);
                Expression skuWhere = new Expression(skuSelect.getPool(),Conjunction.AND);
                skuWhere.add(new Expression(skuSelect.getPool(),"COMPANY_ID",Operator.EQ,company.getId()));
                skuWhere.add(new Expression(skuSelect.getPool(),"NAME",Operator.EQ,skuName));
                List<Sku> skus = skuSelect.where(skuWhere).execute();
                if (skus.size() == 1){
                    Sku sku = skus.get(0);
                    adjustInventory(facility,sku,quantity);
                    return;
                }else {
                    throw new RuntimeException("Invalid SKU Name :" + skuName);
                }
            }else {
                throw new RuntimeException("Invalid Facility Name :" + facilityName);
            }
        }else {
            throw new RuntimeException("Invalid Company Name :" + companyName);
        }



    }

    private void adjustInventory(Facility facility, Sku sku, double quantity) {
        Select inventorySelect = new Select().from(Inventory.class);
        Expression where = new Expression(inventorySelect.getPool(),Conjunction.AND);
        where.add(new Expression(inventorySelect.getPool(),"FACILITY_ID",Operator.EQ,facility.getId()));
        where.add(new Expression(inventorySelect.getPool(),"SKU_ID",Operator.EQ,sku.getId()));

        List<Inventory> inventories = inventorySelect.where(where).execute();
        Inventory inventory = null;
        if (inventories.isEmpty()) {
            inventory = Database.getTable(Inventory.class).newRecord();
            inventory.setFacilityId(facility.getId());
            inventory.setSkuId(sku.getId());
        }else if (inventories.size() == 1){
            inventory = inventories.get(0);
        }else {
            throw new RuntimeException("Cannot find inventory record uniquely:" + toString());
        }
        inventory.adjust(quantity,comment);
    }
}
