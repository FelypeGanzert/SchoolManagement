package model.entites;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity(name = "ResponsavelAluno")
@Table(name = "responsavel_aluno")
public class ResponsibleStudent {

	@EqualsAndHashCode.Include
	@EmbeddedId
	private ResponsavelAlunoId id;
	
	@ManyToOne
	@MapsId("aluno_id")
	private Student student;
	
	@ManyToOne
	@MapsId("responsavel_id")
	private Responsible responsible;
	
	@Column (columnDefinition = "varchar(50) default null")
	private String relationship;
	
    public ResponsibleStudent(Student student, Responsible responsible, String relationship) {
        this.student = student;
        this.responsible= responsible;
        this.relationship = relationship;
        this.id = new ResponsavelAlunoId(student.getId(), responsible.getId());
    }

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@EqualsAndHashCode(onlyExplicitlyIncluded = true)
	@Embeddable
	public static class ResponsavelAlunoId implements Serializable{
		private static final long serialVersionUID = 1L;
		
		@EqualsAndHashCode.Include
		@Column(name = "aluno_id")
		private Integer StudentId;
		
		@EqualsAndHashCode.Include
		@Column(name = "responsavel_id")
		private Integer ResponsibleId;
	}
	
}