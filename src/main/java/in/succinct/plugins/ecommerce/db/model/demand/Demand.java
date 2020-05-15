package in.succinct.plugins.ecommerce.db.model.demand;

import com.venky.swf.db.annotations.column.IS_NULLABLE;
import com.venky.swf.db.annotations.column.IS_VIRTUAL;
import com.venky.swf.db.annotations.column.ui.HIDDEN;
import com.venky.swf.db.annotations.model.EXPORTABLE;
import com.venky.swf.db.annotations.model.MENU;
import com.venky.swf.plugins.calendar.db.model.WorkSlot;
import in.succinct.plugins.ecommerce.db.model.assets.Asset;
import in.succinct.plugins.ecommerce.db.model.inventory.Inventory;
import com.venky.core.util.Bucket;
import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.model.Model;
import in.succinct.plugins.ecommerce.db.model.inventory.Sku;
import in.succinct.plugins.ecommerce.db.model.participation.Facility;

import java.sql.Date;
import java.sql.Timestamp;

@EXPORTABLE(false)
@MENU("Inventory")
public interface Demand extends Model{
    @UNIQUE_KEY
    @HIDDEN
    public long getInventoryId();
    public void setInventoryId(long id);
    public Inventory getInventory();

    @IS_VIRTUAL
    public long getFacilityId();
    public Facility getFacility();

    @IS_VIRTUAL
    public long getSkuId();
    public Sku getSku();



    public Bucket getQuantity();
    public void setQuantity(Bucket demand);

    @IS_NULLABLE
    @UNIQUE_KEY(allowMultipleRecordsWithNull = false)
    public Long getAssetId();
    public void setAssetId(Long id);
    public Asset getAsset();


    @IS_NULLABLE
    @UNIQUE_KEY(allowMultipleRecordsWithNull = false)
    public Date getDemandDate();
    public void setDemandDate(Date date);



    @IS_NULLABLE
    @UNIQUE_KEY(allowMultipleRecordsWithNull = false)
    public Long getWorkSlotId();
    public void setWorkSlotId(Long id);
    public WorkSlot getWorkSlot();
}
