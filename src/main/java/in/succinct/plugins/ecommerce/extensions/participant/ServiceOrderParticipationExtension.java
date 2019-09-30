package in.succinct.plugins.ecommerce.extensions.participant;

import com.venky.core.collections.SequenceSet;
import com.venky.core.util.ObjectUtil;
import com.venky.swf.db.model.User;
import com.venky.swf.plugins.collab.extensions.participation.CompanySpecificParticipantExtension;
import in.succinct.plugins.ecommerce.db.model.service.ServiceOrder;
import in.succinct.plugins.ecommerce.db.model.service.ServiceResource;

import java.util.List;

public class ServiceOrderParticipationExtension extends CompanySpecificParticipantExtension<ServiceOrder> {
    static {
        registerExtension(new ServiceOrderParticipationExtension());
    }

    @Override
    public List<Long> getAllowedFieldValues(User user, ServiceOrder partiallyFilledModel, String fieldName) {
        if (ObjectUtil.equals(fieldName,"COMPANY_ID") || ObjectUtil.equals(fieldName,"USER_ID")){
            return super.getAllowedFieldValues(user, partiallyFilledModel, fieldName);
        }else if (ObjectUtil.equals(fieldName,"SERVICED_BY_ID")){
            List<Long> ret = new SequenceSet<>();
            com.venky.swf.plugins.collab.db.model.user.User u = user.getRawRecord().getAsProxy(com.venky.swf.plugins.collab.db.model.user.User.class);
            if (u.isStaff() && !partiallyFilledModel.getReflector().isVoid(partiallyFilledModel.getServiceId())){
                List<ServiceResource> serviceResources = partiallyFilledModel.getService().getServiceResources();
                for (ServiceResource serviceResource : serviceResources){
                    ret.add(serviceResource.getUserId());
                }
                if (ret.isEmpty()){
                    ret.addAll(u.getCompany().getStaffUserIds());
                }
            }
            return ret;
        }
        return null;
    }
}

