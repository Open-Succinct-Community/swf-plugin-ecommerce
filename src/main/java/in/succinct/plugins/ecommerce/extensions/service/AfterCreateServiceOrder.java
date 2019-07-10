package in.succinct.plugins.ecommerce.extensions.service;


import com.venky.core.util.ObjectUtil;
import com.venky.swf.db.Database;
import com.venky.swf.db.extensions.AfterModelCreateExtension;
import com.venky.swf.db.model.UserEmail;
import com.venky.swf.plugins.collab.db.model.user.User;
import in.succinct.plugins.ecommerce.db.model.participation.ExtendedAddress;
import in.succinct.plugins.ecommerce.db.model.service.ServiceOrder;
import in.succinct.plugins.ecommerce.db.model.service.ServiceOrderAddress;

import java.util.List;
import java.util.StringTokenizer;

public class AfterCreateServiceOrder extends AfterModelCreateExtension<ServiceOrder> {
    static {
        registerExtension(new AfterCreateServiceOrder());
    }

    //Save Addresses
    @Override
    public void afterCreate(ServiceOrder model) {
        ServiceOrderAddress billTo = Database.getTable(ServiceOrderAddress.class).newRecord();
        billTo.setServiceOrderId(model.getId());
        billTo.setAddressType(ServiceOrderAddress.ADDRESS_TYPE_BILL_TO);
        setAddress(model.getUser().getRawRecord().getAsProxy(User.class), billTo);
        billTo.save();
    }
    private void setAddress(User owner, ExtendedAddress latest) {
        StringTokenizer tok = new StringTokenizer(owner.getLongName());
        if (tok.hasMoreTokens()){
            latest.setFirstName(tok.nextToken());
        }
        if (tok.hasMoreTokens()){
            StringBuilder lastName = new StringBuilder();
            while (tok.hasMoreTokens()) {
                if (lastName.length() >0){
                    lastName.append(" ");
                }
                lastName.append(tok.nextToken());
            }
            latest.setLastName(lastName.toString());
        }
        latest.setLat(owner.getLat());
        latest.setLng(owner.getLng());
        latest.setAddressLine1(owner.getAddressLine1());
        latest.setAddressLine2(owner.getAddressLine2());
        latest.setAddressLine3(owner.getAddressLine3());
        latest.setAddressLine4(owner.getAddressLine4());
        latest.setCityId(owner.getCityId());
        latest.setStateId(owner.getStateId());
        latest.setCountryId(owner.getCountryId());
        latest.setPinCodeId(owner.getPinCodeId());
        latest.setPhoneNumber(owner.getPhoneNumber());
        latest.setAlternatePhoneNumber(owner.getAlternatePhoneNumber());
        if (!ObjectUtil.isVoid(owner.getEmail())){
            latest.setEmail(owner.getEmail());
        }else {
            List<UserEmail> emails = owner.getUserEmails();
            if (!emails.isEmpty()){
                UserEmail email = emails.get(0);
                latest.setEmail(email.getEmail());
            }
        }
        if (owner.getReflector().isVoid(latest.getCountryId()) ||
                owner.getReflector().isVoid(latest.getCityId()) ||
                owner.getReflector().isVoid(latest.getStateId()) ||
                owner.getReflector().isVoid(latest.getAddressLine1()) ||
                owner.getReflector().isVoid(latest.getFirstName())){
            throw new RuntimeException("Please update address on your profile first.");
        }
    }
}
