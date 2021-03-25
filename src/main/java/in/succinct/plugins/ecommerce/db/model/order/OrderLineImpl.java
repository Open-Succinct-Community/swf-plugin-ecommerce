package in.succinct.plugins.ecommerce.db.model.order;


import com.venky.cache.Cache;
import com.venky.core.date.DateUtils;
import com.venky.core.util.ObjectUtil;
import com.venky.digest.Encryptor;
import com.venky.geo.GeoCoordinate;
import com.venky.swf.db.annotations.column.IS_VIRTUAL;
import com.venky.swf.plugins.calendar.db.model.WorkCalendar;
import com.venky.swf.plugins.calendar.db.model.WorkSlot;
import in.succinct.plugins.ecommerce.db.model.apis.Cancel;
import in.succinct.plugins.ecommerce.db.model.apis.Pack.PackValidationException;
import in.succinct.plugins.ecommerce.db.model.attributes.AssetCode;
import in.succinct.plugins.ecommerce.db.model.attributes.AssetCodeAttribute;
import in.succinct.plugins.ecommerce.db.model.catalog.Item;
import in.succinct.plugins.ecommerce.db.model.catalog.ItemCategory;
import in.succinct.plugins.ecommerce.db.model.inventory.Inventory;
import in.succinct.plugins.ecommerce.db.model.inventory.InventoryCalculator;
import com.venky.cache.UnboundedCache;
import com.venky.core.util.Bucket;
import com.venky.swf.db.Database;
import com.venky.swf.db.table.ModelImpl;
import com.venky.swf.sql.Conjunction;
import com.venky.swf.sql.Expression;
import com.venky.swf.sql.Operator;
import com.venky.swf.sql.Select;
import in.succinct.plugins.ecommerce.db.model.inventory.InventoryCalculator.ATP;
import in.succinct.plugins.ecommerce.db.model.inventory.Sku;
import in.succinct.plugins.ecommerce.db.model.participation.Company;
import in.succinct.plugins.ecommerce.db.model.participation.Facility;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class OrderLineImpl  extends ModelImpl<OrderLine>{
	public OrderLineImpl() {
		super();
	}

	public OrderLineImpl(OrderLine proxy) {
		super(proxy);
	}
	
	
	private Map<String,OrderLineAttribute> map = null;
	public synchronized Map<String,OrderLineAttribute> getAttributeMap() {
		if (map == null) {
			map = new  UnboundedCache<String,OrderLineAttribute>() {

				private static final long serialVersionUID = 1L;

				@Override
				protected OrderLineAttribute getValue(String name) {
					OrderLineAttribute attr =  Database.getTable(OrderLineAttribute.class).newRecord();
					attr.setName(name);
					if(!getProxy().getRawRecord().isNewRecord()){
                        attr.setOrderLineId(getProxy().getId());
                    }
					return attr;
				}
			};
			getProxy().getAttributes().forEach(a->{
				map.put(a.getName(),a);
			});
		}
		return map;
	}
	public void saveAttributeMap(Map<String,OrderLineAttribute> map) {
		map.keySet().stream().sorted().forEach(a->{
			OrderLineAttribute oa = map.get(a);
			oa.setOrderLineId(getProxy().getId());
			oa.save();
		});
	}
	public OrderLineAttribute getAttribute(String name) {
		Map<String,OrderLineAttribute> map = getProxy().getAttributeMap();
		if (map.containsKey(name)) {
			return map.get(name);
		}
		return null;
	}

	public void pack(){
	    pack(getToPackQuantity());
    }
	public void pack(double quantity) {
		OrderLine orderLine = getProxy();
		double quantityAcknowledged = orderLine.getAcknowledgedQuantity();
		if (quantityAcknowledged < quantity){
		    orderLine.acknowledge();
        }

		double remainingQuantityToPack = orderLine.getToPackQuantity();
		if (quantity > remainingQuantityToPack) {
			throw new PackValidationException("Quantity " + quantity + " Exceeds quantity remaining to be packed" +  remainingQuantityToPack);
		}
		orderLine.setPackedQuantity(orderLine.getPackedQuantity() + quantity);
		orderLine.save();
	}
	public void pack(String unitNumber) {
		OrderLine ol = getProxy(); 
		OrderLineUnitNumber scanned = Database.getTable(OrderLineUnitNumber.class).newRecord(); 
		scanned.setOrderLineId(ol.getId());
		scanned.setUnitNumberType(ol.getUnitNumberTypeRequired());
		scanned.setUnitNumber(unitNumber);
		scanned.save();
		pack(1);
	}

    public double getRemainingCancellableQuantity(){
        return getProxy().getOrderedQuantity() - getProxy().getCancelledQuantity() - getProxy().getReturnedQuantity();
    }

    public double getToShipQuantity() {
		OrderLine ol = getProxy(); 
		return Math.max(0, getRemainingCancellableQuantity()- ol.getShippedQuantity());
	}
    public double getToDeliverQuantity() {
        OrderLine ol = getProxy();
        return Math.max(0, ol.getShippedQuantity() - Math.max(ol.getReturnedQuantity(),ol.getDeliveredQuantity()));
    }

    public double getToAcknowledgeQuantity() {
        OrderLine ol = getProxy();
        return Math.max(0,  getToShipQuantity() - ol.getAcknowledgedQuantity());
    }

    public double getToPackQuantity() {
		OrderLine ol = getProxy(); 
		return Math.max(0, getToShipQuantity() - ol.getPackedQuantity());
	}

	public double getToManifestQuantity() {
		OrderLine ol = getProxy(); 
		return Math.max(0, getToShipQuantity() - ol.getManifestedQuantity());
	}

	
	public void ship() {
		ship(getProxy().getToShipQuantity());
	}
	public void ship(double quantity){
        OrderLine ol = getProxy();
        double remainingShippableQuantity = ol.getToShipQuantity();
        if (quantity > remainingShippableQuantity ){
            throw new IllegalArgumentException("Quantity " + quantity + " Exceeds quantity remaining to be shipped" +  remainingShippableQuantity);
        }
        if (ol.getPackedQuantity() < quantity){
            ol.pack(quantity - ol.getPackedQuantity());
        }
        ol.setShippedQuantity(ol.getShippedQuantity() + quantity);
        ol.save();
    }
    public void deliver() {
	    OrderLine ol = getProxy();
        Item item = ol.getSku().getItem();
	    if (item.getAssetCodeId() != null && item.getAssetCode().isSac()){
	        ol.pack();
	        ol.ship();
        }else {
	        ol.ship();
        }
	    deliver(ol.getToDeliverQuantity());
    }
    public void deliver(double quantity){
        OrderLine ol = getProxy();

        double remainingDeliverableQuanity = ol.getToDeliverQuantity();

        if (quantity > remainingDeliverableQuanity ){
            throw new IllegalArgumentException("Quantity " + quantity + " Exceeds quantity remaining to be delivered" +  remainingDeliverableQuanity);
        }
        ol.setDeliveredQuantity(ol.getDeliveredQuantity() + quantity);
        ol.save();
    }
	public void reject(String reason){
	    cancel(reason,OrderLine.CANCELLATION_INITIATOR_COMPANY, getRemainingCancellableQuantity());
    }
    public void cancel(String reason) {
        cancel(reason,OrderLine.CANCELLATION_INITIATOR_USER, getRemainingCancellableQuantity());
    }

    public void reject(String reason,double quantity){
        cancel(reason,OrderLine.CANCELLATION_INITIATOR_COMPANY,quantity);
    }

    public void cancel(String reason, double quantity){
        cancel(reason,OrderLine.CANCELLATION_INITIATOR_USER,quantity);
    }

    public void cancel(String reason, String initiator) {
        cancel(reason,initiator, getRemainingCancellableQuantity());
    }

    public void cancel(String reason, String initiator, double quantity){
        OrderLine orderLine = getProxy();
        double quantityCancellable = orderLine.getToShipQuantity();
        double quantityReturnable = orderLine.getShippedQuantity() - orderLine.getReturnedQuantity();

        if (quantity > quantityCancellable + quantityReturnable) {
            throw new Cancel.OrderCancellationException("Cannot cancel more than " + quantityCancellable + quantityReturnable);
        }else {
            double quantityToCancel = quantity;
            boolean backOrder = ObjectUtil.isVoid(initiator) && ObjectUtil.isVoid(reason);
            if (quantityToCancel > 0) {
                if (backOrder) {
                    if (quantityCancellable > 0 ){
                        double quantityBackOrdered = Collections.min(Arrays.asList(quantityToCancel,quantityCancellable,orderLine.getAcknowledgedQuantity()));
                        orderLine.setManifestedQuantity(Math.max(0,orderLine.getManifestedQuantity() - quantityBackOrdered));
                        orderLine.setPackedQuantity(Math.max(0,orderLine.getPackedQuantity() - quantityBackOrdered));
                        orderLine.setAcknowledgedQuantity(orderLine.getAcknowledgedQuantity() - quantityBackOrdered);
                    }
                }else {
                    if (quantityCancellable > 0 ){
                        double quantityCancelled = Math.min(quantityToCancel,quantityCancellable);
                        orderLine.setCancelledQuantity(orderLine.getCancelledQuantity()+quantityCancelled);
                        quantityToCancel -= quantityCancelled;
                    }
                    orderLine.setReturnedQuantity(orderLine.getReturnedQuantity() + quantityToCancel);
                    if (orderLine.getReflector().isVoid(orderLine.getCancellationReason())){
                        orderLine.setCancellationReason(reason);
                    }
                    if (orderLine.getReflector().isVoid(orderLine.getCancellationInitiator())) {
                        orderLine.setCancellationInitiator(initiator);
                    }
                }
            }

        }
        orderLine.save();
    }


	public Inventory getInventory(boolean lock) {
        OrderLine line = getProxy();
        if (line.getInventoryId() != null){
            if (!lock){
                return line.getInventory();
            }else{
                return Database.getTable(Inventory.class).lock(line.getInventoryId());
            }
        }
	    return getInventory(lock,line.getSkuId());
    }
    public Inventory getInventory(boolean lock,long skuId) {
	    List<Inventory> inventories = getInventories(lock,skuId);
	    if (inventories.isEmpty()){
	        return null;
        }else{
	        return inventories.get(0);
        }
    }
    public List<Inventory> getInventories(boolean lock,long skuId) {
		OrderLine line = getProxy();
		Select s = new Select(lock).from(Inventory.class);
		Expression w = new Expression(s.getPool(),Conjunction.AND);
		w.add(new Expression(s.getPool(), "FACILITY_ID",Operator.EQ, line.getShipFromId()));
		w.add(new Expression(s.getPool(), "SKU_ID", Operator.EQ, skuId));
		w.add(new Expression(s.getPool(), "QUANTITY" , Operator.GE, line.getToShipQuantity()));


        s.where(w);
        for (OrderLineItemAttributeValue orderLineItemAttributeValue : line.getOrderLineItemAttributeValues()){
            s.add(" and exists (select 1 from inventory_attributes where inventory_id  = inventories.id and attribute_value_id  = " + orderLineItemAttributeValue.getAttributeValueId() +")");
        }

		List<Inventory> inventories = s.orderBy("FACILITY_ID","SKU_ID","ID").execute();

		return inventories;
	}
	public void backorder(){
        cancel("","");
    }
    public void acknowledge(){
        Map<Long, List<ATP>> skuATP = new Cache<Long, List<ATP>>() {
            @Override
            protected List<ATP> getValue(Long skuId) {
                return new ArrayList<>();
            }
        };
        Bucket acknowlededCounter = new Bucket();
        Bucket rejectCounter= new Bucket();
        acknowledge(skuATP,acknowlededCounter,rejectCounter,false);
    }
	public void acknowledge(Map<Long,List<ATP>> skuATP, Bucket acknowledgedLineCounter, Bucket rejectLineCounter, boolean cancelOnShortage ){
        OrderLine ol = getProxy();
        Order order = ol.getOrder();
        if (ol.getToAcknowledgeQuantity() >  0 ) { //Not Ack, Shipped or cancelled,

            {
                if (!skuATP.containsKey(ol.getSkuId())) {
                    InventoryCalculator invCalculator = new InventoryCalculator(ol);
                    for (InventoryCalculator.ATP atp : invCalculator.getInventory()) {
                        skuATP.get(atp.getInventory().getSkuId()).add(atp);
                    }

                    List<ATP> atpList = skuATP.get(ol.getSkuId());
                    if (!atpList.isEmpty()){
                        Set<Long> shipNodeIds = new HashSet<>();
                        for (ATP atp : atpList) {
                            shipNodeIds.add(atp.getInventory().getFacilityId());
                        }

                        Select select = new Select().from(Facility.class);
                        List<Facility> facilities = select.where(new Expression(select.getPool(), "ID", Operator.IN, shipNodeIds.toArray())).execute();
                        Map<Long, Facility> facilityMap = new HashMap<>();
                        facilities.forEach(f -> {
                            facilityMap.put(f.getId(), f);
                        });

                        List<OrderAddress> shipToAddresses = order.getAddresses().stream().filter(a -> OrderAddress.ADDRESS_TYPE_SHIP_TO.equals(a.getAddressType())).collect(Collectors.toList());
                        OrderAddress shipToAddress = shipToAddresses.isEmpty() ? null : shipToAddresses.get(0);
                        if (shipToAddress == null) {
                            throw new RuntimeException("Don't know where to ship the order " + order.getId());
                        }
                        atpList.sort(new Comparator<ATP>() {
                            @Override
                            public int compare(ATP o1, ATP o2) {
                                Facility f1 =  facilityMap.get(o1.getInventory().getFacilityId());
                                Facility f2 =  facilityMap.get(o2.getInventory().getFacilityId());
                                double d1 = new GeoCoordinate(f1).distanceTo(new GeoCoordinate(shipToAddress));
                                double d2 = new GeoCoordinate(f2).distanceTo(new GeoCoordinate(shipToAddress));
                                int ret = (int) (d1 - d2);
                                if (ret == 0){
                                    ret = (int)(o1.getDemandDate() - o2.getDemandDate());
                                }
                                if (ret == 0 && o1.getSlotId() != null && o2.getSlotId() != null){
                                    ret = (o1.getSlot().getStartTime().compareTo(o2.getSlot().getStartTime()));
                                }
                                if (ret == 0) {
                                    ret = (int) (f1.getId() - f2.getId());
                                }
                                return ret;
                            }
                        });
                    }
                }
            }


            if (!skuATP.get(ol.getSkuId()).isEmpty()) {
                ol.setShortage(true);
                for (ATP atp :skuATP.get(ol.getSkuId())){
                    if (atp.getQuantity().doubleValue() < ol.getToAcknowledgeQuantity()){
                        continue;
                    }
                    ol.setShipFromId(atp.getInventory().getFacilityId());
                    ol.setInventoryId(atp.getInventory().getId());
                    long date =  atp.getDemandDate();
                    if (atp.getSlotId() != null && date > 0) {
                        Calendar calendar = Calendar.getInstance();
                        WorkSlot slot = atp.getSlot();

                        calendar.setTimeInMillis(date + DateUtils.getTime(slot.getStartTime()).getTime());
                        ol.setDeliveryExpectedNoEarlierThan(new Timestamp(calendar.getTimeInMillis())); //Set Earliest ship by date.
                        calendar.setTimeInMillis(date + DateUtils.getTime(slot.getEndTime()).getTime());
                        ol.setDeliveryExpectedNoLaterThan(new Timestamp(calendar.getTimeInMillis()));
                        ol.setWorkSlotId(atp.getSlotId());
                    }
                    atp.getQuantity().decrement(ol.getToAcknowledgeQuantity());
                    ol.setAcknowledgedQuantity(ol.getAcknowledgedQuantity() + ol.getToAcknowledgeQuantity());
                    ol.setShortage(false);
                    acknowledgedLineCounter.increment();
                    break;
                }
            } else {
                ol.setShortage(true);
            }
            if (ol.isShortage() && cancelOnShortage){
                ol.setCancelledQuantity(ol.getOrderedQuantity());
                ol.setCancellationReason(OrderLine.CANCELLATION_REASON_OUT_OF_STOCK);
                ol.setCancellationInitiator(OrderLine.CANCELLATION_INITIATOR_COMPANY);
                rejectLineCounter.increment();
            }

            ol.save();
        }
    }

    public void manifest(){
	    OrderLine line = getProxy();
	    if (line.getToManifestQuantity() > 0){
            line.setManifestedQuantity(line.getToManifestQuantity());
            line.save();
	    }
    }

    private String hsn = null;
    public String getHsn(){
        if (hsn == null){
            OrderLine line  = getProxy();
            if (!line.getReflector().isVoid(line.getSkuId())){
                Item item = line.getSku().getItem();
                if (item.getAssetCodeId() != null){
                    AssetCode assetCode =  item.getAssetCode();
                    if (assetCode.isHsn()){
                        return assetCode.getCode();
                    }
                }
                ItemCategory category = line.getSku().getItem().getItemCategory("HSN");
                if (category != null){
                    hsn = category.getMasterItemCategoryValue().getAllowedValue();
                }else {
                    hsn = "";
                }

            }
        }
        return hsn;
    }

    public double getProductSellingPrice() {
        return getProxy().getSellingPrice();
    }

    public double getProductPrice(){
        return Database.getJdbcTypeHelper(getProxy().getReflector().getPool()).getTypeRef(double.class).getTypeConverter().valueOf(getProxy().getPrice());
    }

    public double  getShippingSellingPrice() {
        return 0.0;
    }

    public double getShippingPrice() {
        return 0.0;
    }


}
