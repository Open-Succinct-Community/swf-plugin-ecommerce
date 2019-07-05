package in.succinct.plugins.ecommerce.db.model.participation;

import com.venky.swf.db.annotations.column.COLUMN_DEF;
import com.venky.swf.db.annotations.column.IS_NULLABLE;
import com.venky.swf.db.annotations.column.IS_VIRTUAL;
import com.venky.swf.db.annotations.column.PASSWORD;
import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.defaulting.StandardDefault;
import com.venky.swf.db.annotations.column.ui.HIDDEN;
import com.venky.swf.db.annotations.column.validations.Enumeration;
import com.venky.swf.db.model.Model;
import com.venky.swf.plugins.collab.db.model.CompanySpecific;
import in.succinct.plugins.ecommerce.db.model.order.Manifest;
import in.succinct.plugins.ecommerce.db.model.order.Order;

public interface PreferredCarrier extends CompanySpecific, Model {
    @HIDDEN
    public Long getCompanyId();

    @HIDDEN
    @UNIQUE_KEY("K1,K2")
    public long getFacilityId();
    public void setFacilityId(long id);
    public Facility getFacility();


    @Enumeration("FedEx")
    @UNIQUE_KEY("K1,K2")
    public String getName();
    public void setName(String name);

    public String getAccountNumber();
    public void setAccountNumber(String accountNumber);

    public String getMeterNumber();
    public void setMeterNumber(String meterNumber);

    public String getApiKey();
    public void setApiKey(String key);

    @PASSWORD
    public String getPassword();
    public void setPassword(String password);

    public String getIntegrationEndPoint();
    public void setIntegrationEndPoint(String endPoint);


    @COLUMN_DEF(StandardDefault.BOOLEAN_FALSE)
    public boolean isTaxesPaidBySender();
    public void setTaxesPaidBySender(boolean sender);

    @IS_NULLABLE
    public Double getMaxShippingCharges();
    public void setMaxShippingCharges(Double maxShippingCharges);

    @IS_VIRTUAL
    public Double getEstimatedShippingCharges(Order order);

}
