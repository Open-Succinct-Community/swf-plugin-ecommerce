package in.succinct.plugins.ecommerce.integration.humbhionline;

import com.venky.cache.Cache;
import com.venky.core.math.DoubleUtils;
import com.venky.core.util.Bucket;
import com.venky.core.util.ObjectUtil;
import com.venky.swf.db.Database;
import com.venky.swf.db.annotations.column.ui.mimes.MimeType;
import com.venky.swf.db.model.io.ModelIOFactory;
import com.venky.swf.db.model.reflection.ModelReflector;
import com.venky.swf.integration.FormatHelper;
import com.venky.swf.integration.api.Call;
import com.venky.swf.integration.api.HttpMethod;
import com.venky.swf.integration.api.InputFormat;
import com.venky.swf.plugins.collab.db.model.participants.admin.Address;
import com.venky.swf.sql.Expression;
import com.venky.swf.sql.Operator;
import com.venky.swf.sql.Select;
import in.succinct.plugins.ecommerce.db.model.catalog.Item;
import in.succinct.plugins.ecommerce.db.model.inventory.Inventory;
import in.succinct.plugins.ecommerce.db.model.inventory.Sku;
import in.succinct.plugins.ecommerce.db.model.order.Order;
import in.succinct.plugins.ecommerce.db.model.order.OrderAddress;
import in.succinct.plugins.ecommerce.db.model.order.OrderLine;
import in.succinct.plugins.ecommerce.db.model.participation.Company;
import in.succinct.plugins.ecommerce.db.model.participation.Facility;
import in.succinct.plugins.ecommerce.db.model.participation.MarketPlaceIntegration;
import in.succinct.plugins.ecommerce.db.model.participation.User;
import in.succinct.plugins.ecommerce.integration.MarketPlace;
import in.succinct.plugins.ecommerce.integration.MarketPlace.UserActionHandler;
import in.succinct.plugins.ecommerce.integration.MarketPlace.WarehouseActionHandler;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HumBhiOnline implements MarketPlace , WarehouseActionHandler, UserActionHandler {

    private static Map<Long, HumBhiOnline> instance = new Cache<Long, HumBhiOnline>() {
        @Override
        protected HumBhiOnline getValue(Long marketPlaceIntegrationId) {
            return new HumBhiOnline(marketPlaceIntegrationId);
        }
    };
    private MarketPlaceIntegration marketPlaceIntegration;
    private HumBhiOnline(Long marketPlaceIntegrationId){
        this(Database.getTable(MarketPlaceIntegration.class).get(marketPlaceIntegrationId));
    }
    private HumBhiOnline(MarketPlaceIntegration marketPlaceIntegration){
        this.marketPlaceIntegration = marketPlaceIntegration;
    }

    public static HumBhiOnline getInstance(MarketPlaceIntegration marketPlaceIntegration) {
        if (!instance.containsKey(marketPlaceIntegration.getId())) {
            synchronized (instance) {
                if (!instance.containsKey(marketPlaceIntegration.getId())) {
                    instance.put(marketPlaceIntegration.getId(), new HumBhiOnline(marketPlaceIntegration));
                }
            }
        }
        return instance.get(marketPlaceIntegration.getId());
    }


    @Override
    public long getFacilityId() {
        return marketPlaceIntegration.getFacilityId();
    }

    @Override
    public String getOrderPrefix() {
        return getClass().getSimpleName()+"-";
    }

    public String getUserPrefix(){
        return "HUMBOL-";
    }

    @Override
    public WarehouseActionHandler getWarehouseActionHandler() {
        return this;
    }

    @Override
    public UserActionHandler getUserActionHandler() {
        return this;
    }

    public String getBaseUrl(){
        return marketPlaceIntegration.getBaseUrl();
    }

    @Override
    public void sync(Inventory inventory) {
        JSONObject params = new JSONObject();

        JSONObject adjustmentRequest = new JSONObject();
        params.put("AdjustmentRequest" , adjustmentRequest);

        JSONObject companyJson = new JSONObject();
        companyJson.put("Name","MANDI");

        JSONObject assetCodeJson = new JSONObject();
        Sku sku = inventory.getSku();
        Item item = sku.getItem();
        assetCodeJson.put("Code",item.getHsn());

        JSONObject itemJson = new JSONObject();
        itemJson.put("Name", sku.getPackagingUOMId() == null ? inventory.getSku().getName() : item.getName() ) ; //Sku name is mapped to item name on hbo.
        itemJson.put("AssetCode",assetCodeJson);
        itemJson.put("Company",companyJson);

        JSONObject uomJson = new JSONObject();
        uomJson.put("Measures","Packaging");
        uomJson.put("Name",sku.getPackagingUOMId() == null ? "Each" : sku.getPackagingUOM().getName());

        JSONObject skuJson = new JSONObject();
        skuJson.put("Item",itemJson);
        skuJson.put("PackagingUom",uomJson);
        skuJson.put("Name", itemJson.get("Name") + "-" + uomJson.get("Name"));
        skuJson.put("SkuCode",inventory.getSku().getSkuCode());
        skuJson.put("Published",inventory.getSku().isPublished());

        JSONObject faciltyJson = new JSONObject();
        faciltyJson.put("Name",inventory.getFacility().getName());
        faciltyJson.put("Company",companyJson);

        JSONObject inventoryJson = new JSONObject();

        inventoryJson.put("Sku",skuJson);
        inventoryJson.put("Facility",faciltyJson);
        inventoryJson.put("Company",companyJson);
        inventoryJson.put("Infinite",inventory.isPublished()) ;


        adjustmentRequest.put("Inventory",inventoryJson);
        adjustmentRequest.put("AdjustmentQuantity",0.0);
        adjustmentRequest.put("Comment", "Adjust");

        Call<JSONObject> call = new Call<>();
        if (call.url(marketPlaceIntegration.getBaseUrl()+"/api/adjust").inputFormat(InputFormat.JSON).input(params).method(HttpMethod.POST).
                headers(getDefaultHeaders()).
                hasErrors()){
            throw new RuntimeException(call.getError());
        }
    }
    private Map<String,String> getDefaultHeaders(){
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type", MimeType.APPLICATION_JSON.toString());
        headers.put("ApiKey", getApiKey());
        return headers;
    }
    public String getApiKey(){
        JSONObject loginJson = new JSONObject();
        JSONObject userJson = new JSONObject();

        loginJson.put("User",userJson);
        userJson.put("PhoneNumber",marketPlaceIntegration.getUsername());
        userJson.put("Password",marketPlaceIntegration.getPassword());


        JSONObject response = new Call<JSONObject>().url(marketPlaceIntegration.getBaseUrl() + "/login").inputFormat(InputFormat.JSON).input(loginJson).header("content-type",MimeType.APPLICATION_JSON.toString()).method(HttpMethod.POST).getResponseAsJson();
        if (response != null){
            return (String)(((JSONObject)response.get("User")).get("ApiKey"));
        }
        return null;
    }

    @Override
    public void pullOrders(Order lastOrder) {
        String hboOrderNumber = lastOrder == null ? "0" : lastOrder.getReference().substring(getOrderPrefix().length());
        String maxOrderNumber = String.valueOf(Math.pow(10,hboOrderNumber.length()) - 1) ;
        JSONObject params = new JSONObject();
        params.put("q","FACILITY_ID:" + marketPlaceIntegration.getChannelFacilityRef() + " AND OPEN:Y AND ID:[" + hboOrderNumber + " TO " + maxOrderNumber + "]");
        JSONObject orders = new Call<JSONObject>().url(marketPlaceIntegration.getBaseUrl() + "/orders/search").method(HttpMethod.GET).inputFormat(InputFormat.JSON)
                .headers(getDefaultHeaders()).input(params).getResponseAsJson();
        JSONArray  orderList = (JSONArray) orders.get("Orders");
        for (int i = 0; i < orderList.size() ; i ++){
            JSONObject orderJson = (JSONObject)orderList.get(i);
            book(orderJson);
        }
    }

    public String getMarketPlaceOrderNumber(Order order){
        if (!order.getReflector().getJdbcTypeHelper().getTypeRef(String.class).getTypeConverter().valueOf(order.getReference())
                .startsWith(getOrderPrefix())){
            return null;
        }else {
            return order.getReference().substring(getOrderPrefix().length());
        }
    }

    @Override
    public void pack(Order order) {
        String hboOrderNumber = getMarketPlaceOrderNumber(order);
        if (ObjectUtil.isVoid(hboOrderNumber)) {
            return;
        }
        Call<JSONObject> call = new Call<JSONObject>().url(marketPlaceIntegration.getBaseUrl() + "/orders/pack/"+hboOrderNumber).method(HttpMethod.GET).headers(getDefaultHeaders());
        if (call.hasErrors()){
            throw new RuntimeException(call.getError());
        }
    }

    @Override
    public void ship(Order order) {
        String hboOrderNumber = getMarketPlaceOrderNumber(order);
        if (ObjectUtil.isVoid(hboOrderNumber)) {
            return;
        }
        Call<JSONObject> call = new Call<JSONObject>().url(marketPlaceIntegration.getBaseUrl()+"/orders/ship/"+hboOrderNumber).method(HttpMethod.GET).headers(getDefaultHeaders());
        if (call.hasErrors()){
            throw new RuntimeException(call.getError());
        }

    }


    @Override
    public void reject(OrderLine orderLine) {
        String hboOrderNumber = getMarketPlaceOrderNumber(orderLine.getOrder());
        if (ObjectUtil.isVoid(hboOrderNumber)) {
            return;
        }
        String hboOrderLineId = orderLine.getChannelOrderLineRef();
        Call<JSONObject> call = new Call<JSONObject>().url(marketPlaceIntegration.getBaseUrl()+"/order_lines/reject/"+hboOrderLineId).method(HttpMethod.GET).headers(getDefaultHeaders());
        if (call.hasErrors()){
            throw new RuntimeException(call.getError());
        }
    }



    public List<Order> getOrders(JSONObject orderJson){
        String hboOrderNumber = String.valueOf(orderJson.get("Id"));
        List<Order> orders = new Select().from(Order.class).where(new Expression(ModelReflector.instance(Order.class).getPool(),
                "REFERENCE", Operator.EQ, getOrderPrefix()+ hboOrderNumber)).execute();
        return orders;
    }
    public List<OrderLine> getOrderLines(JSONObject orderLineJson){
        String hboOrderLineNumber = String.valueOf(orderLineJson.get("Id"));
        List<OrderLine> orderLines = new Select().from(OrderLine.class).where(new Expression(ModelReflector.instance(OrderLine.class).getPool(),
                "CHANNEL_ORDER_LINE_REF", Operator.EQ, hboOrderLineNumber)).execute();
        return orderLines;
    }

    @Override
    public void cancel_line(JSONObject orderLineJson) {
        String hboOrderLineNumber = String.valueOf(orderLineJson.get("Id"));
        for (OrderLine line : getOrderLines(orderLineJson)){
            line.cancel("User Cancellation");
        }
    }

    @Override
    public void confirm_delivery(JSONObject orderJson) {
        String hboOrderNumber = String.valueOf(orderJson.get("Id"));
        List<Order> orders = new Select().from(Order.class).where(new Expression(ModelReflector.instance(Order.class).getPool(),
                "REFERENCE", Operator.EQ, getOrderPrefix()+ hboOrderNumber)).execute();
        for (Order order :orders){
            order.deliver();
        }
    }
    @Override
    public void book(JSONObject marketOrderJson) {
        if (!getOrders(marketOrderJson).isEmpty()){
            return;
        }
        FormatHelper<JSONObject> marketOrderHelper = FormatHelper.instance(marketOrderJson);

        JSONObject juser = marketOrderHelper.getElementAttribute("CreatorUser");
        marketOrderHelper.removeAttribute("CreatorUser");


        User user = getUser(juser);
        JSONObject jfacility = marketOrderHelper.getElementAttribute("Facility");
        marketOrderHelper.removeElementAttribute("Facility");
        if (!ObjectUtil.equals(jfacility.get("MerchantFacilityReference"),String.valueOf(marketPlaceIntegration.getFacilityId()))){
            throw new RuntimeException(String.format("Facility mismatch (%s vs %d) for %s",jfacility.get("MerchantFacilityReference"),marketPlaceIntegration.getFacilityId(),
                    marketPlaceIntegration.getName()));
        }
        Order order = Database.getTable(Order.class).newRecord();
        order.setReference(getOrderPrefix() + marketOrderHelper.getAttribute("Id"));
        order.setShipByDate(order.getReflector().getJdbcTypeHelper().getTypeRef(Timestamp.class).getTypeConverter().
                valueOf(marketOrderHelper.getAttribute("ShipByDate")));
        order.setShipAfterDate(order.getReflector().getJdbcTypeHelper().getTypeRef(Timestamp.class).getTypeConverter().
                valueOf(marketOrderHelper.getAttribute("ShipAfterDate")));

        order.setProductPrice(order.getReflector().getJdbcTypeHelper().getTypeRef(Double.class).getTypeConverter().
                valueOf(marketOrderHelper.getAttribute("ProductPrice")));
        order.setProductSellingPrice(order.getReflector().getJdbcTypeHelper().getTypeRef(Double.class).getTypeConverter().
                valueOf(marketOrderHelper.getAttribute("ProductSellingPrice")));
        order.setShippingPrice(order.getReflector().getJdbcTypeHelper().getTypeRef(Double.class).getTypeConverter().
                valueOf(marketOrderHelper.getAttribute("ShippingPrice")));
        order.setShippingSellingPrice(order.getReflector().getJdbcTypeHelper().getTypeRef(Double.class).getTypeConverter().
                valueOf(marketOrderHelper.getAttribute("ShippingSellingPrice")));
        order.setPrice(order.getReflector().getJdbcTypeHelper().getTypeRef(Double.class).getTypeConverter().
                valueOf(marketOrderHelper.getAttribute("Price")));
        order.setSellingPrice(order.getReflector().getJdbcTypeHelper().getTypeRef(Double.class).getTypeConverter().
                valueOf(marketOrderHelper.getAttribute("SellingPrice")));
        order.setCGst(order.getReflector().getJdbcTypeHelper().getTypeRef(Double.class).getTypeConverter().
                valueOf(marketOrderHelper.getAttribute("CGst")));
        order.setSGst(order.getReflector().getJdbcTypeHelper().getTypeRef(Double.class).getTypeConverter().
                valueOf(marketOrderHelper.getAttribute("SGst")));
        order.setIGst(order.getReflector().getJdbcTypeHelper().getTypeRef(Double.class).getTypeConverter().
                valueOf(marketOrderHelper.getAttribute("IGst")));
        order.save();

        Company company = order.getCompany().getRawRecord().getAsProxy(Company.class);
        Facility defaultShippingFacility = marketPlaceIntegration.getFacility();
        Boolean shippingWithinSameState = null;
        for (String at : new String[]{OrderAddress.ADDRESS_TYPE_SHIP_TO , OrderAddress.ADDRESS_TYPE_BILL_TO}){
            OrderAddress address = Database.getTable(OrderAddress.class).newRecord();
            address.setAddressType(at);
            Address.copy(user,address);
            address.setFirstName(user.getFirstName());
            address.setLastName(user.getLastName());
            address.setOrderId(order.getId());
            address.save();
            if (shippingWithinSameState == null && at.equals(OrderAddress.ADDRESS_TYPE_SHIP_TO)){
                shippingWithinSameState = ObjectUtil.equals(defaultShippingFacility.getStateId(),address.getStateId());
            }
        }

        double defaultGSTPct = 18;
        Cache<String, Bucket> buckets = new Cache<String, Bucket>() {
            @Override
            protected Bucket getValue(String fieldName) {
                return new Bucket();
            }
        };

        String[] LINE_FIELDS_TO_SYNC = new String[] {"PRODUCT_SELLING_PRICE","PRODUCT_PRICE","C_GST", "I_GST", "S_GST"};

        for (JSONObject orderLineElement : marketOrderHelper.getChildElements("OrderLine")){
            FormatHelper<JSONObject> lineHelper = FormatHelper.instance(orderLineElement);
            FormatHelper<JSONObject> skuHelper = FormatHelper.instance(lineHelper.getElementAttribute("Sku"));
            String itemName = (String)skuHelper.getElementAttribute("Item").get("Name");
            String uomName = (String)skuHelper.getElementAttribute("PackagingUOM").get("Name");
            Sku sku = Sku.find(order.getCompanyId(),itemName,uomName);
            if (sku == null && ObjectUtil.equals("Each",uomName)){
                sku = Sku.find(order.getCompanyId(),itemName);
            }
            if (sku == null){
                throw new RuntimeException("Invalid sku in Market place :" + itemName + " - " + uomName );
            }

            OrderLine line =  Database.getTable(OrderLine.class).newRecord();
            line.setOrderId(order.getId());
            line.setChannelOrderLineRef(lineHelper.getAttribute("Id"));
            line.setSkuId(sku.getId());
            line.setOrderedQuantity(line.getReflector().getJdbcTypeHelper().getTypeRef(double.class).getTypeConverter().
                    valueOf(lineHelper.getAttribute("OrderedQuantity")));

            line.setMaxRetailPrice(line.getReflector().getJdbcTypeHelper().getTypeRef(double.class).getTypeConverter().
                    valueOf(lineHelper.getAttribute("MaxRetailPrice")));
            line.setSellingPrice(line.getReflector().getJdbcTypeHelper().getTypeRef(double.class).getTypeConverter().
                    valueOf(lineHelper.getAttribute("SellingPrice")));
            line.setPrice(line.getReflector().getJdbcTypeHelper().getTypeRef(double.class).getTypeConverter().
                    valueOf(lineHelper.getAttribute("Price")));

            line.setCGst(line.getReflector().getJdbcTypeHelper().getTypeRef(double.class).getTypeConverter().
                    valueOf(lineHelper.getAttribute("CGst")));
            line.setSGst(line.getReflector().getJdbcTypeHelper().getTypeRef(double.class).getTypeConverter().
                    valueOf(lineHelper.getAttribute("SGst")));
            line.setIGst(line.getReflector().getJdbcTypeHelper().getTypeRef(double.class).getTypeConverter().
                    valueOf(lineHelper.getAttribute("IGst")));


            line.setDiscountPercentage(line.getReflector().getJdbcTypeHelper().getTypeRef(double.class).getTypeConverter().
                    valueOf(lineHelper.getAttribute("DiscountPercentage")));
            line.setShipFromId(getFacilityId());

            for (String priceField : LINE_FIELDS_TO_SYNC) {
                buckets.get(priceField).increment(line.getReflector().getJdbcTypeHelper().getTypeRef(double.class).getTypeConverter().valueOf(line.getReflector().get(line,priceField)));
            }

            line.save();
        }
        if (shippingWithinSameState == null){
            throw new RuntimeException("No Lines passed");
        }

        for (String priceField : LINE_FIELDS_TO_SYNC) {
            if (!DoubleUtils.equals(buckets.get(priceField).doubleValue(),order.getReflector().get(order,priceField))){
                throw new RuntimeException("Error, Price mismatch on " + order.getReference());
            }
        }
        order.save();
    }

    private User getUser(JSONObject juser) {
        FormatHelper<JSONObject> userHelper = FormatHelper.instance(juser);
        userHelper.removeAttribute("Id");
        userHelper.setAttribute("Name", getUserPrefix() + userHelper.getAttribute("Name"));
        userHelper.setAttribute("AutoGenerated", "Y");

        // This may not be email format
        User user = ModelIOFactory.getReader(User.class,JSONObject.class).read(juser);
        if (user.getRawRecord().isNewRecord()){
            user.setNotificationEnabled(false);
        }
        user.save();
        return user;
    }
}
