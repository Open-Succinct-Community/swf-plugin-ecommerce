package in.succinct.plugins.ecommerce.extensions.service;

import com.venky.swf.plugins.collab.extensions.beforesave.BeforeSaveAddress;
import in.succinct.plugins.ecommerce.db.model.service.ServiceOrderAddress;

public class BeforeSaveServiceOrderAddress extends BeforeSaveAddress<ServiceOrderAddress> {
    static {
        registerExtension(new BeforeSaveServiceOrderAddress());
    }
    @Override
    protected boolean isOkToSetLocationAsync() {
        return false;
    }
}
