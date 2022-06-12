package artie.pedagogicalintervention.webservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PedagogicalSoftwareField implements Cloneable {
	
	//Attributes
	private String name;
	private String value;
	
	/**
	 * Overrides equals
	 */
	public boolean equals(Object obj) {
		
		if (this == obj) return true;
	    if (obj == null) return false;
	    if (this.getClass() != obj.getClass()) return false;
		PedagogicalSoftwareField objField = (PedagogicalSoftwareField) obj;
		
		return (this.name.toLowerCase().equals(objField.getName().toLowerCase()) && this.value.toLowerCase().equals(objField.getValue().toLowerCase()));
	}
	
	/**
	 * Overrides clone
	 */
	public PedagogicalSoftwareField clone() {
		return new PedagogicalSoftwareField(name, value);
	}
	
	
	/**
	 * Checks if the value is numeric or not
	 * @return
	 */
	public boolean isNumeric() {
		return StringUtils.isNumeric(this.value);
	}
	
	/**
	 * Function to return the double value in case of the value is numeric
	 * @return
	 */
	public double getDoubleValue() {
		
		if(this.isNumeric()) {
			return Double.valueOf(this.value);
		}else {
			return 0;
		}
	}
}
