package in.succinct.plugins.ecommerce.extensions.catalog;

import com.venky.swf.db.extensions.BeforeModelValidateExtension;
import in.succinct.plugins.ecommerce.db.model.catalog.ItemAttributeValue;

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
    }
}
