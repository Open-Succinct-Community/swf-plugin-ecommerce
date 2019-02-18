package in.succinct.plugins.ecommerce.extensions.manifest;

import com.venky.swf.db.extensions.BeforeModelSaveExtension;
import com.venky.swf.plugins.background.core.TaskManager;
import in.succinct.plugins.ecommerce.agents.order.tasks.ship.ManifestShippingUpdatesTask;
import in.succinct.plugins.ecommerce.db.model.order.Manifest;

public class BeforeSaveManifest extends BeforeModelSaveExtension<Manifest> {
    static {
        registerExtension(new BeforeSaveManifest());
    }
    @Override
    public void beforeSave(Manifest manifest) {
        if (manifest.isClosed()) {
            TaskManager.instance().executeAsync(new ManifestShippingUpdatesTask(manifest.getId()),false);
        }

    }
}
