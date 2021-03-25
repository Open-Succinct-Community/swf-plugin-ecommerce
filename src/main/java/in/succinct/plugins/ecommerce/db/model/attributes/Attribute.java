package in.succinct.plugins.ecommerce.db.model.attributes;

import com.venky.swf.db.model.Model;

import java.util.List;

public interface Attribute extends Model {
    public String getName();
    public void setName(String name);

    public List<AttributeValue> getAttributeValues();
}
