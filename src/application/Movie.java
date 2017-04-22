package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Movie {
	private static final String url = "jdbc:mysql://144.217.243.209:3306/project?useSSL=true";
	private static final String user = "project";
	private static final String password = "letmeinplease";

	private static Connection con;

	private Label title, year, score;
	private ImageView rtImgView, imdbImgView;
	private VBox view;
	private ArrayList<Label> tags = new ArrayList<>();
	private int id;
	private String t;

	public Movie() {
		view = new VBox(5);
	}

	public Movie (int id, String title, int  year, int score, String rtURL, String imdbURL) {
		this.id = id;

		this.title = new Label(title);
		this.t = title;
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


		try {
			con = DriverManager.getConnection(url, user, password);
			PreparedStatement ps = con.prepareStatement("SELECT t.Value FROM tags AS t JOIN movie_tags AS mt ON mt.tagID = t.id JOIN movies AS m ON m.id = mt.movieID WHERE m.title LIKE ?");
			ps.setString(1, t);
			ResultSet rs = ps.executeQuery();

			while(rs.next()) {
				Label tag = new Label(rs.getString("t.Value"));
				tag.setStyle("-fx-background-radius: 5 5 5 5;"
						+ "-fx-background-color: #8FBCA4;"
						+ "-fx-padding: 2;");
				tags.add(tag);
			}

			rs.close();
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		FlowPane tagsView = new FlowPane();
		tagsView.setPadding(new Insets(5,5,5,5));
		tagsView.setHgap(2);
		tagsView.setVgap(2);
		tagsView.getChildren().addAll(tags);

		view = new VBox(5);
		view.getChildren().addAll(info, images, tagsView);
	}

	public VBox getMovie() {
		return view;
	}

	public int getID() {
		return id;
	}
	public String getTitle() {
		return t;
	}

	@Override
	public boolean equals(Object movie) {
		boolean isEqual = false;
		if(movie != null && movie instanceof Movie) {
			isEqual = (this.id == ((Movie)movie).id);
		}
		return isEqual;
	}

	@Override
	public int hashCode() {
		return this.id;
	}
}
