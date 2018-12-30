package in.succinct.plugins.ecommerce.db.model.order;


import com.venky.swf.db.annotations.column.COLUMN_DEF;
import com.venky.swf.db.annotations.column.IS_VIRTUAL;
import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.defaulting.StandardDefault;
import com.venky.swf.db.annotations.column.validations.Enumeration;
import com.venky.swf.db.model.Model;
import com.venky.swf.plugins.collab.db.model.participants.admin.Address;
import org.apache.commons.math3.analysis.function.Add;

public interface OrderAddress extends Model, Address {
	@UNIQUE_KEY
	public long getOrderId();
	public void setOrderId(long id); 
	public Order getOrder();

	@UNIQUE_KEY
	@COLUMN_DEF(value= StandardDefault.SOME_VALUE,args=ADDRESS_TYPE_SHIP_TO)
	@Enumeration(ADDRESS_TYPE_SHIP_TO +","+ADDRESS_TYPE_BILL_TO)
	public  String getAddressType();
	public void setAddressType(String addressType);

	public String getFirstName();
	public void setFirstName(String name);

	public String getLastName();
	public void setLastName(String name);


}
