package in.succinct.plugins.ecommerce.extensions.inventory;

import com.venky.swf.db.extensions.BeforeModelSaveExtension;
import in.succinct.plugins.ecommerce.db.model.inventory.Inventory;
import in.succinct.plugins.ecommerce.db.model.inventory.Sku;
import in.succinct.plugins.ecommerce.db.model.participation.MarketPlaceInventoryUpdateQueue;

public class BeforeSaveInventory extends BeforeModelSaveExtension<Inventory> {
	static {
		registerExtension(new BeforeSaveInventory());
	}
	@Override
	public void beforeSave(Inventory model) {
        model.setCompanyId(model.getFacility().getCompanyId());
		Sku sku = model.getSku();

		if (model.isInfinite() && model.getQuantity() > 0) {
			model.setQuantity(0.0);
		}

		double mrp = Math.max(model.getReflector().getJdbcTypeHelper().getTypeRef(double.class).getTypeConverter().valueOf(model.getSellingPrice()),
				model.getReflector().getJdbcTypeHelper().getTypeRef(double.class).getTypeConverter().valueOf(model.getMaxRetailPrice()) );

		model.setMaxRetailPrice(mrp);
		if (model.getReflector().isVoid(model.getSellingPrice())){
			model.setSellingPrice(mrp);
		}

		if (sku.getMaxRetailPrice() < model.getMaxRetailPrice()){
			sku.setMaxRetailPrice(model.getMaxRetailPrice());
			sku.save();
		}

		if (!model.isInfinite() || model.getRawRecord().isFieldDirty("INFINITE")){
			MarketPlaceInventoryUpdateQueue.push(model);
		}
	}

}
