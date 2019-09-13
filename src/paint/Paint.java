/**
 * Paint Project
 * CS 250 Object Oriented Programming
 * @author Luke Weer
 */
package paint;

import javafx.scene.paint.Color;
import java.awt.image.*;
import java.io.*;
import java.net.MalformedURLException;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.*;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.image.*;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javax.imageio.ImageIO;

/**
 * Paint Project
 *
 * @author lukew
 */
public class Paint extends Application {
    
//All variables
    private MenuBar menu_bar;
    private Menu file_menu, help_menu;
    private MenuItem open_file, save_as, save, snap_draw, help;
    private File file;
    private Image image;
    private ImageView image_view;
    private GraphicsContext graphic;
    private VBox top_menu;
    private Scene scene;
    private Line line;
    private StackPane stack_pane;
    private FileChooser file_chooser;
    private Stage primaryStage;
    private Label label;
    private Canvas canvas;
    private WritableImage writeable_img;
    private FlowPane modification_pane, color_pane;
    private StackPane image_pane;
    private BorderPane full_pane;
    private ColorPicker color_picker;
    private double x, y;
    private Slider slider;
    private double slider_value;
    private ScrollBar h_bar, v_bar;
    private Popup popup;

    @Override
    public void start(Stage primaryStage) {
        
    //Setting scene and menu bar
        menu_bar = new MenuBar(); //Menu Bar
        menu_bar.setLayoutX(0);
        menu_bar.setLayoutY(0);
        file_menu = new Menu("File"); //Menu Header
        open_file = new MenuItem("Open File"); //File Selections
        open_file.setAccelerator(KeyCombination.keyCombination("Ctrl+Shift+O")); //Keyboard shortcut
        open_file.setOnAction(open);
        save_as = new MenuItem("Save As");
        save_as.setOnAction(saveas);
        save = new MenuItem("Save");
        save.setAccelerator(KeyCombination.keyCombination("Ctrl+Shift+S"));
        save.setOnAction(savefile);
        help_menu = new Menu("Help");
        help = new MenuItem("Help");
        help.setOnAction(help_me);
        file_menu.getItems().addAll(open_file, save, save_as); 
        help_menu.getItems().add(help);
        menu_bar.getMenus().addAll(file_menu, help_menu);
        
    //Color settings
        color_picker = new ColorPicker();
        color_picker.setValue(Color.BLACK);
        //color_picker.setOnAction(color_switch);
        
    //Line layout
        slider = new Slider();  //creation of slider for width adjustment
        slider.setMin(1);   //allows value to be set 1-15
        slider.setMax(15);  
        label = new Label("1.0"); //default value of slider is 1
        
    //Image layout
        image_view = new ImageView();
        image_view.setLayoutX(10);
        image = new Image("https://www.publicdomainpictures.net/pictures/30000/nahled/plain-white-background.jpg");
        image_view.setImage(image);
        
    //Line selection
        MenuButton line_choice = new MenuButton("Draw Choice");
        snap_draw = new MenuItem("Snap Draw");
        line_choice.getItems().add(snap_draw);
        snap_draw.setOnAction(snapDrawAction);
 
    //Scene layout
        modification_pane = new FlowPane(line_choice, slider, color_picker);    //Top pane tools
        modification_pane.setHgap(20);
        modification_pane.setVgap(10);
        modification_pane.setAlignment(Pos.CENTER);
        full_pane = new BorderPane();    //fullPane is everything together
        canvas = new Canvas(500,500);   //overlays image and can be drawn on
        graphic = canvas.getGraphicsContext2D();
        image_pane = new StackPane(image_view, canvas);
        top_menu = new VBox(menu_bar);    //pane for menubar at top
        full_pane.setTop(top_menu);
        full_pane.setBottom(image_pane);
        modification_pane.autosize();
        full_pane.setCenter(modification_pane);
        ScrollPane scroll_pane = new ScrollPane(full_pane);
        scroll_pane.setHbarPolicy(ScrollBarPolicy.ALWAYS);
        scroll_pane.setVbarPolicy(ScrollBarPolicy.ALWAYS);

        scene = new Scene(scroll_pane, 500, 600);
        
        
    //Stage layout    
        primaryStage.setTitle("Luke Weber - Paint Project - CS250");
        primaryStage.setScene(scene);
        primaryStage.show();                   
    }
    
