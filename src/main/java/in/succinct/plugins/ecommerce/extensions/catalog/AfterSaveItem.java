package in.succinct.plugins.ecommerce.extensions.catalog;

import com.venky.swf.db.extensions.AfterModelSaveExtension;
import com.venky.swf.plugins.background.core.TaskManager;
import in.succinct.plugins.ecommerce.agents.catalog.HashItemTask;
import in.succinct.plugins.ecommerce.db.model.catalog.Item;
import in.succinct.plugins.ecommerce.db.model.inventory.Sku;

public class AfterSaveItem extends AfterModelSaveExtension<Item> {
    static {
        registerExtension(new AfterSaveItem());
    }
    @Override
    public void afterSave(Item model) {
        if (!model.getReflector().isVoid(model.getAssetCodeId())){
            for (Sku sku : model.getSkus()) {
                sku.setTaxRate(model.getAssetCode().getGstPct());
                sku.save();
            }
            TaskManager.instance().executeAsync(new HashItemTask(model.getId()));
        }
    }
}
