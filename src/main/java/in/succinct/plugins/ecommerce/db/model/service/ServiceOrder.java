package in.succinct.plugins.ecommerce.db.model.service;

import com.venky.swf.db.annotations.column.COLUMN_DEF;
import com.venky.swf.db.annotations.column.IS_NULLABLE;
import com.venky.swf.db.annotations.column.IS_VIRTUAL;
import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.defaulting.StandardDefault;
import com.venky.swf.db.annotations.column.indexing.Index;
import com.venky.swf.db.annotations.column.pm.PARTICIPANT;
import com.venky.swf.db.annotations.column.ui.HIDDEN;
import com.venky.swf.db.annotations.column.ui.PROTECTION;
import com.venky.swf.db.annotations.column.ui.PROTECTION.Kind;
import com.venky.swf.db.annotations.column.validations.Enumeration;
import com.venky.swf.db.annotations.model.EXPORTABLE;
import com.venky.swf.db.annotations.model.MENU;
import com.venky.swf.db.model.Model;
import com.venky.swf.plugins.collab.db.model.user.User;
import com.venky.swf.plugins.collab.db.model.CompanySpecific;
import in.succinct.plugins.ecommerce.db.model.catalog.Service;
import in.succinct.plugins.ecommerce.db.model.participation.Company;
import in.succinct.plugins.ecommerce.db.model.participation.ExtendedEntity;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;


@MENU("Fulfillment")
@EXPORTABLE(false)
public interface ServiceOrder extends ExtendedEntity<ServiceOrderAttribute,ServiceOrderAddress,ServiceOrderPrint> , Model , CompanySpecific {
    @UNIQUE_KEY
    public long getId();

    public String getReference();
    public void setReference(String reference);

    @PARTICIPANT("USER")
    @COLUMN_DEF(StandardDefault.CURRENT_USER)
    @IS_NULLABLE(false)
    @Index
    public Long getUserId();
    public void setUserId(Long  id);
    public User getUser();

    @IS_NULLABLE
    @Index
    @PARTICIPANT(redundant = true)
    public Long getServicedById();
    public void setServicedById(Long  id);
    public User getServicedBy();

    @IS_VIRTUAL
    public String getOrderNumber();


    public static final String FULFILLMENT_STATUS_OPEN = "OPEN";
    public static final String FULFILLMENT_STATUS_COMPLETE = "COMPLETED";
    public static final String FULFILLMENT_STATUS_CANCELLED = "CANCELLED";

    @IS_VIRTUAL
    @HIDDEN
    public boolean isOpen();


    public static enum ServiceStatus {
        OPEN,
        COMPLETED,
        CANCELLED,
    }


    @Enumeration(FULFILLMENT_STATUS_OPEN + "," + FULFILLMENT_STATUS_COMPLETE +"," + FULFILLMENT_STATUS_CANCELLED)
    @COLUMN_DEF(value=StandardDefault.SOME_VALUE,args= FULFILLMENT_STATUS_OPEN)
    @Index
    public String getFulfillmentStatus();
    public void setFulfillmentStatus(String status);

    public Long getCancellationReasonId();
    public void setCancellationReasonId(Long id);
    public ServiceCancellationReason getCancellationReason();

    public String getRemarks();
    public void setRemarks(String  remarks);

    @PARTICIPANT(redundant = true)
    @Index
    public long getServiceId();
    public void setServiceId(long id);
    public Service getService();


    public List<ServiceOrderAddress> getAddresses();

    public List<ServiceOrderAttribute> getAttributes();

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


    @IS_NULLABLE(true)
    @Index
    public Timestamp getEarliestBy();
    public void setEarliestBy(Timestamp start);

    @IS_NULLABLE(true)
    @Index
    public Timestamp getLatestBy();
    public void setLatestBy(Timestamp end);


}
