package in.succinct.plugins.ecommerce.db.model.order;

import com.venky.cache.Cache;
import in.succinct.plugins.ecommerce.agents.order.tasks.OrderStatusMonitor;
import com.venky.cache.UnboundedCache;
import com.venky.core.util.Bucket;
import com.venky.swf.db.Database;
import com.venky.swf.db.JdbcTypeHelper.TypeConverter;
import com.venky.swf.db.table.ModelImpl;
import com.venky.swf.plugins.background.core.TaskManager;
import in.succinct.plugins.ecommerce.db.model.participation.Facility;
import in.succinct.plugins.ecommerce.integration.fedex.RateWebServiceClient;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OrderImpl  extends ModelImpl<Order>{
	public OrderImpl() {
		super();
	}

	public OrderImpl(Order proxy) {
		super(proxy);
	}
	
	private Map<String,OrderAttribute> map = null;

	String orderNumber = null;
	public String getOrderNumber(){
		if (orderNumber == null){
			orderNumber = getProxy().getId() == 0 ? "" : String.valueOf(getProxy().getId());
		}
		return orderNumber;
	}
	public void setOrderNumber(String orderNumber){
		this.orderNumber = orderNumber;
	}


	public synchronized Map<String,OrderAttribute> getAttributeMap() {
		if (map == null) {
			map = new  UnboundedCache<String,OrderAttribute>() {

				private static final long serialVersionUID = 1L;

				@Override
				protected OrderAttribute getValue(String name) {
					OrderAttribute attr =  Database.getTable(OrderAttribute.class).newRecord();
					attr.setName(name);
					if(!getProxy().getRawRecord().isNewRecord()){
						attr.setOrderId(getProxy().getId());
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
	public void saveAttributeMap(Map<String, OrderAttribute> map) { 
		map.keySet().stream().sorted().forEach(a->{
			OrderAttribute oa = map.get(a);
			oa.setOrderId(getProxy().getId());
			oa.save();
		});
	}
	public OrderAttribute getAttribute(String name) { 
		Map<String,OrderAttribute> map = getProxy().getAttributeMap();
		if (map.containsKey(name)) {
			return map.get(name);
		}
		return null;
	}
	
	public void acknowledge() { 
		Order order = getProxy();
		Bucket orderLinesNotAcknowledged = new Bucket();
		Bucket orderLinesAcknowledged = new Bucket();
		TypeConverter<Long> iConv = order.getReflector().getJdbcTypeHelper().getTypeRef(Long.class).getTypeConverter();
		List<OrderLine> orderLines = order.getOrderLines(); 
		Map<Long,Map<Long,Bucket>> skuATP = new Cache<Long, Map<Long, Bucket>>(0,0) {
            @Override
            protected Map<Long,Bucket> getValue(Long skuId) {
                return new Cache<Long, Bucket>(0,0) {
                    @Override
                    protected Bucket getValue(Long facilityId) {
                        return new Bucket();
                    }
                };
            }
        };

		orderLines.forEach(ol->{
		    ol.acknowledge(skuATP,orderLinesAcknowledged,orderLinesNotAcknowledged,false);
		});

		TaskManager.instance().executeAsync(new OrderStatusMonitor(order.getId()),false);

	}
    public void reject() {
	    cancel(OrderLine.CANCELLATION_REASON_OUT_OF_STOCK,OrderLine.CANCELLATION_INITIATOR_COMPANY);
    }
	public void cancel(String reason,String initiator){
        Order order = getProxy();
        order.getOrderLines().forEach(ol->{
            ol.cancel(reason,initiator);
        });

    }
	public void cancel(String reason) {
        cancel(reason,OrderLine.CANCELLATION_INITIATOR_USER);
	}

	public void pack() {
		Order order = getProxy();
		order.getOrderLines().forEach(ol->{
			ol.pack(ol.getToPackQuantity());
		});
		TaskManager.instance().executeAsync(new OrderStatusMonitor(order.getId()),false);
	}
	public void ship() {
		Order order = getProxy(); 
		order.getOrderLines().stream().sorted(new Comparator<OrderLine>() {
			// Prevent Dead Lock
			@Override
			public int compare(OrderLine o1, OrderLine o2) {
				long ret = o1.getSkuId() - o2.getSkuId();
				if (ret == 0L){
					ret = o1.getShipFromId() - o2.getShipFromId();
				}
				return (int)(ret);
			}
		}).forEach(ol->{
			ol.ship();
		});
		TaskManager.instance().executeAsync(new OrderStatusMonitor(order.getId()),false);
	}
	public void deliver() {
		Order order = getProxy();
		order.getOrderLines().forEach(ol->{
			ol.setDeliveredQuantity(ol.getShippedQuantity());
			ol.save();
		});
		TaskManager.instance().executeAsync(new OrderStatusMonitor(order.getId()),false);
	}

	public boolean isShort() {
		Bucket numShortLines = new Bucket();
		getProxy().getOrderLines().forEach(ol->{
			if (ol.isShortage() && ol.getToShipQuantity() > 0){
				numShortLines.increment();
			}
		});
		return numShortLines.intValue() > 0 ;
	}

	public Long getManifestId() {
		String value = getAttribute("manifest_id").getValue();
		long manifestId = getReflector().getJdbcTypeHelper().getTypeRef(Long.class).getTypeConverter().valueOf(value) ;
		if (manifestId != 0){
			return manifestId;
		}
		return null;
	}
	public void setManifestId(Long id) {
		//
	}

}
