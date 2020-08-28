package artie.pedagogicalintervention.webservice.enums;

public enum DistanceEnum {
	
	FAMILY(1),
	ELEMENT(2),
	POSITION(4),
	INPUT(8);
	
	private int value;
	private DistanceEnum(int value) {
        this.value=value;
    }
	public int getValue() {
		return value;
	}
}
