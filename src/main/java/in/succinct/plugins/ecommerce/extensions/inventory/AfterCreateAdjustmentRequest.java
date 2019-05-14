package in.succinct.plugins.ecommerce.extensions.inventory;

import com.venky.swf.db.extensions.AfterModelCreateExtension;
import in.succinct.plugins.ecommerce.db.model.inventory.AdjustmentRequest;
import org.json.simple.JSONObject;

public class AfterCreateAdjustmentRequest extends AfterModelCreateExtension<AdjustmentRequest> {

    static {
        registerExtension(new AfterCreateAdjustmentRequest());
    }
    @Override
    public void afterCreate(AdjustmentRequest model) {
        JSONObject comments = new JSONObject();
        comments.put("Comment",model.getComment());
        comments.put("AdjustmentRequestId",model.getId());
        model.getInventory().adjust(model.getAdjustmentQuantity(),comments.toString());
    }
}
