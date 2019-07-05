package in.succinct.plugins.ecommerce.db.model.participation;

import com.venky.swf.db.table.ModelImpl;
import in.succinct.plugins.ecommerce.db.model.order.FedexTransitTime;
import in.succinct.plugins.ecommerce.db.model.order.Order;
import in.succinct.plugins.ecommerce.db.model.order.OrderAddress;
import in.succinct.plugins.ecommerce.db.model.order.OrderLine;
import in.succinct.plugins.ecommerce.integration.fedex.RateWebServiceClient;

import java.util.List;
import java.util.stream.Collectors;

public class PreferredCarrierImpl extends ModelImpl<PreferredCarrier> {
    public PreferredCarrierImpl(PreferredCarrier preferredCarrier){
        super(preferredCarrier);
    }
    public PreferredCarrierImpl(){

    }

    public Double getEstimatedShippingCharges(Order order){
        PreferredCarrier carrier = getProxy();
        if (!carrier.getName().equalsIgnoreCase("FedEx")){
            return null;
        }
        List<OrderLine> lines = order.getOrderLines();
        if (!lines.isEmpty()){
            Facility facility = null;
            for (OrderLine line : lines){
                if (line.getShipFromId() != null){
                    facility = line.getShipFrom();
                    break;
                }
            }
            if (facility != null) {
                List<OrderAddress> addresses = order.getAddresses().stream().filter(oa -> oa.getAddressType().equals(OrderAddress.ADDRESS_TYPE_SHIP_TO)).collect(Collectors.toList());
                if (!addresses.isEmpty()){
                    FedexTransitTime transitTime = new RateWebServiceClient<OrderAddress>(facility,addresses.get(0)).getTransitTime();
                    return transitTime.getRateFor1KgPackage();
                }
            }
        }
        return null;

    }

}
