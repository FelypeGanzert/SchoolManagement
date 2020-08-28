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

	@Column(columnDefinition = "varchar(50) default null")
	private String situacao;

	@OneToMany(mappedBy = "aluno",
		cascade = CascadeType.ALL,
		orphanRemoval = true)
	private List<ResponsibleStudent> responsaveis = new ArrayList<>();

	@OneToMany(mappedBy = "aluno")
	private List<Registry> matriculas;
	
	@OneToMany(mappedBy = "aluno")
	private List<Annotations> anotacoes;

	public void addResponsavel(Responsible responsavel, String parentesco) {
		ResponsibleStudent responsavelAluno = new ResponsibleStudent(this, responsavel, parentesco);
		this.responsaveis.add(responsavelAluno);
		responsavel.getAlunos().add(responsavelAluno);
	}

	public void removeResponsavel(Responsible responsavel) {
		for (Iterator<ResponsibleStudent> iterator = responsaveis.iterator(); iterator.hasNext();) {
			ResponsibleStudent responsavelAluno = iterator.next();
			if (responsavelAluno.getAluno().equals(this) && responsavelAluno.getResponsavel().equals(responsavel)) {
				iterator.remove();
				responsavelAluno.getAluno().getResponsaveis().remove(responsavelAluno);
				responsavelAluno.setAluno(null);
				responsavelAluno.setResponsavel(null);
			}
		}
	}

}
