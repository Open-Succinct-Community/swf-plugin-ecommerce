package in.succinct.plugins.ecommerce.db.model.catalog;

import com.venky.swf.db.annotations.column.IS_NULLABLE;
import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.pm.PARTICIPANT;
import com.venky.swf.db.annotations.column.relationship.CONNECTED_VIA;
import com.venky.swf.db.annotations.column.ui.PROTECTION;
import com.venky.swf.db.annotations.column.ui.PROTECTION.Kind;
import com.venky.swf.db.annotations.model.HAS_DESCRIPTION_FIELD;
import com.venky.swf.db.model.Model;

import java.io.Reader;
import java.util.List;


@HAS_DESCRIPTION_FIELD("ALLOWED_VALUE")
public interface MasterItemCategoryValue extends Model{
	@UNIQUE_KEY
	@PROTECTION(Kind.NON_EDITABLE)
	@PARTICIPANT
	@IS_NULLABLE(false)
	public Long getMasterItemCategoryId();
	public void setMasterItemCategoryId(Long id);
	public MasterItemCategory getMasterItemCategory();
	
	@UNIQUE_KEY
	public String getAllowedValue();
	public void setAllowedValue(String value);


	@IS_NULLABLE
	public Reader getNotes();
	public void setNotes(Reader reader);

	@CONNECTED_VIA("MASTER_ITEM_CATEGORY_VALUE_ID")
	List<ItemCategory> getItemCategories();

}
