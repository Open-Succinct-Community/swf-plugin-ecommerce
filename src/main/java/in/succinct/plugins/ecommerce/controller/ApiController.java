package in.succinct.plugins.ecommerce.controller;

import com.venky.core.util.ObjectUtil;
import com.venky.swf.controller.Controller;
import com.venky.swf.db.annotations.column.ui.mimes.MimeType;
import com.venky.swf.db.model.Model;
import com.venky.swf.db.model.io.ModelIOFactory;
import com.venky.swf.db.model.reflection.ModelReflector;
import com.venky.swf.integration.FormatHelper;
import com.venky.swf.integration.IntegrationAdaptor;
import com.venky.swf.integration.JSON;
import com.venky.swf.path.Path;
import com.venky.swf.views.View;
import in.succinct.plugins.ecommerce.agents.inventory.AdjustInventoryTask;
import in.succinct.plugins.ecommerce.db.model.apis.Cancel;
import in.succinct.plugins.ecommerce.db.model.apis.Pack;
import in.succinct.plugins.ecommerce.db.model.attachments.Attachment;
import in.succinct.plugins.ecommerce.db.model.attributes.AssetCode;
import in.succinct.plugins.ecommerce.db.model.catalog.Item;
import in.succinct.plugins.ecommerce.db.model.catalog.UnitOfMeasure;
import in.succinct.plugins.ecommerce.db.model.inventory.AdjustmentRequest;
import in.succinct.plugins.ecommerce.db.model.inventory.Inventory;
import in.succinct.plugins.ecommerce.db.model.inventory.Sku;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class ApiController extends Controller {

    public ApiController(Path path) {
        super(path);
    }

    public View pack() {
        IntegrationAdaptor<Pack, JSON> integrationAdaptor = null;
        if (getPath().getProtocol() != MimeType.TEXT_HTML) {
            integrationAdaptor = IntegrationAdaptor.instance(Pack.class, FormatHelper.getFormatClass(path.getProtocol()));
        }
        if (integrationAdaptor == null) {
            throw new RuntimeException("Unsupported input format");
        }
        if (!getPath().getRequest().getMethod().equalsIgnoreCase("POST")) {
            throw new RuntimeException("Unsupported request method. Only POST is allowed");
        }
        List<Pack> inputs = integrationAdaptor.readRequest(getPath());
        inputs.forEach(pack -> {
            pack.pack();
        });
        return integrationAdaptor.createResponse(getPath(), inputs);
    }

    public View cancel() {
        IntegrationAdaptor<Cancel, JSON> integrationAdaptor = null;
        if (getPath().getProtocol() != MimeType.TEXT_HTML) {
            integrationAdaptor = IntegrationAdaptor.instance(Cancel.class, FormatHelper.getFormatClass(path.getProtocol()));
        }
        if (integrationAdaptor == null) {
            throw new RuntimeException("Unsupported input format");
        }
        if (!getPath().getRequest().getMethod().equalsIgnoreCase("POST")) {
            throw new RuntimeException("Unsupported request method. Only POST is allowed");
        }
        List<Cancel> inputs = integrationAdaptor.readRequest(getPath());
        inputs.forEach(cancel -> {
            cancel.reject();
        });
        return integrationAdaptor.createResponse(getPath(), inputs);
    }

    public <T> View adjust() throws Exception{
        IntegrationAdaptor<AdjustmentRequest, T> integrationAdaptor = null;
        if (getPath().getProtocol() != MimeType.TEXT_HTML) {
            integrationAdaptor = IntegrationAdaptor.instance(AdjustmentRequest.class, FormatHelper.getFormatClass(path.getProtocol()));
        }
        if (integrationAdaptor == null) {
            throw new RuntimeException("Unsupported input format");
        }
        if (!getPath().getRequest().getMethod().equalsIgnoreCase("POST")) {
            throw new RuntimeException("Unsupported request method. Only POST is allowed");
        }
        FormatHelper<T> helper =  FormatHelper.instance(integrationAdaptor.getMimeType(),getPath().getInputStream());
        List<AdjustmentRequest> requests = AdjustmentRequest.adjust(helper);
        Map<Class<? extends Model>, List<String>> requestFields = getAdjustmentRequestFields();

        return integrationAdaptor.createResponse(getPath(),requests,requestFields.get(AdjustmentRequest.class),new HashSet<>(),
                requestFields);
    }

    public static Map<Class<? extends Model>, List<String>> getAdjustmentRequestFields() {
        Map<Class<? extends Model>, List<String>> map =  new HashMap<>();
        map.put(AdjustmentRequest.class,Arrays.asList("ID","INVENTORY_ID","NEW_PRODUCT","ADJUSTMENT_QUANTITY"));
        map.put(Inventory.class, ModelReflector.instance(Inventory.class).getFields());
        List<String> itemFields = ModelReflector.instance(Item.class).getUniqueFields();
        itemFields.add("ASSET_CODE_ID");

        map.put(Item.class, itemFields);

        List<String> skuFields = ModelReflector.instance(Sku.class).getUniqueFields();
        skuFields.add("MAX_RETAIL_PRICE");
        skuFields.add("TAX_RATE");

        map.put(Sku.class,skuFields);
        map.put(AssetCode.class, Arrays.asList("CODE","LONG_DESCRIPTION","GST_PCT"));
        map.put(Attachment.class,Arrays.asList("ATTACHMENT_URL"));

        return map;
    }
}
