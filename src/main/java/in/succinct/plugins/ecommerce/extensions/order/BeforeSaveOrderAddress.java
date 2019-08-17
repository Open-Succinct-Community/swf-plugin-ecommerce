package in.succinct.plugins.ecommerce.extensions.order;

import com.venky.core.util.ObjectUtil;
import com.venky.swf.plugins.collab.db.model.participants.admin.Address;
import com.venky.swf.plugins.collab.db.model.user.User;
import com.venky.swf.plugins.collab.db.model.user.UserFacility;
import com.venky.swf.plugins.collab.extensions.beforesave.BeforeSaveAddress;
import in.succinct.plugins.ecommerce.db.model.order.OrderAddress;
import org.apache.commons.math3.analysis.function.Add;

public class BeforeSaveOrderAddress extends BeforeSaveAddress<OrderAddress> {
    static {
        registerExtension(new BeforeSaveOrderAddress());
    }

    @Override
    public void beforeSave(OrderAddress oAddress) {
        if (oAddress.getFacilityId() != null){
            for (UserFacility fu : oAddress.getFacility().getFacilityUsers()){
                if (fu.getUser().isStaff()){
                    User user = fu.getUser();
                    Address.copy(user,oAddress);
                    break;
                }
            }
            Address.copy(oAddress.getFacility(),oAddress);
        }
        super.beforeSave(oAddress);
    }
}
