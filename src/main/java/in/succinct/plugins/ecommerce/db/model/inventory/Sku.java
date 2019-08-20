package in.succinct.plugins.ecommerce.db.model.inventory;

import com.venky.swf.db.annotations.column.COLUMN_DEF;
import com.venky.swf.db.annotations.column.COLUMN_SIZE;
import com.venky.swf.db.annotations.column.IS_NULLABLE;
import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.defaulting.StandardDefault;
import com.venky.swf.db.annotations.column.indexing.Index;
import com.venky.swf.db.annotations.column.pm.PARTICIPANT;
import com.venky.swf.db.annotations.column.ui.PROTECTION;
import com.venky.swf.db.annotations.column.ui.PROTECTION.Kind;
import com.venky.swf.db.annotations.column.ui.WATERMARK;
import com.venky.swf.db.annotations.model.MENU;
import com.venky.swf.db.annotations.model.validations.UniqueKeyValidator;
import com.venky.swf.db.model.Model;
import com.venky.swf.plugins.collab.db.model.CompanySpecific;
import com.venky.swf.sql.Conjunction;
import com.venky.swf.sql.Expression;
import com.venky.swf.sql.Operator;
import com.venky.swf.sql.Select;
import in.succinct.plugins.ecommerce.db.model.catalog.Item;
import in.succinct.plugins.ecommerce.db.model.participation.Company;

import java.sql.Timestamp;
import java.util.List;


@MENU("Inventory")
public interface Sku extends Model,Container, CompanySpecific {

	@PARTICIPANT(redundant = true)
	@UNIQUE_KEY("SKU2")
	//@PROTECTION(Kind.NON_EDITABLE)
	public long getItemId();
	public void setItemId(long id);
	public Item getItem();


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
	@COLUMN_SIZE(512)
	public String getLongDescription();
	public void setLongDescription(String longDescription);

	@WATERMARK("Enter absolute url of small sized product image")
	public String getSmallImageUrl();
	public void setSmallImageUrl(String url);

	@WATERMARK("Enter absolute url of large sized product image")
	public String getLongImageUrl();
	public void setLongImageUrl(String url);

	@COLUMN_SIZE(256)
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

	public static Sku  find(int companyId, String name) {
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


    @COLUMN_DEF(StandardDefault.ZERO)
    public double getTaxRate();
    public void setTaxRate(double taxRate);



}
