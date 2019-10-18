package in.succinct.plugins.ecommerce.controller;

import com.venky.swf.controller.ModelController;
import com.venky.swf.controller.annotations.RequireLogin;
import com.venky.swf.db.Database;
import com.venky.swf.db.annotations.column.ui.mimes.MimeType;
import com.venky.swf.integration.FormatHelper;
import com.venky.swf.integration.IntegrationAdaptor;
import com.venky.swf.path.Path;
import com.venky.swf.views.View;
import in.succinct.plugins.ecommerce.db.model.service.ServiceOrderPrint;

import java.util.Iterator;
import java.util.Map;

public class ServiceOrderPrintsController extends ModelController<ServiceOrderPrint> {
    public ServiceOrderPrintsController(Path path) {
        super(path);
    }
    @RequireLogin
    public View upload() {
        ServiceOrderPrint document = Database.getTable(ServiceOrderPrint.class).newRecord();
        Map<String,Object> fields = getFormFields();
        Iterator<String> e = fields.keySet().iterator();
        while (e.hasNext()) {
            String name = e.next();
            getReflector().set(document,name,fields.get(name));
        }
        document.save();
        return IntegrationAdaptor.instance(ServiceOrderPrint.class, FormatHelper.getFormatClass(MimeType.APPLICATION_JSON)).createResponse(getPath(),document);
    }

}
