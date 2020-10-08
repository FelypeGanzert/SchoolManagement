package model.entites;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity(name = "Cursos")
@Table(name = "courses")
public class Course {
	
	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name="aluno_id")
	private Student student;
	
	@Column (name = "nome", columnDefinition = "varchar(50) default null")
	private String name;
	
	@Column (name = "data_inicio", columnDefinition = "DATETIME default null")
	private Date startDate;
	
	@Column (name = "data_fim", columnDefinition = "DATETIME default null")
	private Date endDate;
	
	@Column (name = "professor", columnDefinition = "varchar(50) default null")
	private String professor;
	
	@Column (name = "carga_horaria", columnDefinition = "int default null")
	private Integer courseLoad;
		

}
