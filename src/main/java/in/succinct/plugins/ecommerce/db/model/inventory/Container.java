package in.succinct.plugins.ecommerce.db.model.inventory;

import in.succinct.plugins.ecommerce.db.model.catalog.UnitOfMeasure;

public interface Container {
	public Double getLength(); 
	public void setLength(Double length);
	
	public Long getLengthUOMId();
	public void setLengthUOMId(Long id);
	public UnitOfMeasure getLengthUOM();
	
	
	public Double getWidth(); 
	public void setWidth(Double length); 
	
	public Long getWidthUOMId();
	public void setWidthUOMId(Long id);
	public UnitOfMeasure getWidthUOM();
	
	
	public Double getHeight(); 
	public void setHeight(Double length);
	
	public Long getHeightUOMId();
	public void setHeightUOMId(Long id);
	public UnitOfMeasure getHeightUOM(); 
	
	public Double getWeight(); 
	public void setWeight(Double weight);
	
	public Long getWeightUOMId();
	public void setWeightUOMId(Long id);
	public UnitOfMeasure getWeightUOM();
	
}
