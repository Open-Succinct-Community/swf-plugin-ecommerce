package in.succinct.plugins.ecommerce.extensions.catalog;

import in.succinct.plugins.ecommerce.db.model.catalog.UnitOfMeasureConversionTable;
import com.venky.core.util.ObjectUtil;
import com.venky.swf.db.extensions.BeforeModelSaveExtension;

public class BeforeSaveUOMConversion extends BeforeModelSaveExtension<UnitOfMeasureConversionTable>{
	static {
		registerExtension(new BeforeSaveUOMConversion());
	}
	@Override
	public void beforeSave(UnitOfMeasureConversionTable model) {
		if (!ObjectUtil.equals(model.getFrom().getMeasures(),model.getTo().getMeasures())){
			throw new RuntimeException("You cannot convert between uoms that measure " + model.getFrom().getMeasures() + " & " + model.getTo().getMeasures() );  
		}
	}

}
