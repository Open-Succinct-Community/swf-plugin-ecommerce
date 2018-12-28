package in.succinct.plugins.ecommerce.extensions.participant;

import in.succinct.plugins.ecommerce.db.model.participation.Facility;
import in.succinct.plugins.ecommerce.db.model.participation.FacilityCategory;
import in.succinct.plugins.ecommerce.db.model.participation.MasterFacilityCategory;
import in.succinct.plugins.ecommerce.db.model.participation.MasterFacilityCategoryValue;
import com.venky.swf.db.Database;
import com.venky.swf.db.extensions.ParticipantExtension;
import com.venky.swf.db.model.User;
import com.venky.swf.pm.DataSecurityFilter;

import java.util.List;

public class FacilityCategoryParticipantExtension extends ParticipantExtension<FacilityCategory>{
	static  {
		registerExtension(new FacilityCategoryParticipantExtension());
	}
	@Override
	protected List<Long> getAllowedFieldValues(User user, FacilityCategory partiallyFilledModel, String fieldName) {
		if (fieldName.equals("FACILITY_ID")){
			return DataSecurityFilter.getIds(DataSecurityFilter.getRecordsAccessible(Facility.class, user));
		}else if (fieldName.equals("MASTER_FACILITY_CATEGORY_ID")){
			return DataSecurityFilter.getIds(DataSecurityFilter.getRecordsAccessible(MasterFacilityCategory.class, user));
		}else if (fieldName.equals("MASTER_FACILITY_CATEGORY_VALUE_ID")){
			if (!Database.getJdbcTypeHelper(getReflector().getPool()).isVoid(partiallyFilledModel.getMasterFacilityCategoryId())){
				return DataSecurityFilter.getIds(partiallyFilledModel.getMasterFacilityCategory().getAllowedValues());
			}else {
				return DataSecurityFilter.getIds(DataSecurityFilter.getRecordsAccessible(MasterFacilityCategoryValue.class, user));
			}
		}
		return null;
	}

}
