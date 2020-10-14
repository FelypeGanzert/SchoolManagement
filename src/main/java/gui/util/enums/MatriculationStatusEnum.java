package gui.util.enums;

public enum MatriculationStatusEnum {
	
	ATIVA("#0984E2"), CANCELADA("#f38375"), CONCLUIDA("#5dd39e");
	
	private String hexColor;
	
	MatriculationStatusEnum(String hexColor){
		this.hexColor = hexColor;
	}
	public String getHexColor() {
		return this.hexColor;
	}
	
    public static MatriculationStatusEnum fromString(String status) {
        for (MatriculationStatusEnum s : MatriculationStatusEnum.values()) {
            if (s.toString().equalsIgnoreCase(status)) {
                return s;
            }
        }
        return null;
    }

}
