package model.entites;

import java.util.Date;
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
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Pessoa {

	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column (columnDefinition = "varchar(50) default null")
	private String nome;
	
	@Column (columnDefinition = "varchar(50) default null")
	private String cpf;
	
	@Column (columnDefinition = "varchar(50) default null")
	private String rg;
	
	@Column (name = "data_nascimento", columnDefinition = "date default null")
	private Date dataNascimento;
	
	@Column (columnDefinition = "varchar(50) default null")
	private String email;
	
	@Column (columnDefinition = "varchar(20) default null")
	private String sexo;
	
	@Column (columnDefinition = "varchar(50) default null")
	private String endereco;
	
	@Column (columnDefinition = "varchar(50) default null")
	private String bairro;
	
	@Column (columnDefinition = "varchar(50) default null")
	private String cidade;
	
	@Column ( columnDefinition = "varchar(20) default null")
	private String uf;
	
	@Column (name = "cadastrado_por", columnDefinition = "varchar(50) default null")
	private String cadastradoPor;
	
	@Column (name = "data_cadastro", columnDefinition = "date default null")
	private Date dataCadastro;
	
	@OneToMany(mappedBy = "pessoa")
	private List<Contato> contatos;

	public Pessoa(Integer id, String nome) {
		this.id = id;
		this.nome = nome;
	}

}
