package artie.pedagogicalintervention.webservice.model;

import java.util.ArrayList;
import java.util.List;

public class PedagogicalSoftwareElement {

	//Attributes
	private String elementType;
	private List<PedagogicalSoftwareField> fields = new ArrayList<>();
	
	
	//Properties
	public String getElementType() {
		return elementType;
	}
	public void setElementType(String elementType) {
		this.elementType = elementType;
	}
	
	public List<PedagogicalSoftwareField> getFields(){
		return fields;
	}
	public void setFields(List<PedagogicalSoftwareField> fields) {
		this.fields = fields;
	}
	
	//Default constructor
	public PedagogicalSoftwareElement() {}
	
	/**
	 * Parameterized constructor
	 * @param elementType
	 * @param fields
	 */
	public PedagogicalSoftwareElement(String elementType, List<PedagogicalSoftwareField> fields) {
		this.elementType = elementType;
		this.fields = fields;
	}
	
	
	//Methods
	public void addField(PedagogicalSoftwareField field) {
		this.fields.add(field);
	}
}
