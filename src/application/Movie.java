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

		if(rtURL.equals("") || rtURL==null) {
			Image rtImg = new Image("http://3.bp.blogspot.com/-EzsIA_YnyN4/T_h_W3IBAJI/AAAAAAAAFcM/v90MaP9pJAc/s1600/not_found-full.png");
			rtImgView = new ImageView(rtImg);
			rtImgView.setFitWidth(200);
			rtImgView.setFitHeight(250);
			rtImgView.setPreserveRatio(true);
		} else {
			Image rtImg = new Image(rtURL);
			rtImgView = new ImageView(rtImg);
			rtImgView.setFitWidth(200);
			rtImgView.setFitHeight(250);
			rtImgView.setPreserveRatio(true);
		}

		if(imdbURL.equals("") || imdbURL==null) {
			Image imdbImg = new Image("http://3.bp.blogspot.com/-EzsIA_YnyN4/T_h_W3IBAJI/AAAAAAAAFcM/v90MaP9pJAc/s1600/not_found-full.png");
			imdbImgView = new ImageView(imdbImg);
			imdbImgView.setFitWidth(200);
			imdbImgView.setFitHeight(250);
			imdbImgView.setPreserveRatio(true);
		} else {
			Image imdbImg = new Image(imdbURL);
			imdbImgView = new ImageView(imdbImg);
			imdbImgView.setFitWidth(200);
			imdbImgView.setFitHeight(250);
			imdbImgView.setPreserveRatio(true);
		}

		VBox info = new VBox(5);
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
