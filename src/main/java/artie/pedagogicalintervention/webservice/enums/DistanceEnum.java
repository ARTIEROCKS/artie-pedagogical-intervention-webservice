package artie.pedagogicalintervention.webservice.enums;

public enum DistanceEnum {
	
	FAMILY(0),
	ELEMENT(1),
	INPUT(2),
	SORT(3);
	
	private int value;
	private DistanceEnum(int value) {
        this.value=value;
    }
	public int getValue() {
		return value;
	}
}
