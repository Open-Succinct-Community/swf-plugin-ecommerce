package in.succinct.plugins.ecommerce.db.model.catalog;

import com.venky.digest.Encryptor;
import com.venky.swf.db.annotations.column.IS_NULLABLE;
import com.venky.swf.db.annotations.column.IS_VIRTUAL;
import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.indexing.Index;
import com.venky.swf.db.annotations.column.pm.PARTICIPANT;
import com.venky.swf.db.annotations.column.ui.PROTECTION;
import com.venky.swf.db.annotations.column.ui.PROTECTION.Kind;
import com.venky.swf.db.annotations.model.MENU;
import com.venky.swf.db.annotations.model.validations.UniqueKeyValidator;
import com.venky.swf.db.model.Model;
import com.venky.swf.plugins.collab.db.model.CompanySpecific;
import com.venky.swf.sql.Conjunction;
import com.venky.swf.sql.Expression;
import com.venky.swf.sql.Operator;
import com.venky.swf.sql.Select;
import in.succinct.plugins.ecommerce.db.model.assets.Asset;
import in.succinct.plugins.ecommerce.db.model.attributes.AssetCode;
import in.succinct.plugins.ecommerce.db.model.attributes.AttributeValue;
import in.succinct.plugins.ecommerce.db.model.inventory.Container;
import in.succinct.plugins.ecommerce.db.model.inventory.Sku;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

@MENU("Catalog")
public interface Item extends Container, Model, CompanySpecific {
	@PROTECTION(Kind.NON_EDITABLE)
	public Long getCompanyId();

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



    public static Item  find(long companyId,String name) {
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


	@IS_NULLABLE
	public Long getAssetCodeId();
	public void setAssetCodeId(Long id);
	public AssetCode getAssetCode();

	@IS_VIRTUAL
	public List<Asset> getAssets();


	@IS_VIRTUAL
	public boolean isRentable();

	public List<ItemAttributeValue> getAttributeValues();

	@UNIQUE_KEY(allowMultipleRecordsWithNull = false)
	public String getItemHash();
	public void setItemHash(String hash);

	@IS_VIRTUAL
	public void computeHash();

	@IS_VIRTUAL
	public String getHsn();

	public static String hash(AssetCode assetCode, Collection<AttributeValue> attributeValues){
		Set<Long> allowedAttributeIds = new HashSet<>();
		assetCode.getAssetCodeAttributes().forEach(a->allowedAttributeIds.add(a.getAttributeId()));

		StringBuilder builder = new StringBuilder();
		builder.append(assetCode.getId());

		SortedSet<Long> attributeValueIds = new TreeSet<>();
		for  (AttributeValue value : attributeValues){
			if (allowedAttributeIds.contains(value.getAttributeId())) {
				attributeValueIds.add(value.getId());
			}
		}

		builder.append(attributeValueIds);
		return Encryptor.encrypt(builder.toString());
	}
}
