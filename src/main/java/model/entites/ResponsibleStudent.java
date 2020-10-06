package model.entites;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

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
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne
	@MapsId("id")
	@JoinColumn(name = "aluno_id")
	private Student student;
	
	@ManyToOne
	@MapsId("id")
	@JoinColumn(name = "responsavel_id")
	private Responsible responsible;
	
	@Column (name = "parentesco", columnDefinition = "varchar(30) default null")
	private String relationship;
	
    public ResponsibleStudent(Student student, Responsible responsible, String relationship) {
        this.student = student;
        this.responsible= responsible;
        this.relationship = relationship;
    }
}