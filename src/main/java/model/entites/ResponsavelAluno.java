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
public class ResponsavelAluno {

	@EqualsAndHashCode.Include
	@EmbeddedId
	private ResponsavelAlunoId id;
	
	@ManyToOne
	@MapsId("aluno_id")
	private Aluno aluno;
	
	@ManyToOne
	@MapsId("responsavel_id")
	private Responsavel responsavel;
	
	@Column (columnDefinition = "varchar(50) default null")
	private String parentesco;
	
    public ResponsavelAluno(Aluno aluno, Responsavel responsavel, String parentesco) {
        this.aluno = aluno;
        this.responsavel= responsavel;
        this.parentesco = parentesco;
        this.id = new ResponsavelAlunoId(aluno.getId(), responsavel.getId());
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
		private Integer AlunoId;
		
		@EqualsAndHashCode.Include
		@Column(name = "responsavel_id")
		private Integer ResponsavelId;
	}
	
}