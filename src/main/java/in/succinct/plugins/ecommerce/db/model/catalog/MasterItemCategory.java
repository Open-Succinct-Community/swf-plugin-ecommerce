package in.succinct.plugins.ecommerce.db.model.catalog;

import in.succinct.plugins.ecommerce.db.model.participation.Company;
import com.venky.swf.db.annotations.column.IS_NULLABLE;
import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.pm.PARTICIPANT;
import com.venky.swf.db.annotations.column.relationship.CONNECTED_VIA;
import com.venky.swf.db.annotations.column.ui.PROTECTION;
import com.venky.swf.db.annotations.column.ui.PROTECTION.Kind;
import com.venky.swf.db.annotations.model.validations.UniqueKeyValidator;
import com.venky.swf.db.model.Model;
import com.venky.swf.sql.Conjunction;
import com.venky.swf.sql.Expression;
import com.venky.swf.sql.Operator;
import com.venky.swf.sql.Select;

import java.util.List;

public interface MasterItemCategory extends Model{
	@PARTICIPANT
	@PROTECTION(Kind.NON_EDITABLE)
	@UNIQUE_KEY
	public long getCompanyId();
	public void setCompanyId(long id);
	public Company getCompany();
	
	@UNIQUE_KEY
	@IS_NULLABLE(false)
	public String getName();
	public void setName(String name);
	
	public List<MasterItemCategoryValue> getAllowedValues();

	static MasterItemCategory find(long companyId,String name,boolean lock){
        Select select = new Select(lock).from(MasterItemCategory.class);
        Expression where = new Expression(select.getPool(), Conjunction.AND);
        where.add(new Expression(select.getPool(),"NAME", Operator.EQ,name));
        where.add(new Expression(select.getPool(),"COMPANY_ID",Operator.EQ,companyId));

        List<MasterItemCategory> categories = select.where(where).execute();
        if (categories.size() == 0) {
            return null;
        }else if(categories.size() > 1) {
            throw new UniqueKeyValidator.UniqueConstraintViolatedException("CompanyId:" + companyId + ", name: " + name);
        }
        return categories.get(0);
    }

    @CONNECTED_VIA("MASTER_ITEM_CATEGORY_ID")
    List<ItemCategory> getItemCategories();

}
