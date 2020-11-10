package in.succinct.plugins.ecommerce.db.model.participation;

import com.venky.swf.db.annotations.column.IS_NULLABLE;
import com.venky.swf.db.annotations.column.IS_VIRTUAL;
import com.venky.swf.plugins.calendar.db.model.WorkCalendar;
import in.succinct.plugins.ecommerce.db.model.attachments.Attachment;
import in.succinct.plugins.ecommerce.db.model.inventory.Inventory;

import java.util.List;

@IS_VIRTUAL(false)
public interface Facility extends com.venky.swf.plugins.collab.db.model.participants.admin.Facility{

	@IS_NULLABLE
	public String getInvoicePrefix();
	public void setInvoicePrefix(String invoicePrefix);

	public List<FacilityCategory> getFacilityCategories();
	
	public List<Inventory> getInventoryList();

	public List<PreferredCarrier> getPreferredCarriers();

	public List<MarketPlaceIntegration> getPreferredMarketPlaceIntegrations();

	public List<Attachment> getAttachments();

}
