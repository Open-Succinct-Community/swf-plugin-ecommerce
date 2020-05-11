package in.succinct.plugins.ecommerce.extensions.inventory;

import com.venky.swf.db.extensions.BeforeModelValidateExtension;
import in.succinct.plugins.ecommerce.db.model.catalog.ItemAttributeValue;
import in.succinct.plugins.ecommerce.db.model.inventory.InventoryAttributeValue;

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
    }
}
