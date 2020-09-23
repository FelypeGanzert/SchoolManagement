package model.entites;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
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
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Entity(name = "Responsavel")
@Table(name = "responsavel")
public class Responsible extends Person {
	
	// Start of comumn atributes to a Person
	
	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column (name = "nome", columnDefinition = "varchar(50) default null")
	private String name;
	
	@Column (name = "cpf", columnDefinition = "varchar(50) default null")
	private String cpf;
	
	@Column (name = "rg", columnDefinition = "varchar(50) default null")
	private String rg;
	
	@Column (name = "data_nascimento", columnDefinition = "date default null")
	private Date dateBirth;
	
	@Column (name = "email", columnDefinition = "varchar(50) default null")
	private String email;

	@Column (name = "send_email", columnDefinition = "boolean default false")
	private Boolean sendEmail;
	
	@Column (name = "sexo", columnDefinition = "varchar(20) default null")
	private String gender;
	
	@Column (name = "estado_civil", columnDefinition = "varchar(20) default null")
	private String civilStatus;
	
	@Column (name = "endereco", columnDefinition = "varchar(50) default null")
	private String adress;
	
	@Column (name = "bairro", columnDefinition = "varchar(50) default null")
	private String neighborhood;
	
	@Column (name = "cidade", columnDefinition = "varchar(50) default null")
	private String city;
	
	@Column (name = "uf", columnDefinition = "varchar(20) default null")
	private String uf;
	
	@Column (name = "local_referencia", columnDefinition = "varchar(50) default null")
	private String adressReference;
	
	@Column (name = "cadastrado_por", columnDefinition = "varchar(50) default null")
	private String registeredBy;
	
	@Column (name = "data_cadastro", columnDefinition = "date default null")
	private Date dateRegistry;
	
	@Column (name = "data_ultima_edicao", columnDefinition = "date default null")
	private Date dateLastRegistryEdit;
	
	@Column (name = "observacao", columnDefinition = "text default null")
	private String observation;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="contact_id_responsible")
	private List<Contact> contacts;

	public int getAge(){
		if (dateBirth == null) {
			return 0;
		}
		Calendar dateOfBirth = new GregorianCalendar();
		dateOfBirth.setTime(this.dateBirth);
		// Hour now
		Calendar today = Calendar.getInstance();
		int age = today.get(Calendar.YEAR) - dateOfBirth.get(Calendar.YEAR);
		dateOfBirth.add(Calendar.YEAR, age);
		if (today.before(dateOfBirth)) {
			age--;
		}
		return age;
	}
	
	// End of comumn atributes to a Person
	
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
	
	public void removeStudent(Student student) {
		for (Iterator<ResponsibleStudent> iterator = students.iterator(); iterator.hasNext();) {
			ResponsibleStudent responsibleStudent = iterator.next();
			if (responsibleStudent.getResponsible().equals(this) && responsibleStudent.getStudent().equals(student)) {
				iterator.remove();
				responsibleStudent.getResponsible().getStudents().remove(responsibleStudent);
				responsibleStudent.setResponsible(null);
				responsibleStudent.setStudent(null);
			}
		}
	}
	
	public List<Student> getAllStudents() {
		return students.stream().map(responsibleStudent -> responsibleStudent.getStudent()).collect(Collectors.toList());
	}
}
