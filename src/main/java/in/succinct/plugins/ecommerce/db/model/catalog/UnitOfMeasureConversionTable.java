package in.succinct.plugins.ecommerce.db.model.catalog;

public interface UnitOfMeasureConversionTable extends com.venky.swf.plugins.collab.db.model.uom.UnitOfMeasureConversionTable {

	public static double convert(Double weight, String measuresWeight, UnitOfMeasure weightUOM, com.venky.swf.plugins.collab.db.model.uom.UnitOfMeasure weightMeasure) {
		return com.venky.swf.plugins.collab.db.model.uom.UnitOfMeasureConversionTable.convert(weight,measuresWeight,weightUOM,weightMeasure);
	}
	public static double convert(double measurement, String measureType, String fromUom, String toUom ) {
		return com.venky.swf.plugins.collab.db.model.uom.UnitOfMeasureConversionTable.convert(measurement,measureType,fromUom,toUom);
	}
}
