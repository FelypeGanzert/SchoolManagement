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
        mappedBy = "responsible",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
	private List<ResponsibleStudent> students = new ArrayList<>();;

	@OneToMany(mappedBy = "responsible")
	private List<Matriculation> matriculationsThatIsResponsible;
	
	public String getRelationship(Student student) {
		for(ResponsibleStudent rs : students) {
			if(rs.getStudent().equals(student) || rs.getResponsible().equals(this)) {
				return rs.getRelationship();
			}
		}
		return null;
	}
}
