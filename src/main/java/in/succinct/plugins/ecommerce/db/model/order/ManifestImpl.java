package in.succinct.plugins.ecommerce.db.model.order;

import com.venky.swf.db.table.ModelImpl;

public class ManifestImpl extends ModelImpl<Manifest>{
    public ManifestImpl(Manifest proxy){
        super(proxy);
    }

    public void close(){
        Manifest manifest = getProxy();
        manifest.setClosed(true);
        manifest.save();
    }
}
