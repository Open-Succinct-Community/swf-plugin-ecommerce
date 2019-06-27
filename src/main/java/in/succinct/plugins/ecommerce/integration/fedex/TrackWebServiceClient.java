package in.succinct.plugins.ecommerce.integration.fedex;

import com.fedex.track.stub.Address;
import com.fedex.track.stub.CarrierCodeType;
import com.fedex.track.stub.ClientDetail;
import com.fedex.track.stub.CompletedTrackDetail;
import com.fedex.track.stub.CustomerExceptionRequestDetail;
import com.fedex.track.stub.DeliveryOptionEligibilityDetail;
import com.fedex.track.stub.Money;
import com.fedex.track.stub.Notification;
import com.fedex.track.stub.NotificationSeverityType;
import com.fedex.track.stub.TrackChargeDetail;
import com.fedex.track.stub.TrackDetail;
import com.fedex.track.stub.TrackEvent;
import com.fedex.track.stub.TrackIdentifierType;
import com.fedex.track.stub.TrackOtherIdentifierDetail;
import com.fedex.track.stub.TrackPackageIdentifier;
import com.fedex.track.stub.TrackPortType;
import com.fedex.track.stub.TrackReply;
import com.fedex.track.stub.TrackRequest;
import com.fedex.track.stub.TrackRequestProcessingOptionType;
import com.fedex.track.stub.TrackSelectionDetail;
import com.fedex.track.stub.TrackServiceLocator;
import com.fedex.track.stub.TrackStatusAncillaryDetail;
import com.fedex.track.stub.TrackStatusDetail;
import com.fedex.track.stub.TrackingDateOrTimestamp;
import com.fedex.track.stub.TrackingDateOrTimestampType;
import com.fedex.track.stub.TransactionDetail;
import com.fedex.track.stub.VersionId;
import com.fedex.track.stub.WebAuthenticationCredential;
import com.fedex.track.stub.WebAuthenticationDetail;
import com.fedex.track.stub.Weight;
import com.venky.core.collections.SequenceSet;
import com.venky.core.date.DateUtils;
import com.venky.core.log.SWFLogger;
import com.venky.core.util.Bucket;
import com.venky.core.util.ObjectUtil;
import com.venky.swf.db.Database;
import com.venky.swf.routing.Config;
import com.venky.swf.sql.Conjunction;
import com.venky.swf.sql.Expression;
import com.venky.swf.sql.Operator;
import com.venky.swf.sql.Select;
import in.succinct.plugins.ecommerce.db.model.order.Manifest;
import in.succinct.plugins.ecommerce.db.model.order.Order;
import in.succinct.plugins.ecommerce.db.model.order.OrderAttribute;
import in.succinct.plugins.ecommerce.db.model.order.OrderIntransitEvent;
import in.succinct.plugins.ecommerce.db.model.participation.PreferredCarrier;

import javax.xml.crypto.Data;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/** 
 * Demo of using the Track service with Axis 
 * to track a shipment.
 * <p>
 * com.fedex.track.stub is generated via WSDL2Java, like this:<br>
 * <pre>
 * java org.apache.axis.wsdl.WSDL2Java -w -p com.fedex.track.stub http://www.fedex.com/...../TrackService?wsdl
 * </pre>
 * 
 * This sample code has been tested with JDK 7 and Apache Axis 1.4
 */
