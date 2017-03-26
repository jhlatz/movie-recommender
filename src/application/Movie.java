package application;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Movie {
	private Label title, year, score;
	private VBox view;

	public Movie (String title, int  year, int score, String rtURL, String imdbURL) {
		this.title = new Label(title);
		this.year = new Label(""+year);
		this.score = new Label(""+score);

		Image rtImg = new Image(rtURL);
		ImageView rtImgView = new ImageView(rtImg);

		Image imdbImg = new Image(imdbURL);
		ImageView imdbImgView = new ImageView(imdbImg);

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

}
