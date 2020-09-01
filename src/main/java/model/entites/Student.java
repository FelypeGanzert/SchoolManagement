package model.entites;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Aluno")
@Table(name = "aluno")
public class Student extends Person {

	@Column(name = "situacao", columnDefinition = "varchar(50) default null")
	private String status;

	@OneToMany(mappedBy = "student",
		cascade = CascadeType.ALL,
		orphanRemoval = true)
	private List<ResponsibleStudent> responsibles = new ArrayList<>();

	@OneToMany(mappedBy = "student")
	private List<Matriculation> matriculations;
	
	@OneToMany(mappedBy = "student")
	private List<Annotations> annotations;

	public void addResponsavel(Responsible responsible, String relationship) {
		ResponsibleStudent responsibleStudent = new ResponsibleStudent(this, responsible, relationship);
		this.responsibles.add(responsibleStudent);
		responsible.getStudents().add(responsibleStudent);
	}

	public void removeResponsavel(Responsible responsible) {
		for (Iterator<ResponsibleStudent> iterator = responsibles.iterator(); iterator.hasNext();) {
			ResponsibleStudent responsibleStudent = iterator.next();
			if (responsibleStudent.getStudent().equals(this) && responsibleStudent.getResponsible().equals(responsible)) {
				iterator.remove();
				responsibleStudent.getStudent().getResponsibles().remove(responsibleStudent);
				responsibleStudent.setStudent(null);
				responsibleStudent.setResponsible(null);
			}
		}
	}

}
