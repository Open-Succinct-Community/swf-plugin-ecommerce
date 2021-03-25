package in.succinct.plugins.ecommerce.extensions.assets;

import com.venky.swf.db.extensions.BeforeModelSaveExtension;
import com.venky.swf.plugins.background.core.TaskManager;
import in.succinct.plugins.ecommerce.agents.asset.HashAssetTask;
import in.succinct.plugins.ecommerce.db.model.assets.Capability;

public class BeforeSaveCapability extends BeforeModelSaveExtension<Capability> {
    static {
        registerExtension(new BeforeSaveCapability());
    }
    @Override
    public void beforeSave(Capability model) {
        if (model.getRawRecord().isFieldDirty("ASSET_CODE_ID") &&
                !model.getRawRecord().isNewRecord()){
            TaskManager.instance().executeAsync(new HashAssetTask(model.getAssetId()));
        }
    }
}
