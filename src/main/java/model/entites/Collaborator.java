package model.entites;

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
	
	@Column(name = "sigla", columnDefinition = "varchar(50) default null")
	private String initials;
	
	@Column(name = "numero_contato", columnDefinition = "varchar(50) default null")
	private String contactNumber;
	
	@Column(name = "email", columnDefinition = "varchar(50) default null")
	private String email;
	
	@Column(name = "cargo", columnDefinition = "varchar(50) default null")
	private String post;
	
	@Column(name = "usuario_login", columnDefinition = "varchar(50) default null")
	private String userLogin;
	
	@Column(name = "senha_login", columnDefinition = "varchar(50) default null")
	private String passwordLogin;

}
