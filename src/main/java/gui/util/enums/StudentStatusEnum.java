package gui.util.enums;

public enum StudentStatusEnum {
	
	ATIVO("#1167B1"), AGUARDANDO("#F05E23"), INATIVO("#1C282E");
	
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
