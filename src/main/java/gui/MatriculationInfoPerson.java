package gui;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

import gui.util.Utils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import model.entites.Person;

public class MatriculationInfoPerson implements Initializable{
	
	
	@FXML private Label labelName;
	@FXML private Label labelBirthDateAndCivilStatus;
	@FXML private Label labelCpfAndRg;
	@FXML private Label labelAdress;
	@FXML private Label labelNeighborhoodAndCity;
	
	@Override
	public void initialize(URL url, ResourceBundle resource) {
		
	}
	
	public void setPerson(Person person) {
		// Name
		labelName.setText(person.getName());
		// BirthDate, age and civil status
		String birthAgeCivilStatus = "Nascimento: ";
		if(person.getDateBirth() != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			birthAgeCivilStatus +=  sdf.format(person.getDateBirth()) + 
					"; " + person.getAge() + " anos; ";
		} else {
			birthAgeCivilStatus += " -- ; ";
		}
		// Civil status
		birthAgeCivilStatus += "Estado Civil: ";
		if(person.getCivilStatus() != null) {
			if(person.getGender() != null && person.getGender().equalsIgnoreCase("Feminino")) {
				int civilStatusLength = person.getCivilStatus().length();
				String feminineCivilStatus = person.getCivilStatus().substring(0, civilStatusLength-1) + "a";
				birthAgeCivilStatus += feminineCivilStatus;
			} else {
				birthAgeCivilStatus += person.getCivilStatus();
			}	
		} else {
			birthAgeCivilStatus +=  " --";
		}
		labelBirthDateAndCivilStatus.setText(birthAgeCivilStatus);
		// CPF and RG
		labelCpfAndRg.setText("CPF: " + checkNull(Utils.formatCPF(person.getCpf())) +
				"; RG: " + checkNull(Utils.formatRG(person.getRg())));
		// Adress
		labelAdress.setText("Endereço: " + checkNull(person.getAdress()));
		// Neighborhood and city
		labelNeighborhoodAndCity.setText("Bairro: " + checkNull(person.getNeighborhood()) +
				"; Cidade: " + checkNull(person.getCity()) + " - " + checkNull(person.getUf()));
	}
	
	private String checkNull(String value) {
		if(value == null) {
			return "--";
		} else {
			return value;
		}
	}
	
}
