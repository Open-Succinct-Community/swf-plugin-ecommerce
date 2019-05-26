package in.succinct.plugins.ecommerce.db.model.order;

import java.io.InputStream;

import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.ui.PROTECTION;
import com.venky.swf.db.annotations.column.ui.PROTECTION.Kind;
import com.venky.swf.db.annotations.model.EXPORTABLE;
import com.venky.swf.db.model.Model;
@EXPORTABLE(false)

public interface OrderPrint extends Model{
	@UNIQUE_KEY
	public long getOrderId(); 
	public void setOrderId(long id); 
	public Order getOrder();
	
	@UNIQUE_KEY
	public String getDocumentId();
	public  void setDocumentId(String id);
	
	public static final String DOCUMENT_TYPE_PACK_SLIP = "PACK_SLIP";
	public static final String DOCUMENT_TYPE_CARRIER_LABEL = "CARRIER_LABEL";
	
	@UNIQUE_KEY
	public String getDocumentType(); 
	public void setDocumentType(String documentType);
	
	public InputStream getImage();
	public void setImage(InputStream is);
	
	@PROTECTION(Kind.NON_EDITABLE)
	public String getImageContentName();
	public void setImageContentName(String name);

	@PROTECTION(Kind.NON_EDITABLE)
	public String getImageContentType();
	public void setImageContentType(String contentType);
	
	@PROTECTION(Kind.NON_EDITABLE)
	public int getImageContentSize();
	public void setImageContentSize(int size);
}
