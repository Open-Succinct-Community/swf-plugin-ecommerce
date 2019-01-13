package in.succinct.plugins.ecommerce.db.model.catalog;

import java.util.List;

import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.relationship.CONNECTED_VIA;
import com.venky.swf.db.annotations.column.validations.Enumeration;
import com.venky.swf.db.annotations.model.MENU;
import com.venky.swf.db.model.Model;
import com.venky.swf.sql.Conjunction;
import com.venky.swf.sql.Expression;
import com.venky.swf.sql.Operator;
import com.venky.swf.sql.Select;

@MENU("Catalog")
public interface UnitOfMeasure extends Model {
	public static final String KILOGRAMS = "Kgs";
	public static final String GRAMS = "Gms";
	public static final String CENTIMETERS = "Cms";
	public static final String INCHES = "Inches";
	@UNIQUE_KEY
	@Enumeration(KILOGRAMS + "," + GRAMS +"," + CENTIMETERS + ","+ INCHES + ",EACH,DOZEN,Celcius,Fahrenheit")
	public String getName(); 
	public void setName(String name); 
	
	static final String MEASURES_WEIGHT = "Weight"; 
	static final String MEASURES_LENGTH = "Length"; 
	static final String MEASURES_PACKAGING = "Packaging";
	static final String MEASURES_TEMPERATURE = "Temperature";
	
	@Enumeration(MEASURES_WEIGHT +"," + MEASURES_LENGTH + "," + MEASURES_PACKAGING + "," + MEASURES_TEMPERATURE)
	public String getMeasures();
	public void setMeasures(String type); 
	
	@CONNECTED_VIA("FROM_ID")
	public List<UnitOfMeasureConversionTable> getConvertableToUOMs();
	
	@CONNECTED_VIA("TO_ID")
	public List<UnitOfMeasureConversionTable> getConvertableFromUOMs();
	
	public static UnitOfMeasure getWeightMeasure(String name) { 
		return getMeasure(MEASURES_WEIGHT,name);
	}

	public static UnitOfMeasure getLengthMeasure(String name) { 
		return getMeasure(MEASURES_LENGTH,name);
	}
	
	public static UnitOfMeasure getMeasure(String measures, String name) { 
		Select s = new Select().from(UnitOfMeasure.class);
		Expression w = new Expression(s.getPool(),Conjunction.AND);
		w.add(new Expression(s.getPool(),"NAME",Operator.EQ,name));
		w.add(new Expression(s.getPool(),"MEASURES",Operator.EQ,measures));
		List<UnitOfMeasure> uoms = s.where(w).execute();
		if (uoms.isEmpty()) {
			return null;
		}else if (uoms.size() > 1) {
			throw new  RuntimeException("Multiple UOMS match " + name + " that measures "+ measures);
		}else {
			return uoms.get(0);
		}
			
		
	}
	public static List<UnitOfMeasure> getMeasures(String measures){
		Select s = new Select().from(UnitOfMeasure.class);
		Expression w = new Expression(s.getPool(),Conjunction.AND);
		w.add(new Expression(s.getPool(),"MEASURES",Operator.EQ,measures));
		return s.where(w).execute();
	}
}
