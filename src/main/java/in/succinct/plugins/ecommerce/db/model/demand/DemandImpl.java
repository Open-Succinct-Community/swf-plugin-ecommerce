package in.succinct.plugins.ecommerce.db.model.demand;

import com.venky.swf.db.table.ModelImpl;

public class DemandImpl extends ModelImpl<Demand> {
    public DemandImpl(){
        super();
    }
    public DemandImpl(Demand demand){
        super(demand);
    }
    public long getSkuId(){
        return getProxy().getInventory().getSkuId();
    }
    public long getFacilityId(){
        return getProxy().getInventory().getFacilityId();
    }

}
