package in.succinct.plugins.ecommerce.db.model.apis;

import in.succinct.plugins.ecommerce.db.model.order.OrderLine;
import com.venky.swf.db.annotations.column.COLUMN_DEF;
import com.venky.swf.db.annotations.column.IS_VIRTUAL;
import com.venky.swf.db.annotations.column.defaulting.StandardDefault;
import com.venky.swf.db.model.Model;

@IS_VIRTUAL
public interface Cancel extends Model {

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

	public void reject() throws OrderCancellationException;

	public void cancel() throws OrderCancellationException;
    public void cancel(String reason, String initiator);

    public static class OrderCancellationException extends RuntimeException {
        public OrderCancellationException() {
        }

        public OrderCancellationException(String message) {
            super(message);
        }

        public OrderCancellationException(String message, Throwable cause) {
            super(message, cause);
        }

        public OrderCancellationException(Throwable cause) {
            super(cause);
        }
    }
}
