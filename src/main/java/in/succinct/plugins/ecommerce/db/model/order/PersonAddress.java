package in.succinct.plugins.ecommerce.db.model.order;

import com.venky.core.string.StringUtil;
import com.venky.core.util.ObjectUtil;
import com.venky.swf.plugins.collab.db.model.participants.admin.Address;

import java.util.StringTokenizer;

public interface PersonAddress extends Address {
    public String getFirstName();
    public void setFirstName(String name);

    public String getLastName();
    public void setLastName(String name);

    public String getLongName();
    public void setLongName(String name);

    public static <T extends PersonAddress> void fixNames(T address) {
        String firstName = StringUtil.valueOf(address.getFirstName());
        String lastName = StringUtil.valueOf(address.getLastName());
        String longName = StringUtil.valueOf(address.getLongName());
        if (ObjectUtil.isVoid(firstName) && ObjectUtil.isVoid(lastName) && !ObjectUtil.isVoid(longName)){
            StringTokenizer tok = new StringTokenizer(longName);
            if (tok.hasMoreTokens()){
                firstName = tok.nextToken();
            }
            if (tok.hasMoreTokens()){
                StringBuilder blastName = new StringBuilder();
                while (tok.hasMoreTokens()) {
                    if (blastName.length() >0){
                        blastName.append(" ");
                    }
                    blastName.append(tok.nextToken());
                }
                lastName = blastName.toString();
            }
            address.setFirstName(firstName);address.setLastName(lastName);
        }else if (ObjectUtil.isVoid(longName)){
            StringBuilder bLongName = new StringBuilder();
            if (!ObjectUtil.isVoid(firstName)){
                bLongName.append(firstName);
            }
            if (!ObjectUtil.isVoid(lastName)){
                if (bLongName.length() > 0){
                    bLongName.append(" ");
                }
                bLongName.append(lastName);
            }
            longName = bLongName.toString();
            address.setLongName(longName);
        }
    }

}
