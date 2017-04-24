package SySoft;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.sql.*;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javax.imageio.ImageIO;

public class SySoft extends Application 
{
    private Text t;
    private ArrayList<Label> labels;
    private ArrayList<TextField> textfields;
    private Button search;
    private Button insert;
    private DbController c;
    private BorderPane root;
    private GridPane grid;
    private GridPane gr;
    private ScrollPane scroll;
    private VBox box;
    private VBox b;
    private Button bt1;
    private Button bt2;
    private Button bt3;
    private Button modify;
    private Button delete;
    private Button refresh;
    private Button update;
    private Button qrcode;
    private ObservableList<ObservableList> data;
    private TableView tableview;
    private String id;
    private MenuItem newAttribute;
    private MenuItem deleteAttribute;
    private Menu menu1;
    private Menu menu2;
    private ToggleGroup group1;
    private ToggleGroup group2;
    private MenuBar menuBar1;
    private MenuBar menuBar2;
    private String radio1;
    private String radio2;
    
    @Override
    public void start(Stage primaryStage) throws ClassNotFoundException, SQLException, URISyntaxException
    {
        primaryStage.setTitle("SySoft 1.0.0");
        c = new DbController();
        root = new BorderPane();
        grid = new GridPane();
        box = new VBox();
        scroll = new ScrollPane();
        MenuBar menu = new MenuBar();
        Menu menuFile = new Menu("Option");
        RadioMenuItem startScreenMenuItem = new RadioMenuItem("Start Screen");
        RadioMenuItem insertMenuItem = new RadioMenuItem("New Record");
        RadioMenuItem searchMenuItem = new RadioMenuItem("Search");
        RadioMenuItem showAllMenuItem = new RadioMenuItem("Total of Records");
        startScreenMenuItem.setSelected(true);
        menuFile.getItems().addAll(startScreenMenuItem, new SeparatorMenuItem(), insertMenuItem, searchMenuItem, showAllMenuItem);
        Menu menuEdit = new Menu("More");
        MenuItem exitMenuItem = new MenuItem("Exit");
        exitMenuItem.setOnAction(actionEvent -> Platform.exit());
        MenuItem about = new MenuItem("About");
        Menu columnsedit = new Menu("Modification of fields");
        newAttribute = new MenuItem("New field");
        deleteAttribute = new MenuItem("Delete field");
        columnsedit.getItems().addAll(newAttribute, deleteAttribute);
        menuEdit.getItems().addAll(columnsedit, new SeparatorMenuItem(), about, new SeparatorMenuItem(), exitMenuItem);
        menu.getMenus().addAll(menuFile, menuEdit);
        insert = new Button("Insert");
        search = new Button("Search");
        modify = new Button("Modify");
        delete = new Button("Delete");
        refresh = new Button("Refresh");
        update = new Button("Update");
        qrcode = new Button("QRCode");
        bt1 = new Button("New Record");
        bt1.setPrefSize(220, 60);
        bt2 = new Button("Search Record");
        bt2.setPrefSize(220, 60);
        bt3 = new Button("Total of Records");
        bt3.setPrefSize(220, 60);
        qrcode.setPrefWidth(150);
        qrcode.setPrefHeight(10);
        modify.setPrefSize(qrcode.getPrefWidth(), qrcode.getPrefHeight());
        delete.setPrefSize(qrcode.getPrefWidth(), qrcode.getPrefHeight());
        refresh.setPrefSize(qrcode.getPrefWidth(), qrcode.getPrefHeight());
        labels = new ArrayList<Label>();
        textfields = new ArrayList<TextField>();
        root.setTop(menu);
        Scene scene = new Scene(root, 1350, 700);
        primaryStage.setScene(scene);
        scene.getStylesheets().add(SySoft.class.getResource("SySoft.css").toExternalForm());
        grid.getStyleClass().add("grid");
        primaryStage.show();
        startScreenMenuItem.setOnAction((ActionEvent e) ->
        {
            cleanScreen();
            startScreenMenuItem.setSelected(true);
            searchMenuItem.setSelected(false);
            insertMenuItem.setSelected(false);
            showAllMenuItem.setSelected(false);
            grid.setMaxSize(400, 380);
            grid.setMinSize(400, 380);
            grid.add(bt1, 0, 0);
            grid.add(bt2, 0, 1);
            grid.add(bt3, 0, 2);
            BorderPane.setAlignment(grid, Pos.CENTER);
            grid.setAlignment(Pos.CENTER);
            root.setCenter(grid);
        });
        startScreenMenuItem.fire();
        searchMenuItem.setOnAction((ActionEvent e) -> 
        {
            cleanScreen();
            TextField boo;
            grid.setMaxSize(300, 280);
            startScreenMenuItem.setSelected(false);
            searchMenuItem.setSelected(true);
            insertMenuItem.setSelected(false);
            showAllMenuItem.setSelected(false);
            t = new Text("Record Search");
            t.setId("welcome");
            grid.add(t, 0, 0, 2, 1);
            try {
                grid.add(reSearch1(), 0, 1);
            } catch (SQLException ex) {
                Logger.getLogger(SySoft.class.getName()).log(Level.SEVERE, null, ex);
            }
            boo = new TextField();
            boo.setText(null);
            textfields.add(boo);
            grid.add(boo, 1, 1);
            try {
                grid.add(reSearch2(), 0, 2);
            } catch (SQLException ex) {
                Logger.getLogger(SySoft.class.getName()).log(Level.SEVERE, null, ex);
            }
            boo = new TextField();
            boo.setText(null);
            textfields.add(boo);
            grid.add(boo, 1, 2);
            box.getChildren().add(search);
            search.setDefaultButton(true);
            box.setAlignment(Pos.BOTTOM_RIGHT);
            box.setStyle(null);
            grid.add(box, 1, 3);
            root.setCenter(grid);
            root.setLeft(null);
        });
        insertMenuItem.setOnAction((ActionEvent e) -> 
        {
            try {
                if (c.reColumnsNames().length == 1)
                    startScreenMenuItem.fire();
                else
                {
                    cleanScreen();
                    gr = new GridPane();
                    b = new VBox();
                    int column = -1, row = -1;
                    b.setAlignment(Pos.CENTER);
                    Label foo;
                    TextField boo;
                    scroll.setMaxSize(800, 650);
                    scroll.getStyleClass().add("scroll");
                    gr.setMaxSize(scroll.getMaxWidth(), scroll.getMaxHeight());
                    gr.getStyleClass().add("gr");
                    scene.getStylesheets().add(SySoft.class.getResource("SySoft.css").toExternalForm());
                    searchMenuItem.setSelected(false);
                    insertMenuItem.setSelected(true);
                    showAllMenuItem.setSelected(false);
                    startScreenMenuItem.setSelected(false);
                    t = new Text("Record Insertion");
                    t.setId("welcome");
                    b.getChildren().add(t);
                    gr.add(b, 0, 0, 10, 1);
                    try {
                        column = 0;
                        row = 2;
                        for(int i=1;i<c.reColumnsNames().length;i++)
                        {
                            foo = new Label(c.reColumnsNames()[i] + ":");
                            labels.add(foo);
                            if (column == 0)
                            {
                                gr.add(foo, column, row);
                                column = 2;
                            }
                            else if(column == 2)
                            {
                                gr.add(foo, column, row);
                                column = 4;
                            }
                            else
                            {
                                gr.add(foo, column, row);
                                column = 0;
                                row++;
                            }
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(SySoft.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    try {
                        column = 1; 
                        row = 2;
                        for(int i=1;i<c.reColumnsNames().length; i++)
                        {
                            boo = new TextField();
                            boo.setMaxWidth(100);
                            textfields.add(boo);
                            if (column == 1)
                            {
                                gr.add(boo, column, row);
                                column = 3;
                            }
                            else if(column == 3)
                            {
                                gr.add(boo, column, row);
                                column = 5;
                            }
                            else
                            {
                                gr.add(boo, column, row);
                                column = 1;
                                row++;
                            }
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(SySoft.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    for(int i=0; i<labels.size(); i++)
                    {
                        StringBuilder sb = new StringBuilder(labels.get(i).getText());
                        sb.deleteCharAt(labels.get(i).getText().length() - 1);
                        Tooltip tooltip = new Tooltip();
                        tooltip.setText(sb.toString());
                        labels.get(i).setTooltip(tooltip);
                        int point = i;
                        labels.get(i).setOnMouseClicked((MouseEvent t1) -> 
                        {
                            TextInputDialog dialog = new TextInputDialog();
                            dialog.setHeaderText(null);
                            dialog.setTitle("Rename field");
                            dialog.setContentText("New name of field: ");
                            Optional<String> result = dialog.showAndWait();
                            if (result.isPresent())
                            {
                                try {
                                    String a = result.get();
                                    c.renameColumn(point, a);
                                    insertMenuItem.fire();
                                } 
                                catch (SQLException ex) 
                                {
                                        Logger.getLogger(SySoft.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        });
                    }
                    box.getChildren().add(insert);
                    insert.setDefaultButton(true);
                    box.setAlignment(Pos.BOTTOM_RIGHT);
                    box.setStyle(null);
                    gr.add(box, 5, row+1);
                    scroll.setContent(null);
                    scroll.setContent(gr);
                    root.setCenter(scroll);
                }
            } catch (SQLException ex) {
                Logger.getLogger(SySoft.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        showAllMenuItem.setOnAction((ActionEvent e) ->
        {
            cleanScreen();
            searchMenuItem.setSelected(false);
            insertMenuItem.setSelected(false);
            showAllMenuItem.setSelected(true);
            startScreenMenuItem.setSelected(false);
            try {
                showResults(c.showAll());
            } catch (SQLException ex) {
                Logger.getLogger(SySoft.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        newAttribute.setOnAction((ActionEvent e) ->
        {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setHeaderText(null);
            dialog.setTitle("New field");
            dialog.setContentText("Name of field: ");
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent())
                try {
                    String a = result.get();
                    c.newAttribute(a);
            } catch (SQLException ex) {
                Logger.getLogger(SySoft.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        deleteAttribute.setOnAction((ActionEvent e) -> 
        {
            try {
                if(c.reColumnsNames().length > 4)
                {
                    int point = -1;
                    do
                    {
                        TextInputDialog dialog = new TextInputDialog(); 
                        dialog.setHeaderText(null);
                        dialog.setTitle("Field deletion");
                        dialog.setContentText("Name of field: ");
                        Optional<String> result = dialog.showAndWait();
                        if (result.isPresent())
                        {
                            try
                            {
                                for(int i=0; i<c.reColumnsNames().length; i++)
                                {
                                    String name = c.reColumnsNames()[i];
                                    String name1 = result.get();
                                    if (name1.equals(name))
                                    {
                                        point = i;
                                    }
                                }
                            } catch (SQLException ex) {
                                Logger.getLogger(SySoft.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        else
                            break;
                        dialog.close();
                        if (point == -1)
                        {
                            Alert alert = new Alert(AlertType.WARNING);
                            alert.setTitle("Caution");
                            alert.setHeaderText(null);
                            alert.setContentText("There is no field with this name");
                            alert.showAndWait();
                        }
                    }while(point == -1);
                    try {
                        if (point != -1)
                            c.deleteAttribute(point);
                    } catch (SQLException ex) {
                        Logger.getLogger(SySoft.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                else
                {
                    Alert alert = new Alert(AlertType.WARNING);
                    alert.setTitle("Caution");
                    alert.setHeaderText(null);
                    alert.setContentText("There can not be less than 3 fields");
                    alert.showAndWait();
                }
            } catch (SQLException ex) {
                Logger.getLogger(SySoft.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        bt1.setOnAction((ActionEvent e) -> 
        {
            insertMenuItem.fire();
        });
        bt2.setOnAction((ActionEvent e) -> 
        {
            searchMenuItem.fire();
        });
        bt3.setOnAction((ActionEvent e) ->
        {
            showAllMenuItem.fire();
        });
        insert.setOnAction((ActionEvent e)->
        {
            int counter = 0;
            for(TextField r : textfields)
            {
                if (r.getText().equals(""))
                    counter++;
            }
            if (counter != textfields.size())
            {
                int y = -1;
                try {
                    y = c.insertExecutor(textfields);
                } catch (SQLException ex) {
                    Logger.getLogger(SySoft.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (y == 1)
                {
                    for(int i=0; i<textfields.size(); i++)
                        textfields.get(i).clear();
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setHeaderText(null);
                    alert.setTitle("Information");
                    alert.setContentText("The insertion was successful");
                    alert.showAndWait();
                }
                else
                {
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setHeaderText(null);
                    alert.setTitle("Information");
                    alert.setContentText("The insertion was unsuccessful");
                    alert.showAndWait();
                }
            }
            else
            {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setTitle("Information");
                alert.setContentText("Please fill at least one field");
                alert.showAndWait();
                insertMenuItem.fire();
            }
        });
        search.setOnAction((ActionEvent e)->
        {
            try {
                if (((textfields.get(0).getText() == null)&&
                        (null == textfields.get(1).getText()))||
                        ((textfields.get(0).getText().isEmpty())
                        &&(null == textfields.get(1).getText()))||
                        ((null == textfields.get(0).getText())
                        &&(textfields.get(1).getText().isEmpty()))||
                        ((textfields.get(0).getText().isEmpty()))
                        &&(textfields.get(1).getText().isEmpty()))
                {
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Caution");
                    alert.setHeaderText(null);
                    alert.setContentText("Please fill at least one field");
                    alert.showAndWait();
                }
                else if((group1.getSelectedToggle() == null)||(group2.getSelectedToggle() == null))
                {
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Caution");
                    alert.setHeaderText(null);
                    alert.setContentText("Please check 2 search options");
                    alert.showAndWait();
                }
                else
                {
                    showResults(c.searchExecutor(radio1, radio2, textfields.get(0).getText(), textfields.get(1).getText()));
                    box.getChildren().remove(refresh);
                    VBox.setMargin(qrcode, new Insets(0, 0, 0, 0));
                }
            } catch (SQLException ex) {
                Logger.getLogger(SySoft.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        about.setOnAction((ActionEvent e) -> 
        {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText("Creator: Karapostolakis Sotirios\nemail: skarapos@outlook.com\nYear: 2017");
            alert.showAndWait();
        });
        modify.setOnAction((ActionEvent e) ->
        {
            ObservableList row = (ObservableList) tableview.getSelectionModel().getSelectedItem();
            newAttribute.setDisable(true);
            deleteAttribute.setDisable(true);
            if (row != null)
            {
                insertMenuItem.fire();
                for(int i=0; i<textfields.size(); i++)
                    textfields.get(i).setText((String) row.get(i+1));
                t.setText("Update Record");
                box.getChildren().remove(insert);
                box.getChildren().add(update);
                update.setDefaultButton(true);
                id = (String) row.get(0);
            }
            else
                caution();
        });
        delete.setOnAction((ActionEvent e) ->
        {
            ObservableList row = (ObservableList) tableview.getSelectionModel().getSelectedItem();
            if (row != null)
            {
                try {
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Deletion Confirmation");
                    alert.setHeaderText(null);
                    alert.setContentText("Are you sure for the deletion?");
                    alert.showAndWait();
                    if (alert.getResult() == ButtonType.OK)
                        c.deleteExecutor(Integer.parseInt((String) row.get(0)));
                    refresh.fire();
                } catch (SQLException ex) {
                    Logger.getLogger(SySoft.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else
                caution();
        });
        refresh.setOnAction((ActionEvent e) ->
        {
            showAllMenuItem.fire();
        });
        update.setOnAction((ActionEvent e) -> 
        {
            
            newAttribute.setDisable(false);
            deleteAttribute.setDisable(false);
            try {
                if (c.updateExecutor(textfields, id) == 1)
                {
                    for(int i=0; i<textfields.size(); i++)
                        textfields.get(i).clear();
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setHeaderText(null);
                    alert.setTitle("Information");
                    alert.setContentText("The update was successful");
                    alert.showAndWait();
                    searchMenuItem.fire();
                }
                else
                {
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setHeaderText(null);
                    alert.setTitle("Information");
                    alert.setContentText("The update was unsuccessful");
                    alert.showAndWait();
                    searchMenuItem.fire();
                }
            } catch (SQLException ex) {
                Logger.getLogger(SySoft.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        qrcode.setOnAction((ActionEvent e) ->
        {
            ObservableList row = (ObservableList) tableview.getSelectionModel().getSelectedItem();
            if (row != null)
            {
                String columns[] = null;
                String myCodeText = "";
                try {
                   columns = c.reColumnsNames();
                } catch (SQLException ex) {
                   Logger.getLogger(SySoft.class.getName()).log(Level.SEVERE, null, ex);
                }
                for(int i=1; i<row.size(); i++)
                   myCodeText = myCodeText + columns[i] + ": " + (String) row.get(i) + "\n";
                int size = 250;
                String fileType = "png";
                try
                {
                    Map<EncodeHintType, Object> hintMap = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
                    hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
                    hintMap.put(EncodeHintType.MARGIN, 1);
                    hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
                    QRCodeWriter qrCodeWriter = new QRCodeWriter();
                    BitMatrix byteMatrix = qrCodeWriter.encode(myCodeText, BarcodeFormat.QR_CODE, size, size, hintMap);
                    int width = byteMatrix.getWidth();
                    BufferedImage image = new BufferedImage(width, width, BufferedImage.TYPE_INT_RGB);
                    image.createGraphics();
                    Graphics2D graphics = (Graphics2D) image.getGraphics();
                    graphics.setColor(Color.WHITE);
                    graphics.fillRect(0, 0, width, width);
                    graphics.setColor(Color.BLACK);
                    for (int i = 0; i < width; i++) 
                    {
                        for (int j = 0; j < width; j++) 
                        {
                            if (byteMatrix.get(i, j))
                                graphics.fillRect(i, j, 1, 1);
                        }
                    }
                    FileChooser fileChooser = new FileChooser();
                    FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("IMAGE file (*.png)","*.png");
                    fileChooser.setInitialFileName((String) row.get(0));
                    fileChooser.getExtensionFilters().add(extFilter);
                    File myFile = fileChooser.showSaveDialog(primaryStage);
                    if (myFile != null)
                        ImageIO.write(image, fileType, myFile);
                }
                catch (WriterException | IOException y)
                {
                    y.printStackTrace();
                }
            }
            else
                caution();
        });
    }
    public void showResults(ResultSet u) throws SQLException
    {
        cleanScreen();
        tableview = new TableView();
        tableview.getStyleClass().add("tableview");
        tableview.setMaxSize(900, 600);
        data = FXCollections.observableArrayList();
        for(int i = 1 ; i < u.getMetaData().getColumnCount(); i++)
        {
	    final int j = i;               
	    TableColumn col = new TableColumn(u.getMetaData().getColumnName(i+1));
	    col.setCellValueFactory(new CallbackImpl(j));
            tableview.getColumns().addAll(col);
	}
        while(u.next())
        {
            ObservableList<String> row = FXCollections.observableArrayList();
	    for(int i=1 ; i <= u.getMetaData().getColumnCount(); i++)
                row.add(u.getString(i));
	    data.add(row);
        }
        tableview.setItems(data);
        root.setCenter(tableview);
        box.getChildren().addAll(modify, delete, qrcode);
        box.setMaxSize(300, 140);
        BorderPane.setAlignment(box, Pos.CENTER);
        box.setAlignment(Pos.CENTER);
        box.getStyleClass().add("box");
        box.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 5, 0.0, 0, 1);");
        BorderPane.setMargin(box, new Insets(0, -20, 0, 65));
        VBox.setMargin(modify, new Insets(0, 0, 15, 0));
        VBox.setMargin(delete, new Insets(0, 0, 15, 0));
        root.setLeft(box);
    }
    public void caution()
    {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Caution");
        alert.setHeaderText(null);
        alert.setContentText("Please select a row");
        alert.showAndWait();
    }
    public void cleanScreen()
    {
        labels.clear();
        textfields.clear();
        grid.getChildren().clear();
        box.getChildren().clear();
        root.setCenter(null);
        root.setLeft(null);
        newAttribute.setDisable(false);
        deleteAttribute.setDisable(false);
    }
    public MenuBar reSearch1() throws SQLException
    {
        menuBar1 = new MenuBar();
        menu1 = new Menu("Search Options");
        group1 = new ToggleGroup();
        for(int i=1; i<c.reColumnsNames().length; i++)
        {
            RadioMenuItem radio = new RadioMenuItem(c.reColumnsNames()[i]);
            radio.setOnAction((ActionEvent e)->
            {
                radio1 = radio.getText();
            });
            menu1.getItems().add(radio);
            radio.setToggleGroup(group1);
        }
        menuBar1.getMenus().add(menu1);
        return menuBar1;
    }
    public MenuBar reSearch2() throws SQLException
    {
        menuBar2 = new MenuBar();
        menu2 = new Menu("Search Options");
        group2 = new ToggleGroup();
        for(int i=1; i<c.reColumnsNames().length; i++)
        {
            RadioMenuItem radio = new RadioMenuItem(c.reColumnsNames()[i]);
            radio.setOnAction((ActionEvent e)->
            {
                radio2 = radio.getText();
            });
            menu2.getItems().add(radio);
            radio.setToggleGroup(group2);
        }
        menuBar2.getMenus().add(menu2);
        return menuBar2;
    }
    public static void main(String[] args) throws ClassNotFoundException, SQLException 
    {
        launch(args);
    }
    private static class CallbackImpl implements Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>> 
    {
        private final int j;
        public CallbackImpl(int j) 
        {
            this.j = j;
        }
        @Override
        public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param)
        {
            return new SimpleStringProperty(param.getValue().get(j).toString());
        }
    }
}