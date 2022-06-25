package artie.pedagogicalintervention.webservice.dto;

import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareBlock;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PedagogicalSoftwareBlockDTO extends PedagogicalSoftwareBlock implements Cloneable{
	
	private int elementPosition = 0;
	
	
	/**
	 * Parameterized constructor
	 * @param element
	 */
	public PedagogicalSoftwareBlockDTO(PedagogicalSoftwareBlock element) {
		super(element.getId(), element.getElementName(), element.getElementFamily(), element.getInputs(), element.getNext(), element.getNested(), element.getPrevious(), element.getParent());
	}
	
	/**
	 * Parameterized constructor
	 * @param element
	 * @param position
	 */
	public PedagogicalSoftwareBlockDTO(PedagogicalSoftwareBlock element, int position) {
		
		super(element.getId(), element.getElementName(), element.getElementFamily(), element.getInputs(), element.getNext(), element.getNested(), element.getPrevious(), element.getParent());
		this.elementPosition = position;
		
	}
	
	
	/**
	 * Overrides clone
	 */
	public PedagogicalSoftwareBlockDTO clone() {
		PedagogicalSoftwareBlock cloneSuper = super.clone();
		return new PedagogicalSoftwareBlockDTO(cloneSuper, this.elementPosition);
	}

}
