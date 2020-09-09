package in.succinct.plugins.ecommerce.db.model.inventory;

import com.venky.swf.db.table.ModelImpl;

import java.util.ArrayList;
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

    public Long getActiveDiscountPlanId(){
        SkuDiscountPlan active = getActiveDiscountPlan();
        if (active != null){
            return active.getId();
        }
        return null;
    }
    public void setActiveDiscountPlanId(Long id){

    }

    public List<ActiveSkuDiscountPlan> getActiveDiscountPlans(){
        List<SkuDiscountPlan> plans = getProxy().getDiscountPlans();
        List<ActiveSkuDiscountPlan> activeSkuDiscountPlans = new ArrayList<>();
        if (!plans.isEmpty()){
            activeSkuDiscountPlans.add(plans.get(0).getRawRecord().getAsProxy(ActiveSkuDiscountPlan.class));
        }
        return activeSkuDiscountPlans;
    }
}
