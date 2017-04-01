package application;

import java.sql.Date;

public class UserRatings {
	private int id;
	private String title;
	private Date time;
	private double rating;


	public UserRatings(String title, Date time, double rating) {
		this.setTitle(title);
		this.setTime(time);
		this.setRating(rating);
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


	public double getRating() {
		return rating;
	}


	public void setRating(double rating) {
		this.rating = rating;
	}




}
