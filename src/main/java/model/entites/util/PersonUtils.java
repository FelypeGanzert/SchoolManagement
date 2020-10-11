package model.entites.util;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

import model.entites.Contact;
import model.entites.Responsible;
import model.entites.Student;

public class PersonUtils {

	public static void parseDataFromResponsibleToStudent(Responsible responsible, Student student) {
		student.setName(responsible.getName());
		student.setCpf(responsible.getCpf());
		student.setRg(responsible.getRg());
		student.setDateBirth(responsible.getDateBirth());
		student.setEmail(responsible.getEmail());
		student.setSendEmail(responsible.getSendEmail());
		student.setGender(responsible.getGender());
		student.setCivilStatus(responsible.getCivilStatus());
		student.setAdress(responsible.getAdress());
		student.setNeighborhood(responsible.getNeighborhood());
		student.setCity(responsible.getCity());
		student.setUf(responsible.getUf());
		student.setAdressReference(responsible.getAdressReference());
		student.setObservation(responsible.getObservation());
		if (responsible.getContacts() != null) {
			if(student.getContacts() == null) {
				student.setContacts(new ArrayList<>());
			}
			for (Contact c : responsible.getContacts()) {
				Contact contactCopy = new Contact();
				contactCopy.setNumber(c.getNumber());
				contactCopy.setDescription(c.getDescription());
				student.getContacts().add(contactCopy);
			}
		}
	}

	public static void parseDataFromStudentToResponsible(Student student, Responsible responsible) {
		responsible.setName(student.getName());
		responsible.setCpf(student.getCpf());
		responsible.setRg(student.getRg());
		responsible.setDateBirth(student.getDateBirth());
		responsible.setEmail(student.getEmail());
		responsible.setSendEmail(student.getSendEmail());
		responsible.setGender(student.getGender());
		responsible.setCivilStatus(student.getCivilStatus());
		responsible.setAdress(student.getAdress());
		responsible.setNeighborhood(student.getNeighborhood());
		responsible.setCity(student.getCity());
		responsible.setUf(student.getUf());
		responsible.setAdressReference(student.getAdressReference());
		responsible.setObservation(student.getObservation());
		if (responsible.getContacts() != null) {
			for (Contact c : student.getContacts()) {
				if(responsible.getContacts() == null) {
					responsible.setContacts(new ArrayList<>());
				}
				Contact contactCopy = new Contact();
				contactCopy.setNumber(c.getNumber());
				contactCopy.setDescription(c.getDescription());
				responsible.getContacts().add(contactCopy);
			}
		}
	}

	public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
	    Set<Object> seen = ConcurrentHashMap.newKeySet();
	    return t -> seen.add(keyExtractor.apply(t));
	}
}
