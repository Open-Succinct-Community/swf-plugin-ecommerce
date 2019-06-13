package in.succinct.plugins.ecommerce.integration.fedex;


import com.fedex.rate.stub.Address;
import com.fedex.rate.stub.ClientDetail;
import com.fedex.rate.stub.Dimensions;
import com.fedex.rate.stub.DropoffType;
import com.fedex.rate.stub.LinearUnits;
import com.fedex.rate.stub.Money;
import com.fedex.rate.stub.Notification;
import com.fedex.rate.stub.NotificationSeverityType;
import com.fedex.rate.stub.PackageRateDetail;
import com.fedex.rate.stub.PackageSpecialServicesRequested;
import com.fedex.rate.stub.PackagingType;
import com.fedex.rate.stub.Party;
import com.fedex.rate.stub.Payment;
import com.fedex.rate.stub.PaymentType;
import com.fedex.rate.stub.RatePortType;
import com.fedex.rate.stub.RateReply;
import com.fedex.rate.stub.RateReplyDetail;
import com.fedex.rate.stub.RateRequest;
import com.fedex.rate.stub.RateServiceLocator;
import com.fedex.rate.stub.RatedPackageDetail;
import com.fedex.rate.stub.RatedShipmentDetail;
import com.fedex.rate.stub.RequestedPackageLineItem;
import com.fedex.rate.stub.RequestedShipment;
import com.fedex.rate.stub.ServiceType;
import com.fedex.rate.stub.ShipmentRateDetail;
import com.fedex.rate.stub.Surcharge;
import com.fedex.rate.stub.TransactionDetail;
import com.fedex.rate.stub.VersionId;
import com.fedex.rate.stub.WebAuthenticationCredential;
import com.fedex.rate.stub.WebAuthenticationDetail;
import com.fedex.rate.stub.Weight;
import com.fedex.rate.stub.WeightUnits;
import com.fedex.ship.stub.ShipServiceLocator;
import com.venky.core.log.SWFLogger;
import com.venky.core.util.ObjectUtil;
import com.venky.swf.db.model.Model;
import com.venky.swf.routing.Config;
import in.succinct.plugins.ecommerce.db.model.participation.Facility;
import in.succinct.plugins.ecommerce.db.model.participation.PreferredCarrier;
import org.apache.axis.types.NonNegativeInteger;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.stream.Collectors;


/** 
 * Sample code to call Rate Web Service with Axis 
 * <p>
 * com.fedex.rate.stub is generated via WSDL2Java, like this:<br>
 * <pre>
 * java org.apache.axis.wsdl.WSDL2Java -w -p com.fedex.rate.stub http://www.fedex.com/...../RateService?wsdl
 * </pre>
 * 
 * This sample code has been tested with JDK 7 and Apache Axis 1.4
 */
