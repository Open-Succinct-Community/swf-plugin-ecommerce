package in.succinct.plugins.ecommerce.db.model.order;

import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.pm.PARTICIPANT;
import com.venky.swf.db.annotations.column.validations.Enumeration;
import com.venky.swf.db.model.Model;

import java.sql.Timestamp;

public interface OrderIntransitEvent extends Model {
    @UNIQUE_KEY
    @PARTICIPANT
    public long getOrderId();
    public void setOrderId(long id);
    public Order getOrder();


    @UNIQUE_KEY
    public int getEventSeqNo();
    public void setEventSeqNo(int seqNo);

    @Enumeration(EVENT_TYPE_LEFT + "," + EVENT_TYPE_ARRIVED)
    public String getEventType();
    public void setEventType(String eventType);

    public Timestamp getEventTimeStamp();
    public void setEventTimestamp(Timestamp eventTimestamp);

    public String getEventDescription();
    public void setEventDescription(String description);


    public String getLocation();
    public void setLocation(String location);

    public static final String LOCATION_ORIGIN = "Origin";
    public static final String LOCATION_DESTINATION = "Destination";


    public static final String EVENT_TYPE_ARRIVED = "Arrived" ;
    public static final String EVENT_TYPE_LEFT = "Left" ;

}
