package artie.pedagogicalintervention.webservice.model;

import org.apache.commons.lang3.StringUtils;

public class PedagogicalSoftwareField implements Cloneable {
	
	//Attributes
	private String name;
	private String value;
	
	//Prperties
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	//Default constructor
	public PedagogicalSoftwareField() {}
	
	//Parameterized constructor
	public PedagogicalSoftwareField(String name, String value) {
		this.name = name;
		this.value = value;
	}
	
	
	/**
	 * Overrides equals
	 */
	public boolean equals(Object obj) {
		
		if (this == obj) return true;
	    if (obj == null) return false;
	    if (this.getClass() != obj.getClass()) return false;
		PedagogicalSoftwareField objField = (PedagogicalSoftwareField) obj;
		
		return (this.name.equals(objField.getName()) && this.value.equals(objField.getValue()));
	}
	
	/**
	 * Overrides clone
	 */
	public PedagogicalSoftwareField clone() {
		return new PedagogicalSoftwareField(name, value);
	}
	
	
	/**
	 * Checks if the value is numeric or not
	 * @return
	 */
	public boolean isNumeric() {
		return StringUtils.isNumeric(this.value);
	}
	
	/**
	 * Function to return the double value in case of the value is numeric
	 * @return
	 */
	public double getDoubleValue() {
		
		if(this.isNumeric()) {
			return Double.valueOf(this.value);
		}else {
			return 0;
		}
	}
}
