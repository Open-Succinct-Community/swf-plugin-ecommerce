package in.succinct.plugins.ecommerce.controller;

import com.venky.swf.controller.ModelController;
import com.venky.swf.controller.annotations.RequireLogin;
import com.venky.swf.controller.annotations.SingleRecordAction;
import com.venky.swf.db.Database;
import com.venky.swf.path.Path;
import com.venky.swf.plugins.background.core.TaskManager;
import com.venky.swf.views.View;
import in.succinct.plugins.ecommerce.agents.order.tasks.ship.CreateManifestTask;
import in.succinct.plugins.ecommerce.db.model.participation.PreferredCarrier;

public class PreferredCarriersController extends ModelController<PreferredCarrier> {
    public PreferredCarriersController(Path path) {
        super(path);
    }

    @RequireLogin
    @SingleRecordAction(icon = "glyphicon-th-list" , tooltip = "Open Manifest")
    public View createManifest(long id){
        TaskManager.instance().executeAsync(new CreateManifestTask(id),false);
        getPath().addInfoMessage("Manifest Job Submitted");
        return back();
    }
}
