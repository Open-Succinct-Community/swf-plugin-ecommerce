package in.succinct.plugins.ecommerce.extensions.inventory;

import com.venky.core.math.DoubleUtils;
import in.succinct.plugins.ecommerce.db.model.inventory.Inventory;
import com.venky.swf.db.extensions.BeforeModelSaveExtension;

public class BeforeSaveInventory extends BeforeModelSaveExtension<Inventory> {
	static {
		registerExtension(new BeforeSaveInventory());
	}
	@Override
	public void beforeSave(Inventory model) {
        model.setCompanyId(model.getFacility().getCompanyId());
        if (model.isInfinite() && !DoubleUtils.equals(model.getQuantity(),0.0)){
        	throw new RuntimeException("First zero out the quantity before marking the inventory as infinite.");
		}
	}

}
