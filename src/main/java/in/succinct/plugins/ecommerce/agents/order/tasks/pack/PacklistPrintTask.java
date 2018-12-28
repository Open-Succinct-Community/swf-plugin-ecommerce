package in.succinct.plugins.ecommerce.agents.order.tasks.pack;

import com.venky.core.util.Bucket;
import com.venky.swf.db.Database;
import com.venky.swf.plugins.collab.db.model.participants.admin.Address;
import com.venky.swf.views.controls.Control;
import com.venky.swf.views.controls.page.Body;
import com.venky.swf.views.controls.page.Html;
import com.venky.swf.views.controls.page.layout.Table;
import com.venky.swf.views.controls.page.layout.Table.Column;
import com.venky.swf.views.controls.page.layout.Table.Row;
import in.succinct.plugins.ecommerce.agents.order.tasks.EntityTask;
import in.succinct.plugins.ecommerce.db.model.inventory.Sku;
import in.succinct.plugins.ecommerce.db.model.order.Order;
import in.succinct.plugins.ecommerce.db.model.order.OrderAddress;
import in.succinct.plugins.ecommerce.db.model.order.OrderLine;
import in.succinct.plugins.ecommerce.db.model.order.OrderPrint;
import in.succinct.plugins.ecommerce.db.model.participation.Facility;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.output.BarcodeCanvasSetupException;
import org.krysalis.barcode4j.output.svg.SVGCanvasProvider;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
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
        Html html = new Html();
        Body body = new Body();
        Table table = new Table();
        body.addControl(table);
        html.addControl(body);

        List<OrderAddress> addressList = order.getAddresses().stream().filter(address -> address.getAddressType().equals(Address.ADDRESS_TYPE_SHIP_TO)).collect(Collectors.toList());
        OrderAddress shipTo = addressList.get(0);
        Facility shipFrom = null;
        for (OrderLine ol : order.getOrderLines()){
            if (ol.getToShipQuantity() > 0 ){
                shipFrom = ol.getShipFrom();
                break;
            }
        }
        int DEFAULT_FONT_SIZE = 10;

        Row first = createRow(table,"SHIP To","SHIP From (If undelivered return to)",DEFAULT_FONT_SIZE);
        first.addClass("shiptobegin");
        Row last = createRows(table, shipTo,shipFrom,DEFAULT_FONT_SIZE);
        first.addClass("shiptoend");

        createBarCode(table,"Order# " , order.getId() , DEFAULT_FONT_SIZE);


        String courier = order.getAttribute("Courier").getValue();
        String trackingNumber = order.getAttribute("TrackingNumber").getValue();

        createRow(table,""  , "Courier " + courier, DEFAULT_FONT_SIZE);
        createBarCode2(table,"Tracking# " , trackingNumber , DEFAULT_FONT_SIZE);

        Table lines = new Table();
        body.addControl(lines);

        Row header = lines.createHeader();
        header.addClass("orderlineheadings");
        header.createColumn().setText("SKU");
        header.createColumn().setText("ITEM");
        header.createColumn().setText("QTY");
        header.createColumn().setText("PRICE");



        Bucket btotal = new Bucket();
        order.getOrderLines().forEach(ol->{
            Row row = lines.createRow();
            Sku sku = ol.getSku();
            row.createColumn().setText(sku.getName());
            row.createColumn().setText(sku.getItem().getName());
            Column qty = row.createColumn();
            qty.setText(String.valueOf(ol.getToShipQuantity()));
            qty.setProperty("align","right");
            Column price = row.createColumn();
            price.setText(String.valueOf(ol.getSellingPrice()));
            price.setProperty("align","right");
            btotal.increment(ol.getSellingPrice());
        });
        Row total = lines.createRow();
        Column column = total.createColumn(3);
        column.setProperty("align","right");
        column.setText("TOTAL");
        Column value = total.createColumn();
        value.setProperty("align","right");
        value.setText(String.valueOf(btotal.value()));

        OrderPrint print = Database.getTable(OrderPrint.class).newRecord();
        print.setOrderId(order.getId());
        print.setDocumentType(OrderPrint.DOCUMENT_TYPE_PACK_SLIP);
        print.setImageContentType("text/html");
        byte[] bytes = html.toString().getBytes();
        print.setImage(new ByteArrayInputStream(bytes));
        print.setImageContentSize(bytes.length);
        print.setImageContentName("packlist"+order.getId()+".html");
        print.save();
    }

    private void createBarCode(Table table, String literal, long orderNumber, int fontSize) {
        createRow(table,literal,"",fontSize);
        String sOrderNumber = String.format("%012d",orderNumber);
        try {
            SVGCanvasProvider provider = new SVGCanvasProvider(0);
            new Code128Bean().generateBarcode(provider,sOrderNumber);
            Row row = table.createRow();
            Column barcode = row.createColumn(); row.createColumn();
            barcode.addControl(new Control("svg"){
                @Override
                public String toString() {
                    return provider.getDOM().toString();
                }
            });

        } catch (BarcodeCanvasSetupException e) {
            //
        }

    }
    private void createBarCode2(Table table, String literal, String value, int fontSize) {
        createRow(table,"",literal,fontSize);
        try {
            SVGCanvasProvider provider = new SVGCanvasProvider(0);
            new Code128Bean().generateBarcode(provider,value);
            Row row = table.createRow();
            row.createColumn();
            Column barcode = row.createColumn();
            barcode.addControl(new Control("svg"){
                @Override
                public String toString() {
                    return provider.getDOM().toString();
                }
            });

        } catch (BarcodeCanvasSetupException e) {
            //
        }

    }
    private Row createRows(Table table, OrderAddress shipTo, Facility shipFrom, int fontSize) {

        createRow(table,shipTo.getFirstName() + " " + shipTo.getLastName(), shipFrom.getName(),fontSize);
        createRow(table,shipTo.getAddressLine1(), shipFrom.getAddressLine1(),fontSize);
        createRow(table,shipTo.getAddressLine2(), shipFrom.getAddressLine2(),fontSize);
        createRow(table,shipTo.getAddressLine3(), shipFrom.getAddressLine3(),fontSize);
        createRow(table,shipTo.getAddressLine4(), shipFrom.getAddressLine4(),fontSize);
        createRow(table,shipTo.getCity().getName(), shipFrom.getCity().getName(),fontSize);
        createRow(table,shipTo.getState().getName(), shipFrom.getState().getName(),fontSize);
        createRow(table,shipTo.getPincode(), shipFrom.getPincode(),fontSize);
        createRow(table,shipTo.getCountry().getName(), shipFrom.getCountry().getName(),fontSize);
        return createRow(table,"Tel: " + shipTo.getPhoneNumber(), "Tel: " +shipFrom.getPhoneNumber(),fontSize);
    }

    public Row createRow(Table table, String textCol1, String textCol2, int fontSize){
        Row row = table.createRow();
        Column firstColumn = row.createColumn();
        Font font = new Font();
        font.setSize(fontSize);
        font.setText(textCol1);
        firstColumn.addControl(font);

        Column secondColumn = row.createColumn();
        font = new Font();
        font.setSize(fontSize);
        font.setText(textCol2);
        secondColumn.addControl(font);
        return row;
    }

    public static class Font extends Control {
        public Font(){
            super("font");
        }
        public void setSize(int size){
            setProperty("size",size);
        }
    }
}
