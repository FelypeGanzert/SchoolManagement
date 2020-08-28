package model.entites;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity(name = "Matricula")
@Table(name = "matricula")
public class Registry {
	
	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer codigo;
	
	@ManyToOne
	@JoinColumn(name="aluno_id")
	private Student aluno;
	
	@ManyToOne
	@JoinColumn(name="responsavel_financeiro_id")
	private Responsible responsavelFinaceiro;
	
	@Column(name="data_matricula", columnDefinition = "date default null")
	private Date dataMatricula;

	@Column(name="matriculado_por", columnDefinition = "varchar(50) default null")
	private String matriculadoPor;
	
	@Column(name="motivo", columnDefinition = "varchar(50) default null")
	private String motivo;
	
	@OneToMany(mappedBy = "matricula")
	private List<Parcel> parcelas;


}
