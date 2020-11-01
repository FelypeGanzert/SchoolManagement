package model.entites.dto;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Id;

import gui.util.DateUtil;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class StudentBirthday implements Comparable<StudentBirthday> {

	
	@EqualsAndHashCode.Include
	@Id
	private Integer id;
	
	private String name;
	
	private Date dateBirth;
	
	private String gender;

	@Override
	public int compareTo(StudentBirthday o) {
		Integer day1 = 0;
		if(dateBirth != null) {
			day1 = DateUtil.dateToCalendar(dateBirth).get(Calendar.DAY_OF_MONTH);
		}
		Integer day2 = 0;
		if(o.getDateBirth() != null) {
			day2 = DateUtil.dateToCalendar(o.getDateBirth()).get(Calendar.DAY_OF_MONTH);
		}
		int resultDayOfBirth = day1.compareTo(day2);
		if (resultDayOfBirth == 0) {
			int resultGender = gender.toUpperCase().compareTo(o.gender.toUpperCase());
			if(resultGender == 0) {
				return name.toUpperCase().compareTo(o.getName().toUpperCase());
			}else {
				return resultGender;
			}
		} else {
			return resultDayOfBirth;
		}
	}
	
}
