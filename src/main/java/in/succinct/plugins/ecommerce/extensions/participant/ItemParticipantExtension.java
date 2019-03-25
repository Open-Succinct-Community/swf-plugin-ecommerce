package in.succinct.plugins.ecommerce.extensions.participant;

import com.venky.swf.plugins.collab.extensions.participation.CompanySpecificParticipantExtension;
import in.succinct.plugins.ecommerce.db.model.catalog.Item;
import in.succinct.plugins.ecommerce.db.model.catalog.UnitOfMeasure;
import in.succinct.plugins.ecommerce.db.model.participation.Company;
import com.venky.swf.db.extensions.ParticipantExtension;
import com.venky.swf.db.model.User;
import com.venky.swf.db.model.reflection.ModelReflector;
import com.venky.swf.pm.DataSecurityFilter;
import com.venky.swf.sql.Expression;
import com.venky.swf.sql.Operator;

import java.util.List;

public class ItemParticipantExtension extends CompanySpecificParticipantExtension<Item> {
	static  {
		registerExtension(new ItemParticipantExtension());
	}
	@Override
	protected List<Long> getAllowedFieldValues(User user, Item partiallyFilledModel, String fieldName) {
		if (fieldName.equals("LENGTH_U_O_M_ID") || fieldName.equals("WIDTH_U_O_M_ID") || fieldName.equals("HEIGHT_U_O_M_ID")){
			ModelReflector<UnitOfMeasure> ref = ModelReflector.instance(UnitOfMeasure.class);
			return DataSecurityFilter.getIds(DataSecurityFilter.getRecordsAccessible(UnitOfMeasure.class, user, new Expression(ref.getPool(), "MEASURES", Operator.EQ, "Length")));
		}else if (fieldName.equals("WEIGHT_U_O_M_ID")) {
			ModelReflector<UnitOfMeasure> ref = ModelReflector.instance(UnitOfMeasure.class);
			return DataSecurityFilter.getIds(DataSecurityFilter.getRecordsAccessible(UnitOfMeasure.class, user, new Expression(ref.getPool(), "MEASURES", Operator.EQ, "Weight")));
		}else {
			return super.getAllowedFieldValues(user,partiallyFilledModel,fieldName);
		}
				
	}

}
