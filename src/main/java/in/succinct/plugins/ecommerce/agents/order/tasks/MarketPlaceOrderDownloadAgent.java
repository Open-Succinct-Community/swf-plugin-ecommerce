package in.succinct.plugins.ecommerce.agents.order.tasks;

import com.venky.swf.db.Database;
import com.venky.swf.plugins.background.core.Task;
import com.venky.swf.plugins.background.core.agent.AgentSeederTask;
import com.venky.swf.plugins.background.core.agent.AgentSeederTaskBuilder;
import com.venky.swf.sql.Select;
import in.succinct.plugins.ecommerce.db.model.participation.Facility;
import in.succinct.plugins.ecommerce.db.model.participation.MarketPlaceIntegration;
import in.succinct.plugins.ecommerce.integration.unicommerce.UniCommerce;

import java.util.ArrayList;
import java.util.List;

public class MarketPlaceOrderDownloadAgent implements Task, AgentSeederTaskBuilder {
    long facilityId = -1;
    public MarketPlaceOrderDownloadAgent(){

    }
    public MarketPlaceOrderDownloadAgent(MarketPlaceIntegration marketPlaceIntegration){
        facilityId = marketPlaceIntegration.getFacilityId();
    }
    @Override
    public void execute() {
        UniCommerce.getInstance(Database.getTable(Facility.class).get(facilityId)).pullOrders();
    }

    @Override
    public AgentSeederTask createSeederTask() {
        return new AgentSeederTask() {
            @Override
            public List<Task> getTasks() {
                List<MarketPlaceIntegration> list = new Select().from(MarketPlaceIntegration.class).execute();
                List<Task> tasks = new ArrayList<>();
                for (MarketPlaceIntegration marketPlaceIntegration : list){
                    tasks.add(new MarketPlaceOrderDownloadAgent(marketPlaceIntegration));
                }
                return tasks;
            }

            @Override
            public String getAgentName() {
                return MARKET_PLACE_ORDER_DOWNLOAD;
            }
        };
    }

    public static final String MARKET_PLACE_ORDER_DOWNLOAD = "MARKET_PLACE_ORDER_DOWNLOAD";
}
