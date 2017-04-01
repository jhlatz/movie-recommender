package application;

import java.sql.Date;

public class UserRatings {
	private int id;
	private String title;
	private Date time;


	public UserRatings(String title, Date time) {
		this.setTitle(title);
		this.setTime(time);
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public Date getTime() {
		return time;
	}


	public void setTime(Date time) {
		this.time = time;
	}




}
