package db;

public class DbConnectionError extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public DbConnectionError(String msg) {
		super(msg);
	}
	
	
}
