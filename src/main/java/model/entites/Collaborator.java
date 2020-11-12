package model.entites;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity(name = "Colaborador")
@Table(name = "colaborador")
public class Collaborator {
	
	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "nome", columnDefinition = "varchar(50) default null")
	private String name;
	
	@Column(name = "sigla", columnDefinition = "varchar(30) default null")
	private String initials;
	
	@Column (name = "cpf", columnDefinition = "varchar(20) default null")
	private String cpf;
	
	@Column (name = "rg", columnDefinition = "varchar(20) default null")
	private String rg;
	
	@Column (name = "data_nascimento", columnDefinition = "DATETIME default null")
	private Date dateBirth;
	
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
	
	@Column (name = "email", columnDefinition = "varchar(50) default null")
	private String email;
	
	@Column(name = "numero_contato", columnDefinition = "varchar(50) default null")
	private String contactNumber;
	
	@Column(name = "cargo", columnDefinition = "varchar(50) default null")
	private String post;
	
	@Column(name = "usuario_login", columnDefinition = "varchar(30) default null")
	private String userLogin;
	
	@Column(name = "senha_login", columnDefinition = "varchar(30) default null")
	private String passwordLogin;
	
	@Column (name = "excluido", columnDefinition = "varchar(1) default null")
	private String excluded;
	
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