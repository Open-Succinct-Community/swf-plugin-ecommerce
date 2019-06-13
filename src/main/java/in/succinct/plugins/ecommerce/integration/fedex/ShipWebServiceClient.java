package in.succinct.plugins.ecommerce.integration.fedex;

import com.fedex.ship.stub.Address;
import com.fedex.ship.stub.AssociatedShipmentDetail;
import com.fedex.ship.stub.ClientDetail;
import com.fedex.ship.stub.CodCollectionType;
import com.fedex.ship.stub.CodDetail;
import com.fedex.ship.stub.CommercialInvoice;
import com.fedex.ship.stub.Commodity;
import com.fedex.ship.stub.CompletedPackageDetail;
import com.fedex.ship.stub.CompletedShipmentDetail;
import com.fedex.ship.stub.Contact;
import com.fedex.ship.stub.ContactAndAddress;
import com.fedex.ship.stub.CustomerReference;
import com.fedex.ship.stub.CustomerReferenceType;
import com.fedex.ship.stub.CustomsClearanceDetail;
import com.fedex.ship.stub.Dimensions;
import com.fedex.ship.stub.DropoffType;
import com.fedex.ship.stub.FreightBaseCharge;
import com.fedex.ship.stub.FreightRateDetail;
import com.fedex.ship.stub.FreightRateNotation;
import com.fedex.ship.stub.InternationalDocumentContentType;
import com.fedex.ship.stub.LabelFormatType;
import com.fedex.ship.stub.LabelSpecification;
import com.fedex.ship.stub.LinearUnits;
import com.fedex.ship.stub.Money;
import com.fedex.ship.stub.Notification;
import com.fedex.ship.stub.NotificationSeverityType;
import com.fedex.ship.stub.PackageOperationalDetail;
import com.fedex.ship.stub.PackageRateDetail;
import com.fedex.ship.stub.PackageRating;
import com.fedex.ship.stub.PackagingType;
import com.fedex.ship.stub.Party;
import com.fedex.ship.stub.Payment;
import com.fedex.ship.stub.PaymentType;
import com.fedex.ship.stub.Payor;
import com.fedex.ship.stub.ProcessShipmentReply;
import com.fedex.ship.stub.ProcessShipmentRequest;
import com.fedex.ship.stub.PurposeOfShipmentType;
import com.fedex.ship.stub.RequestedPackageLineItem;
import com.fedex.ship.stub.RequestedShipment;
import com.fedex.ship.stub.ServiceType;
import com.fedex.ship.stub.ShipPortType;
import com.fedex.ship.stub.ShipServiceLocator;
import com.fedex.ship.stub.ShipmentOperationalDetail;
import com.fedex.ship.stub.ShipmentRateDetail;
import com.fedex.ship.stub.ShipmentRating;
import com.fedex.ship.stub.ShipmentSpecialServiceType;
import com.fedex.ship.stub.ShipmentSpecialServicesRequested;
import com.fedex.ship.stub.ShippingDocument;
import com.fedex.ship.stub.ShippingDocumentImageType;
import com.fedex.ship.stub.ShippingDocumentPart;
import com.fedex.ship.stub.Surcharge;
import com.fedex.ship.stub.TrackingId;
import com.fedex.ship.stub.TransactionDetail;
import com.fedex.ship.stub.VersionId;
import com.fedex.ship.stub.WebAuthenticationCredential;
import com.fedex.ship.stub.WebAuthenticationDetail;
import com.fedex.ship.stub.Weight;
import com.fedex.ship.stub.WeightUnits;
import com.venky.core.log.SWFLogger;
import com.venky.core.math.DoubleHolder;
import com.venky.core.util.Bucket;
import com.venky.core.util.ObjectUtil;
import com.venky.swf.db.Database;
import com.venky.swf.db.annotations.column.ui.mimes.MimeType;
import com.venky.swf.routing.Config;
import in.succinct.plugins.ecommerce.db.model.catalog.ItemCategory;
import in.succinct.plugins.ecommerce.db.model.catalog.UnitOfMeasure;
import in.succinct.plugins.ecommerce.db.model.catalog.UnitOfMeasureConversionTable;
import in.succinct.plugins.ecommerce.db.model.inventory.Sku;
import in.succinct.plugins.ecommerce.db.model.order.Manifest;
import in.succinct.plugins.ecommerce.db.model.order.Order;
import in.succinct.plugins.ecommerce.db.model.order.OrderAddress;
import in.succinct.plugins.ecommerce.db.model.order.OrderAttribute;
import in.succinct.plugins.ecommerce.db.model.order.OrderLine;
import in.succinct.plugins.ecommerce.db.model.order.OrderPrint;
import in.succinct.plugins.ecommerce.db.model.participation.Facility;
import in.succinct.plugins.ecommerce.db.model.participation.PreferredCarrier;
import org.apache.axis.types.NonNegativeInteger;
import org.apache.axis.types.PositiveInteger;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Sample code to call the FedEx Ship Service
 * <p>
 * com.fedex.ship.stub is generated via WSDL2Java, like this:<br>
 * <pre>
 * java org.apache.axis.wsdl.WSDL2Java -w -p com.fedex.ship.stub http://www.fedex.com/...../ShipService?wsdl
 * </pre>
 * <p>
 * This sample code has been tested with JDK 7 and Apache Axis 1.4
 */
