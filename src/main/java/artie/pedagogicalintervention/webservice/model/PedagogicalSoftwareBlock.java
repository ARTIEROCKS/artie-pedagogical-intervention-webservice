package artie.pedagogicalintervention.webservice.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class PedagogicalSoftwareBlock implements Cloneable {

	//Attributes
	private String id;
	private String elementName;
	private String elementFamily;
	private List<PedagogicalSoftwareInput> inputs = new ArrayList<>();
	private PedagogicalSoftwareBlock next;
	private List<PedagogicalSoftwareBlock> nested = new ArrayList<>();
	private PedagogicalSoftwareBlock previous;
	private PedagogicalSoftwareBlock parent;


	/**
	 * Parameterized constructor
	 * @param id
	 * @param elementName
	 * @param elementFamily
	 * @param inputs
	 * @param next
	 * @param nested
	 */
	public PedagogicalSoftwareBlock(String id, String elementName, String elementFamily, List<PedagogicalSoftwareInput> inputs,
									PedagogicalSoftwareBlock next, List<PedagogicalSoftwareBlock> nested,
									PedagogicalSoftwareBlock previous, PedagogicalSoftwareBlock parent) {
		this.id = id;
		this.elementName = elementName;
		this.elementFamily = elementFamily;
		this.next = next;
		this.previous = previous;
		this.parent = parent;
		
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
	    PedagogicalSoftwareBlock objBlock = (PedagogicalSoftwareBlock) obj;
	    
	    //Checks if the name and the family are equals
	    if (!this.elementName.equals(objBlock.getElementName())) return false;
	    if (!this.elementFamily.equals(objBlock.getElementFamily())) return false;
	    
	    //Checks if all the inputs are equals
	    boolean result = this.inputs.size() == objBlock.getInputs().size();
	    for(PedagogicalSoftwareInput i : this.inputs) {
	    	result = result && (objBlock.getInputs().stream().filter(oi -> oi.equals(i)).count() > 0);
	    }
	    
	    //Checks if all the nested elements are equals
	    result = result && this.nested.size() == objBlock.getNested().size();
	    for(PedagogicalSoftwareBlock e : this.nested) {
	    	result = result && (objBlock.getNested().stream().filter(oe -> oe.equals(e)).count() > 0);
	    }
	    
		return result;
		
	}
	
	/**
	 * Overrides clone
	 */
	public PedagogicalSoftwareBlock clone(){
		
		List<PedagogicalSoftwareInput> cloneInputs = (this.inputs != null ? this.inputs.stream().map(i -> i.clone()).collect(Collectors.toList()) : null);
		List<PedagogicalSoftwareBlock> cloneNested = (this.nested != null ? this.nested.stream().map(n -> n.clone()).collect(Collectors.toList()) : null);
		PedagogicalSoftwareBlock cloneNext = null;
		PedagogicalSoftwareBlock clonePrevious = null;
		PedagogicalSoftwareBlock cloneParent = null;

		if(this.next != null) {
			cloneNext = this.next.clone();
		}
		if(this.previous != null){
			clonePrevious = this.previous.clone();
		}
		if(this.parent != null){
			cloneParent = this.parent.clone();
		}
		
		return new PedagogicalSoftwareBlock(this.id, this.elementName, this.elementFamily, cloneInputs, cloneNext, cloneNested, clonePrevious, cloneParent);
	}
}
