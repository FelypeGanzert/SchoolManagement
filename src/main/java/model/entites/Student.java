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

import org.hibernate.annotations.Where;

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
@Entity(name = "Aluno")
@Table(name = "aluno")
public class Student extends Person{
	
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
	
	@Column (name = "data_nascimento", columnDefinition = "DATETIME default null")
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
	
	@Column (name = "data_cadastro", columnDefinition = "DATETIME default null")
	private Date dateRegistry;
	
	@Column (name = "data_ultima_edicao", columnDefinition = "DATETIME default null")
	private Date dateLastRegistryEdit;
	
	@Column (name = "observacao", columnDefinition = "text default null")
	private String observation;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="contact_id_student")
	@Where(clause = "excluido is null")
	private List<Contact> contacts = new ArrayList<>();

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

	@Column(name = "ra_antigo", columnDefinition = "int default null")
	private String oldRA; // old Registro Aluno
	
	@Column(name = "situacao", columnDefinition = "varchar(50) default null")
	private String status;

	@OneToMany(mappedBy = "student",
	        cascade = CascadeType.ALL,
	        orphanRemoval = true
	    )
	@Where(clause = "excluido is null")
	private List<ResponsibleStudent> responsibles = new ArrayList<>();

	@OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
	@Where(clause = "excluido is null")
	private List<Matriculation> matriculations = new ArrayList<>();
	
	@OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
	@Where(clause = "excluido is null")
	private List<Annotation> annotations = new ArrayList<>();
	
	@OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
	@Where(clause = "excluido is null")
	private List<Course> courses = new ArrayList<>();

	public void addResponsavel(ResponsibleStudent responsibleStudent) {
		this.responsibles.add(responsibleStudent);
		responsibleStudent.getResponsible().getStudents().add(responsibleStudent);
	}

	public void removeResponsavel(Responsible responsible) {
		for (Iterator<ResponsibleStudent> iterator = responsibles.iterator(); iterator.hasNext();) {
			ResponsibleStudent responsibleStudent = iterator.next();
			if (responsibleStudent.getStudent().equals(this) && responsibleStudent.getResponsible().equals(responsible)) {
				iterator.remove();
				responsibleStudent.getStudent().getResponsibles().remove(responsibleStudent);
			}
		}
	}
	
	public List<Responsible> getAllResponsibles() {
		return responsibles.stream().map(responsibleStudent -> responsibleStudent.getResponsible()).collect(Collectors.toList());
	}
	
	// Method for field to Fillter the search
	@Override
	public String getValue(String field) {
		if (field.equalsIgnoreCase("Nome")) {
			return name;
		}
		if (field.equalsIgnoreCase("CPF")) {
			return cpf;
		}
		if (field.equalsIgnoreCase("RG")) {
			return rg;
		}
		if (field.equalsIgnoreCase("ID")) {
			String idToString = null;
			try {
				idToString = Integer.toString(id);
			} catch (Exception e) {
			}
			return idToString;
		}
		return null;
	}
	
	@Column (name = "excluido", columnDefinition = "varchar(1) default null")
	private String excluded;

}
