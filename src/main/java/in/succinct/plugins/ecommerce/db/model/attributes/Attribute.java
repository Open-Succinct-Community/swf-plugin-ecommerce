package in.succinct.plugins.ecommerce.db.model.attributes;

import com.venky.swf.db.Database;
import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.model.Model;

import java.util.List;

public interface Attribute extends Model {
    @UNIQUE_KEY
    public String getName();
    public void setName(String name);

    public List<AttributeValue> getAttributeValues();
    public static Attribute find(String name){
        Attribute attribute = Database.getTable(Attribute.class).newRecord();
        attribute.setName( name);
        attribute = Database.getTable(Attribute.class).getRefreshed(attribute);
        if (attribute.getRawRecord().isNewRecord()){
            attribute.save();
        }
        return attribute;
    }
}
