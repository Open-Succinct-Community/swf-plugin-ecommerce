package in.succinct.plugins.ecommerce.controller;

import com.venky.swf.controller.ModelController;
import com.venky.swf.db.model.Model;
import com.venky.swf.db.model.reflection.ModelReflector;
import com.venky.swf.path.Path;
import in.succinct.plugins.ecommerce.db.model.assets.Asset;
import in.succinct.plugins.ecommerce.db.model.attributes.AssetCode;
import in.succinct.plugins.ecommerce.db.model.catalog.Item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemsController  extends ModelController<Item> {
    public ItemsController(Path path) {
        super(path);
    }


    @Override
    protected Map<Class<? extends Model>, List<String>> getIncludedModelFields() {
        Map<Class<? extends Model>, List<String>> map =  super.getIncludedModelFields();
        includedModelFields(map);
        return map;
    }

    public static void includedModelFields(Map<Class<? extends Model>, List<String>> map){
        List<String> itemFields = map.getOrDefault(Item.class, new ArrayList<>());
        itemFields.addAll(ModelReflector.instance(Item.class).getFields());
        map.put(Item.class,itemFields);

        List<String> assetFields = map.getOrDefault(AssetCode.class, new ArrayList<>());
        assetFields.addAll(Arrays.asList("CODE","DESCRIPTION","LONG_DESCRIPTION", "GST_PCT"));
        map.put(AssetCode.class,assetFields);

    }
}
