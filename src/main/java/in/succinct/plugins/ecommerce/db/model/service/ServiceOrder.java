package in.succinct.plugins.ecommerce.db.model.service;

import com.venky.swf.db.annotations.column.COLUMN_DEF;
import com.venky.swf.db.annotations.column.IS_NULLABLE;
import com.venky.swf.db.annotations.column.IS_VIRTUAL;
import com.venky.swf.db.annotations.column.defaulting.StandardDefault;
import com.venky.swf.db.annotations.column.pm.PARTICIPANT;
import com.venky.swf.db.annotations.column.ui.HIDDEN;
import com.venky.swf.db.annotations.column.validations.Enumeration;
import com.venky.swf.db.annotations.model.MENU;
import com.venky.swf.db.model.Model;
import com.venky.swf.db.model.User;
import in.succinct.plugins.ecommerce.db.model.catalog.Service;
import in.succinct.plugins.ecommerce.db.model.participation.Company;
import in.succinct.plugins.ecommerce.db.model.participation.ExtendedEntity;

import java.util.List;


@MENU("Fulfillment")
public interface ServiceOrder extends ExtendedEntity<ServiceOrderAttribute,ServiceOrderAddress,ServiceOrderPrint> , Model {
    public String getReference();
    public void setReference(String reference);

    @PARTICIPANT
    @HIDDEN
    @COLUMN_DEF(StandardDefault.CURRENT_USER)
    public long getUserId();
    public void setUserId(long  id);
    public User getUser();

    @IS_VIRTUAL
    public String getOrderNumber();

    @PARTICIPANT
    public long getCompanyId();
    public void setCompanyId(long id);
    public Company getCompany();


    public static final String FULFILLMENT_STATUS_OPEN = "OPEN";
    public static final String FULFILLMENT_STATUS_APPOINTMENT_PLANNED = "APPOINTMENT_PLANNED";
    public static final String FULFILLMENT_STATUS_SERVICE_ATTEMPTED = "SERVICE_ATTEMPTED";
    public static final String FULFILLMENT_STATUS_COMPLETE = "COMPLETED";
    public static final String FULFILLMENT_STATUS_CANCELLED = "CANCELLED";


    public static enum ServiceStatus {
        OPEN,
        APPOINTMENT_PLANNED,
        SERVICE_ATTEMPTED,
        COMPLETED,
        CANCELLED,
    }

    public static enum CancelReason{
        CANNOT_SERVICE,
        USER_CANCELLATION
    }


    @Enumeration(FULFILLMENT_STATUS_OPEN + "," + FULFILLMENT_STATUS_APPOINTMENT_PLANNED + "," + FULFILLMENT_STATUS_SERVICE_ATTEMPTED + "," + FULFILLMENT_STATUS_COMPLETE +"," + FULFILLMENT_STATUS_CANCELLED)
    @COLUMN_DEF(value=StandardDefault.SOME_VALUE,args= FULFILLMENT_STATUS_OPEN)
    public String getFulfillmentStatus();
    public void setFulfillmentStatus(String status);

    @Enumeration(" ,CANNOT_SERVICE,USER_CANCELLATION")
    @COLUMN_DEF(value = StandardDefault.SOME_VALUE,args = " ")
    public String getCancellationReason();
    public void setCancellationReason(String reason);

    @PARTICIPANT(redundant = true)
    public long getServiceId();
    public void setServiceId(long id);
    public Service getService();

    @IS_NULLABLE
    public Double getSellingPrice();
    public void setSellingPrice(Double price);


    public List<ServiceOrderAddress> getAddresses();

    public List<ServiceOrderAttribute> getAttributes();

    public List<ServiceAppointment> getServiceAttempts();

    public void reject();
    public void cancel();
    public void cancel(String reason, String initiator);


    public static final String CANCELLATION_INITIATOR_COMPANY = "Company";
    public static final String CANCELLATION_INITIATOR_USER = "User";
    @Enumeration(" ,"+CANCELLATION_INITIATOR_COMPANY+","+CANCELLATION_INITIATOR_USER)
    @COLUMN_DEF(value = StandardDefault.SOME_VALUE,args = " ")
    public String getCancellationInitiatedBy();
    public void setCancellationInitiatedBy(String initiatedBy);

    public void complete();

    public List<ServiceOrderPrint> getPrints();

    @COLUMN_DEF(StandardDefault.BOOLEAN_FALSE)
    public boolean isCOD();
    public void setCOD(Boolean valueOf);


    public String getDescription();
    public void setDescription(String description);

}