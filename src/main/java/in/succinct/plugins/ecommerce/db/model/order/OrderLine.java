package in.succinct.plugins.ecommerce.db.model.order;


import com.venky.core.util.Bucket;
import com.venky.swf.db.annotations.column.COLUMN_DEF;
import com.venky.swf.db.annotations.column.COLUMN_NAME;
import com.venky.swf.db.annotations.column.IS_NULLABLE;
import com.venky.swf.db.annotations.column.IS_VIRTUAL;
import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.defaulting.StandardDefault;
import com.venky.swf.db.annotations.column.pm.PARTICIPANT;
import com.venky.swf.db.annotations.column.ui.PROTECTION;
import com.venky.swf.db.annotations.column.ui.PROTECTION.Kind;
import com.venky.swf.db.annotations.column.validations.Enumeration;
import com.venky.swf.db.annotations.model.EXPORTABLE;
import com.venky.swf.db.annotations.model.ORDER_BY;
import com.venky.swf.db.model.Model;
import in.succinct.plugins.ecommerce.db.model.inventory.Inventory;
import in.succinct.plugins.ecommerce.db.model.inventory.Sku;
import in.succinct.plugins.ecommerce.db.model.participation.Facility;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@ORDER_BY("ID")
@EXPORTABLE(false)

public interface OrderLine extends Model {
	@UNIQUE_KEY("K1")
	@PARTICIPANT
	public long getOrderId();
	public void setOrderId(long id); 
	public Order getOrder();

    @UNIQUE_KEY("K1")
	@PARTICIPANT(redundant = true)
	public long getSkuId(); 
	public void setSkuId(long id); 
	public Sku getSku();

	@UNIQUE_KEY(value="K1",allowMultipleRecordsWithNull=false)
	public Long getShipFromId();
	public void setShipFromId(Long id);
	public Facility getShipFrom();

	public Timestamp getAcknowledgeBy();
	public void setAcknowledgeBy(Timestamp ts);
	
	public Timestamp getShipBy();
	public void setShipBy(Timestamp ts);


    @IS_NULLABLE
    public Timestamp getDeliveryExpectedNoEarlierThan();
    public void setDeliveryExpectedNoEarlierThan(Timestamp ts);

    @IS_NULLABLE
    public Timestamp getDeliveryExpectedNoLaterThan();
    public void setDeliveryExpectedNoLaterThan(Timestamp ts);


    //@PROTECTION(Kind.NON_EDITABLE)
	public double getOrderedQuantity();
	public void setOrderedQuantity(double quantity);

	@IS_NULLABLE
	public Timestamp getOrderedTs();
	public void setOrderedTs(Timestamp orderedTs);
	
	
	@COLUMN_DEF(StandardDefault.ZERO)
	@PROTECTION(Kind.NON_EDITABLE)
	public double getAcknowledgedQuantity();
	public void setAcknowledgedQuantity(double qty);


	@IS_NULLABLE
	public Timestamp getAcknowledgedTs();
	public void setAcknowledgedTs(Timestamp orderedTs);


	@COLUMN_DEF(StandardDefault.ZERO)
	@PROTECTION(Kind.NON_EDITABLE)
	public double getPackedQuantity();
	public  void setPackedQuantity(double quantity);

	@IS_NULLABLE
	public Timestamp getPackedTs();
	public void setPackedTs(Timestamp orderedTs);


	@COLUMN_DEF(StandardDefault.ZERO)
	@PROTECTION(Kind.NON_EDITABLE)
	public double getManifestedQuantity();
	public void setManifestedQuantity(double quantity);

	@IS_NULLABLE
	public Timestamp getManifestedTs();
	public void setManifestedTs(Timestamp orderedTs);

	@COLUMN_DEF(StandardDefault.ZERO)
	@PROTECTION(Kind.NON_EDITABLE)
	public double getShippedQuantity();
	public void setShippedQuantity(double quantity);

	@IS_NULLABLE
	public Timestamp getShippedTs();
	public void setShippedTs(Timestamp orderedTs);


	@COLUMN_DEF(StandardDefault.ZERO)
	@PROTECTION(Kind.NON_EDITABLE)
	public double getDeliveredQuantity();
	public void setDeliveredQuantity(double quantity);

	@IS_NULLABLE
	public Timestamp getDeliveredTs();
	public void setDeliveredTs(Timestamp orderedTs);


	@COLUMN_DEF(StandardDefault.ZERO)
	@PROTECTION(Kind.NON_EDITABLE)
	public double getCancelledQuantity();
	public void setCancelledQuantity(double quantity);

	@IS_NULLABLE
	public Timestamp getCancelledTs();
	public void setCancelledTs(Timestamp orderedTs);

	@COLUMN_DEF(StandardDefault.ZERO)
	@PROTECTION(Kind.NON_EDITABLE)
	public double getReturnedQuantity();
	public void setReturnedQuantity(double returnedQuantity);

