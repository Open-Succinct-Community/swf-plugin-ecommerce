package in.succinct.plugins.ecommerce.extensions.participant;

import com.venky.swf.plugins.collab.extensions.participation.CompanySpecificParticipantExtension;
import in.succinct.plugins.ecommerce.db.model.service.ServiceOrder;

public class ServiceOrderParticipationExtension extends CompanySpecificParticipantExtension<ServiceOrder> {
    static {
        registerExtension(new ServiceOrderParticipationExtension());
    }

}

