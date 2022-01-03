package in.succinct.plugins.ecommerce.db.model.attributes;

import com.venky.swf.db.annotations.column.IS_NULLABLE;
import com.venky.swf.db.annotations.column.IS_VIRTUAL;
import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.indexing.Index;
import com.venky.swf.db.annotations.model.HAS_DESCRIPTION_FIELD;
import com.venky.swf.db.model.Model;

@HAS_DESCRIPTION_FIELD("DESCRIPTION")
public interface AttributeValue extends Model {
    @IS_NULLABLE(false)
    @UNIQUE_KEY
    public long getAttributeId();
    public void setAttributeId(long AttributeId);
    public Attribute getAttribute();

    @UNIQUE_KEY
    public String getPossibleValue();
    public void setPossibleValue(String value);

    @IS_VIRTUAL
    @Index
    public String getDescription();
}
