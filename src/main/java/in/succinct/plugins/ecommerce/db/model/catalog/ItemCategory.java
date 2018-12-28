package in.succinct.plugins.ecommerce.db.model.catalog;

import com.venky.swf.db.annotations.column.IS_VIRTUAL;
import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.pm.PARTICIPANT;
import com.venky.swf.db.annotations.column.ui.PROTECTION;
import com.venky.swf.db.annotations.column.ui.PROTECTION.Kind;

@IS_VIRTUAL(false)
public interface ItemCategory extends ItemCategorization {
	@PARTICIPANT
	@UNIQUE_KEY
	@PROTECTION(Kind.NON_EDITABLE)
	public long getItemId();
	public  void setItemId( long id) ;
	public Item getItem();

}
