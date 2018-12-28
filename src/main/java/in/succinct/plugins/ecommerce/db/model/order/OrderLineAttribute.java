package in.succinct.plugins.ecommerce.db.model.order;

import com.venky.swf.db.model.Model;

public interface OrderLineAttribute extends Model {
	public long getOrderLineId();
	public void setOrderLineId(long id);
	public OrderLine getOrderLine(); 
	
	public String getName(); 
	public void setName(String name); 
	
	public String getValue(); 
	public void setValue(String value);
}
