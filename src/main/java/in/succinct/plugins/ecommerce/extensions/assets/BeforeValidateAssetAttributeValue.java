package in.succinct.plugins.ecommerce.extensions.assets;

import com.venky.swf.db.extensions.BeforeModelValidateExtension;
import in.succinct.plugins.ecommerce.db.model.assets.AssetAttributeValue;
import in.succinct.plugins.ecommerce.db.model.catalog.ItemAttributeValue;

public class BeforeValidateAssetAttributeValue extends BeforeModelValidateExtension<AssetAttributeValue> {
    static {
        registerExtension(new BeforeValidateAssetAttributeValue());
    }
    @Override
    public void beforeValidate(AssetAttributeValue model) {
        if (model.getReflector().isVoid(model.getAttributeId())){
            if (!model.getReflector().isVoid(model.getAttributeValueId())){
                model.setAttributeId(model.getAttributeValue().getAttributeId());
            }
        }
    }
}
