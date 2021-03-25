package in.succinct.plugins.ecommerce.db.model.participation;

import com.venky.swf.db.annotations.column.IS_NULLABLE;
import com.venky.swf.plugins.calendar.db.model.WorkCalendar;
import in.succinct.plugins.ecommerce.db.model.catalog.MasterItemCategory;
import com.venky.swf.db.annotations.model.MENU;

import java.util.List;

@MENU("Admin") 
public interface Company extends com.venky.swf.plugins.collab.db.model.participants.admin.Company{

	public List<MasterFacilityCategory> getFacilityCategories();
	public List<MasterItemCategory> getItemCategories();

	@IS_NULLABLE
	public Long getWorkCalendarId();
	public void setWorkCalendarId(Long WorkCalendarId);
	public WorkCalendar getWorkCalendar();

}
