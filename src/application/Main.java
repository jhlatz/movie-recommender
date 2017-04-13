package application;

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
import java.util.HashSet;
import java.util.Map;

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
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
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
	private ArrayList<Movie> selectedMovies = new ArrayList<>();
	private ComboBox<String> genres;
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
			//stage.setMaximized(true);
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
				root.getChildren().remove(3);
				root.getChildren().remove(2);
				seeTopMovies();
			} catch (Exception e1) {
				root.getChildren().remove(2);
				try {
					seeTopMovies();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		});
		topMovies.setMinSize(150, 25);
		buttons.add(topMovies, 1, 0);

		Button title = new Button("Title");
		title.setOnAction(e -> {
			try {
				root.getChildren().remove(3);
				root.getChildren().remove(2);
				searchByTitle();
			} catch (Exception e1) {
				root.getChildren().remove(2);
				try {
					searchByTitle();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		});
		title.setMinSize(150, 25);
		buttons.add(title, 2, 0);

		Button genre = new Button("Genre");
		genre.setOnAction(e-> {
			try {
				root.getChildren().remove(3);
				root.getChildren().remove(2);
				searchByGenre();
			} catch (Exception e1) {
				root.getChildren().remove(2);
				try {
					searchByGenre();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		});
		genre.setMinSize(150, 25);
		buttons.add(genre, 3, 0);

		Button directorName = new Button("Director");
		directorName.setOnAction(e -> {
			try {
				root.getChildren().remove(3);
				root.getChildren().remove(2);
				searchByDirector();
			} catch (Exception e1) {
				root.getChildren().remove(2);
				try {
					searchByDirector();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		});
		directorName.setMinSize(150, 25);
		buttons.add(directorName, 4, 0);

		Button actorName = new Button("Actor");
		actorName.setOnAction(e -> {
			try {
				root.getChildren().remove(3);
				root.getChildren().remove(2);
				searchByActor();
			} catch (Exception e1) {
				root.getChildren().remove(2);
				try {
					searchByActor();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		});
		actorName.setMinSize(150, 25);
		buttons.add(actorName, 5, 0);

		Button tags = new Button("Tags");
		tags.setOnAction(e -> {
			try {
				root.getChildren().remove(3);
				root.getChildren().remove(2);
				searchByTag();
			} catch (Exception e1) {
				root.getChildren().remove(2);
				try {
					searchByTag();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		});
		tags.setMinSize(150, 25);
		buttons.add(tags, 6, 0);

		Button topPopularDirectors = new Button("Top Popular Directors");
		topPopularDirectors.setOnAction(e -> {
			try {
				root.getChildren().remove(3);
				root.getChildren().remove(2);
				seeTopDirectors();
			} catch (Exception e1) {
				root.getChildren().remove(2);
				try {
					seeTopDirectors();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		});
		topPopularDirectors.setMinSize(150, 25);
		buttons.add(topPopularDirectors, 7, 0);

		Button topPopularActors = new Button("Top Popular Actors");
		topPopularActors.setOnAction(e -> {
			try {
				root.getChildren().remove(3);
				root.getChildren().remove(2);
				seeTopActors();
			} catch (Exception e1) {
				root.getChildren().remove(2);
				try {
					seeTopActors();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		});
		topPopularActors.setMinSize(150, 25);
		buttons.add(topPopularActors, 8, 0);

		Button recommendations = new Button("Recommendations");
		recommendations.setOnAction(e -> {
			try {
				root.getChildren().remove(3);
				root.getChildren().remove(2);
				seeTopActors();
			} catch (Exception e1) {
				root.getChildren().remove(2);
				try {
					seeRecommendations();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		});
		recommendations.setMinSize(150, 25);
		buttons.add(recommendations, 9, 0);

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

	//Query 9
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
		rating.setMinWidth(80);
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


		userInfo.setPadding(new Insets(10,10,10,10));
		userInfo.getChildren().addAll(userID, breakdown);
		userInfo.setAlignment(Pos.CENTER);

		return userInfo;
	}

	//Query 1
	private void seeTopMovies() throws SQLException{
		HBox topMoviesButtons = searchButtons();

		numEntries.setPromptText("Top X Movies");

		((ButtonBase) topMoviesButtons.getChildren().get(1)).setOnAction(e -> {
			try {
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
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});

		root.getChildren().add(topMoviesButtons);
	}

	//Query 2
	public void searchByTitle() throws SQLException {
		HBox searchTitleButtons = searchButtons();

		numEntries.setPromptText("Search By Title");

		((ButtonBase) searchTitleButtons.getChildren().get(1)).setOnAction(e -> {
			try {
				getMoviesSPM("SELECT DISTINCT id, title, year, rtAudienceScore, rtPictureURL, imdbPictureURL, rtAudienceScore FROM movies where title LIKE ? ORDER BY movies.rtAudienceScore DESC");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});

		root.getChildren().add(searchTitleButtons);
	}

	//Query 3
	public void searchByGenre() throws SQLException {
		HBox searchGenreButtons = new HBox(5);

		ObservableList<String> genreList = FXCollections.observableArrayList(
				"Action",
				"Adventure",
				"Animation",
				"Children",
				"Comedy",
				"Crime",
				"Documentary",
				"Drama",
				"Fantasy",
				"Film-Noir",
				"Horror",
				"IMAX",
				"Musical",
				"Mystery",
				"Romance",
				"Sci-Fi",
				"Short",
				"Thriller",
				"War",
				"Western");
		genres = new ComboBox<>(genreList);
		genres.getSelectionModel().selectFirst();
		genres.setMinSize(150, 25);

		searchGenreButtons.getChildren().addAll(genres,searchButtons());

		((ButtonBase)((HBox) searchGenreButtons.getChildren().get(1)).getChildren().get(1)).setOnAction(e -> {
			try {
				ArrayList<Movie> list = new ArrayList<>();

				String query = "SELECT DISTINCT id, title, year, rtAudienceScore, rtPictureURL, imdbPictureURL, rtAudienceScore FROM movies where id IN(SELECT movieID FROM movie_genre WHERE genre = ?) ORDER BY movies.rtAudienceScore DESC LIMIT ?";
				PreparedStatement ps = con.prepareStatement(query);
				ps.setString(1, genres.getSelectionModel().getSelectedItem());
				ps.setInt(2, Integer.parseInt(numEntries.getText()));
				ResultSet rs = ps.executeQuery();

				while(rs.next()) {
					list.add(new Movie(rs.getInt("id"), rs.getString("title"), rs.getInt("year"), rs.getInt("rtAudienceScore"), rs.getString("rtPictureURL"), rs.getString("imdbPictureURL")));
				}

				rs.close();
				ps.close();

				setPages(list);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});

		numEntries.setPromptText("How Many Movies?");

		searchGenreButtons.setAlignment(Pos.CENTER);

		root.getChildren().add(searchGenreButtons);
	}

	//Query 4
	public void searchByDirector() throws SQLException {
		HBox searchDirectorButtons = searchButtons();

		numEntries.setPromptText("Which Director");

		((ButtonBase) searchDirectorButtons.getChildren().get(1)).setOnAction(e -> {
			try {
				getMoviesSPM("SELECT DISTINCT m.id, m.title, m.year, m.rtAudienceScore, m.imdbPictureURL, m.rtPictureURL FROM movies m, movie_directors d WHERE m.id = d.movieID AND d.directorName LIKE ?");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});

		root.getChildren().add(searchDirectorButtons);
	}

	//Query 5
	public void searchByActor() throws SQLException {
		HBox searchActorButtons = searchButtons();

		numEntries.setPromptText("Which Actor");

		((ButtonBase) searchActorButtons.getChildren().get(1)).setOnAction(e -> {
			try {
				getMoviesSPM("SELECT DISTINCT m.id, m.title, m.year, m.rtAudienceScore, m.imdbPictureURL, m.rtPictureURL FROM movies m, movie_actors a WHERE m.id = a.movieID AND a.actorName LIKE ?");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});

		root.getChildren().add(searchActorButtons);
	}

	//Query 6
	public void searchByTag() throws SQLException {
		HBox searchTagButtons = searchButtons();

		numEntries.setPromptText("Which Tag");

		((ButtonBase) searchTagButtons.getChildren().get(1)).setOnAction(e -> {
			try {
				getMoviesSPM("SELECT DISTINCT m.id, m.title, m.year, m.rtAudienceScore, m.imdbPictureURL, m.rtPictureURL FROM movies m, movie_tags mt, tags t WHERE m.id = mt.movieID AND mt.tagID = t.id AND t.value LIKE ?");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});

		root.getChildren().add(searchTagButtons);
	}

	//Query 7
	public void seeTopDirectors() throws SQLException{
		HBox topDirectorButtons = searchButtons();

		numEntries.setPromptText("Directed How Many Movies?");

		((ButtonBase) topDirectorButtons.getChildren().get(1)).setOnAction(e -> {
			try {
				getPopular("SELECT d.directorName FROM movie_directors AS d JOIN movies AS m ON m.id = d.movieID WHERE d.directorName IN (SELECT d.directorName FROM movie_directors AS d GROUP BY d.directorName HAVING COUNT(*) >?) ORDER BY m.rtAudienceScore LIMIT 10");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});

		root.getChildren().add(topDirectorButtons);
	}

	//Query 8
	private void seeTopActors() throws SQLException{
		HBox topActorButtons = searchButtons();

		numEntries.setPromptText("Acted in How Many Movies?");

		((ButtonBase) topActorButtons.getChildren().get(1)).setOnAction(e -> {
			try {
				getPopular("SELECT a.actorName FROM movie_actors AS a JOIN movies AS m ON m.id = a.movieID WHERE a.actorName IN (SELECT a.actorName FROM movie_actors AS a GROUP BY a.actorName HAVING COUNT(*) >?) ORDER BY m.rtAudienceScore LIMIT 10");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});

		root.getChildren().add(topActorButtons);
	}

	//Recommendations
	private void seeRecommendations(){
		HBox recommendations = new HBox(5);

		Button byGenre = new Button("By Genre");
		byGenre.setOnAction(e ->{
			try {
				ArrayList<Movie> list = new ArrayList<>();
				HashSet<String> genres = new HashSet<>();
				for(Movie m : selectedMovies) {
					PreparedStatement ps = con.prepareStatement("SELECT g.genre FROM movie_genre AS g JOIN movies AS m ON m.id = g.movieID WHERE m.title LIKE ?");
					ps.setString(1, m.getTitle());
					ResultSet rs = ps.executeQuery();
					while(rs.next()) {
						genres.add(rs.getString("g.genre"));
					}
					rs.close();
					ps.close();
				}

				for(String g : genres) {
					PreparedStatement ps = con.prepareStatement("SELECT DISTINCT id, title, year, rtAudienceScore, rtPictureURL, imdbPictureURL, rtAudienceScore FROM movies where id IN(SELECT movieID FROM movie_genre WHERE genre = ?) ORDER BY movies.rtAudienceScore DESC LIMIT 5");
					ps.setString(1, g);
					ResultSet rs = ps.executeQuery();
					while(rs.next()) {
						list.add(new Movie(rs.getInt("id"), rs.getString("title"), rs.getInt("year"), rs.getInt("rtAudienceScore"), rs.getString("rtPictureURL"), rs.getString("imdbPictureURL")));
					}
					rs.close();
					ps.close();
				}
				setPages(list);
				
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		byGenre.setMinSize(150, 25);


		Button byDirector = new Button("By Director");
		byDirector.setOnAction(e -> {
			try {
				ArrayList<Movie> list = new ArrayList<>();
				HashSet<String> directors = new HashSet<>();
				for(Movie m : selectedMovies) {
					PreparedStatement ps = con.prepareStatement("SELECT d.directorName FROM movie_directors AS d JOIN movies AS m ON m.id = d.movieID WHERE m.title LIKE ?");
					ps.setString(1, m.getTitle());
					ResultSet rs = ps.executeQuery();
					while(rs.next()) {
						directors.add(rs.getString("d.directorName"));
					}
					rs.close();
					ps.close();
				}

				for(String d : directors) {
					PreparedStatement ps = con.prepareStatement("SELECT DISTINCT id, title, year, rtAudienceScore, rtPictureURL, imdbPictureURL, rtAudienceScore FROM movies where id IN (SELECT movieID FROM movie_directors WHERE directorName = ?) ORDER BY movies.rtAudienceScore DESC LIMIT 5");
					ps.setString(1, d);
					ResultSet rs = ps.executeQuery();
					while(rs.next()) {
						list.add(new Movie(rs.getInt("id"), rs.getString("title"), rs.getInt("year"), rs.getInt("rtAudienceScore"), rs.getString("rtPictureURL"), rs.getString("imdbPictureURL")));
					}
					rs.close();
					ps.close();
				}
				setPages(list);
				
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		byDirector.setMinSize(150, 25);

		recommendations.getChildren().addAll(byGenre, byDirector);
		recommendations.setAlignment(Pos.CENTER);
		root.getChildren().add(recommendations);
	}

	public void getMoviesSPM(String query) throws SQLException{
		ArrayList<Movie> list = new ArrayList<>();
		PreparedStatement ps = con.prepareStatement(query);
		ps.setString(1, "%"+numEntries.getText()+"%");
		ResultSet rs = ps.executeQuery();

		while(rs.next()) {
			list.add(new Movie(rs.getInt("id"), rs.getString("title"), rs.getInt("year"), rs.getInt("rtAudienceScore"), rs.getString("rtPictureURL"), rs.getString("imdbPictureURL")));
		}

		rs.close();
		ps.close();

		setPages(list);
	}

	public void getPopular(String query) throws SQLException {
		ObservableList<String> list =  FXCollections.observableArrayList();

		PreparedStatement ps = con.prepareStatement(query);
		ps.setInt(1, Integer.parseInt(numEntries.getText()));

		ResultSet rs = ps.executeQuery();

		while(rs.next()) {
			list.add(rs.getString(1));
		}

		rs.close();
		ps.close();

		ListView<String> listView = new ListView<String>(list);
		listView.setMaxSize(150, 232);

		try {
			root.getChildren().remove(3);
		} catch (Exception e) {

		}
		root.getChildren().add(listView);
	}

	private HBox searchButtons() {
		HBox buttons = new HBox(5);
		numEntries = new TextField();
		numEntries.setMaxSize(150, 25);
		numEntries.setMinSize(150, 25);

		Button search = new Button("Search");
		search.setMaxSize(150, 25);
		search.setMinSize(150, 25);

		buttons.getChildren().addAll(numEntries,search);
		buttons.setAlignment(Pos.CENTER);

		return buttons;
	}

	private void setPages(ArrayList<Movie> list) {
		try {
			root.getChildren().remove(3);
		} catch (Exception e) {

		}
		int elementsPerPage = 5;
		int pages = list.size()/elementsPerPage;
		if(list.size()%elementsPerPage != 0) {
			pages++;
		}

		Pagination page = new Pagination(pages,0);
		page.setPageFactory(new Callback<Integer, Node>() {
			public Node call(Integer pageIndex) {
				HBox movieList = new HBox(5);
				int currPage = pageIndex*elementsPerPage;
				for(int i=currPage; i < currPage+elementsPerPage; i++){
					if(i < list.size()) {
						final Movie fMovie = list.get(i);
						if(selectedMovies.contains(fMovie)) {
							fMovie.getMovie().setStyle("-fx-padding: 2;" +
		                      		"-fx-border-style: solid inside;" +
		                      		"-fx-border-width: 2;" +
		                      		"-fx-border-insets: 5;" +
		                      		"-fx-border-radius: 5;" +
									"-fx-border-color: blue;");
						}
						fMovie.getMovie().setOnMouseClicked(e -> {
							if(!selectedMovies.contains(fMovie) ) {
								selectedMovies.add(fMovie);
								fMovie.getMovie().setStyle("-fx-padding: 2;" +
					                      		"-fx-border-style: solid inside;" +
					                      		"-fx-border-width: 2;" +
					                      		"-fx-border-insets: 5;" +
					                      		"-fx-border-radius: 5;" +
												"-fx-border-color: blue;");
							} else {
								selectedMovies.remove(fMovie);
								fMovie.getMovie().setStyle(null);
							}
						});
						fMovie.getMovie().setOnMouseEntered(e -> {
							fMovie.getMovie().setScaleX(1.2);
							fMovie.getMovie().setScaleY(1.2);

						});
						fMovie.getMovie().setOnMouseExited(e -> {
							fMovie.getMovie().setScaleX(1);
							fMovie.getMovie().setScaleY(1);
						});

						fMovie.getMovie().setPadding(new Insets(50,0,0,0));
						movieList.getChildren().add(fMovie.getMovie());
					} else {
						movieList.getChildren().addAll(new Movie().getMovie());
					}
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