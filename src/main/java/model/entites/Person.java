package model.entites;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Person {
		
	private Integer id;
	private String name;
	private String cpf;
	private String rg;	
	private Date dateBirth;	
	private String email;
	private Boolean sendEmail;
	private String gender;	
	private String civilStatus;	
	private String adress;	
	private String neighborhood;	
	private String city;	
	private String uf;	
	private String adressReference;	
	private String registeredBy;	
	private Date dateRegistry;	
	private Date dateLastRegistryEdit;	
	private String observation;	
	private List<Contact> contacts;
	public abstract int getAge();

}
