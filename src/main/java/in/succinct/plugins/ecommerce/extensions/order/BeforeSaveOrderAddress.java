package in.succinct.plugins.ecommerce.extensions.order;

import com.venky.swf.plugins.collab.db.model.participants.admin.Address;
import com.venky.swf.plugins.collab.extensions.beforesave.BeforeSaveAddress;
import in.succinct.plugins.ecommerce.db.model.order.OrderAddress;

public class BeforeSaveOrderAddress extends BeforeSaveAddress<OrderAddress> {
    static {
        registerExtension(new BeforeSaveOrderAddress());
    }

    @Override
    public void beforeSave(OrderAddress oAddress) {
        if (oAddress.getFacilityId() != null){
            Address.copy(oAddress.getFacility(),oAddress);
        }
        super.beforeSave(oAddress);
    }
}
