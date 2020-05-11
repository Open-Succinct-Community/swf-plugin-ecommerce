package in.succinct.plugins.ecommerce.db.model.catalog;

import com.venky.swf.db.annotations.column.IS_NULLABLE;
import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.ui.HIDDEN;
import com.venky.swf.db.model.Model;
import in.succinct.plugins.ecommerce.db.model.attributes.Attribute;
import in.succinct.plugins.ecommerce.db.model.attributes.AttributeValue;

public interface ItemAttributeValue extends Model {
    @UNIQUE_KEY
    @IS_NULLABLE(false)
    public long getItemId();
    public void setItemId(long id);
    public Item getItem();

    @IS_NULLABLE(false)
    @HIDDEN
    @UNIQUE_KEY
    public long getAttributeId();
    public void setAttributeId(long id);
    public Attribute getAttribute();


    @IS_NULLABLE(false)
    public long getAttributeValueId();
    public void setAttributeValueId(long id);
    public AttributeValue getAttributeValue();


}
