package in.succinct.plugins.ecommerce.db.model.inventory;

import com.venky.swf.db.annotations.column.COLUMN_DEF;
import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.defaulting.StandardDefault;
import com.venky.swf.db.annotations.model.ORDER_BY;
import com.venky.swf.db.model.Model;

import java.sql.Date;

@ORDER_BY("SKU_ID , EFFECTIVE_FROM DESC")
//K2 Defined so that as ActiveSkuPlan, we can get all these columns.
public interface SkuDiscountPlan extends Model {
    @UNIQUE_KEY("K1,K2")
    public long getSkuId();
    public void setSkuId(long id);
    public Sku getSku();

    @UNIQUE_KEY("K1,K2")
    @COLUMN_DEF(StandardDefault.CURRENT_DATE)
    public Date getEffectiveFrom();
    public void setEffectiveFrom(Date date);

    @COLUMN_DEF(value = StandardDefault.SOME_VALUE ,args = "40")
    @UNIQUE_KEY("K2")
    public double getRetailerDiscountPct();
    public void setRetailerDiscountPct(double retailerDiscountPct);

    @COLUMN_DEF(value = StandardDefault.SOME_VALUE,  args = "46")
    @UNIQUE_KEY("K2")
    public double getDistributorDiscountPct();
    public void setDistributorDiscountPct(double distributerDiscountPct);

    @COLUMN_DEF(StandardDefault.ZERO)
    @UNIQUE_KEY("K2")
    public double getGeneralDiscountPct();
    public void setGeneralDiscountPct(double generalDiscountPct);

    @UNIQUE_KEY("K2")
    public double getBuy();
    public void setBuy(double buy);

    @UNIQUE_KEY("K2")
    public double getFree();
    public void setFree(double quantityFree);


}
