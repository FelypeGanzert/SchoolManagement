package gui.util.enums;

public enum StudentStatusEnum {
	
	ATIVO("#0984E2"), AGUARDANDO("#FDCA6E"), INATIVO("#333333");
	
	private String hexColor;
	
	StudentStatusEnum(String hexColor){
		this.hexColor = hexColor;
	}
	public String getHexColor() {
		return this.hexColor;
	}
	
    public static StudentStatusEnum fromString(String status) {
        for (StudentStatusEnum s : StudentStatusEnum.values()) {
            if (s.toString().equalsIgnoreCase(status)) {
                return s;
            }
        }
        return null;
    }

}
