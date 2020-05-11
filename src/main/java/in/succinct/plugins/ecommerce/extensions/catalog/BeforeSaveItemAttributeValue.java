package in.succinct.mandi.extensions;

import com.venky.core.util.ObjectUtil;
import com.venky.swf.db.extensions.BeforeModelSaveExtension;
import com.venky.swf.plugins.background.core.TaskManager;
import in.succinct.plugins.ecommerce.agents.catalog.HashItemTask;
import in.succinct.plugins.ecommerce.db.model.attributes.AssetCodeAttribute;
import in.succinct.plugins.ecommerce.db.model.catalog.Item;
import in.succinct.plugins.ecommerce.db.model.catalog.ItemAttributeValue;

import java.util.HashSet;
import java.util.Set;

public class BeforeSaveItemAttributeValue extends BeforeModelSaveExtension<ItemAttributeValue> {
    static{
        registerExtension(new BeforeSaveItemAttributeValue());
    }
    @Override
    public void beforeSave(ItemAttributeValue model) {
        Item item = model.getItem();
        Set<Long> allowedAttributeIds = new HashSet<>();

        if (!item.getReflector().isVoid(item.getAssetCodeId())){
            item.getAssetCode().getAssetCodeAttributes().forEach(aca->{
                if (ObjectUtil.equals(aca.getAttributeType(), AssetCodeAttribute.ATTRIBUTE_TYPE_CATALOG)){
                    allowedAttributeIds.add(aca.getAttributeId());
                }
            });
        }

        if (!allowedAttributeIds.contains(model.getAttributeValue().getAttributeId())){
            throw new RuntimeException("Not a valid attribute for an item with asset_code of " + model.getItem().getAssetCode().getLongDescription());
        }

        TaskManager.instance().executeAsync(new HashItemTask(model.getItemId()),false);
    }
}
