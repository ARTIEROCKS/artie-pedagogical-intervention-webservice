package artie.pedagogicalintervention.webservice.enums;

public enum DistanceEnum {
	
	FAMILY(0),
	ELEMENT(1),
	POSITION(2),
	INPUT(3);
	
	private int value;
	private DistanceEnum(int value) {
        this.value=value;
    }
	public int getValue() {
		return value;
	}
}
