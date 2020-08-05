package artie.pedagogicalintervention.webservice.dto;

import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareElement;

public class PedagogicalSoftwareElementDTO extends PedagogicalSoftwareElement{
	
	private int elementOrder = 0;

	public int getElementOrder() {
		return elementOrder;
	}

	public void setElementOrder(int elementOrder) {
		this.elementOrder = elementOrder;
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
	 * @param order
	 */
	public PedagogicalSoftwareElementDTO(PedagogicalSoftwareElement element,int order) {
		
		super(element.getElementName(), element.getElementFamily(), element.getInputs(), null);
		this.elementOrder = order;
		
	}

}
