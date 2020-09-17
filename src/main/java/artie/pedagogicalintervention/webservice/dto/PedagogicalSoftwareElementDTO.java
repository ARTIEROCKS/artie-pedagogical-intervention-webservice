package artie.pedagogicalintervention.webservice.dto;

import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareElement;

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
		
		super(element.getElementName(), element.getElementFamily(), element.getInputs(), null);
		
	}
	
	/**
	 * Parameterized constructor
	 * @param element
	 * @param position
	 */
	public PedagogicalSoftwareElementDTO(PedagogicalSoftwareElement element,int position) {
		
		super(element.getElementName(), element.getElementFamily(), element.getInputs(), null);
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
