package in.succinct.plugins.ecommerce.agents.order.tasks;

import com.venky.core.string.StringUtil;
import com.venky.swf.db.Database;
import com.venky.swf.db.model.Model;
import com.venky.swf.db.table.RecordNotFoundException;
import com.venky.swf.plugins.background.core.Task;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;

public abstract class EntityTask<M extends Model> implements Task{
    protected Class<M> getModelClass(EntityTask<M> instance) {
        ParameterizedType pt = (ParameterizedType)instance.getClass().getGenericSuperclass();
        return (Class<M>)pt.getActualTypeArguments()[0];
    }

    protected long  id ;
    public EntityTask(long id){
        this.id = id;
    }

    @Override
    public void execute() {
        M model = Database.getTable(getModelClass(this)).lock(id);
        if (model != null) {
            execute(model);
        }else {
            throw new RecordNotFoundException(getModelClass(this).getName() + ":" + id);
        }
    }
    protected abstract void execute(M model) ;

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + getModelClass(this).getSimpleName() +"Id=" + id + "]";
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        return StringUtil.equals(toString(),obj.toString());
    }

}