	@IS_NULLABLE
	public Timestamp getReturnedTs();
	public void setReturnedTs(Timestamp returnedTs);


	public double getMaxRetailPrice();
	public void setMaxRetailPrice(double mrp); 
	
	@COLUMN_DEF(StandardDefault.ZERO)
	@COLUMN_NAME("DISCOUNT")
	public double getDiscountPercentage();
	public void setDiscountPercentage(double discount);
	
	public double getSellingPrice();
	public void setSellingPrice(double sellingPrice);
	
	public String getShipTogetherCode();
	public  void  setShipTogetherCode(String shipTogetherCode);
	
	
	public static final String CANCELLATION_REASON_OUT_OF_STOCK = "OUT_OF_STOCK";
	public static final String CANCELLATION_REASON_WRONG_PRODUCT = "WRONG_PRODUCT";
	public static final String CANCELLATION_REASON_WRONG_PRICE = "WRONG_PRICE";
    public static final String CANCELLATION_REASON_PARTIAL_CANCEL_NOT_SUPPORTED = "PARTIAL_CANCEL_NOT_SUPPORTED";
    public static final String CANCELLATION_REASON_PARTIAL_LINE_CANCEL_NOT_SUPPORTED = "PARTIAL_LINE_CANCEL_NOT_SUPPORTED";

    public static final String CANCELLATION_INITIATOR_COMPANY = "Company";
    public static final String CANCELLATION_INITIATOR_USER = "User";
    public static final String CANCELLATION_INITIATOR_MARKET_PLACE = "MarketPlaceRules";



    @Enumeration(" ," +CANCELLATION_INITIATOR_COMPANY +"," + CANCELLATION_INITIATOR_USER + "," + CANCELLATION_INITIATOR_MARKET_PLACE)
    public String getCancellationInitiator();
    public void setCancellationInitiator(String initiatedBy);

    public String getCancellationReason();
	public void setCancellationReason(String reason);
	
	
	@COLUMN_DEF(StandardDefault.BOOLEAN_FALSE)
	public boolean isUnitNumberCaptureRequired(); 
	public void setUnitNumberCaptureRequired(boolean unitNumberRequired);
	
	public String getUnitNumberTypeRequired();
	public void setUnitNumberTypeRequired(String unitNumberType);
	
	public List<OrderLineAttribute> getAttributes();
	
	public Map<String,OrderLineAttribute> getAttributeMap();
	public void saveAttributeMap(Map<String,OrderLineAttribute> map);
	public OrderLineAttribute getAttribute(String name); 
	
	public void pack(double quantity);
	public void pack(String unitNumber);
	
	
	@IS_VIRTUAL
	public double getToShipQuantity();

	@IS_VIRTUAL
	public double getToPackQuantity();
	
	@IS_VIRTUAL
	public double getToAcknowledgeQuantity();
	
	@IS_VIRTUAL
	public double getToManifestQuantity();

	@UNIQUE_KEY("K1,ChannelOrderLine")
	public String getChannelOrderLineRef(); 
	public void setChannelOrderLineRef(String orderItemId);
	

	public List<OrderLineUnitNumber> getUnitNumbers();
	@IS_VIRTUAL
	public Inventory getInventory(boolean lock) ;

	@IS_VIRTUAL
	public Inventory getInventory(boolean lock,long skuId);

	public void ship();
	public void deliver();
    public void ship(double quantity);
    public void deliver(double quantity);


	@IS_NULLABLE
	public String getShippingCompany();
	public void setShippingCompany(String company);
	
	@IS_NULLABLE
	public String getTrackingNumber(); 
	public void setTrackingNumber(String trackingNumber);
	

	@COLUMN_DEF(StandardDefault.ZERO)
	public double getShippingCharges();
	public void setShippingCharges(double charges);

	
	public void reject(String reasonCode);
	public void reject(String reasonCode,double quantity);

	public void cancel(String reasonCode);
	public void cancel(String reasonCode, double quantity);

    public void cancel(String reason, String initiator);
    public void cancel(String reason, String initiator, double quantity);

    public void acknowledge();
    public void acknowledge(Map<Long,Map<Long,Bucket>> skuATP, Bucket acknowledgedLineCount, Bucket rejectedLineCount , boolean cancelOnShortage);

    public void manifest();

    @COLUMN_DEF(StandardDefault.BOOLEAN_FALSE)
	public boolean isShortage();
	public void setShortage(boolean b);


	public Double getIGst();
	public void setIGst(Double gst);

	public Double getCGst();
	public void setCGst(Double gst);

	public Double getSGst();
	public void setSGst(Double gst);

	public Double getPrice();
	public void setPrice(Double price);

	@IS_VIRTUAL
	public String getHsn();

    void backorder();
}