public class RateWebServiceClient<M extends Model & com.venky.swf.plugins.collab.db.model.participants.admin.Address> {
	Facility from ;
	M rateable ;
	PreferredCarrier carrier;
	public RateWebServiceClient(Facility from , M rateable){
		this.from = from;
		this.rateable = rateable;
		carrier = from.getPreferredCarriers().stream().filter(pc-> pc.getName().equalsIgnoreCase("FedEx")).collect(Collectors.toList()).get(0);
	}
	public int getTransitDays(){
	    if (rateable.getCityId() != null && rateable.getPinCodeId() != null && rateable.getStateId() != null){
            rate();
        }
		return 3;
	}
	//
	public void rate() {
		// Build a RateRequest request object
		boolean getAllRatesFlag = false; // set to true to get the rates for different service types
	    RateRequest request = new RateRequest();
	    request.setClientDetail(createClientDetail());
        request.setWebAuthenticationDetail(createWebAuthenticationDetail());
        request.setReturnTransitAndCommit(true);
	    //
	    TransactionDetail transactionDetail = new TransactionDetail();
	    transactionDetail.setCustomerTransactionId(rateable.getReflector().getModelClass().getSimpleName() + "." + rateable.getId()); // The client will get the same value back in the response
	    request.setTransactionDetail(transactionDetail);

        //
		VersionId versionId = new VersionId("crs", 24, 0, 0);
     
        request.setVersion(versionId);
        
        //
        RequestedShipment requestedShipment = new RequestedShipment();
        
        requestedShipment.setShipTimestamp(Calendar.getInstance());
        requestedShipment.setDropoffType(DropoffType.REGULAR_PICKUP);
        if (! getAllRatesFlag) {
        	requestedShipment.setServiceType(ServiceType.FEDEX_FREIGHT_ECONOMY);
        	requestedShipment.setPackagingType(PackagingType.YOUR_PACKAGING);
        }
		StringBuilder addressLine1 = new StringBuilder();
		StringBuilder addressLine2 = new StringBuilder();

		if (!ObjectUtil.isVoid(from.getAddressLine1())){
			addressLine1.append(from.getAddressLine1());
		}
		if (!ObjectUtil.isVoid(from.getAddressLine2())){
			if (addressLine1.length() >0 ){
				addressLine1.append(",");
			}
			addressLine1.append(from.getAddressLine2());
		}
		if (!ObjectUtil.isVoid(from.getAddressLine3())){
			addressLine2.append(from.getAddressLine3());
		}
		if (!ObjectUtil.isVoid(from.getAddressLine4())){
			if (addressLine2.length() >0 ){
				addressLine2.append(",");
			}
			addressLine2.append(from.getAddressLine4());
		}


        Party shipper = addShipper();
        requestedShipment.setShipper(shipper);

	    //
        Party recipient = addRecipient();
	    requestedShipment.setRecipient(recipient);

	    //
	    Payment shippingChargesPayment = new Payment();
	    shippingChargesPayment.setPaymentType(PaymentType.SENDER);
	    requestedShipment.setShippingChargesPayment(shippingChargesPayment);

	    RequestedPackageLineItem rp = new RequestedPackageLineItem();
	    rp.setGroupPackageCount(new NonNegativeInteger("1"));
	    rp.setWeight(new Weight(WeightUnits.KG, new BigDecimal(1.0)));
	    //
	    //rp.setInsuredValue(new Money("INR", new BigDecimal("100.00")));
	    //
	    rp.setDimensions(new Dimensions(new NonNegativeInteger("1"), new NonNegativeInteger("1"), new NonNegativeInteger("1"), LinearUnits.IN));
	    PackageSpecialServicesRequested pssr = new PackageSpecialServicesRequested();
	    rp.setSpecialServicesRequested(pssr);
	    requestedShipment.setRequestedPackageLineItems(new RequestedPackageLineItem[] {rp});

	    
	    requestedShipment.setPackageCount(new NonNegativeInteger("1"));
	    request.setRequestedShipment(requestedShipment);
	    
	    //
		try {
			// Initialize the service
			RateServiceLocator service;
			RatePortType port;
			//
			service = new RateServiceLocator();
			updateEndPoint(service);
			port = service.getRateServicePort();
			// This is the call to the web service passing in a RateRequest and returning a RateReply
			RateReply reply = port.getRates(request); // Service call
			if (isResponseOk(reply.getHighestSeverity())) {
				writeServiceOutput(reply);
			} 
			printNotifications(reply.getNotifications());

		} catch (Exception e) {
		    e.printStackTrace();
		} 
	}
	private Party addShipper() {
		Party shipperParty = new Party(); // Sender information
		Address shipperAddress = new Address();

		StringBuilder addressLine1 = new StringBuilder();
		StringBuilder addressLine2 = new StringBuilder();

		if (!ObjectUtil.isVoid(from.getAddressLine1())){
			addressLine1.append(from.getAddressLine1());
		}
		if (!ObjectUtil.isVoid(from.getAddressLine2())){
			if (addressLine1.length() >0 ){
				addressLine1.append(",");
			}
			addressLine1.append(from.getAddressLine2());
		}
		if (!ObjectUtil.isVoid(from.getAddressLine3())){
			addressLine2.append(from.getAddressLine3());
		}
		if (!ObjectUtil.isVoid(from.getAddressLine4())){
			if (addressLine2.length() >0 ){
				addressLine2.append(",");
			}
			addressLine2.append(from.getAddressLine4());
		}


		shipperAddress.setStreetLines(new String[]{addressLine1.toString(),addressLine2.toString()});
		shipperAddress.setCity(from.getCity().getName());
		shipperAddress.setStateOrProvinceCode(from.getState().getCode());
		shipperAddress.setPostalCode(from.getPinCode().getPinCode());
		shipperAddress.setCountryCode(from.getCountry().getIsoCode());
		shipperAddress.setCountryName(from.getCountry().getName());
		shipperAddress.setResidential(false);

		shipperParty.setAddress(shipperAddress);


		return shipperParty;
	}

