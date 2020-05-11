package in.succinct.mandi.extensions;

import com.venky.swf.db.extensions.AfterModelCreateExtension;
import com.venky.swf.plugins.background.core.TaskManager;
import in.succinct.plugins.ecommerce.agents.catalog.HashItemTask;
import in.succinct.plugins.ecommerce.db.model.catalog.Item;

public class AfterCreateItem extends AfterModelCreateExtension<Item> {
    static {
        registerExtension(new AfterCreateItem());
    }
    @Override
    public void afterCreate(Item model) {
        if (!model.getReflector().isVoid(model.getAssetCodeId())){
            TaskManager.instance().executeAsync(new HashItemTask(model.getId()));
        }
    }
}
