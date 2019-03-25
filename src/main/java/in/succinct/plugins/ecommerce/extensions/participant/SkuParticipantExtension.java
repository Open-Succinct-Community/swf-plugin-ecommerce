package in.succinct.plugins.ecommerce.extensions.participant;

import com.venky.swf.plugins.collab.db.model.CompanySpecific;
import com.venky.swf.plugins.collab.extensions.participation.CompanySpecificParticipantExtension;
import in.succinct.plugins.ecommerce.db.model.catalog.Item;
import in.succinct.plugins.ecommerce.db.model.catalog.UnitOfMeasure;
import in.succinct.plugins.ecommerce.db.model.inventory.Sku;
import in.succinct.plugins.ecommerce.db.model.participation.Company;
import com.venky.core.collections.SequenceSet;
import com.venky.swf.db.extensions.ParticipantExtension;
import com.venky.swf.db.model.User;
import com.venky.swf.db.model.reflection.ModelReflector;
import com.venky.swf.pm.DataSecurityFilter;
import com.venky.swf.sql.Expression;
import com.venky.swf.sql.Operator;

import java.util.List;

public class SkuParticipantExtension extends CompanySpecificParticipantExtension<Sku> {
	static  {
		registerExtension(new SkuParticipantExtension());
	}
	@Override
	protected List<Long> getAllowedFieldValues(User user, Sku partiallyFilledModel, String fieldName) {
	    List<Long> ret = null;
		if (fieldName.equals("ITEM_ID")){
		    ret = new SequenceSet<>();
		    if (!partiallyFilledModel.getReflector().isVoid(partiallyFilledModel.getItemId())){
		        if (partiallyFilledModel.getItem().isAccessibleBy(user)){
		            ret.add(partiallyFilledModel.getItemId());
                }
            }else if (!partiallyFilledModel.getReflector().isVoid(partiallyFilledModel.getCompanyId())){
                if (partiallyFilledModel.getCompany().isAccessibleBy(user)){
                    Expression where = new Expression(ModelReflector.instance(Item.class).getPool(),"COMPANY_ID",Operator.EQ,partiallyFilledModel.getCompanyId());
                    ret = DataSecurityFilter.getIds(DataSecurityFilter.getRecordsAccessible(Item.class, user, where));
                }
            }
        }else if (fieldName.equals("LENGTH_U_O_M_ID") || fieldName.equals("WIDTH_U_O_M_ID") || fieldName.equals("HEIGHT_U_O_M_ID")){
			ModelReflector<UnitOfMeasure> ref = ModelReflector.instance(UnitOfMeasure.class);
			ret = DataSecurityFilter.getIds(DataSecurityFilter.getRecordsAccessible(UnitOfMeasure.class, user, new Expression(ref.getPool(), "MEASURES", Operator.EQ, "Length")));
		}else if (fieldName.equals("WEIGHT_U_O_M_ID")) {
			ModelReflector<UnitOfMeasure> ref = ModelReflector.instance(UnitOfMeasure.class);
			ret = DataSecurityFilter.getIds(DataSecurityFilter.getRecordsAccessible(UnitOfMeasure.class, user, new Expression(ref.getPool(), "MEASURES", Operator.EQ, "Weight")));
		}else {
			ret = super.getAllowedFieldValues(user,partiallyFilledModel,fieldName);
		}

		return  ret;
	}

}