    /**
     * Allows user to open an image of their choosing
     * 
     * @author Luke Weber 9/3/2019
     */
    EventHandler<ActionEvent> open = new EventHandler<ActionEvent>(){
        @Override
        public void handle(ActionEvent t){              
            try{
                FileChooser chooser = new FileChooser();    //Creates file chooser
                FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");   //only jpg and png extensions
                FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
                chooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);
                chooser.setTitle("Open Image");
                file = chooser.showOpenDialog(new Stage()); //opens new dialog box
                image = new Image(file.toURI().toURL().toString()); //sets the image
                image_view.setImage(image);    //displays image on canvas
            }
            catch(MalformedURLException ex){
                //error
            }
        }            
    };
    
    
    /**
     * Allows user to save image to their directory
     * 
     * @author Luke Weber 9/4/2019
    */
    EventHandler<ActionEvent> saveas = new EventHandler<ActionEvent>(){
        @Override
        public void handle(ActionEvent t) {
            image_view.setImage(image);
            FileChooser fileChooser = new FileChooser();        //creates new filechooser
            FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");   //Set extension filters to jpg and png
            FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
            fileChooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);
            fileChooser.setTitle("Save Image");
            File file = fileChooser.showSaveDialog(new Stage());    //opens the saveas box
            if (file != null) {     //if file choosen exists save it
                try {
                    WritableImage writableImage = new WritableImage((int)image.getWidth(),(int)image.getHeight());
                    image_pane.snapshot(null, writableImage);        //takes snapshot of current imagePane
                    RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
                    ImageIO.write(renderedImage, "png", file);  //saves image out to choosen file
                }catch(IOException ex) {
                    //error
                }
            }
        }
    };
    
    /**
     * Allows user to save image to their directory
     * 
     * @author Luke Weber 9/4/2019
    */
    EventHandler<ActionEvent> savefile = new EventHandler<ActionEvent>(){
        @Override
        public void handle(ActionEvent t) {
            image_view.setImage(image);
            if (file != null) {     //checks if file path exists
                try {
                    WritableImage writableImage = new WritableImage((int)image.getWidth(),(int)image.getHeight());
                    image_pane.snapshot(null, writableImage);    //takes snapshot of the current imagePane
                    RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
                    ImageIO.write(renderedImage, "png", file);      //saves image to choose file path
                }catch(IOException ex) {
                    //error
                }
            }
        }
    };
    
    /**
     * Allows user to draw a snap line
     * 
     * @author Luke Weber 9/11/2019
    */
    EventHandler<ActionEvent> snapDrawAction = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent me) {
            canvas.setOnMousePressed((MouseEvent snapAction) -> {
                //undoStack.push(saveUndoImage());        //saves image to undoStack
                graphic.beginPath();
                x = snapAction.getSceneX();     //saves clicked X and Y values
                y = snapAction.getSceneY();
            });
            canvas.setOnMouseDragged(null);
            canvas.setOnMouseReleased((MouseEvent snapAction) -> {
                //draws line from clicked X and Y values to current X and Y values
                graphic.strokeLine(x, y-100, snapAction.getSceneX(), snapAction.getSceneY()-100);
                graphic.stroke();
            });
        }
    };
    
    /**
     * Allows user to open a pop-up for help/about
     * 
     * @author Luke Weber 9/11/2019
    */
    EventHandler<ActionEvent> help_me = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent e) { 
            StackPane root2 = new StackPane();
            Label label = new Label("HELP / ABOUT");
            root2.getChildren().add(label);
            Scene secondScene = new Scene(root2, 400,300);
            Stage secondStage = new Stage();
            secondStage.setScene(secondScene); // set the scene
            secondStage.setTitle("Help...");
            secondStage.show();
            primaryStage.close(); // close the first stage (Window)
        } 
    };
            

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    } 
    
}
