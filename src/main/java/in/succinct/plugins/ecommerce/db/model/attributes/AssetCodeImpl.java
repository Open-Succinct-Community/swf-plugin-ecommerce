package in.succinct.plugins.ecommerce.db.model.attributes;

import com.venky.core.string.StringUtil;
import com.venky.swf.db.table.ModelImpl;

public class AssetCodeImpl extends ModelImpl<AssetCode> {
    public AssetCodeImpl(AssetCode code){
        super(code);
    }
    public String getLongDescription(){
        return getProxy().getCode() + " - " + getProxy().getDescription();
    }

    public boolean isSac(){
        return getProxy().getCode().startsWith("99");
    }

    public boolean isHsn(){
        return !isSac();
    }
}
