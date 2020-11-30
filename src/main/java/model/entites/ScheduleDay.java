package model.entites;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import gui.util.enums.ScheduleDayOfWeek;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Entity(name = "Dia")
@Table(name = "dia")
public class ScheduleDay {
	
	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "dia_semana", columnDefinition = "int default null")
	private Integer scheduleDayOfWeek;
	@Column (name = "manha", columnDefinition = "text default null")
	private String morning;
	@Column (name = "tarde", columnDefinition = "text default null")
	private String afternoon;
	@Column (name = "noite", columnDefinition = "text default null")
	private String night;
	
	public ScheduleDay() {
	}
	
	public ScheduleDay(ScheduleDayOfWeek dayOfWeek, String morning, String afternoon, String night) {
		this.scheduleDayOfWeek = dayOfWeek.getValue();
		this.morning = morning;
		this.afternoon = afternoon;
		this.night = night;
	}
	
	public ScheduleDayOfWeek getScheduleDayOfWeek() {
		return ScheduleDayOfWeek.fromInteger(scheduleDayOfWeek);
	}
	
	public void setScheduleDayOfWeek(ScheduleDayOfWeek dayOfWeek) {
		this.scheduleDayOfWeek = dayOfWeek.getValue();
	}


}
