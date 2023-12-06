package artie.pedagogicalintervention.webservice.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class PedagogicalSoftwareInput implements Cloneable {

	private String name;
	private String opcode;
	private List<PedagogicalSoftwareField> fields = new ArrayList<>();

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

	    if(!this.name.equalsIgnoreCase(objInput.getName())) return false;
	    if(!this.opcode.equalsIgnoreCase(objInput.getOpcode())) return false;
	    
	    boolean result = this.fields.size()== objInput.getFields().size();
	    for(PedagogicalSoftwareField field : this.fields) {
	    	result = result && (objInput.getFields().stream().anyMatch(f -> f.equals(field)));
	    }
		
		return result;
	}
	
	
	/**
	 * Overrides clone
	 */
	public PedagogicalSoftwareInput clone() {
		
		List<PedagogicalSoftwareField> cloneFields = this.fields.stream().map(PedagogicalSoftwareField::clone).collect(Collectors.toList());
		return new PedagogicalSoftwareInput(name, opcode, cloneFields);
	}

	/**
	 * Overrides clone
	 * @return
	 */
	@Override
	public String toString(){
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("[").append(name).append("-").append(opcode);
		stringBuilder.append("(");
		for (PedagogicalSoftwareField field : fields) {
			stringBuilder.append(field.toString());
		}
		stringBuilder.append(")").append("]");
		return stringBuilder.toString();
	}
}
