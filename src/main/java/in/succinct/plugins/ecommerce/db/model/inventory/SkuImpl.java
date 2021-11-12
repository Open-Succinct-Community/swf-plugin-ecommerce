package in.succinct.plugins.ecommerce.db.model.inventory;

import com.venky.core.math.DoubleHolder;
import com.venky.core.util.ObjectHolder;
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


    private ObjectHolder<SkuDiscountPlan> activeDiscountPlan = null;
    public SkuDiscountPlan getActiveDiscountPlan(){
        if (activeDiscountPlan == null){
            activeDiscountPlan = new ObjectHolder<>(null);
            List<SkuDiscountPlan> plans = getProxy().getDiscountPlans();
            //The default order by is @ORDER_BY("SKU_ID , EFFECTIVE_FROM DESC")
            if (plans.size() > 0){
                activeDiscountPlan.set(plans.get(0));
            }
        }
        return activeDiscountPlan.get();
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

    public double getGeneralDiscountPct(){
        Sku sku = getProxy();
        SkuDiscountPlan plan = getActiveDiscountPlan();
        if (plan !=null){
            return plan.getGeneralDiscountPct();
        }
        return 0.0D;
    }

    public double getSellingPrice(){
        Sku sku = getProxy();
        SkuDiscountPlan plan = getActiveDiscountPlan();
        if (plan != null){
            return new DoubleHolder(sku.getMaxRetailPrice() * (1.0 - plan.getGeneralDiscountPct()/100.0),2).getHeldDouble().doubleValue();
        }
        return sku.getMaxRetailPrice();
    }
}
