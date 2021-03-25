package in.succinct.plugins.ecommerce.agents.order.tasks.pack;

import com.venky.core.util.ObjectUtil;
import com.venky.swf.db.Database;
import com.venky.swf.db.annotations.column.ui.mimes.MimeType;
import com.venky.swf.db.model.Model;
import com.venky.swf.db.model.reflection.ModelReflector;
import com.venky.swf.plugins.collab.db.model.participants.admin.Address;
import com.venky.swf.plugins.templates.util.templates.TemplateEngine;
import in.succinct.plugins.ecommerce.agents.order.tasks.EntityTask;
import in.succinct.plugins.ecommerce.db.model.inventory.Sku;
import in.succinct.plugins.ecommerce.db.model.order.Order;
import in.succinct.plugins.ecommerce.db.model.order.OrderAddress;
import in.succinct.plugins.ecommerce.db.model.order.OrderLine;
import in.succinct.plugins.ecommerce.db.model.order.OrderPrint;
import in.succinct.plugins.ecommerce.db.model.order.OrderStatus;
import in.succinct.plugins.ecommerce.db.model.participation.Facility;
import in.succinct.plugins.ecommerce.db.model.participation.PreferredCarrier;
import in.succinct.plugins.ecommerce.integration.fedex.ShipWebServiceClient;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PacklistPrintTask extends EntityTask<Order> {
    public PacklistPrintTask(long id) {
        super(id);
    }
    public PacklistPrintTask(){
        this(-1L);
    }

    @Override
    protected void execute(Order order) {
        Map<String,OrderPrint> printMap = new HashMap<>();

        List<OrderPrint> prints = order.getOrderPrints();

        prints.forEach(p->printMap.put(p.getDocumentType(),p));

        if (printMap.get(OrderPrint.DOCUMENT_TYPE_CARRIER_LABEL) == null && ObjectUtil.equals(PreferredCarrier.FEDEX,order.getPreferredCarrierName())) {
            new ShipWebServiceClient(order).ship();
        }
        if (printMap.get(OrderPrint.DOCUMENT_TYPE_PACK_SLIP) == null){
            printPackSlip(order);
        }
    }
    private Map<Class<? extends Model>, List<String>> getEntityFieldMap() {
        Map<Class<? extends Model>,List<String>> entityFieldMap = new HashMap<>();

        entityFieldMap.put(Order.class,ModelReflector.instance(Order.class).getVisibleFields());
        entityFieldMap.put(Sku.class, Arrays.asList("NAME","MAX_RETAIL_PRICE","TAX_RATE"));
        entityFieldMap.put(OrderLine.class,Arrays.asList("HSN","SKU_ID","PACKED_QUANTITY","SHIPPED_QUANTITY","MAX_RETAIL_PRICE","DISCOUNT_PERCENTAGE","SELLING_PRICE","PRICE","I_GST","C_GST","S_GST"));

        List<String> facilityFields = ModelReflector.instance(Facility.class).getUniqueFields();
        facilityFields.addAll(Arrays.asList(Address.getAddressFields()));
        entityFieldMap.put(Facility.class,facilityFields);

        List<String> addressFields = ModelReflector.instance(OrderAddress.class).getUniqueFields();
        addressFields.addAll(Arrays.asList(Address.getAddressFields()));
        addressFields.add("FIRST_NAME");
        addressFields.add("LAST_NAME");

        entityFieldMap.put(OrderAddress.class,Arrays.asList(Address.getAddressFields()));
        return entityFieldMap;

    }
    private void printPackSlip(Order order) {

        List<Model> entities = new ArrayList<>();
        entities.add(order);

        Map<String,Object> entityMap = TemplateEngine.getInstance().createEntityMap(entities);
        for (OrderStatus status : order.getOrderStatuses()){
            entityMap.put("OrderStatus" + status.getFulfillmentStatus(), status);
        }
        OrderAddress shipTo = order.getAddresses().stream().filter(a->ObjectUtil.equals(a.getAddressType(),OrderAddress.ADDRESS_TYPE_SHIP_TO)).findFirst().get();
        OrderAddress billTo = order.getAddresses().stream().filter(a -> ObjectUtil.equals(a.getAddressType(), OrderAddress.ADDRESS_TYPE_BILL_TO)).findFirst().get();
        Facility shipFrom = order.getOrderLines().get(0).getShipFrom();

        entityMap.put("ShipFrom",shipFrom);
        entityMap.put("ShipTo",shipTo);
        entityMap.put("BillTo",billTo);


        Map<Class<? extends Model>,List<String>> entityFieldMap = getEntityFieldMap();

        Map<String, Object> root =TemplateEngine.getInstance().formatEntityMap(entityMap,entityFieldMap);

        String courier = order.getAttribute("courier").getValue();
        root.put("Courier",courier);
        String trackingNumber = order.getAttribute("tracking_number").getValue();
        if (!ObjectUtil.isVoid(trackingNumber)){
            root.put("TrackingNumber",trackingNumber);
        }

        String templateName = "prints/PRINT_packslip.ftlh";
        String html = TemplateEngine.getInstance().exists(templateName)? TemplateEngine.getInstance().publish(templateName, root) : null;
        if (!ObjectUtil.isVoid(html)){
            OrderPrint print = Database.getTable(OrderPrint.class).newRecord();
            print.setOrderId(order.getId());
            print.setDocumentType(OrderPrint.DOCUMENT_TYPE_PACK_SLIP);
            byte[] htmlbytes = html.getBytes();
            byte[] bytes = TemplateEngine.getInstance().htmlToPdf(htmlbytes);
            if  (bytes.length > 0){
                print.setImageContentName("packlist"+order.getOrderNumber()+".pdf");
                print.setImageContentType(MimeType.APPLICATION_PDF.toString());
            }else {
                bytes = htmlbytes;
                print.setImageContentName("packlist"+order.getOrderNumber()+".html");
                print.setImageContentType(MimeType.TEXT_HTML.toString());
            }

            print.setImage(new ByteArrayInputStream(bytes));
            print.setImageContentSize(bytes.length);
            print.save();
        }

    }


}
