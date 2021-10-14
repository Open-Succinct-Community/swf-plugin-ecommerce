package in.succinct.plugins.ecommerce.db.model.inventory;

import com.venky.swf.db.annotations.column.IS_NULLABLE;
import com.venky.swf.db.annotations.column.COLUMN_DEF;
import com.venky.swf.db.annotations.column.COLUMN_SIZE;
import com.venky.swf.db.annotations.column.IS_VIRTUAL;
import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.defaulting.StandardDefault;
import com.venky.swf.db.annotations.column.indexing.Index;
import com.venky.swf.db.annotations.column.pm.PARTICIPANT;
import com.venky.swf.db.annotations.column.ui.HIDDEN;
import com.venky.swf.db.annotations.column.ui.PROTECTION;
import com.venky.swf.db.annotations.column.ui.PROTECTION.Kind;
import com.venky.swf.db.annotations.column.ui.WATERMARK;
import com.venky.swf.db.annotations.model.MENU;
import com.venky.swf.db.annotations.model.ORDER_BY;
import com.venky.swf.db.annotations.model.validations.UniqueKeyValidator;
import com.venky.swf.db.model.Model;
import com.venky.swf.plugins.collab.db.model.CompanySpecific;
import com.venky.swf.sql.Conjunction;
import com.venky.swf.sql.Expression;
import com.venky.swf.sql.Operator;
import com.venky.swf.sql.Select;
import in.succinct.plugins.ecommerce.db.model.attachments.Attachment;
import in.succinct.plugins.ecommerce.db.model.catalog.Item;
import in.succinct.plugins.ecommerce.db.model.catalog.UnitOfMeasure;
import in.succinct.plugins.ecommerce.db.model.participation.Company;

import java.sql.Date;
import java.util.List;


@MENU("Inventory")
@ORDER_BY("COMPANY_ID,PUBLISHED DESC,DISPLAY_SEQUENCE_NO,ID")
public interface Sku extends Model,Container, CompanySpecific {

	@PARTICIPANT(redundant = true)
	@UNIQUE_KEY("SKU2,SKU3")
	//@PROTECTION(Kind.NON_EDITABLE)
	@Index
	public long getItemId();
	public void setItemId(long id);
	public Item getItem();


	@IS_NULLABLE
	@UNIQUE_KEY(value = "SKU3",allowMultipleRecordsWithNull = true)
	@PARTICIPANT(redundant = true)
	@Index
	public Long getPackagingUOMId();
	public void setPackagingUOMId(Long PackagingUnitOfMeasureId);
	public UnitOfMeasure getPackagingUOM();


	@UNIQUE_KEY(value = "UPC",allowMultipleRecordsWithNull = true)
	public String getSkuCode();
	public void setSkuCode(String code);

	//*Denormalized from Item.
	@UNIQUE_KEY("SKU")
	@PROTECTION(Kind.NON_EDITABLE)
	public Long getCompanyId();
	public void setCompanyId(long  id);
	public Company getCompany();


	@Index
	@UNIQUE_KEY("SKU,SKU2")
	public String getName();
	public void setName(String name);

	@Index
	public boolean isPublished();
	public  void setPublished(boolean published);

	@Index
	public String getShortDescription();
	public void setShortDescription(String shortDescription);

	@Index
	@COLUMN_SIZE(8192)
	public String getLongDescription();
	public void setLongDescription(String longDescription);

	@WATERMARK("Enter absolute url of small sized product image")
	public String getSmallImageUrl();
	public void setSmallImageUrl(String url);

	@WATERMARK("Enter absolute url of large sized product image")
	public String getLongImageUrl();
	public void setLongImageUrl(String url);

	@COLUMN_SIZE(1024)
	public String getBenefits();
	public void setBenefits(String benefits);

	public String getCompositionUnitDescription();
	public void setCompositionUnitDescription(String compositionUnitDescription);

	public List<ProductContent> getProductContents();

	public Double getLength();
	@PARTICIPANT(redundant=true)
	public Long getLengthUOMId();

	public Double getWidth();
	@PARTICIPANT(redundant=true)
	public Long getWidthUOMId();


	public Double getHeight();
	@PARTICIPANT(redundant=true)
	public Long getHeightUOMId();

	public Double getWeight();
	@PARTICIPANT(redundant=true)
	public Long getWeightUOMId();

	public static Sku  find(long companyId, String itemName, String uomName) {
		Item item = Item.find(companyId,itemName);
		UnitOfMeasure uom = UnitOfMeasure.getMeasure("Packaging",uomName);
		if (item == null || uom == null){
			return null;
		}
		Select select = new Select().from(Sku.class);
		Expression where = new Expression(select.getPool(), Conjunction.AND);
		where.add(new Expression(select.getPool(),"ITEM_ID",Operator.EQ,item.getId()));
		where.add(new Expression(select.getPool(),"PACKAGING_U_O_M_ID",Operator.EQ,uom.getId()));
		where.add(new Expression(select.getPool(),"COMPANY_ID",Operator.EQ,companyId));

		List<Sku> skus = select.where(where).execute();
		if (skus.size() == 0) {
			return null;
		}else if(skus.size() > 1) {
			throw new UniqueKeyValidator.UniqueConstraintViolatedException("CompanyId = " + companyId + ", Item: " + itemName + ", Uom : " + uomName);
		}
		return skus.get(0);
	}
	public static Sku  find(long companyId, String name) {
		Select select = new Select().from(Sku.class);
		Expression where = new Expression(select.getPool(), Conjunction.AND);
		where.add(new Expression(select.getPool(),"NAME",Operator.EQ,name));
		where.add(new Expression(select.getPool(),"COMPANY_ID",Operator.EQ,companyId));

		List<Sku> skus = select.where(where).execute();

		if (skus.size() == 0) {
			return null;
		}else if(skus.size() > 1) {
			throw new UniqueKeyValidator.UniqueConstraintViolatedException("CompanyId = " + companyId + ", Sku: " + name);
		}
		return skus.get(0);

	}

	public List<Inventory> getInventory();

	@COLUMN_DEF(StandardDefault.ZERO)
    public double getMaxRetailPrice();
    public void setMaxRetailPrice(double sellingPrice);

    @IS_VIRTUAL
	public double getSellingPrice(); //Treated like mrp.  it promotional mrp.


    @COLUMN_DEF(StandardDefault.ZERO)
    public double getTaxRate();
    public void setTaxRate(double taxRate);


    @COLUMN_DEF(StandardDefault.ZERO)
    public int getDisplaySequenceNo();
    public void setDisplaySequenceNo(int displaySequenceNo);

    public List<SkuDiscountPlan> getDiscountPlans();

	@IS_VIRTUAL
	public Long getActiveDiscountPlanId();
    public SkuDiscountPlan getActiveDiscountPlan();
	
	


	List<Attachment> getAttachments();

	@COLUMN_DEF(StandardDefault.BOOLEAN_FALSE)
	public boolean isPublishedToMarketPlace();
	public void setPublishedToMarketPlace(boolean publishedToMarketPlace);

	public Date getExpectedToBeAvailableBy();
	public void setExpectedToBeAvailableBy(Date date);
	
	@Index
	@COLUMN_DEF(StandardDefault.BOOLEAN_FALSE)
	public boolean isActive();
	public  void setActive(boolean isActive);

}
