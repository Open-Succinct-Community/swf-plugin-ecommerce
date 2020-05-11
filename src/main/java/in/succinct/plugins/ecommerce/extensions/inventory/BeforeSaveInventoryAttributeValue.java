package in.succinct.plugins.ecommerce.extensions.inventory;

import com.venky.core.util.ObjectUtil;
import com.venky.swf.db.extensions.BeforeModelSaveExtension;
import com.venky.swf.plugins.background.core.TaskManager;
import in.succinct.plugins.ecommerce.agents.inventory.HashInventoryTask;
import in.succinct.plugins.ecommerce.db.model.attributes.AssetCodeAttribute;
import in.succinct.plugins.ecommerce.db.model.catalog.Item;
import in.succinct.plugins.ecommerce.db.model.inventory.InventoryAttributeValue;

import java.util.HashSet;
import java.util.Set;

public class BeforeSaveInventoryAttributeValue extends BeforeModelSaveExtension<InventoryAttributeValue> {
    static{
        registerExtension(new BeforeSaveInventoryAttributeValue());
    }
    @Override
    public void beforeSave(InventoryAttributeValue model) {
        Item item = model.getInventory().getSku().getItem().getRawRecord().getAsProxy(Item.class);
        Set<Long> allowedAttributeIds = new HashSet<>();

        if (!item.getReflector().isVoid(item.getAssetCodeId())){
            item.getAssetCode().getAssetCodeAttributes().forEach(aca->{
                if (ObjectUtil.equals(aca.getAttributeType(), AssetCodeAttribute.ATTRIBUTE_TYPE_INVENTORY)){
                    allowedAttributeIds.add(aca.getAttributeId());
                }
            });
        }

        if (!allowedAttributeIds.contains(model.getAttributeValue().getAttributeId())){
            throw new RuntimeException("Not a valid attribute to track in inventory for item with asset_code of " + item.getAssetCode().getLongDescription());
        }

        TaskManager.instance().executeAsync(new HashInventoryTask(model.getInventoryId()),false);
    }
}
