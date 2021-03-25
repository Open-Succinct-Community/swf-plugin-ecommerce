package in.succinct.plugins.ecommerce.db.model.service;


import com.venky.swf.db.annotations.column.COLUMN_DEF;
import com.venky.swf.db.annotations.column.COLUMN_NAME;
import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.defaulting.StandardDefault;
import com.venky.swf.db.annotations.column.validations.Enumeration;
import com.venky.swf.db.annotations.model.EXPORTABLE;
import com.venky.swf.db.model.Model;
import in.succinct.plugins.ecommerce.db.model.participation.ExtendedAddress;
@EXPORTABLE(false)

public interface ServiceOrderAddress extends Model, ExtendedAddress {
	@UNIQUE_KEY
	public long getServiceOrderId();
	public void setServiceOrderId(long id);
	public ServiceOrder getServiceOrder();

	@COLUMN_NAME("SERVICE_ORDER_ID")
	public long getEntityId();


	@COLUMN_DEF(value= StandardDefault.SOME_VALUE,args=ADDRESS_TYPE_SERVICE_TO)
	@Enumeration(ADDRESS_TYPE_SERVICE_TO +","+ADDRESS_TYPE_BILL_TO+","+ADDRESS_TYPE_EC)
	public String getAddressType();

	public  static final String ADDRESS_TYPE_SERVICE_TO = "ST";
	public  static final String ADDRESS_TYPE_BILL_TO = "BT";
	public  static final String ADDRESS_TYPE_EC = "EC";

}
