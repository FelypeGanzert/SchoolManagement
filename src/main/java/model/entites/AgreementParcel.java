package model.entites;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
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
@Entity(name = "ParcelaAcordo")
@Table(name = "parcela_acordo")
public class AgreementParcel {

	@EqualsAndHashCode.Include
	@Id
	@Column(name = "numero_documento")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer documentNumber;
	
	@ManyToOne
	@JoinColumn(name = "acordo_codigo")
	private Agreement agreement;
	
	@OneToOne
	@JoinColumn(name = "parcela_normal_numero")
	private Parcel parcel;
	
	@OneToOne
	@JoinColumn(name = "matricula_codigo")
	private Matriculation matriculation;
	
	@Column(name = "parcela_numero", columnDefinition = "int default null")
	private Integer parcelNumber;
	
	@Column(name = "valor", columnDefinition = "numeric(19,4) default null")
	private Double value;
	
	@Column(name = "data_parcela", columnDefinition = "DATETIME default null")
	private Date dateParcel;
	
	@Column(name = "situacao", columnDefinition = "varchar(50) default null")
	private String situation;
	
	@Column(name = "data_pagamento", columnDefinition = "DATETIME  default null")
	private Date datePayment;
	
	@Column(name = "valor_pago", columnDefinition = "numeric(19,4) default null")
	private Double valuePaid;
	
	@Column(name = "pago_com", columnDefinition = "varchar(50) default null")
	private String paidWith;
	
	@Column(name = "pagamento_recebido_por", columnDefinition = "varchar(50) default null")
	private String paymentReceivedBy;
	
	@Column (name = "excluido", columnDefinition = "varchar(1) default null")
	private String excluded;
	
}