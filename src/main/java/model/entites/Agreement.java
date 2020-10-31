package model.entites;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Where;

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
@Entity(name = "Acordo")
@Table(name = "acordo")
public class Agreement {
	
	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer code;
	
	@ManyToOne
	@JoinColumn(name="matricula_codigo")
	private Matriculation matriculation;
	
	@Column(name="data_acordo", columnDefinition = "date default null")
	private Date dateAgreement;

	@Column(name="acordo_por", columnDefinition = "varchar(50) default null")
	private String agreementBy;
	
	@OneToMany(mappedBy = "agreement", cascade = CascadeType.ALL, orphanRemoval = true)
	@Where(clause = "excluido is null")
	private List<AgreementParcel> parcels = new ArrayList<>();
	
	@OneToMany(mappedBy = "agreement", cascade = CascadeType.ALL, orphanRemoval = true)
	@Where(clause = "excluido is null")
	private List<Parcel> normalParcels = new ArrayList<>();

	@Column (name = "excluido", columnDefinition = "varchar(1) default null")
	private String excluded;
	
}