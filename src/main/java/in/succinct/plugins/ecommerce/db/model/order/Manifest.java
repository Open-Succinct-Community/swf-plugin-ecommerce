package in.succinct.plugins.ecommerce.db.model.order;

import com.venky.core.util.Bucket;
import com.venky.swf.db.annotations.column.IS_VIRTUAL;
import com.venky.swf.db.annotations.column.indexing.Index;
import com.venky.swf.db.annotations.column.validations.Enumeration;
import com.venky.swf.db.annotations.model.EXPORTABLE;
import in.succinct.plugins.ecommerce.db.model.participation.Facility;
import com.venky.swf.db.annotations.column.COLUMN_DEF;
import com.venky.swf.db.annotations.column.IS_NULLABLE;
import com.venky.swf.db.annotations.column.defaulting.StandardDefault;
import com.venky.swf.db.annotations.column.ui.PROTECTION;
import com.venky.swf.db.annotations.column.ui.PROTECTION.Kind;
import com.venky.swf.db.annotations.model.MENU;
import com.venky.swf.db.model.Model;
import in.succinct.plugins.ecommerce.db.model.participation.PreferredCarrier;

import java.io.InputStream;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@MENU("Fulfillment")
@EXPORTABLE(false)

public interface Manifest extends Model {
	@Index
	public String getManifestNumber();
	public void setManifestNumber(String number);

	public Long getPreferredCarrierId();
	public void setPreferredCarrierId(Long id);
	public PreferredCarrier getPreferredCarrier();

	@IS_NULLABLE
	public Timestamp getPickupNoLaterThan();
	public void setPickupNoLaterThan(Timestamp ts);

    @IS_NULLABLE
    public Timestamp getPickupNoEarlierThan();
    public void setPickupNoEarlierThan(Timestamp ts);


	
	@IS_NULLABLE
	public InputStream getImage();
	public void setImage(InputStream is);
	
	@PROTECTION(Kind.NON_EDITABLE)
	@IS_NULLABLE
	public String getImageContentName();
	public void setImageContentName(String name);

	@PROTECTION(Kind.NON_EDITABLE)
	@IS_NULLABLE
	public String getImageContentType();
	public void setImageContentType(String contentType);
	
	@PROTECTION(Kind.NON_EDITABLE)
	@COLUMN_DEF(StandardDefault.ZERO)
	public int getImageContentSize();
	public void setImageContentSize(int size);
	
	@COLUMN_DEF(StandardDefault.BOOLEAN_FALSE)
	public boolean isClosed(); 
	public void  setClosed(boolean closed);

	@IS_VIRTUAL
	public List<Order> getOrders();

	public void close();

	public void track();


	@IS_VIRTUAL
	public int getNumOrdersPendingDelivery();

	@IS_VIRTUAL
	Set<Long> getOrderIdsPendingDelivery();
}
