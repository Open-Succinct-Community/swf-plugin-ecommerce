package in.succinct.plugins.ecommerce.extensions.participant;

import in.succinct.plugins.ecommerce.db.model.inventory.Inventory;
import in.succinct.plugins.ecommerce.db.model.inventory.Sku;
import in.succinct.plugins.ecommerce.db.model.participation.Company;
import in.succinct.plugins.ecommerce.db.model.participation.Facility;
import com.venky.core.collections.SequenceSet;
import com.venky.swf.db.extensions.ParticipantExtension;
import com.venky.swf.db.model.User;
import com.venky.swf.db.model.reflection.ModelReflector;
import com.venky.swf.pm.DataSecurityFilter;
import com.venky.swf.sql.Expression;
import com.venky.swf.sql.Operator;

import java.util.List;

public class InventoryParticipantExtension extends ParticipantExtension<Inventory>{
	static  {
		registerExtension(new InventoryParticipantExtension());
	}
	@Override
	protected List<Long> getAllowedFieldValues(User user, Inventory partiallyFilledModel, String fieldName) {
	    List<Long> ret = null;

		if (fieldName.equals("COMPANY_ID")){
		    ret = new SequenceSet<>();
		    if (partiallyFilledModel.getCompanyId()> 0){
		        if (partiallyFilledModel.getCompany().isAccessibleBy(user)){
		            ret.add(partiallyFilledModel.getCompanyId());
                }
            }else {
		        ret = DataSecurityFilter.getIds(DataSecurityFilter.getRecordsAccessible(Company.class, user));
            }
		}else if (fieldName.equals("SKU_ID") ){
		    ret = new SequenceSet<>();
		    if (partiallyFilledModel.getSkuId() > 0) {
		        if (partiallyFilledModel.getSku().isAccessibleBy(user)){
		            ret.add(partiallyFilledModel.getSkuId());
                }
            }else if (partiallyFilledModel.getCompanyId()> 0){
		        if (partiallyFilledModel.getCompany().isAccessibleBy(user)){
                    Expression where = new Expression(ModelReflector.instance(Sku.class).getPool(),"COMPANY_ID",Operator.EQ,partiallyFilledModel.getCompanyId());
                    ret = DataSecurityFilter.getIds(DataSecurityFilter.getRecordsAccessible(Sku.class, user, where));
                }
            }
        }else if (fieldName.equals("FACILITY_ID") ) {
            ret = new SequenceSet<>();
            if (partiallyFilledModel.getFacilityId() > 0) {
                if (partiallyFilledModel.getFacility().isAccessibleBy(user)){
                    ret.add(partiallyFilledModel.getFacilityId());
                }
            }else if (partiallyFilledModel.getCompanyId()> 0){
                if (partiallyFilledModel.getCompany().isAccessibleBy(user)){
                    Expression where = new Expression(ModelReflector.instance(Facility.class).getPool(),"COMPANY_ID",Operator.EQ,partiallyFilledModel.getCompanyId());
                    ret = DataSecurityFilter.getIds(DataSecurityFilter.getRecordsAccessible(Facility.class, user, where));
                }
            }
		}
				
		return ret;
	}

}
