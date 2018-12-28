package in.succinct.plugins.ecommerce.db.model.apis;

import in.succinct.plugins.ecommerce.db.model.order.OrderLine;
import com.venky.swf.db.annotations.column.COLUMN_DEF;
import com.venky.swf.db.annotations.column.IS_VIRTUAL;
import com.venky.swf.db.annotations.column.defaulting.StandardDefault;
import com.venky.swf.db.model.Model;

@IS_VIRTUAL
public interface Ship extends Model {

	public long getOrderLineId();
	public void setOrderLineId(long id);
	public OrderLine getOrderLine();

	public Double getQuantity();
	public void setQuantity(Double Quantity);
	
	@COLUMN_DEF(StandardDefault.BOOLEAN_FALSE)
	public boolean isSuccess(); 
	public void setSuccess(boolean success);

	public String getError();
	public void setError(String error);

	public void ship();


}
