package in.succinct.plugins.ecommerce.controller;

import com.venky.swf.controller.Controller;
import com.venky.swf.db.annotations.column.ui.mimes.MimeType;
import com.venky.swf.db.model.io.ModelIOFactory;
import com.venky.swf.integration.FormatHelper;
import com.venky.swf.integration.IntegrationAdaptor;
import com.venky.swf.integration.JSON;
import com.venky.swf.path.Path;
import com.venky.swf.views.View;
import in.succinct.plugins.ecommerce.agents.inventory.AdjustInventoryTask;
import in.succinct.plugins.ecommerce.db.model.apis.Cancel;
import in.succinct.plugins.ecommerce.db.model.apis.Pack;
import in.succinct.plugins.ecommerce.db.model.inventory.AdjustmentRequest;
import in.succinct.plugins.ecommerce.db.model.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

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
        List<T> adjustmentElements = helper.getChildElements("AdjustmentRequest");
        if (adjustmentElements.isEmpty()){
            T adjustmentElement = helper.getElementAttribute("AdjustmentRequest");
            adjustmentElements.add(adjustmentElement);
        }

        List<AdjustmentRequest> requests = new ArrayList<>();
        for (T adjustmentElement : adjustmentElements){
            T inventoryElement = FormatHelper.instance(adjustmentElement).getElementAttribute("Inventory");
            Inventory inventory = ModelIOFactory.getReader(Inventory.class,helper.getFormatClass()).read(inventoryElement);
            if (inventory.getRawRecord().isNewRecord()){
                inventory.save();//Ensure parent exists
            }

            AdjustmentRequest request = ModelIOFactory.getReader(AdjustmentRequest.class,helper.getFormatClass()).read(adjustmentElement);
            request.setInventoryId(inventory.getId());
            request.save();
            requests.add(request);
        }
        return integrationAdaptor.createResponse(getPath(),requests);
    }
}
