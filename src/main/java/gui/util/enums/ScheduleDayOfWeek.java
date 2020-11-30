package gui.util.enums;

public enum ScheduleDayOfWeek {

	SUNDAY(1), MONDAY(2), TUESDAY(3), WEDNESDAY(4), THURSDAY(5), FRIDAY(6), SATURDAY(7);

	private int value;

	ScheduleDayOfWeek(int value){
			this.value = value;
		}

	public int getValue() {
		return this.value;
	}

	public static ScheduleDayOfWeek fromInteger(Integer value) {
		for (ScheduleDayOfWeek d : ScheduleDayOfWeek.values()) {
			if (d.value == value) {
				return d;
			}
		}
		return null;
	}

}