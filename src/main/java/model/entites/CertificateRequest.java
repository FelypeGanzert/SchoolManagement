package model.entites;

import java.util.Date;

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
@Entity(name = "certificado_pedido")
@Table(name = "certificado_pedido")
public class CertificateRequest {
	
	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column (name = "aluno_codigo", columnDefinition = "int default null")
	private Integer studentCode;
	
	@Column (name = "aluno_nome", columnDefinition = "varchar(50) default null")
	private String studentName;
	
	@Column (name = "data_inicio", columnDefinition = "DATETIME default null")
	private Date startDate;
	
	@Column (name = "data_termino", columnDefinition = "DATETIME default null")
	private Date endDate;
	
	@Column (name = "carga_horaria", columnDefinition = "int default null")
	private Integer courseLoad;
	
	@Column (name = "data_pedido", columnDefinition = "DATETIME default current_timestamp")
	private Date requestDate;
	
	@Column (name = "excluido", columnDefinition = "varchar(1) default null")
	private String excluded;
	
}