//
//Sample code to call the FedEx Ship Service - GDE Express Domestic India Shipment
//
public class ShipWebServiceClient {
    //
    Order order = null;
    Manifest manifest = null;
    PreferredCarrier carrier = null;

    public ShipWebServiceClient(Order order) {
        this.order = order;
        this.manifest = getManifest(order);
        this.carrier = getCourier(manifest);
        // manifestedLinesMap loaded in getManifest
    }
    public void ship(){

        ProcessShipmentRequest request = buildRequest(); // Build a request object
        //
        try {
            // Initialize the service
            ShipServiceLocator service;
            ShipPortType port;
            //
            service = new ShipServiceLocator();
            updateEndPoint(service);
            port = service.getShipServicePort();
            //
            ProcessShipmentReply reply = port.processShipment(request); // This is the call to the ship web service passing in a request object and returning a reply object
            //
            if (isResponseOk(reply.getHighestSeverity())) // check if the call was successful
            {
                writeServiceOutput(reply);
            }

            printNotifications(reply.getNotifications());

            //
        } catch (Exception e) {
            cat.log(Level.WARNING,e.getMessage(),e);
        }
    }

    public Manifest getManifest(Order order) {
        if (!ObjectUtil.equals(Order.FULFILLMENT_STATUS_MANIFESTED, order.getFulfillmentStatus())) {
            throw new RuntimeException("Order is " + order.getFulfillmentStatus() + " cannot manifest.");
        }

        String manifestId = order.getAttribute("manifest_id").getValue();

        Manifest manifest = Database.getTable(Manifest.class).get(Long.valueOf(manifestId));

        return manifest;
    }

    //
    private ProcessShipmentRequest buildRequest() {
        ProcessShipmentRequest request = new ProcessShipmentRequest(); // Build a request object

        request.setClientDetail(createClientDetail());
        request.setWebAuthenticationDetail(createWebAuthenticationDetail());
        //
        TransactionDetail transactionDetail = new TransactionDetail();
        transactionDetail.setCustomerTransactionId(String.valueOf(order.getId())); // The client will get the same value back in the response
        request.setTransactionDetail(transactionDetail);

        //
        VersionId versionId = new VersionId("ship", 23, 0, 0);
        request.setVersion(versionId);

        //
        RequestedShipment requestedShipment = new RequestedShipment();
        requestedShipment.setShipTimestamp(Calendar.getInstance()); // Ship date and time
        requestedShipment.setDropoffType(DropoffType.REGULAR_PICKUP);
        requestedShipment.setServiceType(ServiceType.FEDEX_FREIGHT_ECONOMY); // Service types are STANDARD_OVERNIGHT, PRIORITY_OVERNIGHT, FEDEX_GROUND ...
        requestedShipment.setPackagingType(PackagingType.YOUR_PACKAGING); // Packaging type FEDEX_BOX, FEDEX_PAK, FEDEX_TUBE, YOUR_PACKAGING, ...

        //
        requestedShipment.setShipper(addShipper()); // Sender information
        //
        requestedShipment.setRecipient(addRecipient());
        //
        requestedShipment.setShippingChargesPayment(addShippingChargesPayment());
        //
        //No Special Serivces needed.
        //requestedShipment.setSpecialServicesRequested(addShipmentSpecialServicesRequested());
        //
        //Not Required as Domestic
        requestedShipment.setCustomsClearanceDetail(addCustomsClearanceDetail());
        //
        requestedShipment.setLabelSpecification(addLabelSpecification());
        //
        requestedShipment.setPackageCount(new NonNegativeInteger("1"));
        //
        List<RequestedPackageLineItem> packageLineItems = new ArrayList<>();
        OrderLine box = null;
        Bucket weight = new Bucket();
        for (OrderLine ol :order.getOrderLines()){
            if (ol.getManifestedQuantity() > 0 && ol.getSku().getItem().getItemCategory("BUNDLE_CATEGORY").getMasterItemCategoryValue().getAllowedValue().endsWith("Shipping Box")){
                box = ol;
                //packageLineItems.add(addRequestedPackageLineItem(ol));
            }
            Sku sku = ol.getSku();
            double wt = ol.getManifestedQuantity() *
                    UnitOfMeasureConversionTable.convert(sku.getWeight(), UnitOfMeasure.MEASURES_WEIGHT, sku.getWeightUOM(), UnitOfMeasure.getWeightMeasure(UnitOfMeasure.KILOGRAMS));

            weight.increment(wt);
        }
        if (box != null){
            packageLineItems.add(addRequestedPackageLineItem(box,weight));
        }else {
            packageLineItems.add(addRequestedPackageLineItem(null,weight));
        }


        requestedShipment.setRequestedPackageLineItems(packageLineItems.toArray(new RequestedPackageLineItem[]{}));
        request.setRequestedShipment(requestedShipment);

        cat.warning("Input:\n" + AxisObjectUtil.serializeAxisObject(request));

        //
        return request;
    }

