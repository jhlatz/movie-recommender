package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
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
			stage.setMaximized(true);
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

		HBox buildInfo = null;
		try {
			buildInfo = buildBreakdown();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		menu.getChildren().addAll(buttons, buildInfo);
		menu.setAlignment(Pos.CENTER);

		return menu;
	}

	@SuppressWarnings("unchecked")
	private HBox buildBreakdown() throws SQLException{
		HBox breakdown  = new HBox();

		String query = "SELECT M.title, T.dateTime FROM user_ratedmovies_timestamps AS T JOIN movies as M ON M.id = T.movieID WHERE T.userID = ?";
		PreparedStatement ps = con.prepareStatement(query);
		ps.setInt(1, Integer.parseInt(txfID.getText()));
		ResultSet rs = ps.executeQuery();

		ObservableList<UserRatings> asdf = FXCollections.observableArrayList();
		while(rs.next()) {
			asdf.add(new UserRatings(rs.getString("M.title"), rs.getDate("T.dateTime")));
		}
		TableView<UserRatings>table = new TableView<UserRatings>();
		table.setEditable(false);

		TableColumn<UserRatings, String> title = new TableColumn<UserRatings, String>("Title");
		title.setMinWidth(200);
		title.setCellValueFactory(
				new PropertyValueFactory<UserRatings, String>("title"));

		TableColumn<UserRatings, Date> timeStamp = new TableColumn<UserRatings, Date>("Timestamp");
		timeStamp.setMinWidth(200);
		timeStamp.setCellValueFactory(
				new PropertyValueFactory<UserRatings, Date>("time"));

		table.setItems(asdf);
		table.getColumns().addAll(title, timeStamp);

		query = "SELECT G.genre FROM movie_genre AS G WHERE G.movieID IN (SELECT R.movieID FROM user_ratedmovies AS R WHERE R.userID = ?)";
		ps = con.prepareStatement(query);
		ps.setInt(1, Integer.parseInt(txfID.getText()));
		rs = ps.executeQuery();

		int totalRatings = 0;
		HashMap<String, Integer> userRatings = new HashMap<String, Integer>();
		while(rs.next()) {
			String genre = rs.getString("G.genre");
			if(userRatings.get(genre)==null) {
				userRatings.put(genre, 1);
				totalRatings++;
			} else {
				userRatings.put(genre, userRatings.get(genre)+1);
				totalRatings++;
			}
		}

		DecimalFormat df = new DecimalFormat("##0.00");
		PieChart ratings = new PieChart();
		for(Map.Entry<String, Integer> entry : userRatings.entrySet()) {
			PieChart.Data slice = new PieChart.Data(entry.getKey()+": "+df.format(((double)entry.getValue()/totalRatings)*100)+"%", entry.getValue());
			ratings.getData().add(slice);
		}

		breakdown.getChildren().addAll(table,ratings);
		breakdown.setAlignment(Pos.CENTER);
		return breakdown;

	}

	private void seeTopMovies() throws SQLException{
		ArrayList<Movie> list = new ArrayList<>();

		String query = "SELECT DISTINCT id, title, year, rtAudienceScore, rtPictureURL, imdbPictureURL, rtAllCriticsRating FROM movies ORDER BY rtAllCriticsRating DESC LIMIT ?";
		PreparedStatement ps = con.prepareStatement(query);
		ps.setInt(1, Integer.parseInt(numEntries.getText()));
		ResultSet rs = ps.executeQuery();

		while(rs.next()) {
			list.add(new Movie(rs.getInt("id"), rs.getString("title"), rs.getInt("year"), rs.getInt("rtAudienceScore"), rs.getString("rtPictureURL"), rs.getString("imdbPictureURL")));
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
		int elementsPerPage = 5;
		int pages = list.size()/elementsPerPage;
		Pagination page = new Pagination(pages,0);
		page.setPageFactory(new Callback<Integer, Node>() {
			public Node call(Integer pageIndex) {
				HBox movieList = new HBox(5);
				int currPage = pageIndex*elementsPerPage;
				for(int i=currPage; i < currPage+elementsPerPage; i++){
					final VBox fMovie = ((Movie) list.get(i)).getMovie();
					fMovie.setOnMouseClicked(e -> {
						if(!selectedMovies.contains(fMovie) ) {
							selectedMovies.add(fMovie);
							fMovie.setStyle("-fx-padding: 2;" +
				                      		"-fx-border-style: solid inside;" +
				                      		"-fx-border-width: 2;" +
				                      		"-fx-border-insets: 5;" +
				                      		"-fx-border-radius: 5;" +
											"-fx-border-color: blue;");
						} else {
							selectedMovies.remove(fMovie);
							fMovie.setStyle(null);
						}
					});
					fMovie.setOnMouseEntered(e -> {
						fMovie.setScaleX(1.2);
						fMovie.setScaleY(1.2);

					});
					fMovie.setOnMouseExited(e -> {
						fMovie.setScaleX(1);
						fMovie.setScaleY(1);
					});
					movieList.getChildren().add(fMovie);
				}
				return movieList;
			}
		});

		root.getChildren().remove(1);
		root.getChildren().add(page);
	}


	public static void main(String[] args) throws SQLException, IOException, ClassNotFoundException{
		con = DriverManager.getConnection(url, user, password);
		System.out.println("Connected");

		launch(args);
	}
}
