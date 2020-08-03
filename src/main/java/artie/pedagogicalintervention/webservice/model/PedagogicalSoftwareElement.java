package artie.pedagogicalintervention.webservice.model;

import java.util.ArrayList;
import java.util.List;

public class PedagogicalSoftwareElement {

	//Attributes
	private String elementName;
	private String elementFamily;
	private List<PedagogicalSoftwareField> fields = new ArrayList<>();
	private PedagogicalSoftwareElement nextElement;
	
	
	//Properties
	public String getElementName() {
		return elementName;
	}
	public void setElementName(String elementName) {
		this.elementName = elementName;
	}
	
	public String elementFamily() {
		return this.elementFamily;
	}
	public void setElementFamily(String elementFamily) {
		this.elementFamily = elementFamily;
	}
	
	public List<PedagogicalSoftwareField> getFields(){
		return fields;
	}
	public void setFields(List<PedagogicalSoftwareField> fields) {
		this.fields = fields;
	}
	
	public PedagogicalSoftwareElement getNextElement() {
		return this.nextElement;
	}
	public void setNextElement(PedagogicalSoftwareElement nextElement) {
		this.nextElement = nextElement;
	}
	
	
	//Default constructor
	public PedagogicalSoftwareElement() {}
	
	/**
	 * Parameterized constructor
	 * @param elementName
	 * @param fields
	 */
	public PedagogicalSoftwareElement(String elementName, String elementFamily, List<PedagogicalSoftwareField> fields, PedagogicalSoftwareElement nextElement) {
		this.elementName = elementName;
		this.elementFamily = elementFamily;
		this.fields = fields;
		this.nextElement = nextElement;
	}
	
	
	//Methods
	public void addField(PedagogicalSoftwareField field) {
		this.fields.add(field);
	}
}
