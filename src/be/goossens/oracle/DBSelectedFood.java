package be.goossens.oracle;

/*
 * This class is used to display the food,
 * amound and kcal in a listview
 * */

public class DBSelectedFood {
	private long id;
	private float amound;
	private long unitID;
	private String foodName;
	private String unitName;

	public DBSelectedFood(long id, float amound, long unitID, String foodName,
			String unitName) {
		super();
		this.id = id;
		this.amound = amound;
		this.unitID = unitID;
		this.foodName = foodName;
		this.unitName = unitName;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public float getAmound() {
		return amound;
	}

	public void setAmound(float amound) {
		this.amound = amound;
	}

	public long getUnitID() {
		return unitID;
	}

	public void setUnitID(long unitID) {
		this.unitID = unitID;
	}

	public String getFoodName() {
		return foodName;
	}

	public void setFoodName(String foodName) {
		this.foodName = foodName;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}
	
	public String toString(){
		return foodName;
	}

}
