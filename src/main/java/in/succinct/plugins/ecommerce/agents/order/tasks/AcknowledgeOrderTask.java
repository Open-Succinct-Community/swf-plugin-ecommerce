package in.succinct.plugins.ecommerce.agents.order.tasks;

import com.venky.core.date.DateUtils;
import com.venky.extension.Registry;
import com.venky.swf.plugins.background.core.AsyncTaskManager;
import com.venky.swf.plugins.background.core.AsyncTaskManagerFactory;
import com.venky.swf.plugins.background.core.Task;
import com.venky.swf.plugins.background.core.agent.AgentSeederTask;
import com.venky.swf.plugins.background.core.agent.AgentSeederTaskBuilder;
import com.venky.swf.sql.Conjunction;
import com.venky.swf.sql.Expression;
import com.venky.swf.sql.Operator;
import com.venky.swf.sql.Select;
import in.succinct.plugins.ecommerce.db.model.order.Order;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class AcknowledgeOrderTask implements Task, AgentSeederTaskBuilder  {
    Order order= null;
    public AcknowledgeOrderTask(Order order){
        this.order = order;
    }
    public AcknowledgeOrderTask(){

    }
    @Override
    public void execute() {
        if (this.order != null){
            order.acknowledge();
        }
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getName());
        builder.append("|").append(order.getId());
        return builder.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null){
            return  false;
        }
        if (!(obj instanceof AcknowledgeOrderTask)){
            return false;
        }
        return toString().equals(obj.toString());
    }

    @Override
    public AgentSeederTask createSeederTask() {
        return new AgentSeederTask() {

            long lastId = -1L;
            int numTasksToBuffer = 2 * getAsyncTaskManager().getNumWorkers() + 10;
            @Override
            public List<Task> getTasks() {
                Select select = new Select().from(Order.class);

                Expression expression = new Expression(select.getPool(), Conjunction.AND);
                expression.add(new Expression(select.getPool(),"FULFILLMENT_STATUS", Operator.EQ , Order.FULFILLMENT_STATUS_DOWNLOADED));
                expression.add(new Expression(select.getPool(),"SHIP_AFTER_DATE", Operator.LE , new Date(DateUtils.getStartOfDay(System.currentTimeMillis()))));
                expression.add(new Expression(select.getPool(), "ID" , Operator.GT , lastId));
                List<Task> tasks = new ArrayList<>();
                for (Order order : select.where(expression).orderBy("ID").execute(Order.class,numTasksToBuffer)){
                    tasks.add(new AcknowledgeOrderTask(order));
                    lastId = order.getId();
                }
                if (!tasks.isEmpty()){
                    tasks.add(this);
                }

                return tasks;
            }

            @Override
            public String getAgentName() {
                return ACKNOWLEDGE_ORDER_AGENT;
            }
        };
    }

    public static final String ACKNOWLEDGE_ORDER_AGENT = "ACKNOWLEDGE_ORDER_AGENT";
}
