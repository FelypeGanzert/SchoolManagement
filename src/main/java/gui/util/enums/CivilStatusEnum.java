package gui.util.enums;

public enum CivilStatusEnum {

	SOLTEIRO("Solteiro"), CASADO("Casado"), DIVORCIADO("Divorciado"), VIUVO("Viúvo"), SEPARADO("Separado");

	private String fullCivilStatus;

	CivilStatusEnum(String fullCivilStatus){
		this.fullCivilStatus = fullCivilStatus;
	}

	public String getFullCivilStatus() {
		return this.fullCivilStatus;
	}


	public static CivilStatusEnum fromString(String civilStatus) {
		for (CivilStatusEnum cs : CivilStatusEnum.values()) {
			if (cs.toString().equalsIgnoreCase(civilStatus)) {
				return cs;
			}
		}
		return null;
	}
	
	public static CivilStatusEnum fromFullCivilStatus(String civilStatus) {
		for (CivilStatusEnum cs : CivilStatusEnum.values()) {
			if (cs.getFullCivilStatus().equalsIgnoreCase(civilStatus)) {
				return cs;
			}
		}
		return null;
	}
	
}
