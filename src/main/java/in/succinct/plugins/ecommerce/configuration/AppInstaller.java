package in.succinct.plugins.ecommerce.configuration;

import com.venky.swf.configuration.Installer;
import com.venky.swf.db.Database;
import com.venky.swf.db.model.reflection.ModelReflector;
import com.venky.swf.plugins.background.core.Task;
import com.venky.swf.plugins.background.core.TaskManager;
import com.venky.swf.sql.Expression;
import com.venky.swf.sql.Operator;
import com.venky.swf.sql.Select;
import in.succinct.plugins.ecommerce.db.model.order.OrderAddress;

import java.sql.Timestamp;
import java.util.List;

public class AppInstaller implements Installer {

    public void install() {
        fixLongNames();
    }
    public void fixLongNames(){
        ModelReflector ref = ModelReflector.instance(OrderAddress.class);
        List<OrderAddress> addressList = new Select().from(OrderAddress.class).where(new Expression(ref.getPool(),"LONG_NAME", Operator.EQ)).execute();
        for (OrderAddress address : addressList){
            address.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            TaskManager.instance().executeAsync((Task) address::save,false);
        }
    }

}