public class TrackWebServiceClient {
	//\
	Manifest manifest = null;
	PreferredCarrier carrier = null;
	public TrackWebServiceClient(Manifest manifest){
		this.manifest = manifest;this.carrier = manifest.getPreferredCarrier();

		List<Long> orderIds = new SequenceSet<>();
		Map<String,Long> trackingNumbers = new HashMap<>();
		{
			Select s = new Select().from(OrderAttribute.class);
			Expression where = new Expression(s.getPool(), Conjunction.AND);
			where.add(new Expression(s.getPool(), "NAME", Operator.EQ, "manifest_number"));
			where.add(new Expression(s.getPool(), "VALUE", Operator.EQ, manifest.getManifestNumber()));
			s.where(where).add(" and exists ( select 1 from orders where id = order_attributes.order_id and fulfillment_status == 'SHIPPED' ) ");
			List<OrderAttribute> oas = s.execute();
			oas.forEach(oa -> {
				orderIds.add(oa.getOrderId());
			});
		}
		{
			Select s = new Select().from(OrderAttribute.class);
			Expression where = new Expression(s.getPool(), Conjunction.AND);
			where.add(new Expression(s.getPool(), "NAME", Operator.EQ, "tracking_number"));
			where.add(new Expression(s.getPool(), "ORDER_ID", Operator.IN, orderIds.toArray()));

			List<OrderAttribute> orderAttributes = new Select().from(OrderAttribute.class).
					where(where).execute();
			orderAttributes.forEach(oa->{
				trackingNumbers.put(oa.getValue(),oa.getOrderId());
			});



		}
		Stack<Set<String>> batches = new Stack<>();
		int batchSize = 30;
		for (String trackingNumber: trackingNumbers.keySet()){
			if (batches.isEmpty() || batches.peek().size() >= batchSize){
				batches.push(new HashSet<>());
			}
			batches.peek().add(trackingNumber);
		}

		for (Set<String> batch : batches){
			TrackRequest request = new TrackRequest();

			request.setClientDetail(createClientDetail());
			request.setWebAuthenticationDetail(createWebAuthenticationDetail());
			//
			TransactionDetail transactionDetail = new TransactionDetail();
			transactionDetail.setCustomerTransactionId(manifest.getManifestNumber()); //This is a reference field for the customer.  Any value can be used and will be provided in the response.
			request.setTransactionDetail(transactionDetail);

			//
			VersionId versionId = new VersionId("trck", 16, 0, 0);
			request.setVersion(versionId);
			//

			List<TrackSelectionDetail> list = new ArrayList<>();
			for (String trackingNumber :batch){
				TrackSelectionDetail selectionDetail=new TrackSelectionDetail();
				selectionDetail.setCarrierCode(CarrierCodeType.FDXE);
				TrackPackageIdentifier packageIdentifier=new TrackPackageIdentifier();
				packageIdentifier.setType(TrackIdentifierType.TRACKING_NUMBER_OR_DOORTAG);
				packageIdentifier.setValue(trackingNumber); // tracking number
				selectionDetail.setPackageIdentifier(packageIdentifier);
			}

			request.setSelectionDetails(list.toArray(new TrackSelectionDetail[]{}));
			TrackRequestProcessingOptionType processingOption=TrackRequestProcessingOptionType.INCLUDE_DETAILED_SCANS;
			request.setProcessingOptions(new TrackRequestProcessingOptionType[]{processingOption});

			//
			try {
				// Initializing the service
				TrackServiceLocator service;
				TrackPortType port;
				//
				service = new TrackServiceLocator();
				updateEndPoint(service);
				port = service.getTrackServicePort();
				//
				TrackReply reply = port.track(request); // This is the call to the web service passing in a request object and returning a reply object
				//
				if(printNotifications(reply.getNotifications())){
					printCompletedTrackDetail(reply.getCompletedTrackDetails(),trackingNumbers);
				}
				if (isResponseOk(reply.getHighestSeverity())) // check if the call was successful
				{
					System.out.println("--Track Reply--");
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}



	}
	
	private  void printCompletedTrackDetail(CompletedTrackDetail[] ctd,Map<String,Long> trackingNumberOrderMap) {
		for (int i=0; i< ctd.length; i++) { // package detail information
			boolean cont=true;
			System.out.println("--Completed Tracking Detail--");
			if(ctd[i].getNotifications()!=null){
				System.out.println("  Completed Track Detail Notifications--");
				cont=printNotifications(ctd[i].getNotifications());
				System.out.println("  Completed Track Detail Notifications--");
			}
			if(cont){
				print("DuplicateWayBill", ctd[i].getDuplicateWaybill());
				print("Track Details Count", ctd[i].getTrackDetailsCount());
				if(ctd[i].getMoreData()){
					System.out.println("  Additional package data not yet retrieved");
					if(ctd[i].getPagingToken()!=null){
						print("  Paging Token", ctd[i].getPagingToken());
					}
				}
				printTrackDetail(ctd[i].getTrackDetails(),trackingNumberOrderMap);
			}
			System.out.println("--Completed Tracking Detail--");
			System.out.println();
		}
	}

	private  void printTrackDetail(TrackDetail[] td, Map<String,Long> trackingOrderIdMap){
		for (int i=0; i< td.length; i++) {
			boolean cont=true;
			System.out.println("--Track Details--");
			Order order = null ;
			if(td[i].getNotification()!=null){
				System.out.println("  Track Detail Notification--");
				cont=printNotifications(td[i].getNotification());
				System.out.println("  Track Detail Notification--");
			}
			if(cont){
				print("Tracking Number", td[i].getTrackingNumber());
				Long orderId = trackingOrderIdMap.get(td[i].getTrackingNumber());
				order = Database.getTable(Order.class).get(orderId);
				print("Carrier code", td[i].getCarrierCode());
				if(td[i].getService()!=null){
					if(td[i].getService().getType()!=null && 
							td[i].getService().getDescription()!=null){
						print("Service", td[i].getService().getType());
						print("Description", td[i].getService().getDescription());
					}
				}
				if(td[i].getOtherIdentifiers()!=null){
					System.out.println("--Track Package Identifer--");
					printTrackOtherIdentifierDetail(td[i].getOtherIdentifiers());
					System.out.println("--Track Package Identifer--");
				}
				if(td[i].getStatusDetail()!=null){
					System.out.println("--Status Details--");
					printStatusDetail(td[i].getStatusDetail());
					System.out.println("--Status Details--");
				}
				if(td[i].getOriginLocationAddress()!=null){
					System.out.println("--Origin Location--");
					print(td[i].getOriginLocationAddress());
					System.out.println("--Origin Location--");
				}
				if(td[i].getDestinationAddress()!=null){
					System.out.println("--Destination Location--");
					printDestinationInformation(td[i]);
					System.out.println("--Destination Location--");
				}
				if(td[i].getActualDeliveryAddress()!=null){
					System.out.println("--Delivery Address--");
					print(td[i].getActualDeliveryAddress());
					System.out.println("--Delivery Address--");
				}
				Timestamp deliveryTimestamp = null;
				Timestamp pickUpTimestamp = null;

				DateFormat ISO_8601_24H_FULL_FORMAT = DateUtils.getFormat( "yyyy-MM-dd'T'HH:mm:ssXXX");
				if(td[i].getDatesOrTimes()!=null){
					TrackingDateOrTimestamp[] dates = td[i].getDatesOrTimes();
					for(int j=0; j<dates.length; j++){
						if (dates[j].getType() == TrackingDateOrTimestampType.ACTUAL_DELIVERY){
							order.deliver();
							try {
								deliveryTimestamp = new Timestamp(ISO_8601_24H_FULL_FORMAT.parse(dates[j].getDateOrTimestamp()).getTime());
							}catch (Exception ex){

							}
						}
						if (dates[j].getType() == TrackingDateOrTimestampType.ACTUAL_PICKUP) {
							try{
								pickUpTimestamp = new Timestamp(ISO_8601_24H_FULL_FORMAT.parse(dates[j].getDateOrTimestamp()).getTime());
							}catch (Exception ex){

							}
						}
						print(dates[j].getType().getValue(), dates[j].getDateOrTimestamp());
					}
				}
				if(td[i].getDeliveryAttempts().shortValue()>0){
					System.out.println("--Delivery Information--");
					printDeliveryInformation(td[i]);
					System.out.println("--Delivery Information--");
				}
				if(td[i].getCustomerExceptionRequests()!=null){
					System.out.println("--Customer Exception Information--");
					printCustomerExceptionRequests(td[i].getCustomerExceptionRequests());
					System.out.println("--Customer Exception Information--");
				}
				if(td[i].getCharges()!=null){
					System.out.println("--Charges--");
					printCharges(td[i].getCharges());
					System.out.println("--Charges--");
				}
				if(td[i].getEvents()!=null){
					System.out.println("--Tracking Events--");
					printTrackEvents(td[i].getEvents(),order,pickUpTimestamp,deliveryTimestamp);
					System.out.println("--Tracking Events--");
				}
				System.out.println("--Track Details--");
				System.out.println();
			}
		}
	}

	private  void printCustomerExceptionRequests(CustomerExceptionRequestDetail[] exceptions){
		if(exceptions!=null){
			for(int i=0; i<exceptions.length; i++){
				CustomerExceptionRequestDetail exception=exceptions[i];
				print("Exception Id", exception.getId());
				print("Excpetion Status Code", exception.getStatusCode());
				print("Excpetion Status Description", exception.getStatusDescription());
				if(exception.getCreateTime()!=null){
					System.out.println("  Customer Exception Date--");
					print(exception.getCreateTime());
					System.out.println("  Customer Exception Date--");
				}
			}
		}
	}
	private  void printTrackEvents(TrackEvent[] events,Order order, Timestamp pickTS, Timestamp deliveryTS ){
		DateFormat ISO_8601_24H_FULL_FORMAT = DateUtils.getFormat( "yyyy-MM-dd'T'HH:mm:ssXXX");
		if(events!=null){
			int eventSeqNo = 0;
			for(int i= events.length -1; i > 0 ; i--) {
				TrackEvent event = events[i];
				OrderIntransitEvent oie = Database.getTable(OrderIntransitEvent.class).newRecord();
				oie.setEventSeqNo(eventSeqNo);
				oie.setOrderId(order.getId());
				if (ObjectUtil.equals(event.getEventType(), "AR") || ObjectUtil.equals(event.getEventType(), "DL")){
					oie.setEventType(OrderIntransitEvent.EVENT_TYPE_ARRIVED);
				}else if (ObjectUtil.equals(event.getEventType(),"DP") || ObjectUtil.equals(event.getEventType(),"PU"))  {
					oie.setEventType(OrderIntransitEvent.EVENT_TYPE_LEFT);
				}else {
					continue;
				}
				if (ObjectUtil.equals(event.getEventType(),"DL")){
					oie.setEventTimestamp(deliveryTS);
				}else if (ObjectUtil.equals(event.getEventType(),"PU")){
					oie.setEventTimestamp(pickTS);
				}else {
					oie.setEventTimestamp(new Timestamp(event.getTimestamp().getTimeInMillis()));
				}
				oie.setEventDescription(event.getEventDescription());

				print("Event no. ", i);
				print(event.getTimestamp());
				if(event.getEventType()!=null){
					print("Type", event.getEventType());
				}
				print("Station Id", event.getStationId());
				print("Exception Code", event.getStatusExceptionCode());
				print("", event.getStatusExceptionDescription());
				print("Description", event.getEventDescription());
				if(event.getAddress()!=null){
					System.out.println("  Event Address--");
					printAddress(oie,event.getAddress());
					System.out.println("  Event Address--");
				}
				System.out.println();
				oie.save();
				eventSeqNo ++;
			}
		}
	}
	private  void printStatusDetail(TrackStatusDetail tsd){
		if(tsd!=null){
			print(tsd.getCreationTime());
			print("Code", tsd.getCode());
			if(tsd.getLocation()!=null){
				System.out.println("--Location Address Detail--");
				print(tsd.getLocation());
				System.out.println("--Location Address Detail--");
			}
			if(tsd.getAncillaryDetails()!=null){
				System.out.println("--Ancillary Details--");
				printAncillaryDetails(tsd.getAncillaryDetails());
				System.out.println("--Ancillary Details--");
			}
		}
	}
	private  void printAncillaryDetails(TrackStatusAncillaryDetail[] details){
		if(details!=null){
			for(int i=0; i<details.length;i++){
				if(details[i]!=null){
					if(details[i].getReason()!=null && details[i].getReasonDescription()!=null){
						print(details[i].getReason(), details[i].getReasonDescription());
					}
				}
			}
		}
	}
	private  void printDestinationInformation(TrackDetail td){
		if(td.getDestinationAddress()!=null){
			print(td.getDestinationAddress());
		}
		print("Destination Type", td.getDestinationLocationType());
		print("Service Area", td.getDestinationServiceArea());
		print("Service Area Description", td.getDestinationServiceAreaDescription());
		print("Station Id", td.getDestinationStationId());
		print("Destination Timezone Offset", td.getDestinationLocationTimeZoneOffset());
	}
	private  void printDeliveryOptionEligibility(DeliveryOptionEligibilityDetail[] options){
		for(int i=0; i<options.length; i++){
			DeliveryOptionEligibilityDetail option = options[i];
			if(option!=null){
				print(option.getOption(), option.getEligibility());
			}
		}
	}
	private  void printDeliveryInformation(TrackDetail td){
		System.out.println("Delivery attempts: " + td.getDeliveryAttempts());
		print("Delivery Location", td.getDeliveryLocationDescription());
		print("Delivery Signature", td.getDeliverySignatureName());
		if(td.getDeliveryOptionEligibilityDetails()!=null){
			System.out.println("Delivery Options");
			printDeliveryOptionEligibility(td.getDeliveryOptionEligibilityDetails());
		}
	}
	private  void printTrackOtherIdentifierDetail(TrackOtherIdentifierDetail[] id){
		if(id!=null){
			for(int i=0; i<id.length; i++){
				if(id[i].getPackageIdentifier()!=null){
					print(id[i].getPackageIdentifier().getType(), 
							id[i].getPackageIdentifier().getValue());
				}
			}
		}
	}

	private  void printTime(Calendar calendar){
		if(calendar!=null){
			int month = calendar.get(Calendar.MONTH)+1;
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			int year = calendar.get(Calendar.YEAR);
			String date = new String(year + "-" + month + "-" + day);
			print("Date", date);
			printDOW(calendar);
		}
	}
	private  void printAddress(OrderIntransitEvent oie, Address address){
		StringBuilder location = new StringBuilder();
		print("__________________________________");
		if(address.getStreetLines()!=null){
			String[] streetLines=address.getStreetLines();
			for(int i=0;i<streetLines.length;i++){
				if(streetLines[i]!=null){
					print("Street", streetLines[i]);
							
				}
			}
		}
		location.append(address.getCity()).append( "," ).append(address.getStateOrProvinceCode()).append(",").append(address.getPostalCode());
		if (oie != null) {
			oie.setLocation(location.toString());
		}
		print("City", address.getCity());
		print("State or Province Code", address.getStateOrProvinceCode());
		print("Postal Code", address.getPostalCode());
		print("Country Code", address.getCountryCode());
		if(address.getResidential()!=null){
			if(address.getResidential()){
				print("Address Type","Residential");
			}else{
				print("Address Type", "Commercial");
			}
		}
		print("__________________________________");
	}
	private  void printDOW(Calendar calendar){
		if(calendar!=null){
			String day;
			switch(calendar.get(Calendar.DAY_OF_WEEK)){
				case 1: day="Sunday";
				break;
				case 2: day="Monday";
				break;
				case 3: day="Tuesday";
				break;
				case 4: day="Wedensday";
				break;
				case 5: day="Thursday";
				break;
				case 6: day="Friday";
				break;
				case 7: day="Saturday";
				break;
				default: day="Invalid Date";
				break;
			}
			print("Day of Week", day);			
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

	private void updateEndPoint(TrackServiceLocator serviceLocator) {
		String endPoint = carrier.getIntegrationEndPoint() + "/ship";
		if (endPoint != null) {
			serviceLocator.setTrackServicePortEndpointAddress(endPoint);
		}
	}
	
	private  void printCharges(TrackChargeDetail[] charges){
		if(charges!=null){
			for(int i=0; i<charges.length; i++){
				print("Charge Type", charges[i].getType());
				printMoney(charges[i].getChargeAmount());
			}
		}
	}
	private  void printMoney(Money money){
		if(money!=null){
			String currency = money.getCurrency();
			String amount = money.getAmount().toString();
			print("Charge", currency + " " + amount);
		}
	}
	SWFLogger cat = Config.instance().getLogger(getClass().getName());
	private  boolean printNotifications(Object n) {
		boolean cont=true;
		if(n!=null){
			Notification[] notifications=null;
			Notification notification=null;
			if(n instanceof Notification[]){
				notifications=(Notification[])n;
				if (notifications == null || notifications.length == 0) {
					System.out.println("  No notifications returned");
				}
				for (int i=0; i < notifications.length; i++){
					printNotification(notifications[i]);
					if(!success(notifications[i])){cont=false;}
				}
			}else if(n instanceof Notification){
				notification=(Notification)n;
				printNotification(notification);
				if(!success(notification)){cont=false;}
			}

		}
		return cont;
	}
	private  void printNotification(Notification notification){
		if (notification == null) {
			System.out.println("null");
		}
		NotificationSeverityType nst = notification.getSeverity();

		print("  Severity", (nst == null ? "null" : nst.getValue()));
		print("  Code", notification.getCode());
		print("  Message", notification.getMessage());
		print("  Source", notification.getSource());
	}
	
	private  boolean success(Notification notification){
		Boolean cont = true;
		if(notification!=null){
			if(notification.getSeverity()==NotificationSeverityType.FAILURE || 
					notification.getSeverity()==NotificationSeverityType.ERROR){
				cont=false;
			}
		}
		
		return cont;
	}
	

	private  void print(Object k, Object v) {
		if (k == null || v == null) {
			return;
		}
		String key;
		String value;
		if(k instanceof String){
			key=(String)k;
		}else{
			key=k.toString();
		}
		if(v instanceof String){
			value=(String)v;
		}else{
			value=v.toString();
		}
		System.out.println("  " + key + ": " + value);
	}
	
	private  void print(Object o){
		if(o!=null){
			if(o instanceof String){
				System.out.println((String)o);
			}else if(o instanceof Address){
				printAddress(null,(Address)o);
			}else if(o instanceof Calendar){
				printTime((Calendar)o);
			}else{
				System.out.println(o.toString());
			}
			
		}
	}
	
	private  void printWeight(String msg, Weight weight) {
		if (msg == null || weight == null) {
			return;
		}
		System.out.println(msg + ": " + weight.getValue() + " " + weight.getUnits());
	}

	private  String getSystemProperty(String property){
		String returnProperty = System.getProperty(property);
		if (returnProperty == null){
			return "XXX";
		}
		return returnProperty;
	}

}
