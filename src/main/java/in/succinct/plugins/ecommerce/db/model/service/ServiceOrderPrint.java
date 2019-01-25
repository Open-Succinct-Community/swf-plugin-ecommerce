package in.succinct.plugins.ecommerce.db.model.service;

import com.venky.swf.db.annotations.column.COLUMN_NAME;
import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.ui.PROTECTION;
import com.venky.swf.db.annotations.column.ui.PROTECTION.Kind;
import com.venky.swf.db.model.Model;
import in.succinct.plugins.ecommerce.db.model.order.Order;
import in.succinct.plugins.ecommerce.db.model.participation.ExtendedPrint;

import java.io.InputStream;

public interface ServiceOrderPrint extends ExtendedPrint , Model{
	@UNIQUE_KEY
	public long getServiceOrderId();
	public void setServiceOrderId(long id);
	public ServiceOrder getOrder();

	@COLUMN_NAME("SERVICE_ORDER_ID")
	public long getEntityId();
	
	@UNIQUE_KEY
	public String getDocumentId();

	public static final String DOCUMENT_TYPE_INVOICE = "INVOICE";

	@UNIQUE_KEY
	public String getDocumentType(); 

}
