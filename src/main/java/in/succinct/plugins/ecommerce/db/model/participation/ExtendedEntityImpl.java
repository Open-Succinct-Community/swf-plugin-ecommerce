package in.succinct.plugins.ecommerce.db.model.participation;

import com.venky.cache.UnboundedCache;
import com.venky.swf.db.Database;
import com.venky.swf.db.model.Model;
import com.venky.swf.db.table.ModelImpl;
import com.venky.swf.plugins.collab.db.model.participants.admin.Address;

import java.lang.reflect.ParameterizedType;
import java.util.Map;


public class ExtendedEntityImpl<
        T extends ExtendedAttribute & Model,
        A extends ExtendedAddress & Model,
        P extends ExtendedPrint & Model,
        M extends ExtendedEntity<T,A,P> & Model
        > extends ModelImpl<M> {
    public ExtendedEntityImpl(M m){
        super(m);
    }

    public ExtendedEntityImpl(){

    }
    protected Class<T> getAttributeModelClass(){
        M instance = getProxy();
        ParameterizedType pt = (ParameterizedType)instance.getClass().getGenericSuperclass();
        return (Class<T>) pt.getActualTypeArguments()[0];
    }

    private Map<String, T> map = null;
    public synchronized Map<String,T> getAttributeMap() {
        if (map == null) {
            map = new  UnboundedCache<String, T>() {

                private static final long serialVersionUID = 1L;

                @Override
                protected T getValue(String name) {
                    T attr =  Database.getTable(getAttributeModelClass()).newRecord();
                    attr.setName(name);
                    if(!getProxy().getRawRecord().isNewRecord()){
                        attr.setEntityId(getProxy().getId());
                    }
                    return attr;
                }
            };
            getProxy().getAttributes().forEach(a->{
                map.put(a.getName(),a);
            });
        }
        return map;
    }
    public void saveAttributeMap() {
        Map<String,T> map = getAttributeMap();
        map.keySet().stream().sorted().forEach(attributeName->{
            T attribute = map.get(attributeName);
            attribute.setEntityId(getProxy().getId());
            attribute.save();
        });
    }
    public T getAttribute(String name) {
        Map<String,T> map = getAttributeMap();
        return map.get(name);
    }
}
