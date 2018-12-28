package in.succinct.plugins.ecommerce.extensions.inventory;

import in.succinct.plugins.ecommerce.db.model.inventory.Inventory;
import com.venky.swf.db.extensions.BeforeModelSaveExtension;

public class BeforeSaveInventory extends BeforeModelSaveExtension<Inventory> {
	static {
		registerExtension(new BeforeSaveInventory());
	}
	@Override
	public void beforeSave(Inventory model) {
        model.setCompanyId(model.getFacility().getCompanyId());
	}

}
