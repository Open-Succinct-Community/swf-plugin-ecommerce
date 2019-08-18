package in.succinct.plugins.ecommerce.extensions.order;

import com.venky.core.util.ObjectUtil;
import com.venky.swf.plugins.collab.db.model.participants.admin.Address;
import com.venky.swf.plugins.collab.db.model.user.User;
import com.venky.swf.plugins.collab.db.model.user.UserFacility;
import com.venky.swf.plugins.collab.extensions.beforesave.BeforeSaveAddress;
import in.succinct.plugins.ecommerce.db.model.order.OrderAddress;
import org.apache.commons.math3.analysis.function.Add;

import java.util.StringTokenizer;

public class BeforeSaveOrderAddress extends BeforeSaveAddress<OrderAddress> {
    static {
        registerExtension(new BeforeSaveOrderAddress());
    }

    @Override
    public void beforeSave(OrderAddress oAddress) {
        if (oAddress.getFacilityId() != null && !Address.isAddressChanged(oAddress) && Address.isAddressVoid(oAddress)){
            User user = null;
            for (UserFacility fu : oAddress.getFacility().getFacilityUsers()){
                if (fu.getUser().isStaff()){
                    user = fu.getUser();
                    break;
                }
            }
            Address.copy(oAddress.getFacility(),oAddress);
            if (oAddress.getReflector().isVoid(oAddress.getFirstName()) && user != null){
                oAddress.setFirstName(user.getFirstName());
            }
            if (oAddress.getReflector().isVoid(oAddress.getLastName()) && user != null){
                oAddress.setLastName(user.getLastName());
            }
            if (oAddress.getReflector().isVoid(oAddress.getEmail()) && user != null){
                oAddress.setEmail(user.getEmail());
            }
            if (oAddress.getReflector().isVoid(oAddress.getPhoneNumber()) && user != null){
                oAddress.setPhoneNumber(user.getPhoneNumber());
            }
            if (oAddress.getReflector().isVoid(oAddress.getAlternatePhoneNumber()) && user != null){
                oAddress.setAlternatePhoneNumber(user.getAlternatePhoneNumber());
            }
        }
        super.beforeSave(oAddress);
    }
}
