package artie.pedagogicalintervention.webservice.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="PedagogicalSoftwareData")
public class PedagogicalSoftwareData {

	//Attributes
	@Id
	private String id;
	private String elementType;
	private List<PedagogicalSoftwareField> fields = new ArrayList<>();
	
	
	//Properties
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getElementType() {
		return elementType;
	}
	public void setElementType(String elementType) {
		this.elementType = elementType;
	}
	
	public List<PedagogicalSoftwareField> getFields(){
		return fields;
	}
	public void setFields(List<PedagogicalSoftwareField> fields) {
		this.fields = fields;
	}
	
	//Default constructor
	public PedagogicalSoftwareData() {}
	
	/**
	 * Parameterized constructor
	 * @param elementType
	 * @param fields
	 */
	public PedagogicalSoftwareData(String elementType, List<PedagogicalSoftwareField> fields) {
		this.elementType = elementType;
		this.fields = fields;
	}
}
