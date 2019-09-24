package in.succinct.plugins.ecommerce.extensions;

import com.venky.extension.Extension;
import com.venky.extension.Registry;
import com.venky.swf.plugins.background.core.agent.Agent;
import in.succinct.plugins.ecommerce.agents.catalog.MarketPlaceCatalogSyncAgent;
import in.succinct.plugins.ecommerce.agents.demand.OpendDemandIncrementor;
import in.succinct.plugins.ecommerce.agents.inventory.MarketPlaceInventorySyncAgent;
import in.succinct.plugins.ecommerce.agents.order.tasks.AcknowledgeOrderTask;
import in.succinct.plugins.ecommerce.agents.order.tasks.MarketPlaceOrderDownloadAgent;
import in.succinct.plugins.ecommerce.agents.order.tasks.manifest.ManifestOrderAgent;

public class AgentRegistry {
    static {
        Agent.instance().registerAgentSeederTaskBuilder(ManifestOrderAgent.MANIFEST_ORDER,new ManifestOrderAgent());
        Agent.instance().registerAgentSeederTaskBuilder(AcknowledgeOrderTask.ACKNOWLEDGE_ORDER_AGENT,new AcknowledgeOrderTask());
        Agent.instance().registerAgentSeederTaskBuilder(MarketPlaceInventorySyncAgent.MARKET_PLACE_INVENTORY_SYNC_AGENT,new MarketPlaceInventorySyncAgent());
        Agent.instance().registerAgentSeederTaskBuilder(MarketPlaceCatalogSyncAgent.MARKET_PLACE_CATALOG_SYNC_AGENT,new MarketPlaceCatalogSyncAgent());
        Agent.instance().registerAgentSeederTaskBuilder(MarketPlaceOrderDownloadAgent.MARKET_PLACE_ORDER_DOWNLOAD,new MarketPlaceOrderDownloadAgent());
    }
}
