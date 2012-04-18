package be.goossens.oracle.Objects;

public class DBFoodLanguage {
	private long id;
	private String language;

	public DBFoodLanguage(long id, String language) {
		super();
		this.id = id;
		this.language = language;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

}
