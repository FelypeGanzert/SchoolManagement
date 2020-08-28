package model.entites;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Entity(name = "Anotacoes")
@Table(name = "anotacoes")
public class Annotations {
	
	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name="aluno_id")
	private Student aluno;
	
	@Column (columnDefinition = "text default null")
	private String descricao;
	
	@Column (columnDefinition = "date default null")
	private Date dataAnotacao;
	
	@Column(name = "funcionario_responsavel", columnDefinition = "varchar(50) default null")
	private String funcionarioResponsavel;

}
