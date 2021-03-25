package in.succinct.plugins.ecommerce.agents.catalog;

import com.venky.swf.db.Database;
import com.venky.swf.plugins.background.core.Task;
import in.succinct.plugins.ecommerce.db.model.catalog.Item;


import java.util.Objects;

public class HashItemTask implements Task {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HashItemTask that = (HashItemTask) o;
        return itemId == that.itemId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId);
    }

    long itemId;
    public HashItemTask(long id){
        this.itemId = id;
    }
    public HashItemTask(){

    }
    @Override
    public void execute() {
        Item item = Database.getTable(Item.class).get(itemId);
        item.computeHash();
    }
}
