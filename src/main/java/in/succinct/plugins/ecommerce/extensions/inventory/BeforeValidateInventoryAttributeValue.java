package in.succinct.plugins.ecommerce.extensions.inventory;

import com.venky.core.util.ObjectUtil;
import com.venky.swf.db.extensions.BeforeModelValidateExtension;
import com.venky.swf.plugins.background.core.TaskManager;
import in.succinct.plugins.ecommerce.agents.inventory.HashInventoryTask;
import in.succinct.plugins.ecommerce.db.model.attributes.AssetCodeAttribute;
import in.succinct.plugins.ecommerce.db.model.catalog.Item;
import in.succinct.plugins.ecommerce.db.model.catalog.ItemAttributeValue;
import in.succinct.plugins.ecommerce.db.model.inventory.InventoryAttributeValue;

import java.util.HashSet;
import java.util.Set;

public class BeforeValidateInventoryAttributeValue extends BeforeModelValidateExtension<InventoryAttributeValue> {
    static {
        registerExtension(new BeforeValidateInventoryAttributeValue());
    }
    @Override
    public void beforeValidate(InventoryAttributeValue model) {
        if (model.getReflector().isVoid(model.getAttributeId())){
            if (!model.getReflector().isVoid(model.getAttributeValueId())){
                model.setAttributeId(model.getAttributeValue().getAttributeId());
            }
        }

        Item item = model.getInventory().getSku().getItem().getRawRecord().getAsProxy(Item.class);
        Set<Long> allowedAttributeIds = new HashSet<>();
        if (!item.getReflector().isVoid(item.getAssetCodeId())){
            item.getAssetCode().getAssetCodeAttributes().forEach(aca->{
                if (ObjectUtil.equals(aca.getAttributeType(), AssetCodeAttribute.ATTRIBUTE_TYPE_INVENTORY)){
                    allowedAttributeIds.add(aca.getAttributeId());
                }
            });
        }
        if (allowedAttributeIds.isEmpty()){
            throw new RuntimeException("Don't what what asset code the item " + item.getName() + " belongs to.");
        }

        if (!allowedAttributeIds.contains(model.getAttributeId())){
            throw new RuntimeException("Not a valid attribute to track in inventory for item with asset_code of " + item.getAssetCode().getLongDescription());
        }
        TaskManager.instance().executeAsync(new HashInventoryTask(model.getInventoryId()),false);

    }
}
