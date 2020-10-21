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
@Entity(name = "Contato")
@Table(name = "contato")
public class Contact {
	
	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
		
	@Column (name = "numero", columnDefinition = "varchar(20) default null")
	private String number;
	
	@Column (name = "descricao", columnDefinition = "varchar(30) default null")
	private String description;
	
	@Column (name = "excluido", columnDefinition = "varchar(1) default null")
	private String excluded;

}