    //
    private void writeServiceOutput(ProcessShipmentReply reply) throws Exception {
        try {
            cat.info("Output:\n" + AxisObjectUtil.serializeAxisObject(reply));
            cat.info(reply.getTransactionDetail().getCustomerTransactionId());
            CompletedShipmentDetail csd = reply.getCompletedShipmentDetail();
            String masterTrackingNumber = printMasterTrackingNumber(csd);
            Map<String,OrderAttribute> map = order.getAttributeMap();

            OrderAttribute attr = map.get("tracking_number");
            attr.setValue(masterTrackingNumber);

            printShipmentOperationalDetails(csd.getOperationalDetail());
            printShipmentRating(csd.getShipmentRating(),map);
            CompletedPackageDetail cpd[] = csd.getCompletedPackageDetails();
            printPackageDetails(cpd);
            saveShipmentDocumentsToFile(csd.getShipmentDocuments(), masterTrackingNumber);
            getAssociatedShipmentLabels(csd.getAssociatedShipments());

            order.saveAttributeMap(map);
        } catch (Exception e) {
            cat.log(Level.WARNING,e.getMessage(),e);
        } finally {
            //
        }
    }

    private boolean isResponseOk(NotificationSeverityType notificationSeverityType) {
        if (notificationSeverityType == null) {
            return false;
        }
        if (notificationSeverityType.equals(NotificationSeverityType.WARNING) ||
                notificationSeverityType.equals(NotificationSeverityType.NOTE) ||
                notificationSeverityType.equals(NotificationSeverityType.SUCCESS)) {
            return true;
        }
        return false;
    }

    SWFLogger cat = Config.instance().getLogger(getClass().getName());
    private void printNotifications(Notification[] notifications) {
        cat.info("Notifications:");
        if (notifications == null || notifications.length == 0) {
            cat.info("  No notifications returned");
        }
        for (int i = 0; i < notifications.length; i++) {
            Notification n = notifications[i];
            cat.warning("  Notification no. " + i + ": ");
            if (n == null) {
                continue;
            }

            NotificationSeverityType nst = n.getSeverity();
            cat.warning("    Severity: " + (nst == null ? "null" : nst.getValue()));
            cat.warning("    Code: " + n.getCode());
            cat.warning("    Message: " + n.getMessage());
            cat.warning("    Source: " + n.getSource());
        }
    }

    private void printMoney(Money money, String description, String space) {
        if (money != null) {
            cat.info(space + description + ": " + money.getAmount() + " " + money.getCurrency());
        }
    }

    private void printWeight(Weight weight, String description, String space) {
        if (weight != null) {
            cat.info(space + description + ": " + weight.getValue() + " " + weight.getUnits());
        }
    }

    private Money addMoney(String currency, Double value) {
        Money money = new Money();
        money.setCurrency(currency);
        money.setAmount(new BigDecimal(value));
        return money;
    }

    private Weight addPackageWeight(Double packageWeight, WeightUnits weightUnits) {
        Weight weight = new Weight();
        weight.setUnits(weightUnits);
        weight.setValue(new DoubleHolder(packageWeight,3).getHeldDouble());
        cat.info("Package Weight  = " + packageWeight) ;

        return weight;
    }

    private Dimensions addPackageDimensions(Integer length, Integer height, Integer width, LinearUnits linearUnits) {
        Dimensions dimensions = new Dimensions();
        dimensions.setLength(new NonNegativeInteger(length.toString()));
        dimensions.setHeight(new NonNegativeInteger(height.toString()));
        dimensions.setWidth(new NonNegativeInteger(width.toString()));
        dimensions.setUnits(linearUnits);
        return dimensions;
    }

    private  void printString(String value, String description, String space) {
        if (value != null) {
            cat.info(space + description + ": " + value);
        }
    }

    //Shipment level reply information
    private void printShipmentOperationalDetails(ShipmentOperationalDetail shipmentOperationalDetail) {
        if (shipmentOperationalDetail != null) {
            cat.info("Routing Details");
            printString(shipmentOperationalDetail.getUrsaPrefixCode(), "URSA Prefix", "  ");
            if (shipmentOperationalDetail.getCommitDay() != null)
                printString(shipmentOperationalDetail.getCommitDay().getValue(), "Service Commitment", "  ");
            printString(shipmentOperationalDetail.getAirportId(), "Airport Id", "  ");
            if (shipmentOperationalDetail.getDeliveryDay() != null)
                printString(shipmentOperationalDetail.getDeliveryDay().getValue(), "Delivery Day", "  ");
        }
    }

