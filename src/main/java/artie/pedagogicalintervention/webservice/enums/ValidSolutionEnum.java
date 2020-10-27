package artie.pedagogicalintervention.webservice.enums;

public enum ValidSolutionEnum {

    REJECTED(-1),
    WAITING_APPROVAL(0),
    VALIDATED(1);

    private int value;
    private ValidSolutionEnum(int value) {
        this.value=value;
    }
    public int getValue() {
        return value;
    }
}
