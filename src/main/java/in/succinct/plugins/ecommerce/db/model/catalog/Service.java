package in.succinct.plugins.ecommerce.db.model.catalog;

import com.venky.swf.db.annotations.column.COLUMN_DEF;
import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.defaulting.StandardDefault;
import com.venky.swf.db.annotations.column.indexing.Index;
import com.venky.swf.db.annotations.column.pm.PARTICIPANT;
import com.venky.swf.db.annotations.column.ui.PROTECTION;
import com.venky.swf.db.annotations.column.ui.PROTECTION.Kind;
import com.venky.swf.db.model.Model;
import in.succinct.plugins.ecommerce.db.model.participation.Company;

public interface Service extends Model {
    @PARTICIPANT
    @UNIQUE_KEY
    @PROTECTION(Kind.NON_EDITABLE)
    public Long getCompanyId();
    public void setCompanyId(Long id);
    public Company getCompany();

    @UNIQUE_KEY
    @Index
    public String getName();
    public void setName(String name);


    public double getMaxRetailPrice();
    public void setMaxRetailPrice(double sellingPrice);

    @COLUMN_DEF(StandardDefault.ZERO)
    public double getTaxRate();
    public void setTaxRate(double taxRate);

}
