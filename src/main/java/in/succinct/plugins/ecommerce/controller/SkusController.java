package in.succinct.plugins.ecommerce.controller;

import com.venky.swf.db.model.Model;
import com.venky.swf.path.Path;
import com.venky.swf.controller.ModelController;
import in.succinct.plugins.ecommerce.db.model.attachments.Attachment;
import in.succinct.plugins.ecommerce.db.model.inventory.Sku;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SkusController<T extends Sku> extends ModelController<T> {
    public SkusController(Path path) {
        super(path);
    }
    @Override
    protected Map<Class<? extends Model>, List<String>> getIncludedModelFields() {
        Map<Class<? extends Model>, List<String>> map =  super.getIncludedModelFields();
        ItemsController.includedModelFields(map);
        map.put(Attachment.class, Arrays.asList("ID","ATTACHMENT_URL"));
        return map;
    }
}
