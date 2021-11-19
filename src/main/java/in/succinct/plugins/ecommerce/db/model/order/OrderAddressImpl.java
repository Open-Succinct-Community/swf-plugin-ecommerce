package in.succinct.plugins.ecommerce.db.model.order;

import com.venky.core.util.ObjectUtil;
import com.venky.swf.db.table.ModelImpl;

public class OrderAddressImpl extends ModelImpl<OrderAddress>{
    public OrderAddressImpl(){

    }
    public OrderAddressImpl(OrderAddress add){
        super(add);
    }

    public String getLongName() {
        StringBuilder longName = new StringBuilder();
        OrderAddress address = getProxy();
        String firstName = address.getFirstName();
        String lastName = address.getLastName();
        if (!ObjectUtil.isVoid(firstName)){
            longName.append(firstName);
        }
        if (!ObjectUtil.isVoid(lastName)){
            if (longName.length() > 0){
                longName.append(" ");
            }
            longName.append(lastName);
        }
        return longName.toString();
    }
}
