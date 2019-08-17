package in.succinct.plugins.ecommerce.db.model.inventory;

import com.venky.swf.db.Database;
import com.venky.swf.db.table.ModelImpl;

public class InventoryImpl extends  ModelImpl<Inventory> {

	public InventoryImpl(Inventory proxy) {
		super(proxy);
	}

	public void adjust(double delta,String comment){
		Inventory inv = getProxy();
		if (!inv.isInfinite()) {
			inv.setQuantity(inv.getQuantity() + delta);
			inv.save();
		}
		InventoryAudit audit = Database.getTable(InventoryAudit.class).newRecord();
		audit.setInventoryId(inv.getId());
		audit.setAuditQuantity(delta);
		audit.setComment(comment);
		audit.save();
	}


}
