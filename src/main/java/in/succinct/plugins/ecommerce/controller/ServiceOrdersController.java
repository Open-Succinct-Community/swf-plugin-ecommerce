package in.succinct.plugins.ecommerce.controller;

import com.venky.swf.controller.ModelController;
import com.venky.swf.controller.annotations.SingleRecordAction;
import com.venky.swf.db.Database;
import com.venky.swf.path.Path;
import com.venky.swf.views.View;
import in.succinct.plugins.ecommerce.db.model.service.ServiceOrder;

import java.util.List;

public class ServiceOrdersController extends ModelController<ServiceOrder> {
    public ServiceOrdersController(Path path) {
        super(path);
    }



    @SingleRecordAction(icon = "glyphicon-remove" , tooltip = "Cancel")
    public View cancel(long id) {
        ServiceOrder order = Database.getTable(ServiceOrder.class).get(id);
        order.cancel();
        if (getIntegrationAdaptor() != null) {
            return getIntegrationAdaptor().createResponse(getPath(), order,null,getIgnoredParentModels(), getIncludedModelFields());
        }else {
            return back();
        }
    }

    @SingleRecordAction(icon = "glyphicon-thumbs-up" , tooltip = "Complete")
    public View complete(long id) {
        ServiceOrder order = Database.getTable(ServiceOrder.class).get(id);
        order.complete();
        if (getIntegrationAdaptor() != null) {
            return getIntegrationAdaptor().createResponse(getPath(), order,null,getIgnoredParentModels(), getIncludedModelFields());
        }else {
            return back();
        }
    }

    @SingleRecordAction(icon = "glyphicon-thumbs-down" , tooltip = "Reject")
    public View reject(long id) {
        ServiceOrder order = Database.getTable(ServiceOrder.class).get(id);
        order.reject();
        if (getIntegrationAdaptor() != null) {
            return getIntegrationAdaptor().createResponse(getPath(), order,null,getIgnoredParentModels(), getIncludedModelFields());
        }else {
            return back();
        }
    }


    public View cancel(){
        if (getIntegrationAdaptor() != null){
            List<ServiceOrder> orders = getIntegrationAdaptor().readRequest(getPath());
            //We will get only Orders for cancellation. So No Status. Only ReasonCode and Remarks.
            orders.forEach(o->{
                o.cancel();
            });
        }
        return getIntegrationAdaptor().createStatusResponse(getPath(),null, "Appointment has been cancelled.");
    }

}
