package in.succinct.plugins.ecommerce.agents.inventory;

import com.venky.swf.db.Database;
import com.venky.swf.plugins.background.core.Task;
import in.succinct.plugins.ecommerce.db.model.inventory.Inventory;

import java.util.Objects;

public class HashInventoryTask implements Task {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HashInventoryTask that = (HashInventoryTask) o;
        return inventoryId == that.inventoryId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(inventoryId);
    }

    long inventoryId;
    public HashInventoryTask(long id){
        this.inventoryId = id;
    }
    public HashInventoryTask(){

    }
    @Override
    public void execute() {
        Inventory inventory = Database.getTable(Inventory.class).get(inventoryId);
        inventory.computeHash();
    }
}
