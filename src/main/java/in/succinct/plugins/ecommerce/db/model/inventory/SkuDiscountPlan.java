package in.succinct.plugins.ecommerce.db.model.inventory;

import com.venky.swf.db.annotations.column.COLUMN_DEF;
import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.defaulting.StandardDefault;
import com.venky.swf.db.annotations.model.ORDER_BY;
import com.venky.swf.db.model.Model;

import java.sql.Date;

@ORDER_BY("SKU_ID , EFFECTIVE_FROM DESC")
public interface SkuDiscountPlan extends Model {
    @UNIQUE_KEY
    public long getSkuId();
    public void setSkuId(long id);
    public Sku getSku();

    @UNIQUE_KEY
    public Date getEffectiveFrom();
    public void setEffectiveFrom(Date date);

    @COLUMN_DEF(value = StandardDefault.SOME_VALUE ,args = "40")
    public double getRetailerDiscountPct();
    public void setRetailerDiscountPct(double retailerDiscountPct);

    @COLUMN_DEF(value = StandardDefault.SOME_VALUE,  args = "46")
    public double getDistributorDiscountPct();
    public void setDistributorDiscountPct(double distributerDiscountPct);


    public double getBuy();
    public void setBuy(double buy);

    public double getFree();
    public void setFree(double quantityFree);


}
