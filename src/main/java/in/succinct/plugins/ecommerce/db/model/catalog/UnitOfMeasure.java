package in.succinct.plugins.ecommerce.db.model.catalog;

public interface UnitOfMeasure extends com.venky.swf.plugins.collab.db.model.uom.UnitOfMeasure {
    public static UnitOfMeasure getWeightMeasure(String name) {
        com.venky.swf.plugins.collab.db.model.uom.UnitOfMeasure uom = com.venky.swf.plugins.collab.db.model.uom.UnitOfMeasure.getMeasure(MEASURES_WEIGHT, name);
        if (uom != null) {
            return uom.getRawRecord().getAsProxy(UnitOfMeasure.class);
        }
        return null;
    }

    public static UnitOfMeasure getLengthMeasure(String name) {
        com.venky.swf.plugins.collab.db.model.uom.UnitOfMeasure uom = com.venky.swf.plugins.collab.db.model.uom.UnitOfMeasure.getLengthMeasure(name);
        if (uom != null) {
            return uom.getRawRecord().getAsProxy(UnitOfMeasure.class);
        }
        return null;
    }

    public static UnitOfMeasure getMeasure(String measures, String name) {
        com.venky.swf.plugins.collab.db.model.uom.UnitOfMeasure uom = com.venky.swf.plugins.collab.db.model.uom.UnitOfMeasure.getMeasure(measures,name);
        if (uom != null){
            return uom.getRawRecord().getAsProxy(UnitOfMeasure.class);
        }
        return null;
    }
}