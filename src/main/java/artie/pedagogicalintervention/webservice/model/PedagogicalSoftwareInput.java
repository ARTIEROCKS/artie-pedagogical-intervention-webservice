package artie.pedagogicalintervention.webservice.model;

import java.util.ArrayList;
import java.util.List;

public class PedagogicalSoftwareInput {

	private String name;
	private List<PedagogicalSoftwareField> fields = new ArrayList<>();
	
	
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<PedagogicalSoftwareField> getFields() {
		return fields;
	}
	public void setFields(List<PedagogicalSoftwareField> fields) {
		this.fields = fields;
	}
	
	
	/**
	 * Default constructor
	 */
	public PedagogicalSoftwareInput() {}
	
	/**
	 * Parameterized constructor
	 * @param name
	 * @param fields
	 */
	public PedagogicalSoftwareInput(String name, List<PedagogicalSoftwareField> fields) {
		super();
		this.name = name;
		this.fields = fields;
	}

	/**
	 * Overrides equals
	 */
	public boolean equals(Object obj) {
		
		if (this == obj) return true;
	    if (obj == null) return false;
	    if (this.getClass() != obj.getClass()) return false;
	    PedagogicalSoftwareInput objInput = (PedagogicalSoftwareInput) obj;

	    if(!this.name.equals(objInput.getName())) return false;
	    
	    boolean result = this.fields.size()== objInput.getFields().size();
	    for(PedagogicalSoftwareField field : this.fields) {
	    	result = result && (objInput.getFields().stream().filter(f -> f.equals(field)).count() > 0);
	    }
		
		return result;
	}
}