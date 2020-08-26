package model.entites;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Responsavel extends Pessoa {
	
    @OneToMany(
        mappedBy = "responsavel",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
	private List<ResponsavelAluno> alunos = new ArrayList<>();;

	@OneToMany(mappedBy = "responsavelFinaceiro")
	private List<Matricula> responsavelPelaMatriculas;
}
