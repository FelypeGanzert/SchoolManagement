package model.entites.util;

import model.entites.Contact;
import model.entites.Responsible;
import model.entites.Student;

public class PersonUtils {
	
	public static void parseDataFromStudentToResponsible(Student student, Responsible responsible) {
		student.setName(responsible.getName());
		student.setCpf(responsible.getCpf());
		student.setRg(responsible.getRg());
		student.setDateBirth(responsible.getDateBirth());
		student.setEmail(responsible.getEmail());
		student.setSendEmail(responsible.getSendEmail());
		student.setGender(responsible.getGender());
		student.setCivilStatus(responsible.getCivilStatus());
		student.setAdress(responsible.getCivilStatus());
		student.setNeighborhood(responsible.getNeighborhood());
		student.setCity(responsible.getCity());
		student.setUf(responsible.getUf());
		student.setAdressReference(responsible.getAdressReference());
		student.setObservation(responsible.getObservation());
		for(Contact c : responsible.getContacts()){
			Contact contactCopy = new Contact();
			contactCopy.setNumber(c.getNumber());
			contactCopy.setDescription(c.getDescription());
			student.getContacts().add(contactCopy);
		}
	}

	public static void parseDataFromResponsibleToStudent(Responsible responsible, Student student) {
		responsible.setName(student.getName());
		responsible.setCpf(student.getCpf());
		responsible.setRg(student.getRg());
		responsible.setDateBirth(student.getDateBirth());
		responsible.setEmail(student.getEmail());
		responsible.setSendEmail(student.getSendEmail());
		responsible.setGender(student.getGender());
		responsible.setCivilStatus(student.getCivilStatus());
		responsible.setAdress(student.getCivilStatus());
		responsible.setNeighborhood(student.getNeighborhood());
		responsible.setCity(student.getCity());
		responsible.setUf(student.getUf());
		responsible.setAdressReference(student.getAdressReference());
		responsible.setObservation(student.getObservation());
		for(Contact c : student.getContacts()){
			Contact contactCopy = new Contact();
			contactCopy.setNumber(c.getNumber());
			contactCopy.setDescription(c.getDescription());
			responsible.getContacts().add(contactCopy);
		}
	}
	
}
