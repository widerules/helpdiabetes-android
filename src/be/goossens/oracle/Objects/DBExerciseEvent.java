package be.goossens.oracle.Objects;

import java.util.Date;

public class DBExerciseEvent {

	private long id;
	private String description;
	private Date startTime;
	private Date endTime;
	private Date timeStamp;
	private long exerciseTypeID;
	private long userID;

	public DBExerciseEvent(long id, String description, Date startTime,
			Date endTime, Date timeStamp, long exerciseTypeID, long userID) {
		super();
		this.id = id;
		this.description = description;
		this.startTime = startTime;
		this.endTime = endTime;
		this.timeStamp = timeStamp;
		this.exerciseTypeID = exerciseTypeID;
		this.userID = userID;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Date getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}

	public long getExerciseTypeID() {
		return exerciseTypeID;
	}

	public void setExerciseTypeID(long exerciseTypeID) {
		this.exerciseTypeID = exerciseTypeID;
	}

	public long getUserID() {
		return userID;
	}

	public void setUserID(long userID) {
		this.userID = userID;
	}

}
