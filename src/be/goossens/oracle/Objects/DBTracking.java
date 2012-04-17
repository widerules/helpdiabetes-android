package be.goossens.oracle.Objects;

import java.util.Date;

public class DBTracking {
	private DBExerciseEvent exerciseEvent;
	private DBMealEvent mealEvent;
	private DBBloodGlucoseEvent bloodGlucoseEvent;
	private Date timestamp;
	private String noRecors;

	public DBTracking(DBExerciseEvent exerciseEvent, DBMealEvent mealEvent,
			DBBloodGlucoseEvent bloodGlucoseEvent, Date timestamp,
			String noRecors) {
		super();
		this.exerciseEvent = exerciseEvent;
		this.mealEvent = mealEvent;
		this.bloodGlucoseEvent = bloodGlucoseEvent;
		this.timestamp = timestamp;
		this.noRecors = noRecors;
	}

	public DBExerciseEvent getExerciseEvent() {
		return exerciseEvent;
	}

	public void setExerciseEvent(DBExerciseEvent exerciseEvent) {
		this.exerciseEvent = exerciseEvent;
	}

	public DBMealEvent getMealEvent() {
		return mealEvent;
	}

	public void setMealEvent(DBMealEvent mealEvent) {
		this.mealEvent = mealEvent;
	}

	public DBBloodGlucoseEvent getBloodGlucoseEvent() {
		return bloodGlucoseEvent;
	}

	public void setBloodGlucoseEvent(DBBloodGlucoseEvent bloodGlucoseEvent) {
		this.bloodGlucoseEvent = bloodGlucoseEvent;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getNoRecords() {
		return noRecors;
	}

	public void setNoRecors(String noRecors) {
		this.noRecors = noRecors;
	}

}
