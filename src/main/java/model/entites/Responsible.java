package model.entites;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
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
@Entity(name = "Responsavel")
@Table(name = "responsavel")
public class Responsible extends Person {
	
    @OneToMany(
        mappedBy = "responsavel",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
	private List<ResponsibleStudent> alunos = new ArrayList<>();;

	@OneToMany(mappedBy = "responsavelFinaceiro")
	private List<Registry> responsavelPelaMatriculas;
}
