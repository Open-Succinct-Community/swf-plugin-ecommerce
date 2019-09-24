package in.succinct.plugins.ecommerce.db.model.participation;

import com.venky.swf.db.annotations.column.IS_NULLABLE;
import com.venky.swf.db.annotations.column.PASSWORD;
import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.model.Model;

public interface MarketPlaceIntegration extends Model {
    @IS_NULLABLE(false)
    @UNIQUE_KEY
    public long getFacilityId();
    public void setFacilityId(long id);
    public Facility getFacility();

    public String getBaseUrl();
    public void setBaseUrl(String baseUrl);

    public String getClientId();
    public void setClientId(String clientId);

    public String getUsername();
    public void setUsername(String username);

    @PASSWORD
    public String getPassword();
    public void setPassword(String password);
}
