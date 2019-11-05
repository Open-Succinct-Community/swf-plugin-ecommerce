package in.succinct.plugins.ecommerce.controller;

import com.venky.swf.controller.ModelController;
import com.venky.swf.db.model.Model;
import com.venky.swf.path.Path;
import in.succinct.plugins.ecommerce.db.model.inventory.ProductContent;
import in.succinct.plugins.ecommerce.db.model.inventory.Sku;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SkusController extends ModelController<Sku> {
    public SkusController(Path path) {
        super(path);
    }

    @Override
    protected Map<Class<? extends Model>, List<String>> getIncludedModelFields() {
        Map<Class<? extends Model>,List<String>> map = super.getIncludedModelFields();
        map.put(ProductContent.class, Arrays.asList("NAME","VALUE"));
        return map;
    }
}