	private Party addRecipient() {
		Party recipient = new Party(); // Recipient information

		Address addressRecip = new Address();

		StringBuilder addressLine1 = new StringBuilder();
		StringBuilder addressLine2 = new StringBuilder();

		if (!ObjectUtil.isVoid(rateable.getAddressLine1())){
			addressLine1.append(rateable.getAddressLine1());
		}
		if (!ObjectUtil.isVoid(rateable.getAddressLine2())){
			if (addressLine1.length() >0 ){
				addressLine1.append(",");
			}
			addressLine1.append(rateable.getAddressLine2());
		}
		if (!ObjectUtil.isVoid(rateable.getAddressLine3())){
			addressLine2.append(rateable.getAddressLine3());
		}
		if (!ObjectUtil.isVoid(rateable.getAddressLine4())){
			if (addressLine2.length() >0 ){
				addressLine2.append(",");
			}
			addressLine2.append(rateable.getAddressLine4());
		}

		addressRecip.setStreetLines(new String[]{addressLine1.toString(),addressLine2.toString()});
		addressRecip.setCity(rateable.getCity().getName());
		addressRecip.setStateOrProvinceCode(rateable.getState().getCode());
		addressRecip.setPostalCode(rateable.getPinCode().getPinCode());
		addressRecip.setCountryCode(rateable.getCountry().getIsoCode());
		addressRecip.setCountryName(rateable.getCountry().getName());
		addressRecip.setResidential(new Boolean(true));

		recipient.setAddress(addressRecip);
		return recipient;
	}
	public  void writeServiceOutput(RateReply reply) {
		RateReplyDetail[] rrds = reply.getRateReplyDetails();
		for (int i = 0; i < rrds.length; i++) {
			RateReplyDetail rrd = rrds[i];
			//print("\nService type", rrd.getServiceType());
			print("Packaging type", rrd.getPackagingType());
			//print("Delivery DOW", rrd.getDeliveryDayOfWeek());
			if(rrd.getDeliveryDayOfWeek() != null){
				int month = rrd.getDeliveryTimestamp().get(Calendar.MONTH)+1;
				int date = rrd.getDeliveryTimestamp().get(Calendar.DAY_OF_MONTH);
				int year = rrd.getDeliveryTimestamp().get(Calendar.YEAR);
				String delDate = new String(month + "/" + date + "/" + year);
				print("Delivery date", delDate);
				print("Calendar DOW", rrd.getDeliveryTimestamp().get(Calendar.DAY_OF_WEEK));
			}

			RatedShipmentDetail[] rsds = rrd.getRatedShipmentDetails();
			for (int j = 0; j < rsds.length; j++) {
				print("RatedShipmentDetail " + j, "");
				RatedShipmentDetail rsd = rsds[j];
				ShipmentRateDetail srd = rsd.getShipmentRateDetail();
				print("  Rate type", srd.getRateType());
				printWeight("  Total Billing weight", srd.getTotalBillingWeight());
				printMoney("  Total surcharges", srd.getTotalSurcharges());
				printMoney("  Total net charge", srd.getTotalNetCharge());

				RatedPackageDetail[] rpds = rsd.getRatedPackages();
				if (rpds != null && rpds.length > 0) {
					print("  RatedPackageDetails", "");
					for (int k = 0; k < rpds.length; k++) {
						print("  RatedPackageDetail " + i, "");
						RatedPackageDetail rpd = rpds[k];
						PackageRateDetail prd = rpd.getPackageRateDetail();
						if (prd != null) {
							printWeight("    Billing weight", prd.getBillingWeight());
							printMoney("    Base charge", prd.getBaseCharge());
							Surcharge[] surcharges = prd.getSurcharges();
							if (surcharges != null && surcharges.length > 0) {
								for (int m = 0; m < surcharges.length; m++) {
									Surcharge surcharge = surcharges[m];
									printMoney("    " + surcharge.getDescription() + " surcharge", surcharge.getAmount());
								}
							}
						}
					}
				}
			}
			cat.info("");
		}
	}
	
	private  ClientDetail createClientDetail() {
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
	
	private  void printNotifications(Notification[] notifications) {
		cat.info("Notifications:");
		if (notifications == null || notifications.length == 0) {
			cat.info("  No notifications returned");
		}
		for (int i=0; i < notifications.length; i++){
			Notification n = notifications[i];
			System.out.print("  Notification no. " + i + ": ");
			if (n == null) {
				cat.info("null");
				continue;
			} else {
				cat.info("");
			}
			NotificationSeverityType nst = n.getSeverity();

			cat.info("    Severity: " + (nst == null ? "null" : nst.getValue()));
			cat.info("    Code: " + n.getCode());
			cat.info("    Message: " + n.getMessage());
			cat.info("    Source: " + n.getSource());
		}
	}
	
	private  boolean isResponseOk(NotificationSeverityType notificationSeverityType) {
		if (notificationSeverityType == null) {
			return false;
		}
		if (notificationSeverityType.equals(NotificationSeverityType.WARNING) ||
			notificationSeverityType.equals(NotificationSeverityType.NOTE)    ||
			notificationSeverityType.equals(NotificationSeverityType.SUCCESS)) {
			return true;
		}
 		return false;
	}
	
	private  void print(String msg, Object obj) {
		if (msg == null || obj == null) {
			return;
		}
		cat.info(msg + ": " + obj.toString());
	}
	
	private  void printMoney(String msg, Money money) {
		if (msg == null || money == null) {
			return;
		}
		cat.info(msg + ": " + money.getAmount() + " " + money.getCurrency());
	}
	
	private  void printWeight(String msg, Weight weight) {
		if (msg == null || weight == null) {
			return;
		}
		cat.info(msg + ": " + weight.getValue() + " " + weight.getUnits());
	}

	private void updateEndPoint(RateServiceLocator serviceLocator) {
		String endPoint = carrier.getIntegrationEndPoint() + "/rate";
		if (endPoint != null) {
			serviceLocator.setRateServicePortEndpointAddress(endPoint);
		}
	}

	SWFLogger cat = Config.instance().getLogger(getClass().getName());
}