    private  void printShipmentRating(ShipmentRating shipmentRating, Map<String, OrderAttribute> map) {
        if (shipmentRating != null) {
            cat.info("Shipment Rate Details");
            ShipmentRateDetail[] srd = shipmentRating.getShipmentRateDetails();
            for (int j = 0; j < srd.length; j++) {
                map.get(carrier.getName()+"-RateType").setValue(srd[j].getRateType().getValue());
                map.get(carrier.getName()+"-BillingWeight").setValue(srd[j].getTotalBillingWeight().getValue().toString());
                Money money = srd[j].getTotalBaseCharge();
                map.get(carrier.getName()+"-BaseCharge").setValue(money.getAmount().toString() + " " + money.getCurrency()) ;
                money = srd[j].getTotalNetCharge();
                map.get(carrier.getName()+"-NetCharge").setValue(money.getAmount().toString() + " " + money.getCurrency()) ;
                money = srd[j].getTotalSurcharges();
                map.get(carrier.getName()+ "-TotalSurcharge").setValue(money.getAmount().toString() + " " + money.getCurrency());


                cat.info("  Rate Type: " + srd[j].getRateType().getValue());
                printWeight(srd[j].getTotalBillingWeight(), "Shipment Billing Weight", "    ");
                printMoney(srd[j].getTotalBaseCharge(), "Shipment Base Charge", "    ");
                printMoney(srd[j].getTotalNetCharge(), "Shipment Net Charge", "    ");
                printMoney(srd[j].getTotalSurcharges(), "Shipment Total Surcharge", "    ");
                if (null != srd[j].getSurcharges()) {
                    cat.info("    Surcharge Details");
                    Surcharge[] s = srd[j].getSurcharges();
                    for (int k = 0; k < s.length; k++) {
                        printMoney(s[k].getAmount(), s[k].getSurchargeType().getValue(), "      ");
                    }
                }
                printFreightDetail(srd[j].getFreightRateDetail());
            }
        }
    }

    //Package level reply information
    private  void printPackageOperationalDetails(PackageOperationalDetail packageOperationalDetail) {
        if (packageOperationalDetail != null) {
            cat.info("  Routing Details");
            printString(packageOperationalDetail.getAstraHandlingText(), "Astra", "    ");
            printString(packageOperationalDetail.getGroundServiceCode(), "Ground Service Code", "    ");
        }
    }

    private  void printPackageDetails(CompletedPackageDetail[] cpd) throws Exception {
        if (cpd != null) {
            cat.info("Package Details");
            for (int i = 0; i < cpd.length; i++) { // Package details / Rating information for each package
                String trackingNumber = cpd[i].getTrackingIds()[0].getTrackingNumber();
                printTrackingNumbers(cpd[i]);
                //
                printPackageRating(cpd[i].getPackageRating());
                //	Write label buffer to file
                ShippingDocument sd = cpd[i].getLabel();
                saveLabelToFile(sd, trackingNumber);
                printPackageOperationalDetails(cpd[i].getOperationalDetail());
            }
        }
    }

    private  void printPackageRating(PackageRating packageRating) {
        if (packageRating != null) {
            cat.info("Package Rate Details");
            PackageRateDetail[] prd = packageRating.getPackageRateDetails();
            for (int j = 0; j < prd.length; j++) {
                cat.info("  Rate Type: " + prd[j].getRateType().getValue());
                printWeight(prd[j].getBillingWeight(), "Billing Weight", "    ");
                printMoney(prd[j].getBaseCharge(), "Base Charge", "    ");
                printMoney(prd[j].getNetCharge(), "Net Charge", "    ");
                printMoney(prd[j].getTotalSurcharges(), "Total Surcharge", "    ");
                if (null != prd[j].getSurcharges()) {
                    cat.info("    Surcharge Details");
                    Surcharge[] s = prd[j].getSurcharges();
                    for (int k = 0; k < s.length; k++) {
                        printMoney(s[k].getAmount(), s[k].getSurchargeType().getValue(), "      ");
                    }
                }
            }
        }
    }

    private  void printTrackingNumbers(CompletedPackageDetail completedPackageDetail) {
        if (completedPackageDetail.getTrackingIds() != null) {
            TrackingId[] trackingId = completedPackageDetail.getTrackingIds();
            for (int i = 0; i < trackingId.length; i++) {
                String trackNumber = trackingId[i].getTrackingNumber();
                String trackType = trackingId[i].getTrackingIdType().getValue();
                String formId = trackingId[i].getFormId();
                printString(trackNumber, trackType + " tracking number", "  ");
                printString(formId, "Form Id", "  ");
            }
        }
    }

    private String getPayorAccountNumber() {
        // See if payor account number is set as system property,
        // if not default it to "XXX"
        String payorAccountNumber = carrier.getAccountNumber();
        if (payorAccountNumber == null) {
            payorAccountNumber = "XXX"; // Replace "XXX" with the payor account number
        }
        return payorAccountNumber;
    }

    private Party addShipper() {
        Party shipperParty = new Party(); // Sender information
        Contact shipperContact = new Contact();
        Facility facility = manifest.getPreferredCarrier().getFacility();

        shipperContact.setPersonName(facility.getName());
        shipperContact.setCompanyName(facility.getCompany().getName());
        shipperContact.setPhoneNumber(facility.getPhoneNumber());

        Address shipperAddress = new Address();

        StringBuilder addressLine1 = new StringBuilder();
        StringBuilder addressLine2 = new StringBuilder();

        if (!ObjectUtil.isVoid(facility.getAddressLine1())){
            addressLine1.append(facility.getAddressLine1());
        }
        if (!ObjectUtil.isVoid(facility.getAddressLine2())){
            if (addressLine1.length() >0 ){
                addressLine1.append(",");
            }
            addressLine1.append(facility.getAddressLine2());
        }
        if (!ObjectUtil.isVoid(facility.getAddressLine3())){
            addressLine2.append(facility.getAddressLine3());
        }
        if (!ObjectUtil.isVoid(facility.getAddressLine4())){
            if (addressLine2.length() >0 ){
                addressLine2.append(",");
            }
            addressLine2.append(facility.getAddressLine4());
        }


        shipperAddress.setStreetLines(new String[]{addressLine1.toString(),addressLine2.toString()});
        shipperAddress.setCity(facility.getCity().getName());
        shipperAddress.setStateOrProvinceCode(facility.getState().getCode());
        shipperAddress.setPostalCode(facility.getPinCode().getPinCode());
        shipperAddress.setCountryCode(facility.getCountry().getIsoCode());
        shipperAddress.setCountryName(facility.getCountry().getName());
        shipperAddress.setResidential(false);

        shipperParty.setContact(shipperContact);
        shipperParty.setAddress(shipperAddress);


        return shipperParty;
    }

