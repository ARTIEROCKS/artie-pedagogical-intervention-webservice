package artie.pedagogicalintervention.webservice.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PedagogicalSoftwareElement implements Cloneable {

	//Attributes
	private String elementName;
	private String elementFamily;
	private List<PedagogicalSoftwareInput> inputs = new ArrayList<>();
	private PedagogicalSoftwareElement next;
	private List<PedagogicalSoftwareElement> nested = new ArrayList<>();
	
	
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
	
	public List<PedagogicalSoftwareElement> getNested(){
		return this.nested;
	}
	public void setNested(List<PedagogicalSoftwareElement> nested) {
		this.nested = nested;
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
		this.next = next;
		
		if(inputs==null){
			this.inputs = new ArrayList<>();
		}else {
			this.inputs = inputs;
		}
	}
	
	/**
	 * Parameterized constructor
	 * @param elementName
	 * @param inputs
	 */
	public PedagogicalSoftwareElement(String elementName, String elementFamily, List<PedagogicalSoftwareInput> inputs, PedagogicalSoftwareElement next, List<PedagogicalSoftwareElement> nested) {
		this.elementName = elementName;
		this.elementFamily = elementFamily;
		this.next = next;
		
		if(inputs==null){
			this.inputs = new ArrayList<>();
		}else {
			this.inputs = inputs;
		}
		
		if(nested==null) {
			this.nested = new ArrayList<>();
		}else {
			this.nested = nested;
		}
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
	    
	    //Checks id all the nested elements are equals
	    result = result && this.nested.size() == objElement.getNested().size();
	    for(PedagogicalSoftwareElement e : this.nested) {
	    	result = result && (objElement.getNested().stream().filter(oe -> oe.equals(e)).count() > 0);
	    }
	    
		return result;
		
	}
	
	/**
	 * Overrides clone
	 */
	public PedagogicalSoftwareElement clone(){
		
		List<PedagogicalSoftwareInput> cloneInputs = this.inputs.stream().map(i -> i.clone()).collect(Collectors.toList());
		List<PedagogicalSoftwareElement> cloneNested = this.nested.stream().map(n -> n.clone()).collect(Collectors.toList());
		PedagogicalSoftwareElement cloneNext = null;
		
		if(this.next != null) {
			cloneNext = this.next.clone();
		}
		
		return new PedagogicalSoftwareElement(this.elementName, this.elementFamily, cloneInputs, cloneNext, cloneNested);
	}
}
