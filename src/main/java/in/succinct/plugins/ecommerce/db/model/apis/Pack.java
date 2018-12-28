package in.succinct.plugins.ecommerce.db.model.apis;

import in.succinct.plugins.ecommerce.db.model.order.OrderLine;
import com.venky.swf.db.annotations.column.COLUMN_DEF;
import com.venky.swf.db.annotations.column.IS_VIRTUAL;
import com.venky.swf.db.annotations.column.defaulting.StandardDefault;
import com.venky.swf.db.model.Model;

@IS_VIRTUAL
public interface Pack extends Model {
	
	public long getOrderLineId();
	public void setOrderLineId(long id); 
	public OrderLine getOrderLine();
	
	public String getUnitNumber(); 
	public void setUnitNumber(String  serialNumber); 
	
	public Double getPackedQuantity();
	public void setPackedQuantity(Double packedQuantity);
	
	@COLUMN_DEF(StandardDefault.BOOLEAN_FALSE)
	public boolean isSuccess(); 
	public void setSuccess(boolean success);

	public String getError();
	public void setError(String error);

	public void pack();


	
	public static class PackValidationException extends RuntimeException {
		public PackValidationException() {
			super();
			
		}

		public PackValidationException(String message, Throwable cause, boolean enableSuppression,
				boolean writableStackTrace) {
			super(message, cause, enableSuppression, writableStackTrace);
			
		}

		public PackValidationException(String message, Throwable cause) {
			super(message, cause);
			
		}

		public PackValidationException(String message) {
			super(message);
			
		}

		public PackValidationException(Throwable cause) {
			super(cause);
			
		}

		private static final long serialVersionUID = 8911749681043278056L; 
		
	}
}
