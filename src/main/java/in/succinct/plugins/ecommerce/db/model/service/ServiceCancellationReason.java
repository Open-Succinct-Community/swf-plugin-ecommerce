package in.succinct.plugins.ecommerce.db.model.service;

import com.venky.swf.db.annotations.column.validations.Enumeration;
import com.venky.swf.db.model.Model;
import in.succinct.plugins.ecommerce.db.model.catalog.Service;

public interface ServiceCancellationReason extends Model {
    public Long getServiceId();
    public void setServiceId(Long id);
    public Service getService();

    @Enumeration(ServiceOrder.CANCELLATION_INITIATOR_USER + "," + ServiceOrder.CANCELLATION_INITIATOR_COMPANY)
    public String getInitiator();
    public void setInitiator(String initiator);


    public boolean isDefaultReason();
    public void setDefaultReason(boolean defaultReason);

    public String getReason();
    public void setReason(String reason);

    public boolean isRemarksRequired();
    public void setRemarksRequired(boolean remarksRequired);
}
