package gui.util.enums;

public enum StudentStatusEnum {
	
	ATIVO("#A2D6F9"), AGUARDANDO("#B7E4C7"), INATIVO("#BFBFBF");
	
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
