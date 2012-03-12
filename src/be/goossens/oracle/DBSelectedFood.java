package be.goossens.oracle;

/*
 * This class is used to display the food,
 * amound and kcal in a listview
 * */

public class DBSelectedFood {
	private String id;
	private float amound;
	private String foodName;
	private float kcal;
	private float carbs;
	private float prot;
	private float fat;
	private float standardAmound;
	private String unitName;

	public DBSelectedFood(String id, float amound, String foodName,
			float kcal, String unitName, float carbs, float prot, float fat,
			float standardAmound) {
		super();
		this.id = id;
		this.amound = amound;
		this.foodName = foodName;
		this.kcal = kcal;
		this.unitName = unitName;
		this.carbs = carbs;
		this.prot = prot;
		this.fat = fat;
		this.standardAmound = standardAmound;
	}

	public float getCarbs() {
		return carbs;
	}

	public void setCarbs(float carbs) {
		this.carbs = carbs;
	}

	public float getProt() {
		return prot;
	}

	public void setProt(float prot) {
		this.prot = prot;
	}

	public float getFat() {
		return fat;
	}

	public void setFat(float fat) {
		this.fat = fat;
	}

	public float getStandardAmound() {
		return standardAmound;
	}

	public void setStandardAmound(float standardAmound) {
		this.standardAmound = standardAmound;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public float getAmound() {
		return amound;
	}

	public void setAmound(float amound) {
		this.amound = amound;
	}

	public String getFoodName() {
		return foodName;
	}

	public void setFoodName(String foodName) {
		this.foodName = foodName;
	}

	public float getKcal() {
		return kcal;
	}

	public void setKcal(float kcal) {
		this.kcal = kcal;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

}
