package in.succinct.plugins.ecommerce.db.model.participation;

import com.venky.swf.db.annotations.column.IS_VIRTUAL;
import in.succinct.plugins.ecommerce.db.model.inventory.Inventory;

import java.util.List;

@IS_VIRTUAL(false)
public interface Facility extends com.venky.swf.plugins.collab.db.model.participants.admin.Facility{
	

	public List<FacilityCategory> getFacilityCategories();
	
	public List<Inventory> getInventoryList();

	public List<PreferredCarrier> getPreferredCarriers();
	
}
