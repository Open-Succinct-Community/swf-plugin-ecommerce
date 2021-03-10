package in.succinct.plugins.ecommerce.agents.inventory;

import com.venky.core.collections.SequenceSet;
import com.venky.swf.db.Database;
import com.venky.swf.plugins.background.core.Task;
import com.venky.swf.plugins.background.core.agent.AgentSeederTask;
import com.venky.swf.plugins.background.core.agent.AgentSeederTaskBuilder;
import com.venky.swf.sql.Select;
import in.succinct.plugins.ecommerce.db.model.inventory.Inventory;
import in.succinct.plugins.ecommerce.db.model.inventory.Sku;
import in.succinct.plugins.ecommerce.db.model.participation.Facility;
import in.succinct.plugins.ecommerce.db.model.participation.MarketPlaceInventoryUpdateQueue;
import in.succinct.plugins.ecommerce.integration.MarketPlace;
import in.succinct.plugins.ecommerce.integration.unicommerce.UniCommerce;

import java.util.List;
import java.util.Objects;

public class MarketPlaceInventorySyncAgent implements Task, AgentSeederTaskBuilder {
    public MarketPlaceInventorySyncAgent(){

    }
    private long skuId = -1;
    private long facilityId = -1;
    public MarketPlaceInventorySyncAgent(long skuId, long facilityId){
        this.skuId = skuId;
        this.facilityId = facilityId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarketPlaceInventorySyncAgent that = (MarketPlaceInventorySyncAgent) o;
        return skuId == that.skuId &&
                facilityId == that.facilityId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(skuId, facilityId);
    }

    @Override
    public void execute() {
        MarketPlaceInventoryUpdateQueue.entries(skuId,facilityId).forEach(e-> e.destroy());
        Sku sku = Database.getTable(Sku.class).get(skuId);
        Facility facility = Database.getTable(Facility.class).get(facilityId);
        for (MarketPlace mp : MarketPlace.get(facilityId)){
            mp.getWarehouseActionHandler().sync(Inventory.find(facility,sku));
        }
    }

    @Override
    public AgentSeederTask createSeederTask() {
        return new AgentSeederTask() {
            @Override
            public List<Task> getTasks() {
                List<MarketPlaceInventoryUpdateQueue> list = new Select().from(MarketPlaceInventoryUpdateQueue.class).execute();
                SequenceSet<Task> tasks = new SequenceSet<>();
                for (MarketPlaceInventoryUpdateQueue entry : list){
                    tasks.add(new MarketPlaceInventorySyncAgent(entry.getSkuId(),entry.getFacilityId()));
                }
                tasks.add(getFinishUpTask());
                return tasks;
            }

            @Override
            public String getAgentName() {
                return MARKET_PLACE_INVENTORY_SYNC_AGENT;
            }
        };
    }

    public static final String MARKET_PLACE_INVENTORY_SYNC_AGENT = "MARKET_PLACE_INVENTORY_SYNC_AGENT";
}
