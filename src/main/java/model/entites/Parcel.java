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
@Entity(name = "Parcela")
@Table(name = "parcela")
public class Parcel {

	@EqualsAndHashCode.Include
	@Id
	@Column(name = "numero_documento")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer numeroDocumento;
	
	@ManyToOne
	@JoinColumn(name = "matricula_codigo")
	private Registry matricula;
	
	@Column(columnDefinition = "int default null")
	private Integer parcela;
	
	@Column(name = "data_parcela", columnDefinition = "date default null")
	private Date dataParcela;
	
	@Column(name = "data_pagamento", columnDefinition = "date default null")
	private Date dataPagamento;
	
	@Column(columnDefinition = "numeric(19,4) default null")
	private Double valor;
	
	@Column(columnDefinition = "numeric(19,4) default null")
	private Double desconto;
	
	@Column(name = "dias_desconto", columnDefinition = "numeric(19,4) default null")
	private Integer diasDesconto;
	
	@Column(columnDefinition = "varchar(50) default null")
	private String situacao;
	
}
