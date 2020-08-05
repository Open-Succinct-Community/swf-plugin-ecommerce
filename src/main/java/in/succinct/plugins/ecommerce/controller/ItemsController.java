package in.succinct.plugins.ecommerce.controller;

import com.venky.swf.controller.ModelController;
import com.venky.swf.db.model.Model;
import com.venky.swf.path.Path;
import in.succinct.plugins.ecommerce.db.model.assets.Asset;
import in.succinct.plugins.ecommerce.db.model.catalog.Item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ItemsController  extends ModelController<Item> {
    public ItemsController(Path path) {
        super(path);
    }


    @Override
    protected Map<Class<? extends Model>, List<String>> getIncludedModelFields() {
        Map<Class<? extends Model>, List<String>> map =  super.getIncludedModelFields();
        List<String> itemFields = map.getOrDefault(Item.class, new ArrayList<>());
        itemFields.addAll(getReflector().getFields());
        map.put(Item.class,itemFields);

        List<String> assetFields = map.getOrDefault(Asset.class, new ArrayList<>());
        assetFields.addAll(Arrays.asList("CODE","DESCRIPTION","LONG_DESCRIPTION"));
        map.put(Asset.class,assetFields);

        return map;
    }
}
