package in.succinct.plugins.ecommerce.extensions.inventory;

import com.venky.core.util.ObjectUtil;
import com.venky.swf.db.extensions.BeforeModelCreateExtension;
import in.succinct.plugins.ecommerce.db.model.inventory.AdjustmentRequest;
import org.json.simple.JSONObject;

public class BeforeCreateAdjustmentRequest extends BeforeModelCreateExtension<AdjustmentRequest> {

    static {
        registerExtension(new BeforeCreateAdjustmentRequest());
    }
    @Override
    public void beforeCreate(AdjustmentRequest model) {
        if (ObjectUtil.isVoid(model.getComment())){
            throw new RuntimeException("Comment cannot be blank!");
        }
        JSONObject comments = new JSONObject();
        comments.put("Comment",model.getComment());
        comments.put("AdjustmentRequestId",model.getId());
        model.getInventory().adjust(model.getAdjustmentQuantity(),comments.toString());
    }
}
