package in.succinct.plugins.ecommerce.extensions.assets;

import com.venky.swf.db.extensions.AfterModelCreateExtension;
import com.venky.swf.plugins.background.core.TaskManager;
import in.succinct.plugins.ecommerce.agents.asset.HashAssetTask;
import in.succinct.plugins.ecommerce.db.model.assets.Capability;

public class AfterCreateCapability extends AfterModelCreateExtension<Capability> {
    static {
        registerExtension(new AfterCreateCapability());
    }
    @Override
    public void afterCreate(Capability model) {
        TaskManager.instance().executeAsync(new HashAssetTask(model.getAssetId()));
    }
}
