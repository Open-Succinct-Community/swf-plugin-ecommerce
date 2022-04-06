package in.succinct.plugins.ecommerce.db.model.inventory;

import com.venky.core.util.ObjectUtil;
import com.venky.digest.Encryptor;
import com.venky.swf.db.Database;
import com.venky.swf.db.annotations.column.IS_VIRTUAL;
import com.venky.swf.db.model.reflection.ModelReflector;
import com.venky.swf.db.table.ModelImpl;
import com.venky.swf.plugins.background.core.TaskManager;
import com.venky.swf.plugins.calendar.db.model.WorkCalendar;
import com.venky.swf.plugins.lucene.index.LuceneIndexer;
import com.venky.swf.plugins.lucene.index.background.IndexManager;
import com.venky.swf.plugins.lucene.index.background.IndexTask;
import com.venky.swf.plugins.lucene.index.background.IndexTask.Operation;
import com.venky.swf.routing.Config;
import com.venky.swf.sql.Conjunction;
import com.venky.swf.sql.Expression;
import com.venky.swf.sql.Operator;
import com.venky.swf.sql.Select;
import in.succinct.plugins.ecommerce.db.model.assets.Asset;
import in.succinct.plugins.ecommerce.db.model.assets.Capability;
import in.succinct.plugins.ecommerce.db.model.attributes.AssetCodeAttribute;
import in.succinct.plugins.ecommerce.db.model.catalog.Item;
import in.succinct.plugins.ecommerce.db.model.inventory.InventoryCalculator.ATP;
import in.succinct.plugins.ecommerce.db.model.participation.Company;
import in.succinct.plugins.ecommerce.db.model.participation.Facility;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;

public class InventoryImpl extends  ModelImpl<Inventory> {
	public InventoryImpl(){
		super();
	}
	public InventoryImpl(Inventory proxy) {
		super(proxy);
	}

	public void adjust(double delta,String comment){
		Inventory inv = getProxy();
		if (!inv.isInfinite()) {
			inv.setQuantity(inv.getQuantity() + delta);
		}
		JSONObject object;
		try {
			object = (JSONObject)JSONValue.parse(comment);
		}catch (Exception ex){
			object = null ;
		}
		if (object == null){
			object = new JSONObject();
			object.put("Comment",comment);
		}
		for (String f: inv.getRawRecord().getDirtyFields()){
			JSONObject audit = new JSONObject();
			object.put(f, audit);
			audit.put("old",inv.getRawRecord().getOldValue(f));
			audit.put("new",inv.getReflector().get(inv,f));
		}
		inv.save();
		InventoryAudit audit = Database.getTable(InventoryAudit.class).newRecord();
		audit.setInventoryId(inv.getId());
		audit.setAuditQuantity(delta);
		audit.setComment(object.toString());
		audit.save();
	}

	public void computeHash() {
		Inventory inventory = getProxy();
		Item item = inventory.getSku().getItem().getRawRecord().getAsProxy(Item.class);
		if (item.getReflector().isVoid(item.getAssetCodeId())){
			return;
		}


		Set<Long> allowedAttributeIds = new HashSet<>();
		item.getAssetCode().getAssetCodeAttributes().forEach(aca->{
			if (ObjectUtil.equals(aca.getAttributeType(), AssetCodeAttribute.ATTRIBUTE_TYPE_INVENTORY)){
				allowedAttributeIds.add(aca.getAttributeId());
			}
		});

		StringBuilder builder = new StringBuilder();
		builder.append(item.getAssetCodeId());

		SortedSet<Long> set = new TreeSet<>();
		if (!inventory.getRawRecord().isNewRecord()){
			inventory.getInventoryAttributes().forEach(a->{
				if (allowedAttributeIds.contains(a.getAttributeValue().getAttributeId())){
					set.add(a.getAttributeValueId());
				}else {
					a.destroy();
				}
			});
		}
		builder.append(set);

		inventory.setInventoryHash(Encryptor.encrypt(builder.toString()));
		inventory.save();
		reindex(inventory);

	}
	public void reindex(Inventory inventory){

		LuceneIndexer indexer = LuceneIndexer.instance(Inventory.class);

		try {
			final Document document = indexer.getDocument(inventory.getRawRecord());
			for (InventoryAttributeValue a : inventory.getInventoryAttributes()) {
				indexer.addTextField(document, a.getAttribute().getName(), a.getAttributeValue().getPossibleValue(), Store.YES);
			}
			IndexTask task = new IndexTask();
			task.setOperation(Operation.MODIFY);
			task.setDirectory(Database.getTable(Inventory.class).getTableName());
			task.setDocuments(Arrays.asList(document));
			TaskManager.instance().executeAsync(task,false);

		}catch (Exception ex){
			Config.instance().getLogger(getModelClass().getName()).log(Level.INFO,"Reindex on inventory failed" ,ex);
		}

	}

	public List<Asset> getAssets() {
		Inventory inventory  = getProxy();
		Item item = inventory.getSku().getItem().getRawRecord().getAsProxy(Item.class);
		ModelReflector<Capability> ref = ModelReflector.instance(Capability.class);
		List<Asset> assets =  new Select().from(Capability.class).where(new Expression(ref.getPool(), Conjunction.AND).
				add(new Expression(ref.getPool(),"inventory_hash", Operator.EQ, inventory.getInventoryHash())).
				add(new Expression(ref.getPool(), "asset_code_id",Operator.EQ,item.getAssetCodeId()))).execute();
		return assets;
	}

	public WorkCalendar getWorkCalendar() {
		Inventory inventory = getProxy();
		WorkCalendar calendar = null;
		//If item facility calendar is required, one could define that too and use here ... Possible extenstion to design of work calendar.
		Facility facility = inventory.getFacility();
		if (facility.getWorkCalendarId() != null){
			calendar = facility.getWorkCalendar();
		}
		if (calendar == null){
			Company company = facility.getCompany().getRawRecord().getAsProxy(Company.class);
			if (company.getWorkCalendarId() != null){
				calendar = company.getWorkCalendar();
			}
		}
		return calendar;
	}
	private InventoryCalculator inventoryCalculator = null;
	public InventoryCalculator getInventoryCalculator(){
		Inventory inventory = getProxy();
		if (inventory.getRawRecord().isNewRecord()){
			return null;
		}
		if (inventoryCalculator == null){
			inventoryCalculator = new InventoryCalculator(getProxy().getSku(),getProxy().getFacility());
		}
		return inventoryCalculator;
	}

	public boolean isPublished(){
		Inventory inventory = getProxy();
		/* if (inventory.getRawRecord().isNewRecord()){
			return false;
		} Can  be external inventory
		*/
		InventoryCalculator inventoryCalculator = getInventoryCalculator();

		return inventory.isEnabled() && ( inventory.isInfinite() || (inventoryCalculator != null && inventoryCalculator.getTotalInventory() > 0));
	}

	public double  getAvailableToPromise() {
		InventoryCalculator calculator = getInventoryCalculator();
		if (calculator == null){
			return 0;
		}
		return calculator.getTotalInventory();
	}

	public double getDemandQuantity() {
		InventoryCalculator calculator = getInventoryCalculator();
		if (calculator == null){
			return 0;
		}
		return calculator.getPendingShip();
	}


}
