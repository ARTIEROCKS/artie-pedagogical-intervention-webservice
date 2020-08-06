package artie.pedagogicalintervention.webservice.model;

import java.util.ArrayList;
import java.util.List;

public class PedagogicalSoftwareElement {

	//Attributes
	private String elementName;
	private String elementFamily;
	private List<PedagogicalSoftwareInput> inputs = new ArrayList<>();
	private PedagogicalSoftwareElement next;
	
	
	//Properties
	public String getElementName() {
		return elementName;
	}
	public void setElementName(String elementName) {
		this.elementName = elementName;
	}
	
	public String getElementFamily() {
		return this.elementFamily;
	}
	public void setElementFamily(String elementFamily) {
		this.elementFamily = elementFamily;
	}
	
	public List<PedagogicalSoftwareInput> getInputs(){
		return inputs;
	}
	public void setInputs(List<PedagogicalSoftwareInput> inputs) {
		this.inputs = inputs;
	}
	
	public PedagogicalSoftwareElement getNext() {
		return this.next;
	}
	public void setNext(PedagogicalSoftwareElement next) {
		this.next = next;
	}
	
	
	//Default constructor
	public PedagogicalSoftwareElement() {}
	
	/**
	 * Parameterized constructor
	 * @param elementName
	 * @param inputs
	 */
	public PedagogicalSoftwareElement(String elementName, String elementFamily, List<PedagogicalSoftwareInput> inputs, PedagogicalSoftwareElement next) {
		this.elementName = elementName;
		this.elementFamily = elementFamily;
		this.inputs = inputs;
		this.next = next;
	}
	
	
	/**
	 * Overrides equals
	 */
	public boolean equals(Object obj) {
		
		
		if (this == obj) return true;
	    if (obj == null) return false;
	    if (this.getClass() != obj.getClass()) return false;
	    PedagogicalSoftwareElement objElement = (PedagogicalSoftwareElement) obj;
	    
	    //Checks if the name and the family are equals
	    if (!this.elementName.equals(objElement.getElementName())) return false;
	    if (!this.elementFamily.equals(objElement.getElementFamily())) return false;
	    
	    //Checks if all the inputs are equals
	    boolean result = this.inputs.size() == objElement.getInputs().size();
	    for(PedagogicalSoftwareInput i : this.inputs) {
	    	result = result && (objElement.getInputs().stream().filter(oi -> oi.equals(i)).count() > 0);
	    }
	    
		return result;
		
	}
}