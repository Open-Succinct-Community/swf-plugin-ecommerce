package in.succinct.plugins.ecommerce.db.model.participation;

import com.venky.swf.db.annotations.column.COLUMN_DEF;
import com.venky.swf.db.annotations.column.IS_NULLABLE;
import com.venky.swf.db.annotations.column.IS_VIRTUAL;
import com.venky.swf.db.annotations.column.PASSWORD;
import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.defaulting.StandardDefault;
import com.venky.swf.db.annotations.column.ui.HIDDEN;
import com.venky.swf.db.annotations.column.ui.PROTECTION;
import com.venky.swf.db.annotations.column.ui.PROTECTION.Kind;
import com.venky.swf.db.annotations.column.validations.Enumeration;
import com.venky.swf.db.annotations.model.HAS_DESCRIPTION_FIELD;
import com.venky.swf.db.model.Model;
import com.venky.swf.plugins.collab.db.model.CompanySpecific;
import in.succinct.plugins.ecommerce.db.model.order.Manifest;
import in.succinct.plugins.ecommerce.db.model.order.Order;

public interface PreferredCarrier extends CompanySpecific, Model {
    @HIDDEN
    public Long getCompanyId();

    @PROTECTION(Kind.NON_EDITABLE)
    @UNIQUE_KEY("K1,K2")
    public long getFacilityId();
    public void setFacilityId(long id);
    public Facility getFacility();

    public static final String FEDEX = "FedEx";
    public static final String INDIA_POST = "IndiaPost";
    public static final String ECOMM_EXPRESS = "ECommExpress";
    public static final String MARKET_PLACE = "MarketPlace";
    public static final String HAND_DELIVERY = "HandDelivery";

    public static final String PREFERRED_CARRIER_NAMES="," + FEDEX +","+ INDIA_POST +"," + ECOMM_EXPRESS +"," + MARKET_PLACE +"," + HAND_DELIVERY;

    @Enumeration(PREFERRED_CARRIER_NAMES)
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
