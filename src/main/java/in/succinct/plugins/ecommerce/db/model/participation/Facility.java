package in.succinct.plugins.ecommerce.db.model.participation;

import in.succinct.plugins.ecommerce.db.model.inventory.Inventory;
import com.venky.swf.db.annotations.column.COLUMN_DEF;
import com.venky.swf.db.annotations.column.defaulting.StandardDefault;

import java.util.List;

public interface Facility extends com.venky.swf.plugins.collab.db.model.participants.admin.Facility{
	
	@COLUMN_DEF(StandardDefault.BOOLEAN_FALSE)
	public boolean isInventoryAllocatedBeforePack();
	public void setInventoryAllocatedBeforePack(boolean blocked);
	
	
	public List<FacilityCategory> getFacilityCategories();
	
	public List<Inventory> getInventoryList();
	
}
