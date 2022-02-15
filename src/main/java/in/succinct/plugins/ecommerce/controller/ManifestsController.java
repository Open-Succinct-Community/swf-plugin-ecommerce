package in.succinct.plugins.ecommerce.controller;

import com.venky.swf.controller.annotations.SingleRecordAction;
import com.venky.swf.db.Database;
import com.venky.swf.path.Path;
import com.venky.swf.controller.ModelController;
import com.venky.swf.views.RedirectorView;
import com.venky.swf.views.View;
import in.succinct.plugins.ecommerce.db.model.order.Manifest;

public class ManifestsController extends ModelController<Manifest> {

	public ManifestsController(Path path) {
		super(path);
		
	}

    @SingleRecordAction(icon="glyphicon-refresh", tooltip="Print Manifest(Refresh)")
	public View refresh(long manifestId){
        Manifest manifest = Database.getTable(Manifest.class).get(manifestId);
        manifest.setImage(null);
        manifest.setImageContentName(null);
        manifest.setImageContentSize(0);
        manifest.setImageContentType(null);
        return download(manifestId);
    }
	
	@SingleRecordAction(icon="glyphicon-print", tooltip="Print Manifest")
	public View download(long manifestId) {
		Manifest manifest = Database.getTable(Manifest.class).get(manifestId); 
		if (manifest.getImageContentSize() == 0) {
			//manifest.downloadManifest(manifest);
		}
		return new RedirectorView(getPath(), getPath().controllerPath() + "/view/"+manifestId );
	}
	@SingleRecordAction(icon="glyphicon-road",tooltip="Close Manifest")
	public View close(long manifestId) { 
		Manifest manifest = Database.getTable(Manifest.class).get(manifestId); 
		manifest.close();
		return back();
	}

	@SingleRecordAction(icon="glyphicon-gift",tooltip="Track Orders")
	public View track(long manifestId){
		Manifest manifest = Database.getTable(Manifest.class).get(manifestId);
		manifest.track();
		return back();
	}
}
