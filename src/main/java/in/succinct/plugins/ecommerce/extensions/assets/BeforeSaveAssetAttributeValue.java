package in.succinct.mandi.extensions;

import com.venky.swf.db.extensions.BeforeModelSaveExtension;
import com.venky.swf.plugins.background.core.TaskManager;
import in.succinct.plugins.ecommerce.agents.asset.HashAssetTask;
import in.succinct.plugins.ecommerce.db.model.assets.Asset;
import in.succinct.plugins.ecommerce.db.model.assets.AssetAttributeValue;
import in.succinct.plugins.ecommerce.db.model.assets.Capability;
import in.succinct.plugins.ecommerce.db.model.attributes.AssetCode;

import java.util.HashSet;
import java.util.Set;

public class BeforeSaveAssetAttributeValue extends BeforeModelSaveExtension<AssetAttributeValue> {
    static{
        registerExtension(new BeforeSaveAssetAttributeValue());
    }
    @Override
    public void beforeSave(AssetAttributeValue model) {
        Set<Long> allowedAttributeIds = new HashSet<>();
        Set<String> assetCodeDescriptions = new HashSet<>();

        Asset asset = model.getAsset();
        for (Capability capability: asset.getAssetCapabilities()){
            AssetCode assetCode =  capability.getAssetCode();
            assetCode.getAssetCodeAttributes().forEach(aca->{
                allowedAttributeIds.add(aca.getAttributeId());
                //Both catalog and inventory attributes are allowed.
            });
            assetCodeDescriptions.add(assetCode.getLongDescription());
        }
        if (allowedAttributeIds.isEmpty()){
            throw new RuntimeException("Don't know what asset code this asset belongs to.");
        }
        if (!allowedAttributeIds.contains(model.getAttributeValue().getAttributeId()) ){
            throw new RuntimeException("Not a valid attribute for an asset with asset_code(s) of " + assetCodeDescriptions);
        }

        TaskManager.instance().executeAsync(new HashAssetTask(model.getAssetId()),false);
    }
}
