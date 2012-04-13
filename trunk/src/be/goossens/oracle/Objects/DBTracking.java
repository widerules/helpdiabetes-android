package be.goossens.oracle.Objects;

import java.util.Date;

public class DBTracking {
	private DBExerciseEvent exerciseEvent;
	private DBMealEvent mealEvent;
	private Date timestamp;
	private String noRecors;

	public DBTracking(DBExerciseEvent exerciseEvent, DBMealEvent mealEvent,
			Date timestamp, String noRecors) {
		super();
		this.exerciseEvent = exerciseEvent;
		this.mealEvent = mealEvent;
		this.timestamp = timestamp;
		this.noRecors = noRecors;
	}

	public DBMealEvent getMealEvent() {
		return mealEvent;
	}

	public void setMealEvent(DBMealEvent mealEvent) {
		this.mealEvent = mealEvent;
	}

	public DBExerciseEvent getExerciseEvent() {
		return exerciseEvent;
	}

	public void setExerciseEvent(DBExerciseEvent exerciseEvent) {
		this.exerciseEvent = exerciseEvent;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getNoRecors() {
		return noRecors;
	}

	public void setNoRecors(String noRecors) {
		this.noRecors = noRecors;
	}

}
