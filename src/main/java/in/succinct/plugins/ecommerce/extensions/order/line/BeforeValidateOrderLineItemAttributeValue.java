package in.succinct.plugins.ecommerce.extensions.order.line;

import com.venky.core.util.ObjectUtil;
import com.venky.swf.db.extensions.BeforeModelValidateExtension;
import in.succinct.plugins.ecommerce.db.model.attributes.AssetCodeAttribute;
import in.succinct.plugins.ecommerce.db.model.catalog.Item;
import in.succinct.plugins.ecommerce.db.model.order.OrderLineItemAttributeValue;

import java.util.HashSet;
import java.util.Set;

public class BeforeValidateOrderLineItemAttributeValue extends BeforeModelValidateExtension<OrderLineItemAttributeValue> {
    static {
        registerExtension(new BeforeValidateOrderLineItemAttributeValue());
    }
    @Override
    public void beforeValidate(OrderLineItemAttributeValue model) {
        if (model.getReflector().isVoid(model.getAttributeId())){
            if (!model.getReflector().isVoid(model.getAttributeValueId())){
                model.setAttributeId(model.getAttributeValue().getAttributeId());
            }
        }

        Item item = model.getOrderLine().getSku().getItem();
        Set<Long> allowedAttributeIds = new HashSet<>();
        if (!item.getReflector().isVoid(item.getAssetCodeId())){
            item.getAssetCode().getAssetCodeAttributes().forEach(aca->{
                if (ObjectUtil.equals(aca.getAttributeType(), AssetCodeAttribute.ATTRIBUTE_TYPE_INVENTORY)){
                    allowedAttributeIds.add(aca.getAttributeId());
                }
            });
        }
        if (allowedAttributeIds.isEmpty()){
            throw new RuntimeException("Don't what what asset code the item " + item.getName() + " belongs to. OrderLine:" + model.getOrderLineId());
        }

        if (!allowedAttributeIds.contains(model.getAttributeValue().getAttributeId())){
            throw new RuntimeException("Not a valid attribute for an item with asset_code of " + item.getAssetCode().getLongDescription());
        }

    }
}