    private Party addRecipient() {
        Party recipient = new Party(); // Recipient information
        List<OrderAddress> addresses = order.getAddresses().stream().filter(oa -> oa.getAddressType().equals(OrderAddress.ADDRESS_TYPE_SHIP_TO)).collect(Collectors.toList());
        if (addresses.isEmpty()) {
            throw new RuntimeException("No Shipto Address found for order  " + order.getId());
        }

        OrderAddress shipTo = addresses.get(0);

        Contact contactRecip = new Contact();
        contactRecip.setPersonName(shipTo.getFirstName() + " " + shipTo.getLastName());
        contactRecip.setCompanyName("");
        contactRecip.setPhoneNumber(shipTo.getPhoneNumber());
        recipient.setContact(contactRecip);
        //
        Address addressRecip = new Address();

        StringBuilder addressLine1 = new StringBuilder();
        StringBuilder addressLine2 = new StringBuilder();

        if (!ObjectUtil.isVoid(shipTo.getAddressLine1())){
            addressLine1.append(shipTo.getAddressLine1());
        }
        if (!ObjectUtil.isVoid(shipTo.getAddressLine2())){
            if (addressLine1.length() >0 ){
                addressLine1.append(",");
            }
            addressLine1.append(shipTo.getAddressLine2());
        }
        if (!ObjectUtil.isVoid(shipTo.getAddressLine3())){
            addressLine2.append(shipTo.getAddressLine3());
        }
        if (!ObjectUtil.isVoid(shipTo.getAddressLine4())){
            if (addressLine2.length() >0 ){
                addressLine2.append(",");
            }
            addressLine2.append(shipTo.getAddressLine4());
        }

        addressRecip.setStreetLines(new String[]{addressLine1.toString(),addressLine2.toString()});
        addressRecip.setCity(shipTo.getCity().getName());
        addressRecip.setStateOrProvinceCode(shipTo.getState().getCode());
        addressRecip.setPostalCode(shipTo.getPinCode().getPinCode());
        addressRecip.setCountryCode(shipTo.getCountry().getIsoCode());
        addressRecip.setCountryName(shipTo.getCountry().getName());
        addressRecip.setResidential(new Boolean(true));

        recipient.setAddress(addressRecip);
        return recipient;
    }

    private ContactAndAddress addFinancialInstitutionParty() {
        ContactAndAddress contactAndAddress = new ContactAndAddress(); // Recipient information
        Contact contactRecip = new Contact();
        contactRecip.setPersonName("Recipient Name");
        contactRecip.setCompanyName("Recipient Company Name");
        contactRecip.setPhoneNumber("1234567890");
        contactAndAddress.setContact(contactRecip);
        //
        Address addressRecip = new Address();
        addressRecip.setStreetLines(new String[]{"1 RECIPIENT STREET"});
        addressRecip.setCity("NEWDELHI");
        addressRecip.setStateOrProvinceCode("DL");
        addressRecip.setPostalCode("110010");
        addressRecip.setCountryCode("IN");
        addressRecip.setCountryName("INDIA");
        addressRecip.setResidential(new Boolean(false));
        contactAndAddress.setAddress(addressRecip);
        return contactAndAddress;
    }

    private Payment addShippingChargesPayment() {
        Payment payment = new Payment(); // Payment information
        payment.setPaymentType(PaymentType.SENDER);

        Payor payor = new Payor();

        Party responsibleParty = new Party();
        responsibleParty.setAccountNumber(getPayorAccountNumber());
        Address responsiblePartyAddress = new Address();
        responsiblePartyAddress.setCountryCode("IN");
        responsibleParty.setAddress(responsiblePartyAddress);
        responsibleParty.setContact(new Contact());
        payor.setResponsibleParty(responsibleParty);
        payment.setPayor(payor);
        return payment;
    }

    private Payment addDutiesPayment() {
        Payment payment = new Payment(); // Payment information
        if (carrier.isTaxesPaidBySender()){
            payment.setPaymentType(PaymentType.SENDER);
        }else {
            payment.setPaymentType(PaymentType.RECIPIENT);
        }

        Payor payor = new Payor();
        Party responsibleParty = addShipper();
        payor.setResponsibleParty(responsibleParty);
        responsibleParty.setAccountNumber(getPayorAccountNumber());
        payment.setPayor(payor);

        return payment;
    }

    private class ObjectHolder<T> {
        T value;

        ObjectHolder(T value) {
            this.value = value;
        }
    }

