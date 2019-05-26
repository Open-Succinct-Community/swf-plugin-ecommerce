package in.succinct.plugins.ecommerce.db.model.order;

import com.venky.swf.db.annotations.model.EXPORTABLE;
import com.venky.swf.db.model.Model;
@EXPORTABLE(false)

public interface OrderAttribute extends Model {
	public long getOrderId();
	public void setOrderId(long id);
	public Order getOrder(); 
	
	public String getName(); 
	public void setName(String name); 
	
	public String getValue(); 
	public void setValue(String value);
}
