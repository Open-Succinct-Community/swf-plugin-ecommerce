package in.succinct.plugins.ecommerce.db.model.assets;

import com.venky.swf.db.annotations.column.IS_VIRTUAL;
import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.model.Model;
import com.venky.swf.plugins.calendar.db.model.WorkCalendar;

import java.util.List;

public interface Asset extends Model {
    @UNIQUE_KEY
    public String getAssetNumber();
    public void setAssetNumber(String assetNumber);


    public List<Capability> getAssetCapabilities();

    public List<AssetAttributeValue> getAssetAttributeValues();

    @IS_VIRTUAL
    public List<Capability> getLoanableCapabilities();

    public Long getWorkCalendarId();
    public void setWorkCalendarId(Long WorkCalendarId);
    public WorkCalendar getWorkCalendar();

    @IS_VIRTUAL
    public boolean isRentable();


    @IS_VIRTUAL
    public void computeHash();

}
