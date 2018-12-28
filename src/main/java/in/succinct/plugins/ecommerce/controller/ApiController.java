package in.succinct.plugins.ecommerce.controller;

import com.venky.swf.controller.Controller;
import com.venky.swf.db.annotations.column.ui.mimes.MimeType;
import com.venky.swf.integration.FormatHelper;
import com.venky.swf.integration.IntegrationAdaptor;
import com.venky.swf.integration.JSON;
import com.venky.swf.path.Path;
import com.venky.swf.views.View;
import in.succinct.plugins.ecommerce.db.model.apis.Cancel;
import in.succinct.plugins.ecommerce.db.model.apis.Pack;

import java.util.List;

public class ApiController extends Controller{

	public ApiController(Path path) {
		super(path);
	}
	
	public View pack() {
		IntegrationAdaptor<Pack, JSON> integrationAdaptor = null;
		if (getPath().getProtocol() != MimeType.TEXT_HTML){
        	integrationAdaptor = IntegrationAdaptor.instance(Pack.class, FormatHelper.getFormatClass(path.getProtocol()));
        }
		if (integrationAdaptor == null) { 
			throw new RuntimeException("Unsupported input format");
		}
        if (!getPath().getRequest().getMethod().equalsIgnoreCase("POST")) {
        	throw new RuntimeException("Unsupported request method. Only POST is allowed");
        }
        List<Pack> inputs = integrationAdaptor.readRequest(getPath());
        inputs.forEach(pack->{ 
        	pack.pack();
        });
		return integrationAdaptor.createResponse(getPath(),inputs);
	}

	public View cancel() {
        IntegrationAdaptor<Cancel, JSON> integrationAdaptor = null;
        if (getPath().getProtocol() != MimeType.TEXT_HTML){
            integrationAdaptor = IntegrationAdaptor.instance(Cancel.class, FormatHelper.getFormatClass(path.getProtocol()));
        }
        if (integrationAdaptor == null) {
            throw new RuntimeException("Unsupported input format");
        }
        if (!getPath().getRequest().getMethod().equalsIgnoreCase("POST")) {
            throw new RuntimeException("Unsupported request method. Only POST is allowed");
        }
        List<Cancel> inputs = integrationAdaptor.readRequest(getPath());
        inputs.forEach(cancel->{
            cancel.reject();
        });
        return integrationAdaptor.createResponse(getPath(),inputs);
    }



}
