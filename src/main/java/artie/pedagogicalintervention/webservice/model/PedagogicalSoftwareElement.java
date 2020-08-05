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
}
