package artie.pedagogicalintervention.webservice.dto;

import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareElement;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareInput;

public class PedagogicalSoftwareElementDTO extends PedagogicalSoftwareElement implements Cloneable{
	
	private int elementPosition = 0;

	public int getElementPosition() {
		return elementPosition;
	}

	public void setElementPosition(int elementPosition) {
		this.elementPosition = elementPosition;
	}
	
	
	/**
	 * Default constructor
	 */
	public PedagogicalSoftwareElementDTO() {}
	
	/**
	 * Parameterized constructor
	 * @param element
	 */
	public PedagogicalSoftwareElementDTO(PedagogicalSoftwareElement element) {
		super(element.getId(), element.getElementName(), element.getElementFamily(), element.getInputs(), element.getNext(), element.getNested(), element.getPrevious(), element.getParent());
	}
	
	/**
	 * Parameterized constructor
	 * @param element
	 * @param position
	 */
	public PedagogicalSoftwareElementDTO(PedagogicalSoftwareElement element, int position) {
		
		super(element.getId(), element.getElementName(), element.getElementFamily(), element.getInputs(), element.getNext(), element.getNested(), element.getPrevious(), element.getParent());
		this.elementPosition = position;
		
	}
	
	
	/**
	 * Overrides clone
	 */
	public PedagogicalSoftwareElementDTO clone() {
		
		PedagogicalSoftwareElement cloneSuper = super.clone();
		return new PedagogicalSoftwareElementDTO(cloneSuper, this.elementPosition);
	}

}
