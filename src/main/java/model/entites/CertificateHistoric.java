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
@Entity(name = "certificado_historico")
@Table(name = "certificado_historico")
public class CertificateHistoric {
	
	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column (name = "aluno_nome", columnDefinition = "varchar(50) default null")
	private String studentName;
	
	@Column (name = "curso", columnDefinition = "text default null")
	private String course;
	
	@Column (name = "data_inicio", columnDefinition = "DATETIME default null")
	private Date startDate;
	
	@Column (name = "data_termino", columnDefinition = "DATETIME default null")
	private Date endDate;
	
	@Column (name = "carga_horaria", columnDefinition = "int default null")
	private Integer courseLoad;
	
	@Column (name = "data_emissao", columnDefinition = "DATETIME default null")
	private Date printDate;
	
	@Column (name = "ata_numero", columnDefinition = "int default null")
	private Integer recordNumber;
	
	@Column (name = "ata_numero_pagina", columnDefinition = "int default null")
	private Integer recordPageNumber;
	
	public String getFullRecordPath() {
		String full = "";
		if(recordNumber != null) {
			full += recordNumber;
		}
		full += " / ";
		if(recordPageNumber != null) {
			full += recordPageNumber;
		}
		return full;
	}
	
	@Column (name = "excluido", columnDefinition = "varchar(1) default null")
	private String excluded;
	
}