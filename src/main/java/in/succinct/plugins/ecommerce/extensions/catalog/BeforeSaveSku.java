package in.succinct.plugins.ecommerce.extensions.catalog;

import in.succinct.plugins.ecommerce.db.model.attributes.AssetCode;
import in.succinct.plugins.ecommerce.db.model.catalog.Item;
import in.succinct.plugins.ecommerce.db.model.inventory.Sku;
import com.venky.swf.db.Database;
import com.venky.swf.db.JdbcTypeHelper;
import com.venky.swf.db.extensions.BeforeModelSaveExtension;

public class BeforeSaveSku extends BeforeModelSaveExtension<Sku>{
	static { 
		registerExtension(new BeforeSaveSku());
	}
	@Override
	public void beforeSave(Sku sku) {
		JdbcTypeHelper helper = Database.getJdbcTypeHelper(sku.getReflector().getPool());

		Item item = sku.getItem();
		if (helper.isVoid(sku.getCompanyId())) {
			sku.setCompanyId(item.getCompanyId());
		}
		if (helper.isVoid(sku.getName())) {
			sku.setName(sku.getItem().getName());
		}
		if (helper.isVoid(sku.getLength())){
		    sku.setLength(item.getLength());
		    sku.setLengthUOMId(item.getLengthUOMId());
        }
        if (helper.isVoid(sku.getWidth())){
            sku.setWidth(item.getWidth());
            sku.setWidthUOMId(item.getWidthUOMId());
        }
        if (helper.isVoid(sku.getHeight())){
            sku.setHeight(item.getHeight());
            sku.setHeightUOMId(item.getHeightUOMId());
        }
        if (helper.isVoid(sku.getWeight())){
            sku.setWeight(item.getWeight());
            sku.setWeightUOMId(item.getWeightUOMId());
        }

        if (!helper.isVoid(sku.getItem().getAssetCodeId())){
			AssetCode assetCode = sku.getItem().getAssetCode();
			if (!helper.isVoid(assetCode.getGstPct())){
				sku.setTaxRate(assetCode.getGstPct());
			}
		}

	}


}