    private RequestedPackageLineItem addRequestedPackageLineItem(OrderLine ol, Bucket weight) {
        RequestedPackageLineItem requestedPackageLineItem = new RequestedPackageLineItem();
        requestedPackageLineItem.setSequenceNumber(new PositiveInteger("1"));
        requestedPackageLineItem.setGroupPackageCount(new NonNegativeInteger("1"));

        if (ol != null){
            Sku sku = ol.getSku();
            double l = Math.ceil(UnitOfMeasureConversionTable.convert(sku.getLength(), UnitOfMeasure.MEASURES_LENGTH, sku.getLengthUOM(), UnitOfMeasure.getLengthMeasure(UnitOfMeasure.CENTIMETERS)));
            double w = Math.ceil(UnitOfMeasureConversionTable.convert(sku.getWidth(), UnitOfMeasure.MEASURES_LENGTH, sku.getWidthUOM(), UnitOfMeasure.getLengthMeasure(UnitOfMeasure.CENTIMETERS)));
            double h = Math.ceil(ol.getManifestedQuantity() * UnitOfMeasureConversionTable.convert(sku.getHeight(), UnitOfMeasure.MEASURES_LENGTH, sku.getHeightUOM(), UnitOfMeasure.getLengthMeasure(UnitOfMeasure.CENTIMETERS)));
            requestedPackageLineItem.setDimensions(addPackageDimensions((int)l,(int)h,(int)w, LinearUnits.CM));
        }

        double wt = weight.doubleValue();

        requestedPackageLineItem.setWeight(addPackageWeight(wt, WeightUnits.KG));
        requestedPackageLineItem.setCustomerReferences(new CustomerReference[]{
                addCustomerReference(CustomerReferenceType.CUSTOMER_REFERENCE.getValue(), String.valueOf(order.getReference())),
                addCustomerReference(CustomerReferenceType.INVOICE_NUMBER.getValue(), order.getOrderNumber()),
                addCustomerReference(CustomerReferenceType.P_O_NUMBER.getValue(), "B2C" ), //order.getOrderNumber()
                addCustomerReference(CustomerReferenceType.DEPARTMENT_NUMBER.getValue(),carrier.isTaxesPaidBySender()? "BILL D/T: SENDER" : "BILL D/T: RECEIPIENT"),

        });
        return requestedPackageLineItem;
    }

    private ShipmentSpecialServicesRequested addShipmentSpecialServicesRequested() {
        ShipmentSpecialServicesRequested shipmentSpecialServicesRequested = new ShipmentSpecialServicesRequested();
        ShipmentSpecialServiceType shipmentSpecialServiceType[] = new ShipmentSpecialServiceType[1];
        shipmentSpecialServiceType[0] = ShipmentSpecialServiceType.COD;
        shipmentSpecialServicesRequested.setSpecialServiceTypes(shipmentSpecialServiceType);
        CodDetail codDetail = new CodDetail();
        codDetail.setCollectionType(CodCollectionType.CASH);
        Money codMoney = new Money();
        codMoney.setCurrency("INR");
        codMoney.setAmount(new BigDecimal(400));
        codDetail.setCodCollectionAmount(codMoney);
        codDetail.setFinancialInstitutionContactAndAddress(addFinancialInstitutionParty());
        codDetail.setRemitToName("Remitter");
        shipmentSpecialServicesRequested.setCodDetail(codDetail);
        return shipmentSpecialServicesRequested;
    }

    private  CustomerReference addCustomerReference(String customerReferenceType, String customerReferenceValue) {
        CustomerReference customerReference = new CustomerReference();
        customerReference.setCustomerReferenceType(CustomerReferenceType.fromString(customerReferenceType));
        customerReference.setValue(customerReferenceValue);
        return customerReference;
    }

    private  LabelSpecification addLabelSpecification() {
        LabelSpecification labelSpecification = new LabelSpecification(); // Label specification
        labelSpecification.setImageType(ShippingDocumentImageType.PNG);// Image types PDF, PNG, DPL, ...
        labelSpecification.setLabelFormatType(LabelFormatType.COMMON2D); //LABEL_DATA_ONLY, COMMON2D
        //labelSpecification.setLabelStockType(LabelStockType.value2); // STOCK_4X6.75_LEADING_DOC_TAB
        //labelSpecification.setLabelPrintingOrientation(LabelPrintingOrientationType.TOP_EDGE_OF_TEXT_FIRST);
        return labelSpecification;
    }

    private CustomsClearanceDetail addCustomsClearanceDetail() {
        CustomsClearanceDetail customs = new CustomsClearanceDetail(); // International details
        customs.setDutiesPayment(addDutiesPayment());
        customs.setCustomsValue(addMoney("INR", new DoubleHolder(order.getSellingPrice(),2).getHeldDouble().doubleValue()));
        customs.setDocumentContent(InternationalDocumentContentType.NON_DOCUMENTS);
        customs.setCommercialInvoice(addCommercialInvoice());
        List<Commodity> commodities = new ArrayList<>();
        order.getOrderLines().forEach(ol->{
            if (ol.getManifestedQuantity() > 0){
                commodities.add(addCommodity(ol));
            }
        });
        if (!commodities.isEmpty()){
            customs.setCommodities(commodities.toArray(new Commodity[]{}));// Commodity details
        }
        return customs;
    }

