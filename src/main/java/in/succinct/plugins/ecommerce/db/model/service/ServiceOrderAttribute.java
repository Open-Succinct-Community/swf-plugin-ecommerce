package in.succinct.plugins.ecommerce.db.model.service;

import com.venky.swf.db.annotations.column.COLUMN_NAME;
import com.venky.swf.db.annotations.model.EXPORTABLE;
import com.venky.swf.db.model.Model;
import in.succinct.plugins.ecommerce.db.model.participation.ExtendedAttribute;
@EXPORTABLE(false)

public interface ServiceOrderAttribute extends Model, ExtendedAttribute {
	public long getServiceOrderId();
	public void setServiceOrderId(long id);
	public ServiceOrder getServiceOrder();

	@COLUMN_NAME("SERVICE_ORDER_ID")
	public long getEntityId();

}
