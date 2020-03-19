package in.succinct.plugins.ecommerce.controller;

import com.venky.swf.controller.ModelController;
import com.venky.swf.controller.annotations.RequireLogin;
import com.venky.swf.db.model.Model;
import com.venky.swf.path.Path;
import com.venky.swf.sql.Select;
import com.venky.swf.sql.Select.ResultFilter;
import com.venky.swf.views.View;
import in.succinct.plugins.ecommerce.db.model.inventory.ProductContent;
import in.succinct.plugins.ecommerce.db.model.inventory.Sku;

import java.util.Arrays;
import java.util.Iterator;
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

    @RequireLogin(false)
    public View search() {
        return super.search();
    }

    @RequireLogin(false)
    public View search(String qry) {
        return super.search(qry);
    }

    @Override
    @RequireLogin(false)
    public View index() {
        return super.index();
    }

    protected Select.ResultFilter<Sku> getFilter() {
        if (getSessionUser() == null){
            return new ResultFilter<Sku>() {
                @Override
                public boolean pass(Sku record) {
                    return record.isPublished();
                }
            };
        }else {
            return super.getFilter();
        }
    }

}
