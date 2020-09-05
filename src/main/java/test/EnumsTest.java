package test;

import gui.util.enums.StudentStatusEnum;

public class EnumsTest {

	public static void main(String[] args) {
		StudentStatusEnum status = StudentStatusEnum.fromString("ativo");
		System.out.println(status.getHexColor());
	}

}
