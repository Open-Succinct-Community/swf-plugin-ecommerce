package in.succinct.plugins.ecommerce.db.model.apis;

import in.succinct.plugins.ecommerce.db.model.order.OrderLine;
import com.venky.core.util.ObjectUtil;
import com.venky.swf.db.table.ModelImpl;

public class PackImpl extends ModelImpl<Pack>{
    public PackImpl(Pack pack){
        super(pack);
    }

    public void pack(){
        Pack pack = getProxy();
        try {
            OrderLine ol = pack.getOrderLine();
            if (ol == null) {
                throw new Pack.PackValidationException("Could not determine order line");
            }else {
                if (pack.getPackedQuantity() == null){
                    if (!ObjectUtil.isVoid(pack.getUnitNumber())) {
                        pack.setPackedQuantity(1.0);
                    }else{
                        pack.setPackedQuantity(ol.getToPackQuantity());
                    }
                }
                if (ol.isUnitNumberCaptureRequired()) {
                    if (pack.getPackedQuantity() > 1) {
                        throw new Pack.PackValidationException("Sku " + ol.getSku().getName()  + " requires scanning of " + ol.getUnitNumberTypeRequired() + " and therefore PackQuantity cannot be more than 1");
                    }else if (ObjectUtil.isVoid(pack.getUnitNumber())) {
                        throw new Pack.PackValidationException("Sku " + ol.getSku().getName()  + " requires scanning of " + ol.getUnitNumberTypeRequired());
                    }else {
                        ol.pack(pack.getUnitNumber());
                    }
                }else {
                    ol.pack(pack.getPackedQuantity());
                }
            }
            pack.setSuccess(true);
        }catch(Pack.PackValidationException ex){
            pack.setError(ex.getMessage());
            pack.setSuccess(false);
        }
    }
}
