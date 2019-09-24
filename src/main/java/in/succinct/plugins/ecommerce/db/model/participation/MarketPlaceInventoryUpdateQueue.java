package in.succinct.plugins.ecommerce.db.model.participation;

import com.venky.swf.db.Database;
import com.venky.swf.db.model.Model;
import com.venky.swf.sql.Conjunction;
import com.venky.swf.sql.Expression;
import com.venky.swf.sql.Operator;
import com.venky.swf.sql.Select;
import in.succinct.plugins.ecommerce.db.model.inventory.Inventory;
import in.succinct.plugins.ecommerce.db.model.inventory.Sku;

import java.util.List;

public interface MarketPlaceInventoryUpdateQueue extends Model {
    static void push(Inventory inventory) {
        if (inventory.getFacility().getPreferredMarketPlaceIntegrations().isEmpty()){
            return;
        }
        List<MarketPlaceInventoryUpdateQueue> queueRecords = entries(inventory.getSkuId(),inventory.getFacilityId());

        if (queueRecords.isEmpty()){
            MarketPlaceInventoryUpdateQueue entry = Database.getTable(MarketPlaceInventoryUpdateQueue.class).newRecord();
            entry.setSkuId(inventory.getSkuId());
            entry.setFacilityId(inventory.getFacilityId());
            entry.save();
        }
    }
    static List<MarketPlaceInventoryUpdateQueue> entries(long skuId, long  facilityId){
        Select select = new Select().from(MarketPlaceInventoryUpdateQueue.class);
        Expression where = new Expression(select.getPool(), Conjunction.AND);
        where.add(new Expression(select.getPool(),"SKU_ID", Operator.EQ,skuId));
        where.add(new Expression(select.getPool(),"FACILITY_ID",Operator.EQ,facilityId));
        return select.where(where).execute();

    }

    public long getSkuId();
    public void setSkuId(long id);
    public Sku getSku();

    public long getFacilityId();
    public void setFacilityId(long id);
    public Facility getFacility();
}
