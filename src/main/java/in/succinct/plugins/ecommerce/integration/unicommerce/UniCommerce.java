package in.succinct.plugins.ecommerce.integration.unicommerce;

import com.venky.cache.Cache;
import com.venky.core.collections.SequenceSet;
import com.venky.core.date.DateUtils;
import com.venky.core.io.ByteArrayInputStream;
import com.venky.core.util.Bucket;
import com.venky.core.util.ObjectUtil;
import com.venky.swf.db.Database;
import com.venky.swf.db.annotations.column.ui.mimes.MimeType;
import com.venky.swf.integration.api.Call;
import com.venky.swf.integration.api.InputFormat;
import com.venky.swf.plugins.collab.db.model.config.City;
import com.venky.swf.plugins.collab.db.model.config.Country;
import com.venky.swf.plugins.collab.db.model.config.PinCode;
import com.venky.swf.plugins.collab.db.model.config.State;
import com.venky.swf.sql.Conjunction;
import com.venky.swf.sql.Expression;
import com.venky.swf.sql.Operator;
import com.venky.swf.sql.Select;
import in.succinct.plugins.ecommerce.db.model.catalog.ItemCategory;
import in.succinct.plugins.ecommerce.db.model.catalog.UnitOfMeasure;
import in.succinct.plugins.ecommerce.db.model.catalog.UnitOfMeasureConversionTable;
import in.succinct.plugins.ecommerce.db.model.inventory.InventoryCalculator;
import in.succinct.plugins.ecommerce.db.model.inventory.Sku;
import in.succinct.plugins.ecommerce.db.model.order.Order;
import in.succinct.plugins.ecommerce.db.model.order.OrderAddress;
import in.succinct.plugins.ecommerce.db.model.order.OrderAttribute;
import in.succinct.plugins.ecommerce.db.model.order.OrderLine;
import in.succinct.plugins.ecommerce.db.model.order.OrderPrint;
import in.succinct.plugins.ecommerce.db.model.participation.Facility;
import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class UniCommerce {
    private static Map<Long,UniCommerce> instance = new Cache<Long, UniCommerce>() {
        @Override
        protected UniCommerce getValue(Long facilityId) {
            return new UniCommerce(facilityId);
        }
    };
    private Facility facility = null;
    private UniCommerce(Long facilityId){
        facility = Database.getTable(Facility.class).get(facilityId);
        if (facility.getPreferredMarketPlaceIntegrations().isEmpty()){
            throw new RuntimeException("Marketplace integration not configured for facility " + facility.getName());
        }
    }


    public static UniCommerce getInstance(Facility facility) {
        return instance.get(facility.getId());
    }

    private String getBaseUrl(){
        return facility.getPreferredMarketPlaceIntegrations().get(0).getBaseUrl();
    }
    private String getPassword() {
        return facility.getPreferredMarketPlaceIntegrations().get(0).getPassword();
    }

    private String getUserName() {
        return facility.getPreferredMarketPlaceIntegrations().get(0).getUsername();
    }

    private String getClientId() {
        return facility.getPreferredMarketPlaceIntegrations().get(0).getClientId();
    }
    private String getAccessToken(){
        String url = String.format(getBaseUrl()+"/oauth/token?grant_type=password&client_id=%s&username=%s&password=%s",getClientId(),getUserName(),getPassword());
        JSONObject object = new Call<JSONAware>().url(url).getResponseAsJson();
        return String.format("%s %s",object.get("Token_Type"),object.get("Access_Token"));
    }

    private Map<String,String> getDefaultHeaders(){
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type", MimeType.APPLICATION_JSON.toString());
        headers.put("Authorization", getAccessToken());
        return headers;
    }

    public void sync(Sku sku) {
        if (!sku.isPublished()) {
            return;
        }
        Map<String, String> headers = getDefaultHeaders();
        JSONObject input = new JSONObject();
        JSONObject itemType = new JSONObject();
        input.put("itemType", itemType);

        itemType.put("skuCode", sku.getSkuCode());
        itemType.put("name", sku.getName());
        itemType.put("description", sku.getShortDescription());
        itemType.put("length", UnitOfMeasureConversionTable.convert(sku.getLength(), UnitOfMeasure.MEASURES_LENGTH, sku.getLengthUOM().getName(), UnitOfMeasure.CENTIMETERS));
        itemType.put("width", UnitOfMeasureConversionTable.convert(sku.getWidth(), UnitOfMeasure.MEASURES_LENGTH, sku.getWidthUOM().getName(), UnitOfMeasure.CENTIMETERS));
        itemType.put("height", UnitOfMeasureConversionTable.convert(sku.getHeight(), UnitOfMeasure.MEASURES_LENGTH, sku.getHeightUOM().getName(), UnitOfMeasure.CENTIMETERS));
        itemType.put("weight", UnitOfMeasureConversionTable.convert(sku.getWeight(), UnitOfMeasure.MEASURES_WEIGHT, sku.getWeightUOM().getName(), UnitOfMeasure.GRAMS));
        itemType.put("maxRetailPrice", sku.getMaxRetailPrice());
        ItemCategory category = sku.getItem().getItemCategory("HSN");
        if (category != null) {
            itemType.put("hsnCode", sku.getItem().getItemCategory("HSN").getMasterItemCategoryValue().getAllowedValue());
        }
        itemType.put("imageUrl", sku.getSmallImageUrl());
        Call<JSONAware> call = new Call<JSONAware>().url(getBaseUrl(), "services/rest/v1/catalog/itemType/createOrEdit").headers(headers).inputFormat(InputFormat.JSON).input(input);

        JSONObject object = execute(call);
    }

    public void syncInventory(Sku sku){
        syncInventory(sku,getDefaultHeaders());
    }
    private void syncInventory(Sku sku, Map<String,String> headers){
        if (!sku.isPublished()){
            return;
        }
        InventoryCalculator calculator = new InventoryCalculator(sku,facility);
        double atp = calculator.getTotalInventory();
        double inventoryInUC = getUnicommerceInventory(sku,headers);

        double inventoryToUpdate = atp + getMarketDemand(sku,facility);
        double inventoryToAdd = inventoryToUpdate - inventoryInUC;
        // This is because Unicommerce does inventory - demand to show atp in the market place.!! We need to update inventory in unicommerse in such a way that
        // unicom inventory - unicom demand =  wiggles atp.
        JSONObject inventory = new JSONObject();
        JSONObject adjust = new JSONObject();
        inventory.put("inventoryAdjustment",adjust);
        adjust.put("itemSKU",sku.getSkuCode());
        adjust.put("quantity",inventoryToAdd);
        adjust.put("shelfCode","DEFAULT");
        adjust.put("inventoryType","GOOD_INVENTORY");
        adjust.put("facilityCode",facility.getName());
        adjust.put("adjustmentType","ADD");// Need to update not add / substract!!

        Call<JSONObject> adjustCall = new Call<JSONObject>().url(getBaseUrl(),"services/rest/v1/inventory/adjust");
        adjustCall.inputFormat(InputFormat.JSON).input(inventory).headers(headers);
        JSONObject response = execute(adjustCall);

    }

    private double getUnicommerceInventory(Sku sku,Map<String,String> headers){
        JSONObject input = new JSONObject();
        JSONArray  skus = new JSONArray();
        input.put("itemTypeSKUS",skus);
        skus.add(sku.getSkuCode());
        input.put("updatedSinceMinutes",2*24*60); //last 2 days.

        Call<JSONObject> call = new Call<JSONObject>().url(getBaseUrl(),"/services/rest/v1/inventory/inventorySnapshot/get").headers(headers).
                inputFormat(InputFormat.JSON).input(input);
        if (call.hasErrors()){
            throw new RuntimeException(call.getError());
        }
        JSONObject response = call.getResponseAsJson();
        if (!sku.getReflector().getJdbcTypeHelper().getTypeRef(Boolean.class).getTypeConverter().valueOf(response.get("successful"))){
            throw new RuntimeException("Got " + response);
        }
        JSONArray snapshots = (JSONArray) response.get("inventorySnapshots");
        if (snapshots.size() == 0){
            return 0;
        }
        Double quantity = sku.getReflector().getJdbcTypeHelper().getTypeRef(double.class).getTypeConverter().valueOf(((JSONObject)snapshots.get(0)).get("inventory"));
        return quantity;
    }

    private double getMarketDemand(Sku sku,Facility facility){
        Select select = new Select().from(OrderLine.class);
        Expression where = new Expression(select.getPool(), Conjunction.AND);
        where.add(new Expression(select.getPool(),"SKU_ID", Operator.EQ,sku.getId()));
        where.add(new Expression(select.getPool(), "SHIP_FROM_ID",Operator.EQ,facility.getId()));
        select.where(where).add(" AND ORDERED_QUANTITY - CANCELLED_QUANTITY - SHIPPED_QUANTITY - RETURNED_QUANTITY > 0 AND EXISTS ( select 1 from orders where id = order_lines.order_id and reference like 'UC%' )");
        List<OrderLine> lines = select.execute();
        Bucket demand = new Bucket();
        for (OrderLine line : lines){
            demand.increment(line.getToShipQuantity());
        }
        return demand.doubleValue();
    }
    public void pullOrders(){
        Select select = new Select().from(Order.class);
        Expression where = new Expression(select.getPool(), Conjunction.AND);
        where.add(new Expression(select.getPool(),"REFERENCE",Operator.LK,"UC%"));
        select.where(where).add(String.format(" and exist (select 1 from order_lines where order_id = orders.id and ship_from_id = %d )",facility.getId()));
        List<Order> orders = select.orderBy("ID DESC").execute(1);
        Order order = null;
        if (!orders.isEmpty()){
            order = orders.get(0);
        }
        Timestamp timestamp = order == null ? new Timestamp(0L) : order.getCreatedAt();
        pullOrders(timestamp);
    }
    public void pullOrders(Timestamp after){
        Map<String,String> headers = getDefaultHeaders();
        JSONObject input =  new JSONObject();
        String DATE_TIME_FORMAT_WITH_TZ_STR = "yyyy-MM-dd'T'HH:mm:ssZ";
        input.put("fromDate", DateUtils.getFormat(DATE_TIME_FORMAT_WITH_TZ_STR).format(after));
        JSONArray facilityCodes = new JSONArray();
        facilityCodes.add(facility.getName());
        input.put("facilityCodes",facilityCodes);

        Call<JSONObject> call = new Call<JSONObject>().url(getBaseUrl(),"/services/rest/v1/oms/saleOrder/search").inputFormat(InputFormat.JSON).input(input)
                .headers(headers);

        JSONObject response = execute(call);

        JSONArray elements = (JSONArray)response.get("elements");
        List<String> orderNumbers = new ArrayList<>();
        for (Object element : elements){
            JSONObject jsonObject = (JSONObject)element;
            orderNumbers.add(String.valueOf(jsonObject.get("code")));
        }
        for (String orderNumber :orderNumbers){
            pullOrder(orderNumber,headers);
        }
    }

    private void pullOrder(String orderNumber, Map<String, String> headers) {


        JSONObject input = new JSONObject();
        input.put("code",orderNumber);
        Call<JSONObject> call = new Call<JSONObject>().url(getBaseUrl(),"/services/rest/v1/oms/saleOrder/get").inputFormat(InputFormat.JSON).input(input)
                .headers(headers);
        JSONObject response = execute(call);

        String reference = "UC-"+orderNumber;
        Select select = new Select().from(Order.class);
        List<Order> orders = select.where(new Expression(select.getPool(),"REFERENCE",Operator.EQ,reference)).execute();

        JSONObject saleOrder = (JSONObject)response.get("saleOrderDTO");

        if (!orders.isEmpty()){
            if (orders.size() != 1){
                throw new RuntimeException("Multiple orders found for reference " + reference);
            }
            Order order = orders.get(0);
            if (!ObjectUtil.equals(order.getFulfillmentStatus(),Order.FULFILLMENT_STATUS_CANCELLED) &&
                    ObjectUtil.equals(Order.FULFILLMENT_STATUS_CANCELLED,saleOrder.get("status"))){
                order.cancel("","Market Place");
            }
            return;
        }
        Order order = Database.getTable(Order.class).newRecord();
        order.setReference("UC-" + saleOrder.get("code"));
        order.setCreatedAt(new Timestamp(order.getReflector().getJdbcTypeHelper().getTypeRef(Long.class).getTypeConverter().valueOf(saleOrder.get("created"))));
        order.setUpdatedAt(new Timestamp(order.getReflector().getJdbcTypeHelper().getTypeRef(Long.class).getTypeConverter().valueOf(saleOrder.get("updated"))));

        order.save();
        JSONObject billingAddress = (JSONObject)saleOrder.get("billingAddress");
        OrderAddress billTo = Database.getTable(OrderAddress.class).newRecord();
        billTo.setOrderId(order.getId());
        billTo.setAddressType(OrderAddress.ADDRESS_TYPE_BILL_TO);
        billTo.setFirstName(billTo.getReflector().getJdbcTypeHelper().getTypeRef(String.class).getTypeConverter().valueOf(billingAddress.get("name")));
        billTo.setAddressLine1(billTo.getReflector().getJdbcTypeHelper().getTypeRef(String.class).getTypeConverter().valueOf(billingAddress.get("addressLine1")));
        billTo.setAddressLine2(billTo.getReflector().getJdbcTypeHelper().getTypeRef(String.class).getTypeConverter().valueOf(billingAddress.get("addressLine2")));
        billTo.setCountryId(Country.findByISO(billTo.getReflector().getJdbcTypeHelper().getTypeRef(String.class).getTypeConverter().valueOf(billingAddress.get("country"))).getId());
        billTo.setStateId(State.findByCountryAndCode(billTo.getCountryId(),billTo.getReflector().getJdbcTypeHelper().getTypeRef(String.class).getTypeConverter().valueOf(billingAddress.get("state"))).getId());
        billTo.setCityId(City.findByStateAndName(billTo.getStateId(),billTo.getReflector().getJdbcTypeHelper().getTypeRef(String.class).getTypeConverter().valueOf(billingAddress.get("city"))).getId());
        billTo.setPinCodeId(PinCode.find(billTo.getReflector().getJdbcTypeHelper().getTypeRef(String.class).getTypeConverter().valueOf(billingAddress.get("pincode"))).getId());
        billTo.setPhoneNumber(billTo.getReflector().getJdbcTypeHelper().getTypeRef(String.class).getTypeConverter().valueOf(billingAddress.get("phone")));
        billTo.setEmail(billTo.getReflector().getJdbcTypeHelper().getTypeRef(String.class).getTypeConverter().valueOf(billingAddress.get("email")));

        OrderAddress shipTo = Database.getTable(OrderAddress.class).newRecord();
        shipTo.getRawRecord().load(billTo.getRawRecord());
        shipTo.setAddressType(OrderAddress.ADDRESS_TYPE_SHIP_TO);

        billTo.save();
        shipTo.save();

        JSONArray saleOrderItems = (JSONArray)saleOrder.get("saleOrderItems");
        for (Object o : saleOrderItems){
            JSONObject saleOrderItem = (JSONObject)o;
            OrderLine line = Database.getTable(OrderLine.class).newRecord();
            line.setOrderId(order.getId());
            if (!ObjectUtil.equals(saleOrderItem.get("facilityCode"),facility.getName())){
                throw new RuntimeException("Some thing went wrong in filtering for specific facility from Unicommerce! " + facility.getName());
            }
            line.setShipFromId(facility.getId());
            Sku sku = Sku.find(facility.getCompanyId(),line.getReflector().getJdbcTypeHelper().getTypeRef(String.class).getTypeConverter().valueOf("itemSku"));
            line.setSkuId(sku.getId());
            line.setOrderedQuantity(1.0D);
            line.setMaxRetailPrice(sku.getMaxRetailPrice());
            line.setSellingPrice(line.getReflector().getJdbcTypeHelper().getTypeRef(Double.class).getTypeConverter().valueOf("sellingPrice"));
            line.setShipTogetherCode(line.getReflector().getJdbcTypeHelper().getTypeRef(String.class).getTypeConverter().valueOf("shippingPackageCode"));
            line.setPrice(line.getSellingPrice()/(1 + sku.getTaxRate()/100.0));
            double tax = (sku.getTaxRate()/100.0)*line.getPrice();
            if (ObjectUtil.equals(shipTo.getStateId(),facility.getStateId())){
                line.setIGst(0.0);
                line.setCGst(tax/2.0);
                line.setSGst(tax/2.0);
            }else {
                line.setIGst(tax);
                line.setCGst(0.0);
                line.setSGst(0.0);
            }

            line.setDiscountPercentage((line.getMaxRetailPrice()-line.getSellingPrice()) * 100.0/(line.getMaxRetailPrice()));
            line.save();
        }



    }
    public void pack(Order order){
        if (ObjectUtil.equals(order.getFulfillmentStatus(),Order.FULFILLMENT_STATUS_PACKED) ||
                ObjectUtil.equals(order.getFulfillmentStatus(),Order.FULFILLMENT_STATUS_MANIFESTED)){
            List<String> packageCodes = new SequenceSet<>();
            for (OrderLine orderLine : order.getOrderLines()) {
                if (!ObjectUtil.isVoid(orderLine.getShipTogetherCode()) && orderLine.getToShipQuantity() > 0.0D && orderLine.getToPackQuantity() == 0.0D){
                    packageCodes.add(orderLine.getShipTogetherCode());
                }
            }
            Map<String,String> headers = getDefaultHeaders();
            Map<String, OrderAttribute> map = order.getAttributeMap();
            for (String packageCode : packageCodes){
                JSONObject input = new JSONObject() ;
                input.put("shippingPackageCode",packageCode);
                Call<JSONObject> call = new Call<JSONObject>().url(getBaseUrl(),"/services/rest/v1/oms/shippingPackage/createInvoiceAndAllocateShippingProvider")
                        .inputFormat(InputFormat.JSON).headers(headers).input(input);

                JSONObject response = execute(call);
                map.get("InvoiceCode-" + packageCode).setValue(order.getReflector().getJdbcTypeHelper().getTypeRef(String.class).getTypeConverter().valueOf(response.get("invoiceCode")));
                map.get("ShippingProviderCode-" + packageCode).setValue(order.getReflector().getJdbcTypeHelper().getTypeRef(String.class).getTypeConverter().valueOf(response.get("shippingProviderCode")));

                downloadPackList(order,packageCode,headers);

                //String invoiceCode = map.get("InvoiceCode-" + packageCode).getValue();

                Call<JSONObject> invoiceDetail = new Call<JSONObject>().url(getBaseUrl(),"/services/rest/v1/invoice/details/get")
                        .inputFormat(InputFormat.JSON).input(input).headers(headers);

                JSONObject jsonInvoice = execute(invoiceDetail);
                String trackingNumber = (String)jsonInvoice.get("trackingNumber");
                map.get("TrackingNumber-" + packageCode).setValue(trackingNumber);


            }
            order.saveAttributeMap(map);
        }
    }

    private <T extends  JSONAware> JSONObject execute(Call<T> call){
        if (call.hasErrors()){
            throw new RuntimeException(call.getError());
        }

        JSONObject response = call.getResponseAsJson();
        if (!Database.getJdbcTypeHelper("").getTypeRef(Boolean.class).getTypeConverter().valueOf(response.get("successful"))) {
            throw new RuntimeException("Got " + response);
        }
        return response;
    }
    public void dispatch(Order order){
        if (!ObjectUtil.equals(order.getFulfillmentStatus(),Order.FULFILLMENT_STATUS_SHIPPED)){
            return;
        }
        List<String> packageCodes = new SequenceSet<>();
        for (OrderLine orderLine : order.getOrderLines()) {
            if (!ObjectUtil.isVoid(orderLine.getShipTogetherCode()) && orderLine.getShippedQuantity()>0){
                packageCodes.add(orderLine.getShipTogetherCode());
            }
        }
        Map<String,String> headers = getDefaultHeaders();
        for (String packageCode : packageCodes) {
            JSONObject input = new JSONObject();
            input.put("shippingPackageCode", packageCode);
            Call<JSONObject> call = new Call<JSONObject>().url(getBaseUrl(), "/services/rest/v1/oms/shippingPackage/dispatch")
                    .inputFormat(InputFormat.JSON).headers(headers).input(input);
            execute(call);
        }
    }

    private void downloadPackList(Order order, String packageCode, Map<String,String> headers){
        Optional<OrderPrint> opPrint = order.getOrderPrints().stream().filter(p-> ObjectUtil.equals(p.getDocumentType(), OrderPrint.DOCUMENT_TYPE_PACK_SLIP)).findFirst();
        if (opPrint.isPresent()){
            return;
        }

        JSONObject input = new JSONObject() ;
        input.put("shippingPackageCode",packageCode);
        Call<JSONObject> call = new Call<JSONObject>().url(getBaseUrl(),"/services/rest/v1/oms/shippingPackage/invoiceLabel")
                .inputFormat(InputFormat.JSON).headers(headers).input(input);
        JSONObject response = execute(call);

        String labelbase64 = (String)response.get("label");
        byte[] bytes = Base64.getDecoder().decode(labelbase64.getBytes(StandardCharsets.UTF_8));
        OrderPrint print = Database.getTable(OrderPrint.class).newRecord();
        print.setOrderId(order.getId());
        print.setDocumentType(OrderPrint.DOCUMENT_TYPE_PACK_SLIP);
        print.setImageContentName("Carton-" + packageCode + ".png" );
        print.setImageContentType(MimeType.IMAGE_PNG.toString());
        print.setImageContentSize(bytes.length);
        print.setImage(new ByteArrayInputStream(bytes));
        print.save();
    }

}
