package in.succinct.plugins.ecommerce.extensions.catalog;

import com.venky.swf.db.Database;
import com.venky.swf.db.JdbcTypeHelper;
import com.venky.swf.db.extensions.AfterModelCreateExtension;
import in.succinct.plugins.ecommerce.db.model.catalog.Item;
import in.succinct.plugins.ecommerce.db.model.inventory.Sku;

public class AfterCreateItem extends AfterModelCreateExtension<Item> {
	static { 
		registerExtension(new AfterCreateItem());
	}
	@Override
	public void afterCreate(Item item) {
		JdbcTypeHelper helper = Database.getJdbcTypeHelper(item.getReflector().getPool());

		Sku sku = Database.getTable(Sku.class).newRecord();
		sku.setItemId(item.getId());
		sku.save(); //Fill Defaults for sku.

	}


}
