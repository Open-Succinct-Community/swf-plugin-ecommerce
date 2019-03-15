package in.succinct.plugins.ecommerce.db.model.order;

import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.pm.PARTICIPANT;
import com.venky.swf.db.model.Model;

import java.sql.Timestamp;

public interface OrderStatus extends Model {
    @UNIQUE_KEY
    @PARTICIPANT
    public long getOrderId();
    public void setOrderId(long id);
    public Order getOrder();

    @UNIQUE_KEY
    public String getFulfillmentStatus();
    public void setFulfillmentStatus(String status);

    public Timestamp getStatusDate();
    public void setStatusDate(Timestamp at);


}
