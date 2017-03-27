package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;


public class Main extends Application {
	private static final String url = "jdbc:mysql://144.217.243.209:3306/project?useSSL=true";
	private static final String user = "project";
	private static final String password = "letmeinplease";

	private static Connection con;

	protected Stage stage;
	protected Scene menu,loginMenu;
	protected TextField txfSearch, numEntries, txfID, txfPW;
	protected Label txtInfo;
	protected VBox root;
	protected GridPane login;
	private ArrayList<VBox> selectedMovies = new ArrayList<>();

	@Override
	public void start(Stage primaryStage) {
		try {
			stage = primaryStage;
			login = buildLoginPane();
			loginMenu = new Scene(login);
			loginMenu.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			stage.setTitle("Movie Recommender");
			stage.setScene(loginMenu);
			stage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public GridPane buildLoginPane() throws SQLException{
		GridPane gridLogin = new GridPane();
		gridLogin.setVgap(5);
		gridLogin.setHgap(10);

		Label id = new Label("User ID");
		gridLogin.add(id, 0, 0);

		txfID = new TextField();
		gridLogin.add(txfID, 0, 1);

		Button login = new Button("Login");
		login.setOnAction(new loginEventHandler());
		gridLogin.add(login, 0, 2);

		return gridLogin;
	}

	private class loginEventHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			root = buildMenuPane();
			menu = new Scene(root);
			menu.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			stage.setScene(menu);
		}
	}

	private VBox buildMenuPane() {
		VBox menu = new VBox();
		menu.setPadding(new Insets(5,5,5,5));
		menu.setSpacing(10);

		GridPane buttons = new GridPane();
		buttons.setAlignment(Pos.CENTER);
		buttons.setHgap(5);
		buttons.setVgap(5);
		buttons.setPadding(new Insets(5,5,5,5));

		numEntries = new TextField();
		numEntries.setPromptText("Top X Movies");
		numEntries.setMaxWidth(150);
		buttons.add(numEntries, 0, 0);

		Button topMovies = new Button("Top Movies");
		topMovies.setOnAction(e -> {
			try {
				seeTopMovies();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		topMovies.setMinSize(150, 25);
		buttons.add(topMovies, 0, 1);

		Button search = new Button("Search");
		search.setOnAction(e -> {
			System.out.println("stuff");
		});
		search.setMinSize(150, 25);
		buttons.add(search, 1, 2);

		Button topPopularDirectors = new Button("Top Popular Directors");
		topPopularDirectors.setOnAction(e -> {
			try {
				seeTopDirectors();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		});
		topPopularDirectors.setMinSize(150, 25);
		buttons.add(topPopularDirectors, 1, 1);

		Button topPopularActors = new Button("Top Popular Actors");
		topPopularActors.setOnAction(e -> {
			seeTopActors();
		});
		topPopularActors.setMinSize(150, 25);
		buttons.add(topPopularActors, 2, 1);

		Button movieTags = new Button("User Ratings");
		movieTags.setOnAction(e -> {
			System.out.println("stuff");
		});
		movieTags.setMinSize(150, 25);
		buttons.add(movieTags, 2, 2);

		txfSearch = new TextField();
		txfSearch.setPromptText("Search");
		buttons.add(txfSearch, 0, 2);

		txtInfo = new Label();
		txtInfo.setText("This is our Movie Recommender");

		menu.getChildren().addAll(buttons, txtInfo);
		menu.setAlignment(Pos.CENTER);

		return menu;
	}

	private void seeTopMovies() throws SQLException{
		ArrayList<Movie> list = new ArrayList<>();

		String query = "SELECT DISTINCT title, year, rtAudienceScore, rtPictureURL, imdbPictureURL, rtAllCriticsRating FROM movies ORDER BY rtAllCriticsRating DESC LIMIT ?";
		PreparedStatement ps = con.prepareStatement(query);
		ps.setInt(1, Integer.parseInt(numEntries.getText()));
		ResultSet rs = ps.executeQuery();

		while(rs.next()) {
			list.add(new Movie(rs.getString("title"), rs.getInt("year"), rs.getInt("rtAudienceScore"), rs.getString("rtPictureURL"), rs.getString("imdbPictureURL")));
		}

		rs.close();
		ps.close();

		setPages(list);
	}

	private void seeTopDirectors() throws SQLException{
		ArrayList<String> list = new ArrayList<>();

		String query = "SELECT directorName, rtAllCriticsRating FROM movie_directors, movies WHERE movie_directors.movieID = movies.id";
		PreparedStatement ps = con.prepareStatement(query);
		ps.setInt(1, Integer.parseInt(numEntries.getText()));

		ResultSet rs = ps.executeQuery();

		while(rs.next()) {
			list.add(rs.getString("directorName"));
		}

		rs.close();
		ps.close();

		setPages(list);
	}

	private void seeTopActors() {
		ArrayList<String> list = new ArrayList<>();
		try {
			Scanner scan = new Scanner(new File("C:/Users/Jacob/Documents/My Classes/Fall 2016/Intro to Cyber Security/Workspace/Database_GUI/src/actors"));
			while(scan.hasNextLine()) {
				list.add(scan.nextLine());
			}
			scan.close();
		} catch (FileNotFoundException e) {
			System.out.println("File Not Found!");
		}
		setPages(list);
	}

	private void setPages(ArrayList<?> list) {
		int elementsPerPage = 1;
		int pages = list.size()/elementsPerPage;
		Pagination page = new Pagination(pages,0);
		page.setPageFactory(new Callback<Integer, Node>() {
			public Node call(Integer pageIndex) {
				VBox movie = null;
				int currPage = pageIndex*elementsPerPage;
				for(int i=currPage; i < currPage+elementsPerPage; i++){
					movie = ((Movie) list.get(i)).getMovie();
				}
				final VBox fMovie = movie;
				fMovie.setOnMouseClicked(e -> {
					if(!selectedMovies.contains(fMovie) ) {
						selectedMovies.add(fMovie);
						fMovie.setStyle("-fx-padding: 2;" +
			                      		"-fx-border-style: solid inside;" +
			                      		"-fx-border-width: 2;" +
			                      		"-fx-border-insets: 5;" +
			                      		"-fx-border-radius: 5;" +
										"-fx-border-color: blue;");
						stage.sizeToScene();
					} else {
						selectedMovies.remove(fMovie);
						fMovie.setStyle(null);
						stage.sizeToScene();
					}
				});
				return fMovie;
			}
		});

		root.getChildren().remove(1);
		root.getChildren().add(page);
		stage.sizeToScene();
	}

	public static void main(String[] args) throws SQLException, IOException, ClassNotFoundException{
		con = DriverManager.getConnection(url, user, password);
		System.out.println("Database connected sucessfully");

		launch(args);
	}
}
