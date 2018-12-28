package in.succinct.plugins.ecommerce.db.model.apis;

import in.succinct.plugins.ecommerce.db.model.order.OrderLine;
import com.venky.swf.db.model.io.xml.XMLModelWriter;
import com.venky.swf.db.table.ModelImpl;
import com.venky.swf.db.table.RecordNotFoundException;
import com.venky.swf.routing.Config;
import com.venky.xml.XMLDocument;
import com.venky.xml.XMLElement;

public class ShipImpl extends ModelImpl<Ship>{
    public ShipImpl(Ship ship){
        super(ship);
    }


    public void ship() {
        Ship ship = getProxy();
        try {
            OrderLine ol = ship.getOrderLine();
            if (ol == null) {
                throw new RecordNotFoundException("Invalid Order Line id " + ship.getOrderLineId());
            }
            if (ship.getQuantity() == null) {
                ol.ship();
            }else {
                ol.ship(ship.getQuantity());
            }
            ship.setSuccess(true);
        }catch (RuntimeException ex){
            ship.setError(ex.getMessage());
            ship.setSuccess(false);
            XMLModelWriter<Ship> w = new XMLModelWriter<Ship>(Ship.class);
            XMLElement shipElem = new XMLDocument("ShipRequests").getDocumentRoot();
            w.write(ship,shipElem, null);
            Config.instance().getLogger(getClass().getName()).warning(shipElem.toString());

        }
    }

}
