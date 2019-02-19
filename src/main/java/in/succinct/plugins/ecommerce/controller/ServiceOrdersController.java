package in.succinct.plugins.ecommerce.controller;

import com.venky.swf.controller.ModelController;
import com.venky.swf.controller.annotations.SingleRecordAction;
import com.venky.swf.db.Database;
import com.venky.swf.path.Path;
import com.venky.swf.views.RedirectorView;
import com.venky.swf.views.View;
import in.succinct.plugins.ecommerce.db.model.service.ServiceOrder;
import in.succinct.plugins.ecommerce.db.model.service.ServiceOrder.CancelReason;

public class ServiceOrdersController extends ModelController<ServiceOrder> {
    public ServiceOrdersController(Path path) {
        super(path);
    }

    @SingleRecordAction(icon = "glyphicon-calendar", tooltip = "Plan Appointment")
    public View plan(long id){
        ServiceOrder order = Database.getTable(ServiceOrder.class).get(id);
        if (order.getServiceAttempts().isEmpty()){
            return new RedirectorView(getPath(),"show/"+id+"/service_appointments/blank");
        }else {
            return new RedirectorView(getPath(), "show/"+id+"?_select_tab=Service Appointment");
        }
    }

    @SingleRecordAction(icon = "glyphicon-remove" , tooltip = "Cancel")
    public View cancel(long id) {
        ServiceOrder order = Database.getTable(ServiceOrder.class).get(id);
        order.cancel();
        if (getIntegrationAdaptor() != null) {
            return getIntegrationAdaptor().createResponse(getPath(), order,null,getIgnoredParentModels(),getIncludedChildModelFields());
        }else {
            return back();
        }
    }

    @SingleRecordAction(icon = "glyphicon-thumbs-down" , tooltip = "Reject")
    public View reject(long id) {
        ServiceOrder order = Database.getTable(ServiceOrder.class).get(id);
        order.reject();
        if (getIntegrationAdaptor() != null) {
            return getIntegrationAdaptor().createResponse(getPath(), order,null,getIgnoredParentModels(),getIncludedChildModelFields());
        }else {
            return back();
        }
    }


}