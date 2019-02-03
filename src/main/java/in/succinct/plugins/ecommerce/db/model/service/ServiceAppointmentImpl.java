package in.succinct.plugins.ecommerce.db.model.service;

import com.venky.swf.db.table.ModelImpl;
import in.succinct.plugins.ecommerce.db.model.service.ServiceAppointment.AttemptStatus;

public class ServiceAppointmentImpl extends ModelImpl<ServiceAppointment> {
    public ServiceAppointmentImpl(ServiceAppointment proxy){
        super(proxy);
    }
    public ServiceAppointmentImpl(){
        super();
    }
    public boolean isPending(){
        ServiceAppointment attempt = getProxy();
        return AttemptStatus.valueOf(attempt.getStatus()) == AttemptStatus.PENDING;
    }
    public void success(){
        ServiceAppointment attempt = getProxy();
        attempt.setStatus(AttemptStatus.SUCCESS.toString());
        attempt.save();
    }
    public void fail(){
        ServiceAppointment attempt = getProxy();
        attempt.setStatus(AttemptStatus.FAIL.toString());
        attempt.save();
    }

}
