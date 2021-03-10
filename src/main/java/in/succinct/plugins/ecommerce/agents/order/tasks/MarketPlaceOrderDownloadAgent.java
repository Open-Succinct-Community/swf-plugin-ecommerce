package in.succinct.plugins.ecommerce.agents.order.tasks;

import com.venky.swf.db.Database;
import com.venky.swf.plugins.background.core.Task;
import com.venky.swf.plugins.background.core.agent.AgentSeederTask;
import com.venky.swf.plugins.background.core.agent.AgentSeederTaskBuilder;
import com.venky.swf.pm.DataSecurityFilter;
import com.venky.swf.sql.Select;
import in.succinct.plugins.ecommerce.db.model.participation.Facility;
import in.succinct.plugins.ecommerce.db.model.participation.MarketPlaceIntegration;
import in.succinct.plugins.ecommerce.integration.MarketPlace;
import in.succinct.plugins.ecommerce.integration.unicommerce.UniCommerce;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MarketPlaceOrderDownloadAgent implements Task, AgentSeederTaskBuilder {
    long facilityId = -1;
    public MarketPlaceOrderDownloadAgent(){

    }
    public MarketPlaceOrderDownloadAgent(long facilityId){
        this.facilityId = facilityId;
    }
    @Override
    public void execute() {
        MarketPlace.get(facilityId).forEach(mp->mp.pullOrders());
    }

    @Override
    public AgentSeederTask createSeederTask() {
        return new AgentSeederTask() {
            @Override
            public List<Task> getTasks() {
                Select select = new Select().from(Facility.class);
                select.add(" where exists ( select 1 from market_place_integrations where facility_id = facilities.id)");

                List<Facility> facilities = select.execute();
                Set<Long> facilityIds = DataSecurityFilter.getIds(facilities);
                List<Task> tasks = new ArrayList<>();
                for (long facilityId : facilityIds){
                    tasks.add(new MarketPlaceOrderDownloadAgent(facilityId));
                }
                tasks.add(getFinishUpTask());
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
