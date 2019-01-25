package in.succinct.plugins.ecommerce.db.model.participation;


import com.venky.swf.db.model.Model;

import java.util.List;
import java.util.Map;

public interface ExtendedEntity<
        T extends Model & ExtendedAttribute,
        A extends Model & ExtendedAddress,
        P extends Model & ExtendedPrint
        >{

    public List<T> getAttributes();

    public Map<String,T> getAttributeMap();
    public void saveAttributeMap(Map<String, T> map);
    public T getAttribute(String name);

    public List<A> getAddresses();

    public List<P> getPrints();

}
