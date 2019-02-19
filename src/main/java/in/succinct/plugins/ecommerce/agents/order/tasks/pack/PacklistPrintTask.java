package in.succinct.plugins.ecommerce.agents.order.tasks.pack;

import com.venky.core.math.DoubleHolder;
import com.venky.core.util.Bucket;
import com.venky.swf.db.Database;
import com.venky.swf.plugins.collab.db.model.participants.admin.Address;
import com.venky.swf.routing.Config;
import com.venky.swf.views.controls.Control;
import com.venky.swf.views.controls.page.Body;
import com.venky.swf.views.controls.page.Css;
import com.venky.swf.views.controls.page.Head;
import com.venky.swf.views.controls.page.Html;
import com.venky.swf.views.controls.page.Image;
import com.venky.swf.views.controls.page.layout.Div;
import com.venky.swf.views.controls.page.layout.Table;
import com.venky.swf.views.controls.page.layout.Table.Column;
import com.venky.swf.views.controls.page.layout.Table.Row;
import com.venky.xml.XMLDocument;
import in.succinct.plugins.ecommerce.agents.order.tasks.EntityTask;
import in.succinct.plugins.ecommerce.db.model.inventory.Sku;
import in.succinct.plugins.ecommerce.db.model.order.Order;
import in.succinct.plugins.ecommerce.db.model.order.OrderAddress;
import in.succinct.plugins.ecommerce.db.model.order.OrderLine;
import in.succinct.plugins.ecommerce.db.model.order.OrderPrint;
import in.succinct.plugins.ecommerce.db.model.participation.Facility;
import in.succinct.plugins.ecommerce.integration.fedex.ShipWebServiceClient;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.output.BarcodeCanvasSetupException;
import org.krysalis.barcode4j.output.svg.SVGCanvasProvider;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        //.stream().filter(op->op.getDocumentType().equals(OrderPrint.DOCUMENT_TYPE_CARRIER_LABEL)).collect(Collectors.toList());
        if (printMap.get(OrderPrint.DOCUMENT_TYPE_CARRIER_LABEL) == null) {
            new ShipWebServiceClient(order).ship();
        }
        if (printMap.get(OrderPrint.DOCUMENT_TYPE_PACK_SLIP) == null){
            Html html = new Html();
            Head head = new Head();
            html.addControl(head);
            createHead(head,order);


            Body body = new Body();
            Table table = new Table();
            table.addClass("packlist");

            body.addControl(table);
            html.addControl(body);

            List<OrderAddress> addressList = order.getAddresses().stream().filter(address -> address.getAddressType().equals(OrderAddress.ADDRESS_TYPE_SHIP_TO)).collect(Collectors.toList());
            OrderAddress shipTo = addressList.get(0);
            Facility shipFrom = null;
            for (OrderLine ol : order.getOrderLines()){
                if (ol.getToShipQuantity() > 0  || ol.getShippedQuantity() > 0){
                    shipFrom = ol.getShipFrom();
                    break;
                }
            }
            table.createHeader().createColumn(3).setText("Packing Slip");


            Row address = table.createRow();
            Div shipToAddress = createShipToAddress(shipTo);
            address.createColumn().addControl(shipToAddress);

            Div shipFromAddress = createShipFromAddress(shipFrom);
            address.createColumn().addControl(shipFromAddress);

            Div orderBarcode = createOrderBarCode("Order# " + order.getOrderNumber(), order.getOrderNumber() );
            Row barcodes = table.createRow();
            barcodes.createColumn().addControl(orderBarcode);


            String courier = order.getAttribute("courier").getValue();
            String trackingNumber = order.getAttribute("tracking_number").getValue();
            long manifestNumber = Long.valueOf(order.getAttribute("manifest_id").getValue());


            Div courierBarCode = createCourierBarCode(courier, "AWB #" + trackingNumber, trackingNumber);

            barcodes.createColumn().addControl(courierBarCode);

            Table lines = new Table();
            lines.addClass("orderlines");
            table.createRow().createColumn(2).addControl(lines);


            Row header = lines.createHeader();
            header.createColumn().setText("SKU");
            header.createColumn().setText("QTY");
            header.createColumn().setText("PRICE");
            header.createColumn().setText("CGST");
            header.createColumn().setText("SGST");
            header.createColumn().setText("IGST");

            header.createColumn().setText("SELLING PRICE");


            order.getOrderLines().forEach(ol->{
                Row row = lines.createRow();
                Sku sku = ol.getSku();
                row.createColumn().setText((sku.getName()));

                Column qty = row.createColumn();
                qty.addClass("numeric");
                qty.setText((String.valueOf(ol.getPackedQuantity())));

                Column price = row.createColumn();
                price.addClass("numeric");
                price.setText(String.valueOf(new DoubleHolder(ol.getPrice(),2).getHeldDouble().doubleValue()));

                Column cgst = row.createColumn();
                cgst.addClass("numeric");
                cgst.setText(String.valueOf(new DoubleHolder(ol.getCGst(),2).getHeldDouble().doubleValue()));

                Column sgst = row.createColumn();
                sgst.addClass("numeric");
                sgst.setText(String.valueOf(new DoubleHolder(ol.getSGst(),2).getHeldDouble().doubleValue()));

                Column igst = row.createColumn();
                igst.addClass("numeric");
                igst.setText(String.valueOf(new DoubleHolder(ol.getIGst(),2).getHeldDouble().doubleValue()));

                Column sellingprice = row.createColumn();
                sellingprice.addClass("numeric");
                sellingprice.setText(String.valueOf(new DoubleHolder(ol.getSellingPrice(),2).getHeldDouble().doubleValue()));


            });

            Row total = lines.createRow();
            Column column = total.createColumn(2);
            column.setText("TOTAL");
            column.addClass("numeric");

            Column price = total.createColumn();
            price.setText(String.valueOf(new DoubleHolder(order.getPrice(),2).getHeldDouble().doubleValue()));
            price.addClass("numeric");

            Column cgst = total.createColumn();
            cgst.addClass("numeric");
            cgst.setText(String.valueOf(new DoubleHolder(order.getCGst(),2).getHeldDouble().doubleValue()));

            Column sgst = total.createColumn();
            sgst.addClass("numeric");
            sgst.setText(String.valueOf(new DoubleHolder(order.getSGst(),2).getHeldDouble().doubleValue()));

            Column igst = total.createColumn();
            igst.addClass("numeric");
            igst.setText(String.valueOf(new DoubleHolder(order.getIGst(),2).getHeldDouble().doubleValue()));


            Column sellingPrice = total.createColumn();
            sellingPrice.setText(String.valueOf(order.getSellingPrice()));
            sellingPrice.addClass("numeric");


            OrderPrint print = Database.getTable(OrderPrint.class).newRecord();
            print.setOrderId(order.getId());
            print.setDocumentType(OrderPrint.DOCUMENT_TYPE_PACK_SLIP);
            print.setImageContentType("text/html");
            byte[] bytes = html.toString().getBytes();
            print.setImage(new ByteArrayInputStream(bytes));
            print.setImageContentSize(bytes.length);
            print.setImageContentName("packlist"+order.getOrderNumber()+".html");
            print.save();
        }
    }

    private Div createCarrierLabel(OrderPrint print) {
        Div div = new Div();
        div.addClass(print.getDocumentType());
        div.addControl(new Image(Config.instance().getServerBaseUrl() + "/orders/show/" + print.getOrderId() + "/order_prints/view/" + print.getId()));
        return div;
    }

    protected void createHead(Head head , Order order){

        String cssPath = "/scripts/prints/css/packlist.css";
        URL r = getClass().getResource(cssPath);
        if (r != null){
            head.addControl(new Css(Config.instance().getServerBaseUrl() + "/resources" + cssPath));
        }

        cssPath = "/scripts/prints/css/packlist_"+order.getCompany().getId()+".css";
        r = getClass().getResource(cssPath);
        if (r != null){
            head.addControl(new Css(Config.instance().getServerBaseUrl() + "/resources" + cssPath));
        }
    }

    private Div createShipFromAddress(Facility shipFrom) {
        Div div =  new Div();
        div.addClass("from");
        div.addClass("address");
        Table table = new Table();
        div.addControl(table);
        table.createHeader().createColumn().setText("Shipped by");
        table.createRow().createColumn().setText(shipFrom.getCompany().getName());
        table.createRow().createColumn().setText("DC: " +shipFrom.getName());
        fillAddress(table,shipFrom);
        return  div;
    }

    private Div createShipToAddress(OrderAddress shipTo) {
        Div div =  new Div();
        div.addClass("to");
        div.addClass("address");
        Table table = new Table();
        div.addControl(table);
        table.createHeader().createColumn().setText("Deliver To");
        table.createRow().createColumn().setText(shipTo.getFirstName() + " " + shipTo.getLastName());
        table.createRow().createColumn();
        fillAddress(table,shipTo);
        return div;
    }

    private void fillAddress(Table table, Address address) {
        table.createRow().createColumn().setText(address.getAddressLine1());
        table.createRow().createColumn().setText(address.getAddressLine2());
        table.createRow().createColumn().setText(address.getAddressLine3());
        table.createRow().createColumn().setText(address.getAddressLine4());
        table.createRow().createColumn().setText(address.getCity().getName());
        table.createRow().createColumn().setText(address.getState().getName() + " - "  + address.getPinCode().getPinCode());
        table.createRow().createColumn().setText(address.getCountry().getName());
        table.createRow().createColumn().setText("Tel: " + address.getPhoneNumber());
    }
    private Div createCourierBarCode(String courier, String trackingNumberLiteral, String trackingNumber) {
        Div barcode = new Div();
        barcode.addClass("barcode");
        barcode.addClass( "courier");

        Table table = new Table();
        barcode.addControl(table);
        table.createHeader().createColumn().setText("Courier :" + courier);
        table.createRow().createColumn().setText(trackingNumberLiteral);
        try {
            SVGCanvasProvider provider = new SVGCanvasProvider(0);
            new Code128Bean().generateBarcode(provider,trackingNumber);

            table.createRow().createColumn().addControl(new Control("svg"){
                @Override
                public String toString() {
                    return new XMLDocument(provider.getDOM()).toString();
                }
            });

        } catch (BarcodeCanvasSetupException e) {
            //
        }
        return barcode;

    }
    private Div createOrderBarCode(String literal, String data) {
        Div barcode = new Div();
        barcode.addClass("barcode");
        barcode.addClass( "order");

        Table table = new Table();
        barcode.addControl(table);
        table.createHeader().createColumn().setText(literal);
        try {
            SVGCanvasProvider provider = new SVGCanvasProvider(0);
            new Code128Bean().generateBarcode(provider,data);

            table.createRow().createColumn().addControl(new Control("svg"){
                @Override
                public String toString() {
                    return new XMLDocument(provider.getDOM()).toString();
                }
            });

        } catch (BarcodeCanvasSetupException e) {
            //
        }
        return barcode;
    }

}
