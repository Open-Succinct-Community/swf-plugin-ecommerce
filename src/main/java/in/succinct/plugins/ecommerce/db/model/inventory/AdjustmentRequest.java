package in.succinct.plugins.ecommerce.db.model.inventory;

import com.venky.core.util.ObjectUtil;
import com.venky.swf.db.annotations.column.IS_NULLABLE;
import com.venky.swf.db.annotations.column.ui.HIDDEN;
import com.venky.swf.db.annotations.column.validations.MinLength;
import com.venky.swf.db.annotations.model.EXPORTABLE;
import com.venky.swf.db.annotations.model.MENU;
import com.venky.swf.db.model.Model;
import com.venky.swf.db.model.io.ModelIOFactory;
import com.venky.swf.db.model.reflection.ModelReflector;
import com.venky.swf.integration.FormatHelper;
import in.succinct.plugins.ecommerce.db.model.attributes.AssetCode;
import in.succinct.plugins.ecommerce.db.model.catalog.Item;
import in.succinct.plugins.ecommerce.db.model.catalog.UnitOfMeasure;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@EXPORTABLE(false)
@MENU("Inventory")
public interface AdjustmentRequest extends Model {

    @HIDDEN
    public long getInventoryId();

    public void setInventoryId(long id);

    public Inventory getInventory();

    public double getAdjustmentQuantity();

    public void setAdjustmentQuantity(double auditQuantity);

    @MinLength(1)
    @IS_NULLABLE(false)
    public String getComment();

    public void setComment(String comment);

    public static <T> List<AdjustmentRequest> adjust(FormatHelper<T> helper) {
        List<T> adjustmentElements = helper.getArrayElements("AdjustmentRequest");
        if (adjustmentElements.isEmpty()) {
            T adjustmentElement = helper.getElementAttribute("AdjustmentRequest");
            if (adjustmentElement != null) {
                adjustmentElements.add(adjustmentElement);
            }
        }

        List<AdjustmentRequest> requests = new ArrayList<>();
        ModelReflector<AdjustmentRequest> ref = ModelReflector.instance(AdjustmentRequest.class);
        for (T adjustmentElement : adjustmentElements) {
            FormatHelper<T> adjustmentElementHelper = FormatHelper.instance(adjustmentElement);
            boolean newProduct = ref.getJdbcTypeHelper().getTypeRef(Boolean.class).getTypeConverter().valueOf(adjustmentElementHelper.getAttribute("NewProduct"));

            T inventoryElement = adjustmentElementHelper.getElementAttribute("Inventory");
            T skuElement = FormatHelper.instance(inventoryElement).getElementAttribute("Sku");
            if (skuElement != null) {
                FormatHelper<T> skuHelper = FormatHelper.instance(skuElement);

                T itemElement = skuHelper.getElementAttribute("Item");
                if (itemElement != null){
                    FormatHelper<T> itemHelper = FormatHelper.instance(itemElement);

                    T uomElement = skuHelper.getElementAttribute("PackagingUOM");
                    if (uomElement != null) {
                        UnitOfMeasure uom = ModelIOFactory.getReader(UnitOfMeasure.class, helper.getFormatClass()).read(uomElement,false);
                        uom.save();
                    } else if (ObjectUtil.isVoid(skuHelper.getAttribute("Name"))) {
                        skuHelper.setAttribute("Name", (String)itemHelper.getAttribute("Name"));
                    }
                    T assetCodeElement = itemHelper.getElementAttribute("AssetCode");
                    if (assetCodeElement != null && ObjectUtil.isVoid(FormatHelper.instance(assetCodeElement).getAttribute("Code"))) {
                        itemHelper.removeElementAttribute("AssetCode");
                    }

                    Item item = ModelIOFactory.getReader(Item.class, helper.getFormatClass()).read(itemElement,false);
                    item.save();
                }

                Sku sku = ModelIOFactory.getReader(Sku.class, helper.getFormatClass()).read(skuElement,false);
                AssetCode assetCode = sku.getItem().getAssetCode();
                if (assetCode != null && !assetCode.getReflector().isVoid(assetCode.getGstPct())) {
                    sku.setTaxRate(assetCode.getGstPct());
                }
                sku.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                sku.save();
            }

            Inventory inventory = ModelIOFactory.getReader(Inventory.class, helper.getFormatClass()).read(inventoryElement,false);
            if (inventory.getRawRecord().isNewRecord()) {
                inventory.save();//Ensure parent exists
            }

            AdjustmentRequest request = ModelIOFactory.getReader(AdjustmentRequest.class, helper.getFormatClass()).read(adjustmentElement,false);
            request.setInventoryId(inventory.getId());
            request.save();
            requests.add(request);
        }
        return requests;
    }
}
