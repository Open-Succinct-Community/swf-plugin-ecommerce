package in.succinct.plugins.ecommerce.db.model.participation;


import com.venky.swf.db.annotations.column.COLUMN_DEF;
import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.defaulting.StandardDefault;
import com.venky.swf.db.annotations.column.validations.Enumeration;
import com.venky.swf.plugins.collab.db.model.participants.admin.Address;

public interface ExtendedAddress extends Address {

    public long getEntityId();
    public void setEntityId(long entityId);


    @UNIQUE_KEY
    public  String getAddressType();
    public void setAddressType(String addressType);

    public String getFirstName();
    public void setFirstName(String name);

    public String getLastName();
    public void setLastName(String name);

}
