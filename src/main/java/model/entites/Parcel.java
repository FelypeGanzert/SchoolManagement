package model.entites;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
	private Integer documentNumber;
	
	@ManyToOne
	@JoinColumn(name = "matricula_codigo")
	private Matriculation matriculation;
	
	@Column(name = "parcela_numero", columnDefinition = "int default null")
	private Integer parcelNumber;
	
	@Column(name = "valor", columnDefinition = "numeric(19,4) default null")
	private Double value;
	
	@Column(name = "data_parcela", columnDefinition = "DATETIME default null")
	private Date dateParcel;
	
	@Column(name = "dias_multa_atraso", columnDefinition = "int default null")
	private Integer daysFineDelay;
	
	@Column(name = "valor_multa_atraso", columnDefinition = "numeric(19,4) default null")
	private Double valueFineDelay;
	
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
	
	public Date getDateFineDelay() {
		if (getDateParcel() != null && getDaysFineDelay() != null ) {
			Date date = getDateParcel();
			// add days to original date
			if(getDaysFineDelay() > 0) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(date); // set date to calendar
				cal.add(Calendar.DAY_OF_MONTH, getDaysFineDelay()); // add the X daysFineDelay
				date = cal.getTime(); // set changed date in calendar to dat
			}
			return date;
		}
		return null;
	}
	
	public Double getValueWithFineDelay() {
		if(getValueFineDelay() != null) {
			return getValue() + getValueFineDelay();
		}
		return null;
	}
	
	// This will be used to remove parcels from Matriculation
	@Transient
	private BooleanProperty  selected = new SimpleBooleanProperty(true);
	
	public void setSelected(boolean selected) {
		this.selected.set(selected);
	}
	
	public boolean isSelected() {
		return this.selected.get();
	}
	
}