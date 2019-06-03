package in.succinct.plugins.ecommerce.db.model.service;

import com.venky.swf.db.annotations.column.COLUMN_DEF;
import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.defaulting.StandardDefault;
import com.venky.swf.db.annotations.column.validations.Enumeration;
import com.venky.swf.db.annotations.model.HAS_DESCRIPTION_FIELD;
import com.venky.swf.db.annotations.model.MENU;
import com.venky.swf.db.annotations.model.ORDER_BY;
import com.venky.swf.db.model.Model;
import in.succinct.plugins.ecommerce.db.model.catalog.Service;

@HAS_DESCRIPTION_FIELD("REASON")
@ORDER_BY("SEQUENCE,ID")
@MENU("Catalog")
public interface ServiceCancellationReason extends Model {

    @COLUMN_DEF(StandardDefault.ZERO)
    public int getSequence();
    public void setSequence(int sequence);

    @UNIQUE_KEY
    public Long getServiceId();
    public void setServiceId(Long id);
    public Service getService();

    @Enumeration(ServiceOrder.CANCELLATION_INITIATOR_USER + "," + ServiceOrder.CANCELLATION_INITIATOR_COMPANY)
    public String getInitiator();
    public void setInitiator(String initiator);


    public boolean isDefaultReason();
    public void setDefaultReason(boolean defaultReason);

    @UNIQUE_KEY
    public String getReason();
    public void setReason(String reason);

    public boolean isRemarksRequired();
    public void setRemarksRequired(boolean remarksRequired);
}
