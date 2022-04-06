package in.succinct.plugins.ecommerce.db.model.inventory;

import com.venky.cache.Cache;
import com.venky.core.date.DateUtils;
import com.venky.core.util.Bucket;
import com.venky.swf.db.model.reflection.ModelReflector;
import com.venky.swf.plugins.calendar.db.model.WorkSlot;
import com.venky.swf.pm.DataSecurityFilter;
import com.venky.swf.sql.Conjunction;
import com.venky.swf.sql.Expression;
import com.venky.swf.sql.Operator;
import com.venky.swf.sql.Select;
import in.succinct.plugins.ecommerce.db.model.attributes.AttributeValue;
import in.succinct.plugins.ecommerce.db.model.demand.Demand;
import in.succinct.plugins.ecommerce.db.model.order.OrderLine;
import in.succinct.plugins.ecommerce.db.model.order.OrderLineItemAttributeValue;
import in.succinct.plugins.ecommerce.db.model.participation.Facility;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class InventoryCalculator {
	//OrderLine orderLine = null;
	Facility facility = null;
	Sku sku = null;
	List<AttributeValue> inventoryAttributeValues = new ArrayList<>();
	Timestamp after = null;
	Timestamp before = null;
	public InventoryCalculator(OrderLine orderLine) {
		//this.orderLine = orderLine;
		this.facility = orderLine.getShipFrom();
		this.sku = orderLine.getSku();
		for (OrderLineItemAttributeValue orderLineItemAttributeValue : orderLine.getOrderLineItemAttributeValues()) {
			this.inventoryAttributeValues.add(orderLineItemAttributeValue.getAttributeValue());
		}
		this.after = orderLine.getOrder().getShipAfterDate();
		this.before = orderLine.getOrder().getShipByDate();
	}
	public InventoryCalculator(Sku sku){
		this(sku,null);
	}
	public InventoryCalculator(Sku sku, Facility facility){
		this.sku = sku ;
		this.facility = facility;
		this.inventoryAttributeValues = new ArrayList<>();
	}

	public Double getTotalInventory() { 
		Bucket total = new Bucket();
		getInventory().forEach(atp->{
			total.increment(atp.getQuantity().doubleValue());
		});
		return total.doubleValue();
	}
	public Double getPendingShip(){
		Bucket total = new Bucket();
		getInventory().forEach(atp->{
			total.increment(atp.getPendingShipQuantity().doubleValue());
		});
		return total.doubleValue();
	}
	
	public static class ATP { 
		Inventory inventory;
		List<Demand> pendShip;
		long onDate;
		WorkSlot slot;
		public Inventory getInventory() {
			return inventory;
		}
		public long getDemandDate(){
			return onDate;
		}
		public WorkSlot getSlot(){
			return slot;
		}
		public Long getSlotId(){
			if (slot == null){
				return null;
			}
			return slot.getId();
		}
		Bucket total = null;
		Bucket pendShipQuantity = null;

		public Bucket getPendingShipQuantity(){
			if (pendShipQuantity != null){
				return pendShipQuantity;
			}
			getQuantity();
			return pendShipQuantity;
		}
        public Bucket getQuantity() {
        	if (total != null){
				return total;
			}
			total = new Bucket();
			pendShipQuantity = new Bucket();
			boolean isItemRentable = inventory.getSku().getItem().isRentable();

			if (isItemRentable){
				Bucket workSlotCapacityConsumed = new Bucket();

				pendShip.forEach(demand-> {
					workSlotCapacityConsumed.increment(demand.getQuantity().doubleValue());
				});

				total.increment(inventory.getQuantity());
				total.decrement(workSlotCapacityConsumed.doubleValue());

			}else {
				pendShip.forEach(demand->{
					pendShipQuantity.increment(demand.getQuantity().doubleValue());
				});
				total.increment(inventory.isInfinite() ? Double.POSITIVE_INFINITY : inventory.getQuantity() - pendShipQuantity.doubleValue());
			}
			if (total.doubleValue() < 0 ){
				total = new Bucket(0);
			}
			return total;
		}
	}

	private List<Demand> getDemands(Set<Long> inventoryIds){
		ModelReflector<Demand> ref = ModelReflector.instance(Demand.class);

		Expression where = new  Expression(ref.getPool(), Conjunction.AND);
		where.add(new Expression(ref.getPool(),"INVENTORY_ID" , Operator.IN, inventoryIds.toArray()));

		String[] orderBy = new String[]{"ID"} ;

		if (sku.getItem().isRentable() ){
			if (after != null){
				where.add(new Expression(ref.getPool(),"DEMAND_DATE" , Operator.GE, after));
			}
			if (before != null){
				where.add(new Expression(ref.getPool(),"DEMAND_DATE" , Operator.LE, before));
			}
			orderBy = new String[]{"DEMAND_DATE","ID"};
		}
		Select select = new Select().from(Demand.class).where(where).orderBy(orderBy);

		List<Demand> demands = select.execute();

		return demands;
	}
	public List<ATP> getInventory(){
		List<ATP> inv = new ArrayList<>();

		ModelReflector<Inventory> ref = ModelReflector.instance(Inventory.class);
		Expression where = new  Expression(ref.getPool(), Conjunction.AND);
		if (facility != null){
			where.add(new Expression(ref.getPool(),"FACILITY_ID" , Operator.EQ, facility.getId()));
		}
		where.add(new Expression(ref.getPool(),"SKU_ID" , Operator.EQ, sku.getId()));

		Select select =  new Select().from(Inventory.class).where(where);

		for (AttributeValue attributeValue : inventoryAttributeValues){
			select.add(" and exists ( select 1 from inventory_attribute_values where inventory_id = inventories.id and attribute_value_id = " + attributeValue.getId() + ")");
		}

		List<Inventory> inventories = select.execute();

		Set<Long> inventoryIds = DataSecurityFilter.getIds(inventories);
		List<Demand> demands = getDemands(inventoryIds);
		Map<Long,Map<Date,Map<Long,List<Demand>>>> map = new Cache<Long, Map<Date, Map<Long,List<Demand>>>>(0,0) {
			@Override
			protected Map<Date, Map<Long,List<Demand>>> getValue(Long inventoryId) {
				return new Cache<Date, Map<Long,List<Demand>>>(0,0) {
					@Override
					protected Map<Long,List<Demand>> getValue(Date date) {
						return new Cache<Long, List<Demand>>() {
							@Override
							protected List<Demand> getValue(Long slotId) {
								return new ArrayList<>();
							}
						};
					}
				};
			}
		};

		demands.forEach(demand -> map.get(demand.getInventoryId()).get(demand.getDemandDate()).get(demand.getWorkSlotId()).add(demand));

		inventories.forEach(inventory->{
			if (sku.getItem().isRentable() ) {
				for (long date = after.getTime() ; date <= before.getTime() ;) {
					for (WorkSlot slot : inventory.getWorkCalendar().getWorkSlots()){
						ATP atp = new ATP();
						atp.inventory = inventory;
						atp.onDate = date;
						atp.slot = slot;
						atp.pendShip = map.get(inventory.getId()).get(new Date(date)).get(slot.getId());
						inv.add(atp);
					}
					date = DateUtils.addHours(date,24);
				}
			}else {
				ATP atp = new ATP();
				atp.inventory = inventory;
				atp.pendShip = map.get(inventory.getId()).get(null).get(null);
				inv.add(atp);
			}
		});

		return inv;
	}
}
