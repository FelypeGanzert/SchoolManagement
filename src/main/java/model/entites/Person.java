package model.entites;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity(name = "Pessoa")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Person {

	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
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

	@Column (name = "send_email", columnDefinition = "boolean default null")
	private boolean sendEmail;
	
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
	
	@Column (name = "cadastrado_por", columnDefinition = "varchar(50) default null")
	private String registeredBy;
	
	@Column (name = "data_cadastro", columnDefinition = "date default null")
	private Date dateRegistry;
	
	@Column (name = "data_ultima_edicao", columnDefinition = "date default null")
	private Date dateLastRegistryEdit;
	
	@Column (name = "observacao", columnDefinition = "text default null")
	private String observation;
	
	@OneToMany(mappedBy = "person")
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
}
