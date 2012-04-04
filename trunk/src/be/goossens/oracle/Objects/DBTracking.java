package be.goossens.oracle.Objects;

import java.util.Date;

public class DBTracking {
	private DBExerciseEvent exerciseEvent;
	private Date timestamp;
	private String noRecors;
	
	public DBTracking(DBExerciseEvent exerciseEvent, Date timestamp,
			String noRecors) {
		super();
		this.exerciseEvent = exerciseEvent;
		this.timestamp = timestamp;
		this.noRecors = noRecors;
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
