package in.succinct.plugins.ecommerce.agents.order.tasks.manifest;

import com.venky.swf.plugins.background.core.CompositeTask;
import com.venky.swf.plugins.background.core.Task;
import com.venky.swf.plugins.background.core.agent.AgentFinishUpTask;
import com.venky.swf.plugins.background.core.agent.AgentSeederTask;
import com.venky.swf.plugins.background.core.agent.AgentSeederTaskBuilder;
import com.venky.swf.sql.Expression;
import com.venky.swf.sql.Operator;
import com.venky.swf.sql.Select;
import in.succinct.plugins.ecommerce.agents.order.tasks.ship.CreateManifestTask;
import in.succinct.plugins.ecommerce.db.model.order.Manifest;

import java.util.ArrayList;
import java.util.List;

public class ManifestOrderAgent extends  AgentSeederTask implements AgentSeederTaskBuilder {
    @Override
    public AgentSeederTask createSeederTask() {
        return this;
    }

    @Override
    public List<Task> getTasks() {
        Select select = new Select().from(Manifest.class);
        select.where(new Expression(select.getPool(), "CLOSED", Operator.EQ, false));
        List<Manifest> manifests = select.execute();
        List<Task> tasks = new ArrayList<>();

        for (Manifest m : manifests){
            CreateManifestTask task = new CreateManifestTask(m.getManifestNumber(), m.getFacilityId(), m.getCourier());
            task.setTaskPriority(getTaskPriority());
            tasks.add(task);
        }
        if (!tasks.isEmpty()){
            Task last = tasks.remove(tasks.size()-1);
            AgentFinishUpTask finishUpTask = new AgentFinishUpTask(getAgentName());
            finishUpTask.setPriority(getTaskPriority());
            tasks.add(new CompositeTask(true,last,finishUpTask));
        }
        return tasks;
    }


    @Override
    public String getAgentName() {
        return MANIFEST_ORDER;
    }

    @Override
    public Priority getTaskPriority() {
        return Priority.LOW;
    }

    public static final String MANIFEST_ORDER = "MANIFEST_ORDER";
}
