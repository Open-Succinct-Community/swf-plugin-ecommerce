package in.succinct.plugins.ecommerce.db.model.participation;

import com.venky.swf.db.annotations.column.IS_VIRTUAL;

public interface ExtendedAttribute  {

    //* point to the right column of your entity*/
    public long getEntityId();
    public void setEntityId(long id);

    public String getName();
    public void setName(String name);

    public String getValue();
    public void setValue(String value);
}
