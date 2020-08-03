package in.succinct.plugins.ecommerce.db.model.service;

import com.venky.swf.db.annotations.column.IS_NULLABLE;
import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.indexing.Index;
import com.venky.swf.db.annotations.column.pm.PARTICIPANT;
import com.venky.swf.db.annotations.model.EXPORTABLE;
import com.venky.swf.db.model.Model;
import com.venky.swf.plugins.collab.db.model.user.User;
import in.succinct.plugins.ecommerce.db.model.catalog.Service;
@EXPORTABLE(false)

public interface ServiceResource extends Model {
    @PARTICIPANT
    @Index
    @UNIQUE_KEY
    public Long getServiceId();
    public void setServiceId(Long id);
    public Service getService();


    @PARTICIPANT(redundant = true)
    @Index
    @IS_NULLABLE(false)
    @UNIQUE_KEY
    public Long getUserId();
    public void setUserId(Long userId);
    public User getUser();
}
