package in.succinct.plugins.ecommerce.db.model.order;

import com.venky.swf.db.table.ModelImpl;
import com.venky.swf.plugins.background.core.TaskManager;
import in.succinct.plugins.ecommerce.agents.order.tasks.ship.ManifestShippingUpdatesTask;

public class ManifestImpl extends ModelImpl<Manifest>{
    public ManifestImpl(Manifest proxy){
        super(proxy);
    }

    public void close(){
        Manifest manifest = getProxy();
        if (!manifest.isClosed()){
            manifest.setClosed(true);
            manifest.save();
        }else {
            TaskManager.instance().executeAsync(new ManifestShippingUpdatesTask(manifest.getId()), false);
        }

    }
}
