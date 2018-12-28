package in.succinct.plugins.ecommerce.db.model.participation;

import in.succinct.plugins.ecommerce.db.model.catalog.MasterItemCategory;
import com.venky.swf.db.annotations.model.MENU;

import java.util.List;

@MENU("Admin") 
public interface Company extends com.venky.swf.plugins.collab.db.model.participants.admin.Company{

	public List<MasterFacilityCategory> getFacilityCategories();
	public List<MasterItemCategory> getItemCategories();
	
}
