package application;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Movie {
	private String rtURL;
	private String imdbURL;

	private Label title, year, score;
	private HBox info, images;
	private VBox view;

	public Movie (String title, int  year, int score, String rtURL, String imdbURL) {
		this.title = new Label(title);
		this.year = new Label(""+year);
		this.score = new Label(""+score);

		this.rtURL = rtURL;
		this.imdbURL = imdbURL;

		Image rtImg = new Image(rtURL);
		ImageView rtImgView = new ImageView(rtImg);

		Image imdbImg = new Image(imdbURL);
		ImageView imdbImgView = new ImageView(imdbImg);

		info = new HBox(50);
		info.getChildren().addAll(this.title, this.year, this.score);

		images = new HBox(5);
		images.getChildren().addAll(rtImgView, imdbImgView);

		view = new VBox(5);
		view.getChildren().addAll(info, images);
	}

	public VBox getMovie() {
		return view;
	}

}