    private  CommercialInvoice addCommercialInvoice() {
        CommercialInvoice commercialInvoice = new CommercialInvoice();
        commercialInvoice.setPurpose(PurposeOfShipmentType.SOLD);
        /*
        commercialInvoice.setCustomerReferences(new CustomerReference[]{
                addCustomerReference(CustomerReferenceType.CUSTOMER_REFERENCE.getValue(), order.getReference()),
        });
        */
        return commercialInvoice;
    }

    private  Commodity addCommodity(OrderLine ol) {
        Commodity commodity = new Commodity();
        commodity.setNumberOfPieces(new NonNegativeInteger("1"));
        commodity.setDescription(ol.getSku().getItem().getItemCategory("BUNDLE_CATEGORY").getMasterItemCategoryValue().getAllowedValue());
        commodity.setCountryOfManufacture("IN");
        commodity.setWeight(new Weight());
        Bucket wt = new Bucket();
        wt.increment(ol.getManifestedQuantity() * UnitOfMeasureConversionTable.convert(
                ol.getReflector().getJdbcTypeHelper().getTypeRef(Double.class).getTypeConverter().valueOf(ol.getSku().getWeight())  ,
                UnitOfMeasure.MEASURES_WEIGHT,ol.getSku().getWeightUOM().getName(),UnitOfMeasure.KILOGRAMS));

        commodity.getWeight().setValue(new BigDecimal(wt.doubleValue()));
        commodity.getWeight().setUnits(WeightUnits.KG);
        commodity.setQuantity(new BigDecimal(ol.getManifestedQuantity()));
        commodity.setQuantityUnits("EA");
        commodity.setUnitPrice(new Money());
        commodity.getUnitPrice().setAmount(new DoubleHolder(ol.getSellingPrice()/ol.getManifestedQuantity(), 2).getHeldDouble());
        commodity.getUnitPrice().setCurrency("INR");
        commodity.setCustomsValue(new Money());
        commodity.getCustomsValue().setAmount(new DoubleHolder(ol.getSellingPrice(),2).getHeldDouble());
        commodity.getCustomsValue().setCurrency("INR");
        commodity.setCountryOfManufacture("IN");
        ItemCategory hsn = ol.getSku().getItem().getItemCategory("HSN");
        if (hsn != null){
            String  hsnCode = hsn.getMasterItemCategoryValue().getAllowedValue();
            commodity.setHarmonizedCode(hsnCode);
        }else {
            //throw new RuntimeException("HSN Code not configured for item " + ol.getSku().getItem().getName());
        }
        cat.info("Commodity Weight " + ol.getId() + " = " +wt.doubleValue()) ;

        return commodity;
    }

    private  void printFreightDetail(FreightRateDetail freightRateDetail) {
        if (freightRateDetail != null) {
            cat.info("  Freight Details");
            printFreightNotations(freightRateDetail);
            printFreightBaseCharges(freightRateDetail);

        }
    }

    private  void printFreightNotations(FreightRateDetail frd) {
        if (null != frd.getNotations()) {
            cat.info("    Notations");
            FreightRateNotation notations[] = frd.getNotations();
            for (int n = 0; n < notations.length; n++) {
                printString(notations[n].getCode(), "Code", "      ");
                printString(notations[n].getDescription(), "Notification", "      ");
            }
        }
    }

    private  void printFreightBaseCharges(FreightRateDetail frd) {
        if (null != frd.getBaseCharges()) {
            FreightBaseCharge baseCharges[] = frd.getBaseCharges();
            for (int i = 0; i < baseCharges.length; i++) {
                cat.info("    Freight Rate Details");
                printString(baseCharges[i].getDescription(), "Description", "      ");
                printString(baseCharges[i].getFreightClass().getValue(), "Freight Class", "      ");
                printString(baseCharges[i].getRatedAsClass().getValue(), "Rated Class", "      ");
                printWeight(baseCharges[i].getWeight(), "Weight", "      ");
                printString(baseCharges[i].getChargeBasis().getValue(), "Charge Basis", "      ");
                printMoney(baseCharges[i].getChargeRate(), "Charge Rate", "      ");
                printMoney(baseCharges[i].getExtendedAmount(), "Extended Amount", "      ");
                printString(baseCharges[i].getNmfcCode(), "NMFC Code", "      ");
            }
        }
    }

    private  String printMasterTrackingNumber(CompletedShipmentDetail csd) {
        String trackingNumber = "";
        if (null != csd.getMasterTrackingId()) {
            trackingNumber = csd.getMasterTrackingId().getTrackingNumber();
            cat.info("Master Tracking Number");
            cat.info("  Type: "
                    + csd.getMasterTrackingId().getTrackingIdType());
            cat.info("  Tracking Number: "
                    + trackingNumber);
        }
        return trackingNumber;
    }

