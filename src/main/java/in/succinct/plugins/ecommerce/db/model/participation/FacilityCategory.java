package in.succinct.plugins.ecommerce.db.model.participation;

import com.venky.swf.db.annotations.column.IS_VIRTUAL;
import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.pm.PARTICIPANT;
import com.venky.swf.db.annotations.column.ui.PROTECTION;
import com.venky.swf.db.annotations.column.ui.PROTECTION.Kind;

@IS_VIRTUAL(false)
public interface FacilityCategory extends FacilityCategorization {
	@PARTICIPANT
	@UNIQUE_KEY
	@PROTECTION(Kind.NON_EDITABLE)
	public long getFacilityId(); 
	public  void setFacilityId( long id) ;
	public Facility getFacility();

	@UNIQUE_KEY
	public Long getMasterFacilityCategoryId();
	
}
