package artie.pedagogicalintervention.webservice.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PedagogicalSoftwareInput implements Cloneable {

	private String name;
	private String opcode;
	private List<PedagogicalSoftwareField> fields = new ArrayList<>();
	
	
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOpCode() { return this.opcode; }
	public void setOpcode(String opcode) { this.opcode = opcode; }
	public List<PedagogicalSoftwareField> getFields() {
		return fields;
	}
	public void setFields(List<PedagogicalSoftwareField> fields) {
		this.fields = fields;
	}
	
	
	/**
	 * Default constructor
	 */
	public PedagogicalSoftwareInput() {}
	
	/**
	 * Parameterized constructor
	 * @param name
	 * @param opcode
	 * @param fields
	 */
	public PedagogicalSoftwareInput(String name, String opcode, List<PedagogicalSoftwareField> fields) {
		super();
		this.name = name;
		this.opcode = opcode;
		
		if(fields != null) {
			this.fields = fields;
		}
	}

	/**
	 * Overrides equals
	 */
	public boolean equals(Object obj) {
		
		if (this == obj) return true;
	    if (obj == null) return false;
	    if (this.getClass() != obj.getClass()) return false;
	    PedagogicalSoftwareInput objInput = (PedagogicalSoftwareInput) obj;

	    if(!this.name.toLowerCase().equals(objInput.getName().toLowerCase())) return false;
	    if(!this.opcode.toLowerCase().equals(objInput.getOpCode().toLowerCase())) return false;
	    
	    boolean result = this.fields.size()== objInput.getFields().size();
	    for(PedagogicalSoftwareField field : this.fields) {
	    	result = result && (objInput.getFields().stream().filter(f -> f.equals(field)).count() > 0);
	    }
		
		return result;
	}
	
	
	/**
	 * Overrides clone
	 */
	public PedagogicalSoftwareInput clone() {
		
		List<PedagogicalSoftwareField> cloneFields = this.fields.stream().map(f -> f.clone()).collect(Collectors.toList());
		return new PedagogicalSoftwareInput(name, opcode, cloneFields);
	}
}
