package in.succinct.plugins.ecommerce.db.model.attributes;

import com.venky.swf.db.annotations.column.IS_NULLABLE;
import com.venky.swf.db.annotations.column.IS_VIRTUAL;
import com.venky.swf.db.annotations.column.indexing.Index;
import com.venky.swf.db.annotations.model.HAS_DESCRIPTION_FIELD;
import com.venky.swf.db.model.Model;

import java.util.List;

@HAS_DESCRIPTION_FIELD("DESCRIPTION")
public interface AssetCode extends Model {
    @Index
    @IS_NULLABLE(false)
    public String getCode();
    public void setCode(String code);

    @Index
    @IS_NULLABLE(false)
    public String getDescription();
    public void setDescription(String description);


    @IS_NULLABLE
    public Long getRentalAssetCodeId();
    public void setRentalAssetCodeId(Long RentalAssetCodeId);
    public AssetCode getRentalAssetCode();


    @IS_VIRTUAL
    @Index
    public String getLongDescription();

    public Double getGstPct();
    public void setGstPct(Double gstPct);

    @IS_VIRTUAL
    public boolean isHsn();

    @IS_VIRTUAL
    public boolean isSac();

    public List<AssetCodeAttribute> getAssetCodeAttributes();



}
