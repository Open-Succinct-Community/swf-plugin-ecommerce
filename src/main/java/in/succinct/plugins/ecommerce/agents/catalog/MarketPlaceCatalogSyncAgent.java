package in.succinct.plugins.ecommerce.agents.catalog;

import com.venky.core.collections.SequenceSet;
import com.venky.swf.plugins.background.core.Task;
import com.venky.swf.plugins.background.core.agent.AgentSeederTask;
import com.venky.swf.plugins.background.core.agent.AgentSeederTaskBuilder;
import com.venky.swf.sql.Select;
import in.succinct.plugins.ecommerce.db.model.inventory.Inventory;
import in.succinct.plugins.ecommerce.db.model.participation.MarketPlaceSkuUpdateQueue;
import in.succinct.plugins.ecommerce.integration.MarketPlace;

import java.util.List;
import java.util.Objects;

public class MarketPlaceCatalogSyncAgent implements Task, AgentSeederTaskBuilder {
    public MarketPlaceCatalogSyncAgent(){

    }
    private long skuId = -1;
    private long facilityId = -1;
    public MarketPlaceCatalogSyncAgent(long skuId, long facilityId){
        this.skuId = skuId;
        this.facilityId = facilityId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarketPlaceCatalogSyncAgent that = (MarketPlaceCatalogSyncAgent) o;
        return skuId == that.skuId &&
                facilityId == that.facilityId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(skuId, facilityId);
    }

    @Override
    public void execute() {
        MarketPlaceSkuUpdateQueue.entries(skuId,facilityId).forEach(e-> e.destroy());
        MarketPlace.get(facilityId).forEach(mp->mp.getWarehouseActionHandler().sync(Inventory.find(facilityId,skuId)));
    }

    @Override
    public AgentSeederTask createSeederTask() {
        return new AgentSeederTask() {
            @Override
            public List<Task> getTasks() {
                List<MarketPlaceSkuUpdateQueue> list = new Select().from(MarketPlaceSkuUpdateQueue.class).execute();
                SequenceSet<Task> tasks = new SequenceSet<>();
                for (MarketPlaceSkuUpdateQueue entry : list){
                    tasks.add(new MarketPlaceCatalogSyncAgent(entry.getSkuId(),entry.getFacilityId()));
                }
                tasks.add(getFinishUpTask());
                return tasks;
            }

            @Override
            public String getAgentName() {
                return MARKET_PLACE_CATALOG_SYNC_AGENT;
            }
        };
    }

    public static final String MARKET_PLACE_CATALOG_SYNC_AGENT = "MARKET_PLACE_CATALOG_SYNC_AGENT";
}
