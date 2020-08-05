package artie.pedagogicalintervention.webservice.model;

public class PedagogicalSoftwareField {
	
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
}
