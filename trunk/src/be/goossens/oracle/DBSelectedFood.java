package be.goossens.oracle;

/*
 * This class is used to display the food,
 * amound and kcal in a listview
 * */

public class DBSelectedFood {
	private String id;
	private String amound;
	private String foodName;
	private int kcal;
	private String unitName;

	public DBSelectedFood(String id, String amound, String foodName,
			int kcal, String unitName) {
		super();
		this.id = id;
		this.amound = amound;
		this.foodName = foodName;
		this.kcal = kcal;
		this.unitName = unitName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAmound() {
		return amound;
	}

	public void setAmound(String amound) {
		this.amound = amound;
	}

	public String getFoodName() {
		return foodName;
	}

	public void setFoodName(String foodName) {
		this.foodName = foodName;
	}

	public int getKcal() {
		return kcal;
	}

	public void setKcal(int kcal) {
		this.kcal = kcal;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

}
