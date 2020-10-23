package gui.util;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {

	public static Calendar dateToCalendar(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}

	public static Date calendarToDate(Calendar calendar) {
		return calendar.getTime();
	}
	
	public static int compareTwoDates(Date startDate, Date endDate) {
		Date sDate = getZeroTimeDate(startDate);
		Date eDate = getZeroTimeDate(endDate);

		if (sDate.before(eDate)) {
			return -1;
		}

		if (sDate.after(eDate)) {
			return 1;
		}

		return 0;
	}

	private static Date getZeroTimeDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		date = calendar.getTime();
		return date;
	}
}