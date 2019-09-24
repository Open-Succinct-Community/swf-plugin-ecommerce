package in.succinct.plugins.ecommerce.extensions.catalog;

import com.venky.swf.db.extensions.AfterModelSaveExtension;
import in.succinct.plugins.ecommerce.agents.inventory.MarketPlaceInventorySyncAgent;
import in.succinct.plugins.ecommerce.db.model.inventory.Inventory;
import in.succinct.plugins.ecommerce.db.model.inventory.Sku;
import in.succinct.plugins.ecommerce.db.model.participation.MarketPlaceInventoryUpdateQueue;
import in.succinct.plugins.ecommerce.db.model.participation.MarketPlaceSkuUpdateQueue;

import java.util.List;

public class AfterSaveSku extends AfterModelSaveExtension<Sku> {
    static {
        registerExtension(new AfterSaveSku());
    }
    @Override
    public void afterSave(Sku sku) {
        List<Inventory> inventoryList = sku.getInventory();
        for (Inventory inventory : inventoryList){
            MarketPlaceSkuUpdateQueue.push(inventory);
        }
    }
}
