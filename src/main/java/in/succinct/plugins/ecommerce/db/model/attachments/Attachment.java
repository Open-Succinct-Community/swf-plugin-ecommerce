package in.succinct.plugins.ecommerce.db.model.attachments;

import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.ui.PROTECTION;
import com.venky.swf.db.annotations.column.ui.PROTECTION.Kind;
import in.succinct.plugins.ecommerce.db.model.inventory.Sku;
import in.succinct.plugins.ecommerce.db.model.participation.Facility;

public interface Attachment extends com.venky.swf.plugins.attachment.db.model.Attachment {
    @PROTECTION(Kind.NON_EDITABLE)
    @UNIQUE_KEY
    public Long getFacilityId();
    public void setFacilityId(Long id);
    public Facility getFacility();

    @PROTECTION(Kind.NON_EDITABLE)
    @UNIQUE_KEY
    public Long getSkuId();
    public void setSkuId(Long id);
    public Sku   getSku();

}
