package in.succinct.plugins.ecommerce.extensions;

import com.venky.extension.Extension;
import com.venky.extension.Registry;
import com.venky.swf.plugins.background.core.agent.Agent;
import in.succinct.plugins.ecommerce.agents.demand.OpendDemandIncrementor;
import in.succinct.plugins.ecommerce.agents.order.tasks.AcknowledgeOrderTask;
import in.succinct.plugins.ecommerce.agents.order.tasks.manifest.ManifestOrderAgent;

public class AgentRegistry {
    static {
        Agent.instance().registerAgentSeederTaskBuilder(ManifestOrderAgent.MANIFEST_ORDER,new ManifestOrderAgent());
        Agent.instance().registerAgentSeederTaskBuilder(AcknowledgeOrderTask.ACKNOWLEDGE_ORDER_AGENT,new AcknowledgeOrderTask());

    }
}
