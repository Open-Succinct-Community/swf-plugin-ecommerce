package in.succinct.plugins.ecommerce.db.model.participation;

import com.venky.swf.db.annotations.column.relationship.CONNECTED_VIA;
import in.succinct.plugins.ecommerce.db.model.service.ServiceResource;

import java.util.List;

public interface User extends com.venky.swf.plugins.collab.db.model.user.User {
    @CONNECTED_VIA("USER_ID")
    public List<ServiceResource> getServiceResources();

}
