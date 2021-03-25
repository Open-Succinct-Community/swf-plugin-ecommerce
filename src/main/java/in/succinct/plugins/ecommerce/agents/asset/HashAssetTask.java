package in.succinct.plugins.ecommerce.agents.asset;

import com.venky.swf.db.Database;
import com.venky.swf.plugins.background.core.Task;
import in.succinct.plugins.ecommerce.db.model.assets.Asset;

import java.util.Objects;

public class HashAssetTask implements Task {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HashAssetTask that = (HashAssetTask) o;
        return assetId == that.assetId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(assetId);
    }

    long assetId;
    public HashAssetTask(long id){
        this.assetId = id;
    }
    public HashAssetTask(){

    }
    @Override
    public void execute() {
        Asset asset = Database.getTable(Asset.class).get(assetId);
        asset.computeHash();
    }
}
