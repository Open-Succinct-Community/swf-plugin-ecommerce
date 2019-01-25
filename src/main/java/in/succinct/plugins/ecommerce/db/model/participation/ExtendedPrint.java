package in.succinct.plugins.ecommerce.db.model.participation;

import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.ui.PROTECTION;
import com.venky.swf.db.annotations.column.ui.PROTECTION.Kind;
import in.succinct.plugins.ecommerce.db.model.order.Order;

import java.io.InputStream;

public interface ExtendedPrint {

    @UNIQUE_KEY
    public long getEntityId();
    public void setEntityId(long id);

    @UNIQUE_KEY
    public String getDocumentId();
    public  void setDocumentId(String id);

    @UNIQUE_KEY
    public String getDocumentType();
    public void setDocumentType(String documentType);

    public InputStream getImage();
    public void setImage(InputStream is);

    @PROTECTION(Kind.NON_EDITABLE)
    public String getImageContentName();
    public void setImageContentName(String name);

    @PROTECTION(Kind.NON_EDITABLE)
    public String getImageContentType();
    public void setImageContentType(String contentType);

    @PROTECTION(Kind.NON_EDITABLE)
    public int getImageContentSize();
    public void setImageContentSize(int size);
}
