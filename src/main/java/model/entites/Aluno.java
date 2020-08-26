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
public class Aluno extends Pessoa {

	@Column(columnDefinition = "varchar(50) default null")
	private String situacao;

	@OneToMany(mappedBy = "aluno",
		cascade = CascadeType.ALL,
		orphanRemoval = true)
	private List<ResponsavelAluno> responsaveis = new ArrayList<>();

	@OneToMany(mappedBy = "aluno")
	private List<Matricula> matriculas;
	
	@OneToMany(mappedBy = "aluno")
	private List<Anotacoes> anotacoes;

	public void addResponsavel(Responsavel responsavel, String parentesco) {
		ResponsavelAluno responsavelAluno = new ResponsavelAluno(this, responsavel, parentesco);
		this.responsaveis.add(responsavelAluno);
		responsavel.getAlunos().add(responsavelAluno);
	}

	public void removeResponsavel(Responsavel responsavel) {
		for (Iterator<ResponsavelAluno> iterator = responsaveis.iterator(); iterator.hasNext();) {
			ResponsavelAluno responsavelAluno = iterator.next();
			if (responsavelAluno.getAluno().equals(this) && responsavelAluno.getResponsavel().equals(responsavel)) {
				iterator.remove();
				responsavelAluno.getAluno().getResponsaveis().remove(responsavelAluno);
				responsavelAluno.setAluno(null);
				responsavelAluno.setResponsavel(null);
			}
		}
	}

}
