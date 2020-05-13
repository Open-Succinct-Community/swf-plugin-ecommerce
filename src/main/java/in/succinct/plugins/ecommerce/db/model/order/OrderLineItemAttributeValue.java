package in.succinct.plugins.ecommerce.db.model.order;

import com.venky.swf.db.annotations.column.IS_NULLABLE;
import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.ui.HIDDEN;
import com.venky.swf.db.model.Model;
import in.succinct.plugins.ecommerce.db.model.attributes.Attribute;
import in.succinct.plugins.ecommerce.db.model.attributes.AttributeValue;

public interface OrderLineItemAttributeValue extends Model {
    @UNIQUE_KEY
    public long getOrderLineId();
    public void setOrderLineId(long id);
    public OrderLine getOrderLine();


    /**
     * An Inventory attribute of the item the order line points to.
     * @return
     */
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
