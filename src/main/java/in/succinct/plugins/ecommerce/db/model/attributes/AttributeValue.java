package in.succinct.plugins.ecommerce.db.model.attributes;

import com.venky.swf.db.annotations.column.IS_NULLABLE;
import com.venky.swf.db.model.Model;

public interface AttributeValue extends Model {
    @IS_NULLABLE(false)
    public long getAttributeId();
    public void setAttributeId(long AttributeId);
    public Attribute getAttribute();

    public String getPossibleValue();
    public void setPossibleValue(String value);
}
