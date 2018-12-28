package in.succinct.plugins.ecommerce.db.model.order;

import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.validations.Enumeration;
import com.venky.swf.db.model.Model;

public interface OrderLineUnitNumber extends Model{
	@UNIQUE_KEY
	public long getOrderLineId();
	public void setOrderLineId(long id);
	public OrderLine getOrderLine(); 
	
	public static final String UNIT_NUMBER_TYPE_SERIAL = "SERIAL";
	public static final String UNIT_NUMBER_TYPE_IMEI = "IMEI";
	public static final String UNIT_NUMBER_TYPE_MEID = "MEID";
	
	
	@Enumeration(UNIT_NUMBER_TYPE_SERIAL +","+UNIT_NUMBER_TYPE_IMEI +"," +UNIT_NUMBER_TYPE_MEID)
	@UNIQUE_KEY
	public String getUnitNumberType();
	public void setUnitNumberType(String type);
	
	@UNIQUE_KEY
	public String getUnitNumber(); 
	public void setUnitNumber(String  serialNumber); 
	
}
