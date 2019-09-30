package in.succinct.plugins.ecommerce.db.model.catalog;

import com.venky.swf.db.annotations.column.COLUMN_DEF;
import com.venky.swf.db.annotations.column.IS_NULLABLE;
import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.defaulting.StandardDefault;
import com.venky.swf.db.annotations.column.indexing.Index;
import com.venky.swf.db.annotations.column.pm.PARTICIPANT;
import com.venky.swf.db.annotations.column.ui.PROTECTION;
import com.venky.swf.db.annotations.column.ui.PROTECTION.Kind;
import com.venky.swf.db.annotations.column.validations.Enumeration;
import com.venky.swf.db.annotations.model.CONFIGURATION;
import com.venky.swf.db.annotations.model.MENU;
import com.venky.swf.db.model.Model;
import com.venky.swf.db.model.User;
import com.venky.swf.plugins.collab.db.model.CompanySpecific;
import com.venky.swf.plugins.security.db.model.Role;
import in.succinct.plugins.ecommerce.db.model.participation.Company;
import in.succinct.plugins.ecommerce.db.model.service.ServiceCancellationReason;
import in.succinct.plugins.ecommerce.db.model.service.ServiceResource;

import java.util.List;

@MENU("Catalog")
@CONFIGURATION
public interface Service extends Model, CompanySpecific {
    @UNIQUE_KEY("K2")
    @IS_NULLABLE
    @Index
    public String getCode();
    public void setCode(String code);

    @UNIQUE_KEY
    @Index
    public String getName();
    public void setName(String name);

    @UNIQUE_KEY("K1,K2")
    public Long getCompanyId();



    public String getTaxCode();
    public void setTaxCode(String taxCode);

    public double getMaxRetailPrice();
    public void setMaxRetailPrice(double sellingPrice);

    @COLUMN_DEF(StandardDefault.ZERO)
    public double getTaxRate();
    public void setTaxRate(double taxRate);

    public List<ServiceCancellationReason> getServiceCancellationReasons();
    public List<ServiceResource> getServiceResources();


}
