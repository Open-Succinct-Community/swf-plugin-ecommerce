package in.succinct.plugins.ecommerce.db.model.participation;

import com.venky.swf.db.annotations.column.ui.HIDDEN;
import com.venky.swf.db.model.Model;
import com.venky.swf.plugins.collab.db.model.CompanySpecific;
import in.succinct.plugins.ecommerce.db.model.order.Manifest;

public interface PreferredCarrier extends CompanySpecific, Model {
    @HIDDEN
    public Long getCompanyId();

    @HIDDEN
    public long getFacilityId();
    public void setFacilityId(long id);
    public Facility getFacility();


    public String getName();
    public void setName(String name);

}
