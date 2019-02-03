package in.succinct.plugins.ecommerce.controller;

import com.venky.swf.controller.ModelController;
import com.venky.swf.controller.annotations.SingleRecordAction;
import com.venky.swf.db.Database;
import com.venky.swf.path.Path;
import com.venky.swf.views.RedirectorView;
import com.venky.swf.views.View;
import in.succinct.plugins.ecommerce.db.model.service.ServiceAppointment;
import in.succinct.plugins.ecommerce.db.model.service.ServiceOrder;

public class ServiceAppointmentsController extends ModelController<ServiceAppointment> {
    public ServiceAppointmentsController(Path path) {
        super(path);
    }

    @SingleRecordAction(icon = "glyphicon-thumbs-up" ,tooltip =  "Successfully Completed")
    public View complete(long id){
        ServiceAppointment appointment = Database.getTable(ServiceAppointment.class).get(id);
        appointment.success();
        return back();
    }

    @SingleRecordAction(icon = "glyphicon-retweet" ,tooltip =  "Attempt failed.")
    public View attempted(long id){
        ServiceAppointment appointment = Database.getTable(ServiceAppointment.class).get(id);
        appointment.fail();
        return back();
    }
}