    //Saving and displaying shipping documents (labels)
    private  void saveLabelToFile(ShippingDocument shippingDocument, String trackingNumber) throws Exception {
        ShippingDocumentPart[] sdparts = shippingDocument.getParts();
        for (int a = 0; a < sdparts.length; a++) {
            ShippingDocumentPart sdpart = sdparts[a];
            String shippingDocumentType = shippingDocument.getType().getValue();

            OrderPrint print = Database.getTable(OrderPrint.class).newRecord();
            print.setOrderId(order.getId());
            print.setDocumentType(OrderPrint.DOCUMENT_TYPE_CARRIER_LABEL);
            print.setDocumentId(shippingDocumentType);
            print.setImageContentType(MimeType.IMAGE_PNG.toString());
            print.setImageContentName(shippingDocumentType + "." + trackingNumber + ".png");
            print.setImage(new ByteArrayInputStream(sdpart.getImage()));
            print.setImageContentSize(sdpart.getImage().length);

            print.save();
        }
    }

    private  void saveShipmentDocumentsToFile(ShippingDocument[] shippingDocument, String trackingNumber) throws Exception {
        if (shippingDocument != null) {
            for (int i = 0; i < shippingDocument.length; i++) {
                ShippingDocumentPart[] sdparts = shippingDocument[i].getParts();
                for (int a = 0; a < sdparts.length; a++) {
                    ShippingDocumentPart sdpart = sdparts[a];
                    String labelName = shippingDocument[i].getType().getValue();
                    OrderPrint print = Database.getTable(OrderPrint.class).newRecord();
                    print.setOrderId(order.getId());
                    print.setDocumentType(OrderPrint.DOCUMENT_TYPE_CARRIER_LABEL);
                    print.setDocumentId(labelName);
                    print.setImageContentType(MimeType.IMAGE_PNG.toString());
                    print.setImageContentName(labelName + "." + trackingNumber + "_" + a + ".png");
                    print.setImage(new ByteArrayInputStream(sdpart.getImage()));
                    print.setImageContentSize(sdpart.getImage().length);
                    print.save();
                }
            }
        }
    }

    private  void getAssociatedShipmentLabels(AssociatedShipmentDetail[] associatedShipmentDetail) throws Exception {
        if (associatedShipmentDetail != null) {
            for (int j = 0; j < associatedShipmentDetail.length; j++) {
                if (associatedShipmentDetail[j].getLabel() != null && associatedShipmentDetail[j].getType() != null) {
                    String trackingNumber = associatedShipmentDetail[j].getTrackingId().getTrackingNumber();
                    String associatedShipmentType = associatedShipmentDetail[j].getType().getValue();
                    ShippingDocument associatedShipmentLabel = associatedShipmentDetail[j].getLabel();
                    saveAssociatedShipmentLabelToFile(associatedShipmentLabel, trackingNumber, associatedShipmentType);
                }
            }
        }
    }

    private  void saveAssociatedShipmentLabelToFile(ShippingDocument shippingDocument, String trackingNumber, String labelName) throws Exception {
        ShippingDocumentPart[] sdparts = shippingDocument.getParts();
        for (int a = 0; a < sdparts.length; a++) {
            ShippingDocumentPart sdpart = sdparts[a];
            OrderPrint print = Database.getTable(OrderPrint.class).newRecord();
            print.setOrderId(order.getId());
            print.setDocumentType(OrderPrint.DOCUMENT_TYPE_CARRIER_LABEL);
            print.setDocumentId(labelName);
            print.setImageContentType(MimeType.IMAGE_PNG.toString());
            print.setImageContentName(labelName + "." + trackingNumber + "_" + a + ".png");
            print.setImage(new ByteArrayInputStream(sdpart.getImage()));
            print.save();
        }
    }

    private PreferredCarrier getCourier(Manifest manifest) {
        return manifest.getPreferredCarrier();
    }

    private ClientDetail createClientDetail() {
        ClientDetail clientDetail = new ClientDetail();
        String accountNumber = carrier.getAccountNumber();
        String meterNumber = carrier.getMeterNumber();
        clientDetail.setAccountNumber(accountNumber);
        clientDetail.setMeterNumber(meterNumber);
        return clientDetail;
    }

    private WebAuthenticationDetail createWebAuthenticationDetail() {
        WebAuthenticationCredential userCredential = new WebAuthenticationCredential();
        userCredential.setKey(carrier.getApiKey());
        userCredential.setPassword(carrier.getPassword());

        WebAuthenticationCredential parentCredential = null;
        Boolean useParentCredential = false; //Set this value to true is using a parent credential
        if (useParentCredential) {

            String parentKey = System.getProperty("parentkey");
            String parentPassword = System.getProperty("parentpassword");
            //
            // See if the parentkey and parentpassword properties are set,
            // if set use those values, otherwise default them to "XXX"
            //
            if (parentKey == null) {
                parentKey = "XXX"; // Replace "XXX" with clients parent key
            }
            if (parentPassword == null) {
                parentPassword = "XXX"; // Replace "XXX" with clients parent password
            }
            parentCredential = new WebAuthenticationCredential();
            parentCredential.setKey(parentKey);
            parentCredential.setPassword(parentPassword);
        }
        return new WebAuthenticationDetail(parentCredential, userCredential);
    }

    private void updateEndPoint(ShipServiceLocator serviceLocator) {
        String endPoint = carrier.getIntegrationEndPoint() + "/ship";
        if (endPoint != null) {
            serviceLocator.setShipServicePortEndpointAddress(endPoint);
        }
    }
}