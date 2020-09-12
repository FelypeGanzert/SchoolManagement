package gui.util.enums;

public enum GenderEnum {

	M("Masculino"), F("Feminino");

	private String fullGender;

	GenderEnum(String fullGender){
		this.fullGender = fullGender;
	}

	public String getfullGender() {
		return this.fullGender;
	}


	public static GenderEnum fromString(String gender) {
		for (GenderEnum g : GenderEnum.values()) {
			if (g.toString().equalsIgnoreCase(gender)) {
				return g;
			}
		}
		return null;
	}

}
