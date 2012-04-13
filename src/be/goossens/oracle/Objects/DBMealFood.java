package be.goossens.oracle.Objects;

public class DBMealFood {
	private long id;
	private long mealEventId;
	private long foodId;
	private float amount;

	public DBMealFood(long id, long mealEventId, long foodId, float amount) {
		super();
		this.id = id;
		this.mealEventId = mealEventId;
		this.foodId = foodId;
		this.amount = amount;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getMealEventId() {
		return mealEventId;
	}

	public void setMealEventId(long mealEventId) {
		this.mealEventId = mealEventId;
	}

	public long getFoodId() {
		return foodId;
	}

	public void setFoodId(long foodId) {
		this.foodId = foodId;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

}
