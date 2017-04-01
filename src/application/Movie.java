package application;

import java.util.ArrayList;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Movie {
	private Label title, year, score;
	private ImageView rtImgView, imdbImgView;
	private VBox view;
	private ArrayList<String> tags;
	private int id;

	public Movie (int id, String title, int  year, int score, String rtURL, String imdbURL) {
		this.id = id;

		this.title = new Label(title);
		this.year = new Label(""+year);
		this.score = new Label(""+score);

		Image rtImg = new Image(rtURL);
		rtImgView = new ImageView(rtImg);
		rtImgView.resize(128, 72);

		Image imdbImg = new Image(imdbURL);
		imdbImgView = new ImageView(imdbImg);
		imdbImgView.resize(128, 72);

		HBox info = new HBox(100);
		info.getChildren().addAll(this.title, this.year, this.score);

		HBox images = new HBox(5);
		images.getChildren().addAll(rtImgView, imdbImgView);

		view = new VBox(5);
		view.getChildren().addAll(info, images);
	}

	public VBox getMovie() {
		return view;
	}

	public int getID() {
		return id;
	}

}
