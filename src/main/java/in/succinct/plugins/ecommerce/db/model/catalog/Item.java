package in.succinct.plugins.ecommerce.db.model.catalog;

import in.succinct.plugins.ecommerce.db.model.inventory.Container;
import in.succinct.plugins.ecommerce.db.model.inventory.Sku;
import in.succinct.plugins.ecommerce.db.model.participation.Company;
import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.indexing.Index;
import com.venky.swf.db.annotations.column.pm.PARTICIPANT;
import com.venky.swf.db.annotations.column.ui.PROTECTION;
import com.venky.swf.db.annotations.column.ui.PROTECTION.Kind;
import com.venky.swf.db.annotations.model.MENU;
import com.venky.swf.db.annotations.model.validations.UniqueKeyValidator;
import com.venky.swf.db.model.Model;
import com.venky.swf.sql.Conjunction;
import com.venky.swf.sql.Expression;
import com.venky.swf.sql.Operator;
import com.venky.swf.sql.Select;

import java.util.List;

@MENU("Catalog")
public interface Item extends Container, Model{
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
	
	List<Sku> getSkus();
	List<ItemCategory> getItemCategories();

    public ItemCategory getItemCategory(String categoryName);
    public ItemCategory setItemCategory(String categoryName,String categoryValue);



    public static Item  find(int companyId,String name) {
        Select select = new Select().from(Item.class);
        Expression where = new Expression(select.getPool(), Conjunction.AND);
        where.add(new Expression(select.getPool(),"NAME",Operator.EQ,name));
        where.add(new Expression(select.getPool(),"COMPANY_ID",Operator.EQ,companyId));

        List<Item> items = select.where(where).orderBy("ID").execute();
        if (items.size() == 0) {
            return null;
        }else if(items.size() > 1) {
            throw new UniqueKeyValidator.UniqueConstraintViolatedException("CompanyId:" + companyId + ", Item: " + name);
        }
        return items.get(0);

    }
}
