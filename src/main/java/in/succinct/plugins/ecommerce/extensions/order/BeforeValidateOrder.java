package in.succinct.plugins.ecommerce.extensions.order;

import com.venky.swf.db.extensions.BeforeModelValidateExtension;
import com.venky.swf.sql.Conjunction;
import com.venky.swf.sql.Expression;
import com.venky.swf.sql.Operator;
import com.venky.swf.sql.Select;
import in.succinct.plugins.ecommerce.db.model.order.Order;
import in.succinct.plugins.ecommerce.db.model.participation.PreferredCarrier;

import java.util.List;

public class BeforeValidateOrder extends BeforeModelValidateExtension<Order> {
    static {
        registerExtension(new BeforeValidateOrder());
    }
    @Override
    public void beforeValidate(Order model) {
        if (model.getRawRecord().isFieldDirty("PREFERRED_CARRIER_NAME") && !model.getReflector().isVoid(model.getPreferredCarrierName())){
            Expression where = new Expression(model.getReflector().getPool(), Conjunction.AND);
            where.add(new Expression(model.getReflector().getPool(),"PREFERRED_CARRIER_NAME", Operator.EQ,model.getPreferredCarrierName()));
            where.add(new Expression(model.getReflector().getPool(),"COMPANY_ID", Operator.EQ,model.getCompanyId()));

            List<PreferredCarrier> carrierList = new Select().from(PreferredCarrier.class).where(where).execute(1);
            if (carrierList.isEmpty()){
                throw new RuntimeException(model.getCompany().getName()  + " does not have a relation ship with " + model.getPreferredCarrierName());
            }
        }
        if (model.getPreferredCarrierId() != null){
            model.setPreferredCarrierName(model.getPreferredCarrier().getName());
        }
    }
}
