package in.succinct.plugins.ecommerce.db.model.participation;

import com.venky.swf.db.annotations.column.IS_NULLABLE;
import com.venky.swf.db.annotations.column.IS_VIRTUAL;
import com.venky.swf.db.annotations.column.relationship.CONNECTED_VIA;
import com.venky.swf.db.annotations.column.ui.HIDDEN;
import in.succinct.plugins.ecommerce.db.model.assets.Asset;
import in.succinct.plugins.ecommerce.db.model.service.ServiceResource;

import java.sql.Date;
import java.util.List;

public interface User extends com.venky.swf.plugins.collab.db.model.user.User {
    @CONNECTED_VIA("USER_ID")
    @HIDDEN
    public List<ServiceResource> getServiceResources();


    @IS_NULLABLE
    public Long getAssetId();
    public void setAssetId(Long AssetId);
    public Asset getAsset();

    @IS_VIRTUAL
    public Date getWorkDate();
}
