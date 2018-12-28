package in.succinct.plugins.ecommerce.db.model.participation;

import com.venky.swf.db.annotations.column.IS_NULLABLE;
import com.venky.swf.db.annotations.column.IS_VIRTUAL;
import com.venky.swf.db.annotations.column.pm.PARTICIPANT;
import com.venky.swf.db.annotations.column.ui.OnLookupSelect;
import com.venky.swf.db.model.Model;

@IS_VIRTUAL
public interface FacilityCategorization extends  Model{
	@PARTICIPANT(value="MASTER_FACILITY_CATEGORY",redundant=true)
	@IS_NULLABLE(false)
	@OnLookupSelect(processor="in.succinct.plugins.ecommerce.db.model.participation.MasterFacilityCategorySelectionProcessor")
	public Long getMasterFacilityCategoryId();
	public void setMasterFacilityCategoryId(Long id);
	public MasterFacilityCategory getMasterFacilityCategory();
	
	@OnLookupSelect(processor="in.succinct.plugins.ecommerce.db.model.participation.MasterFacilityCategorySelectionProcessor")
	@PARTICIPANT(value="MASTER_FACILITY_CATEGORY_VALUE",redundant=true)
	@IS_NULLABLE(false)
	public Long getMasterFacilityCategoryValueId();
	public void setMasterFacilityCategoryValueId(Long id);
	public MasterFacilityCategoryValue getMasterFacilityCategoryValue();
}
