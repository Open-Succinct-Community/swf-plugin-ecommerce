package in.succinct.plugins.ecommerce.db.model.service;

import com.venky.swf.db.annotations.column.COLUMN_DEF;
import com.venky.swf.db.annotations.column.defaulting.StandardDefault;
import com.venky.swf.db.annotations.column.ui.PROTECTION;
import com.venky.swf.db.annotations.column.ui.PROTECTION.Kind;
import com.venky.swf.db.annotations.column.validations.Enumeration;
import com.venky.swf.db.model.Model;

import java.sql.Time;
import java.sql.Timestamp;

public interface ServiceAttempt extends Model {
    public long getServiceOrderId();
    public void setServiceOrderId(long id);
    public ServiceOrder getServiceOrder();


    public Timestamp getEarliestBy();
    public void setEarliestBy(Timestamp start);

    public Timestamp getLatestBy();
    public void setLatestBy(Timestamp end);


    @Enumeration("SUCCESS,FAIL,PENDING")
    @COLUMN_DEF(value = StandardDefault.SOME_VALUE,args = "PENDING")

    @PROTECTION(Kind.NON_EDITABLE)
    public String getStatus();
    public void setStatus(String status);

    public static enum AttemptStatus {
        PENDING,
        SUCCESS,
        FAIL,

    }
    public void success();
    public void fail();
    public boolean isPending();
}
