package in.succinct.plugins.ecommerce.extensions.catalog;

import com.venky.swf.db.extensions.BeforeModelSaveExtension;
import com.venky.swf.plugins.background.core.TaskManager;
import in.succinct.plugins.ecommerce.agents.catalog.HashItemTask;
import in.succinct.plugins.ecommerce.db.model.catalog.Item;

public class BeforeSaveItem extends BeforeModelSaveExtension<Item> {
    static {
        registerExtension(new BeforeSaveItem());
    }
    @Override
    public void beforeSave(Item model) {
        if (model.getRawRecord().isFieldDirty("ASSET_CODE_ID") && !model.getRawRecord().isNewRecord()){
            TaskManager.instance().executeAsync(new HashItemTask(model.getId()));
        }
    }

}
