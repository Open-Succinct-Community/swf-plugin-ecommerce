package in.succinct.plugins.ecommerce.db.model.assets;

import com.venky.digest.Encryptor;
import com.venky.swf.db.table.ModelImpl;
import in.succinct.plugins.ecommerce.db.model.attributes.AssetCodeAttribute;
import in.succinct.plugins.ecommerce.db.model.attributes.AttributeValue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class AssetImpl extends ModelImpl<Asset> {
    public AssetImpl(Asset asset){
        super(asset);
    }
    public AssetImpl(){
        super();
    }

    public boolean isRentable(){
        boolean rentable = false;

        for (Iterator<Capability> i = getProxy ().getAssetCapabilities().iterator(); !rentable && i.hasNext(); ){
            Capability capability = i.next();
            rentable = rentable || capability.getAssetCode().isSac();
        }
        return rentable;
    }

    public List<Capability> getLoanableCapabilities(){
        return getProxy().getAssetCapabilities().stream().filter(c->c.getAssetCode().isSac()).collect(Collectors.toList());
    }
    public void computeHash(){
        Asset asset = getProxy();

        Map<Long, AttributeValue> attributeIdValueMap = new HashMap<>();
        asset.getAssetAttributeValues().forEach(a->{
            AttributeValue attributeValue = a.getAttributeValue();
            attributeIdValueMap.put(attributeValue.getAttributeId(),attributeValue);
        });

        Set<Long> allowedAttributeIds = new HashSet<>();
        for (Capability assetCapability : asset.getAssetCapabilities()) {
            Set<Long> catalogAttributeValueIds = new TreeSet<>();
            Set<Long> inventoryAttributeValueIds = new TreeSet<>();

            assetCapability.getAssetCode().getAssetCodeAttributes().forEach(attr->{
                allowedAttributeIds.add(attr.getAttributeId());
                AttributeValue attributeValue = attributeIdValueMap.get(attr.getAttributeId());

                if (attributeValue != null){
                    switch (attr.getAttributeType()){
                        case AssetCodeAttribute.ATTRIBUTE_TYPE_CATALOG:
                            catalogAttributeValueIds.add(attributeValue.getId());
                            break;
                        case AssetCodeAttribute.ATTRIBUTE_TYPE_INVENTORY:
                            inventoryAttributeValueIds.add(attributeValue.getId());
                            break;
                    }
                }
            });

            String catalogHash = Encryptor.encrypt(assetCapability.getAssetCodeId()+catalogAttributeValueIds.toString());
            assetCapability.setAssetCatalogHash(catalogHash);

            String inventoryHash = Encryptor.encrypt(assetCapability.getAssetCodeId()+inventoryAttributeValueIds.toString());
            assetCapability.setAssetInventoryHash(inventoryHash);
            assetCapability.save();
        }
        attributeIdValueMap.forEach((attrId, attrValue)->{
            if (!allowedAttributeIds.contains(attrId)){
                attrValue.destroy();
            }
        });
    }

}
