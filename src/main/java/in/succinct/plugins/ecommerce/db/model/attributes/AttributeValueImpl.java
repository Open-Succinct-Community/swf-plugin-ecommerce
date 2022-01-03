package in.succinct.plugins.ecommerce.db.model.attributes;

import com.venky.core.util.ObjectUtil;
import com.venky.swf.db.table.ModelImpl;

public class AttributeValueImpl extends ModelImpl<AttributeValue> {
    public AttributeValueImpl(){

    }
    public AttributeValueImpl(AttributeValue value){
        super(value);
    }

    public String getDescription() {
        AttributeValue proxy = getProxy();
        StringBuilder desc = new StringBuilder();
        if (!ObjectUtil.isVoid(proxy.getAttributeId())) {
            desc.append(proxy.getAttribute().getName());
            desc.append("-");
            if (!ObjectUtil.isVoid(proxy.getPossibleValue())){
                desc.append(proxy.getPossibleValue());
            }
        }
        return desc.toString();
    }
}
