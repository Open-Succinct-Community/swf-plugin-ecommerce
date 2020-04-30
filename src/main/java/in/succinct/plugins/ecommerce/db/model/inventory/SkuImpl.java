package in.succinct.plugins.ecommerce.db.model.inventory;

import com.venky.swf.db.table.ModelImpl;

import java.util.List;

public class SkuImpl extends ModelImpl<Sku> {
    public SkuImpl(Sku sku){
        super(sku);
    }
    public SkuImpl(){
        super();
    }
    public SkuDiscountPlan getActiveDiscountPlan(){
        List<SkuDiscountPlan> plans = getProxy().getDiscountPlans();
        //The default order by is @ORDER_BY("SKU_ID , EFFECTIVE_FROM DESC")
        SkuDiscountPlan active = null;
        if (plans.size() > 0){
            active = plans.get(0);
        }
        return active;
    }
}
