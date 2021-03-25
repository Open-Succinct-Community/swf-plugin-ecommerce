package in.succinct.plugins.ecommerce.extensions.catalog;

import com.venky.core.util.ObjectUtil;
import com.venky.swf.db.extensions.BeforeModelValidateExtension;
import com.venky.swf.plugins.background.core.TaskManager;
import in.succinct.plugins.ecommerce.agents.catalog.HashItemTask;
import in.succinct.plugins.ecommerce.db.model.attributes.AssetCodeAttribute;
import in.succinct.plugins.ecommerce.db.model.catalog.Item;
import in.succinct.plugins.ecommerce.db.model.catalog.ItemAttributeValue;

import java.util.HashSet;
import java.util.Set;

public class BeforeValidateItemAttributeValue extends BeforeModelValidateExtension<ItemAttributeValue> {
    static {
        registerExtension(new BeforeValidateItemAttributeValue());
    }
    @Override
    public void beforeValidate(ItemAttributeValue model) {
        if (model.getReflector().isVoid(model.getAttributeId())){
            if (!model.getReflector().isVoid(model.getAttributeValueId())){
                model.setAttributeId(model.getAttributeValue().getAttributeId());
            }
        }

        Item item = model.getItem();

        Set<Long> allowedAttributeIds = new HashSet<>();
        if (!item.getReflector().isVoid(item.getAssetCodeId())){
            item.getAssetCode().getAssetCodeAttributes().forEach(aca->{
                if (ObjectUtil.equals(aca.getAttributeType(), AssetCodeAttribute.ATTRIBUTE_TYPE_CATALOG)){
                    allowedAttributeIds.add(aca.getAttributeId());
                }
            });
        }
        if (allowedAttributeIds.isEmpty()){
            throw new RuntimeException("Don't what what asset code the item " + model.getItem().getName() + " belongs to.");
        }
        if (!allowedAttributeIds.contains(model.getAttributeValue().getAttributeId())){
            throw new RuntimeException("Not a valid attribute for an item with asset_code of " + model.getItem().getAssetCode().getLongDescription());
        }
        TaskManager.instance().executeAsync(new HashItemTask(model.getItemId()),false);

    }
}
