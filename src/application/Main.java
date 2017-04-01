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
import javafx.scene.text.Font;
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
	private int UID;

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
			UID = Integer.parseInt(txfID.getText());
			root = buildMenuPane();
			menu = new Scene(root);
			menu.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			stage.setScene(menu);
			stage.setMaximized(true);
		}
	}

	private VBox buildMenuPane() {
		VBox menu  = new VBox();
		menu.setPadding(new Insets(5,5,5,5));
		menu.setSpacing(10);

		Label searchBy = new Label("Search By: ");
		searchBy.setFont(new Font("Cambria",32));

		GridPane buttons = new GridPane();
		buttons.setAlignment(Pos.CENTER);
		buttons.setHgap(5);
		buttons.setVgap(5);
		buttons.setPadding(new Insets(5,5,5,5));

		Button userInfo = new Button("User Info");
		userInfo.setOnAction(e -> {
			try {
				root.getChildren().remove(3);
				root.getChildren().remove(2);
				root.getChildren().addAll(buildBreakdown());
			} catch (Exception e1) {
				root.getChildren().remove(2);
				try {
					root.getChildren().addAll(buildBreakdown());
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		});
		userInfo.setMinSize(150, 25);
		buttons.add(userInfo, 0, 0);

		Button topMovies = new Button("Top Movies");
		topMovies.setOnAction(e -> {
			try {
				seeTopMovies();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		topMovies.setMinSize(150, 25);
		buttons.add(topMovies, 1, 0);

		Button title = new Button("Title");
		title.setOnAction(e -> {
			System.out.println("stuff");
		});
		title.setMinSize(150, 25);
		buttons.add(title, 2, 0);

		Button genre = new Button("Genre");
		genre.setOnAction(e-> {
			//Stuff
		});
		genre.setMinSize(150, 25);
		buttons.add(genre, 3, 0);

		Button directorName = new Button("Director");
		directorName.setOnAction(e -> {
			//Stuff
		});
		directorName.setMinSize(150, 25);
		buttons.add(directorName, 4, 0);

		Button actorName = new Button("Actor");
		actorName.setOnAction(e -> {
			//Stuff
		});
		actorName.setMinSize(150, 25);
		buttons.add(actorName, 5, 0);

		Button tags = new Button("Tags");
		tags.setOnAction(e -> {
			//Stuff
		});
		tags.setMinSize(150, 25);
		buttons.add(tags, 6, 0);

		Button topPopularDirectors = new Button("Top Popular Directors");
		topPopularDirectors.setOnAction(e -> {
			try {
				seeTopDirectors();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		});
		topPopularDirectors.setMinSize(150, 25);
		buttons.add(topPopularDirectors, 7, 0);

		Button topPopularActors = new Button("Top Popular Actors");
		topPopularActors.setOnAction(e -> {
			seeTopActors();
		});
		topPopularActors.setMinSize(150, 25);
		buttons.add(topPopularActors, 8, 0);

		VBox buildInfo = null;
		try {
			buildInfo = buildBreakdown();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		menu.getChildren().addAll(searchBy, buttons, buildInfo);
		menu.setAlignment(Pos.TOP_CENTER);

		return menu;
	}

	@SuppressWarnings("unchecked")
	private VBox buildBreakdown() throws SQLException{
		VBox userInfo = new VBox();

		Label userID = new Label("User ID: "+ UID);
		userID.setFont(new Font("Cambria",32));

		HBox breakdown  = new HBox();

		String query = "SELECT M.title, T.dateTime, T.rating FROM user_ratedmovies_timestamps AS T JOIN movies as M ON M.id = T.movieID WHERE T.userID = ?";
		PreparedStatement ps = con.prepareStatement(query);
		ps.setInt(1, Integer.parseInt(txfID.getText()));
		ResultSet rs = ps.executeQuery();

		ObservableList<UserRatings> userRatingsList = FXCollections.observableArrayList();
		while(rs.next()) {
			userRatingsList.add(new UserRatings(rs.getString("M.title"), rs.getDate("T.dateTime"), rs.getDouble("T.rating")));
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

		TableColumn<UserRatings, Double> rating = new TableColumn<UserRatings, Double>("Rating");
		rating.setMinWidth(100);
		rating.setCellValueFactory(
				new PropertyValueFactory<UserRatings, Double>("rating"));

		table.setItems(userRatingsList);
		table.getColumns().addAll(title, timeStamp, rating);

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
		breakdown.setPadding(new Insets(10,10,10,10));
		breakdown.setAlignment(Pos.CENTER);

		userInfo.getChildren().addAll(userID, breakdown);
		userInfo.setPadding(new Insets(10,10,10,10));
		userInfo.setAlignment(Pos.CENTER);

		return userInfo;
	}

	private void seeTopMovies() throws SQLException{
		root.getChildren().remove(2);

		HBox topMoviesButtons = new HBox();
		numEntries = new TextField();
		numEntries.setPromptText("Top X Movies");
		numEntries.setMinSize(150, 25);

		Button search = new Button("Search");
		search.setOnAction(e -> {
			try {
				getTopMovies();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		search.setMinSize(150, 25);

		topMoviesButtons.getChildren().addAll(numEntries,search);
		topMoviesButtons.setAlignment(Pos.CENTER);

		root.getChildren().add(topMoviesButtons);
	}

	public void getTopMovies() throws SQLException {
		ArrayList<Movie> list = new ArrayList<>();

		String query = "SELECT DISTINCT id, title, year, rtAudienceScore, rtPictureURL, imdbPictureURL, rtAudienceScore FROM movies ORDER BY rtAudienceScore DESC LIMIT ?";
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

					fMovie.setPadding(new Insets(50,0,0,0));
					movieList.getChildren().add(fMovie);
				}
				return movieList;
			}
		});

		root.getChildren().add(page);
	}

	public static void main(String[] args) throws SQLException, IOException, ClassNotFoundException{
		con = DriverManager.getConnection(url, user, password);
		System.out.println("Connected");

		launch(args);
	}
}