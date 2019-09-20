/**
 * Paint Project
 * CS 250 Object Oriented Programming
 * @author Luke Weer
 */
package paint;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import java.io.File;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import java.awt.image.*;
import java.io.*;
import java.net.MalformedURLException;
import java.util.Optional;
import java.util.Stack;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.image.*;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.WindowEvent;
import javax.imageio.ImageIO;

/**
 * Paint Project
 *
 * @author lukew
 */
public class Paint extends Application {
    private final Stack<Image> undoStack = new Stack();
    private final Stack<Image> redoStack = new Stack();
    
//All variables
    private MenuBar menu_bar;
    private Menu file_menu, help_menu, edit_menu;
    private MenuItem open_file, save_as, save, snap_draw, freehand_draw,
            square_draw, circle_draw, elipse_draw, help, undo, color_select;
    private File file;
    private Image image;
    private ImageView image_view;
    private GraphicsContext graphic;
    private VBox top_menu;
    private Scene scene;
    private Alert alert;
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
    private ColorPicker color_picker, color_dropper;
    private double x, y;
    private Slider slider;
    private double slider_value;
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
        save_as = new MenuItem("Save As");
        save = new MenuItem("Save");
        help_menu = new Menu("Help");
        edit_menu = new Menu("Edit");
        help = new MenuItem("Help");
        undo = new MenuItem("Undo");
        save.setAccelerator(KeyCombination.keyCombination("Ctrl+Shift+S"));
        help.setAccelerator(KeyCombination.keyCombination("Ctrl+Shift+h"));
        undo.setAccelerator(KeyCombination.keyCombination("Ctrl+z"));
        color_select = new MenuItem("Color Dropper");
        file_menu.getItems().addAll(open_file, save, save_as); 
        help_menu.getItems().add(help);
        edit_menu.getItems().addAll(undo, color_select);
        
    //Line layout
        slider = new Slider();  //creation of slider for width adjustment
        slider.setMin(1);   //allows value to be set 1-15
        slider.setMax(15);  
        label = new Label("1.0"); //default value of slider is 1
        
    //Line selection
        MenuButton line_choice = new MenuButton("Draw Choice");
        snap_draw = new MenuItem("Snap Draw");
        freehand_draw = new MenuItem("Freehand Draw");
        square_draw = new MenuItem("Square Draw");
        circle_draw = new MenuItem("Circle Draw");
        elipse_draw = new MenuItem("Elipse Draw");
        line_choice.getItems().addAll(snap_draw, freehand_draw, square_draw, circle_draw, elipse_draw);
        
 
    //Scene layout
        canvas = new Canvas(500,500);   //cavas to be drawn on 
        graphic = canvas.getGraphicsContext2D();
        
        ToolBar tool_bar = new ToolBar();
        color_picker = new ColorPicker();
        color_picker.setValue(Color.BLACK);
        tool_bar.getItems().addAll(color_picker, line_choice, slider);
        
        menu_bar.getMenus().addAll(file_menu, edit_menu, help_menu);
        
        ScrollPane scroll_pane = new ScrollPane();
        //scroll_pane.setPannable(true);
        Image new_image = new Image("http://www.russellandtate.com/blog/wp-content/uploads/2014/02/2015-03-31-blank-white-square-500x500.png");
        graphic.drawImage(new_image,0,0);
        
        VBox root = new VBox();
        VBox img_container = new VBox();
        
        img_container.getChildren().add(canvas);
        scroll_pane.setContent(img_container);
       
        root.getChildren().addAll(menu_bar, tool_bar, scroll_pane);

        scene = new Scene(root, 500, 500);
        
        
    //Stage layout    
        primaryStage.setTitle("Luke Weber - Paint Project - CS250");
        primaryStage.setScene(scene);
        primaryStage.show();                   
    
    
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
                    FileChooser.ExtensionFilter extFilterGIF = new FileChooser.ExtensionFilter("GIF files (*.gif)", "*.GIF");                    
                    chooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG, extFilterGIF);
                    chooser.setTitle("Open Image");
                    file = chooser.showOpenDialog(new Stage()); //opens new dialog box
                    image = new Image(file.toURI().toURL().toString()); //sets the image
                    //primaryStage.setHeight(image.getHeight() + menu_bar.getHeight());
                    //primaryStage.setWidth(image.getWidth());
                    graphic.drawImage(image, 0, 0);  //displays image on canvas
                    img_container.getChildren().add(canvas);
                    
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
                
                WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
                WritableImage snapshot = canvas.snapshot(new SnapshotParameters(), writableImage);
            
                FileChooser file_chooser = new FileChooser();        //creates new filechooser
                FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");   //Set extension filters to jpg and png
                FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
                FileChooser.ExtensionFilter extFilterGIF = new FileChooser.ExtensionFilter("GIF files (*.gif)", "*.GIF");
                file_chooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG, extFilterGIF);
                file_chooser.setTitle("Save Image");

                    
                if (file != null) {     //if file choosen exists save it
                    try {
                        ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", file_chooser.showSaveDialog(primaryStage));
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
                
                WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
                WritableImage snapshot = canvas.snapshot(new SnapshotParameters(), writableImage);
            
                if (file != null) {     //checks if file path exists
                    try {
                        ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", file);
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
        EventHandler<ActionEvent> snap_draw_action = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent me) {
                canvas.setOnMousePressed((MouseEvent snapAction) -> {
                    undoStack.push(saveUndoImage());        //saves image to undoStack
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
                Label label = new Label("Paint Replica - v0.0.0.3");
                root2.getChildren().add(label);
                Scene secondScene = new Scene(root2, 400,300);
                Stage secondStage = new Stage();
                secondStage.setScene(secondScene); // set the scene
                secondStage.setTitle("Help...");
                secondStage.show();
                //primaryStage.close(); // close the first stage (Window)
            } 
        };
        
        /**
         * Allows user to switch color for drawing brush
         * 
         * @author Luke Weber 9/18/2019
        */
        EventHandler<ActionEvent> color_switch = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) { 
                graphic.setStroke(color_picker.getValue());     //sets colorpicker to new value is changed
            } 
        };
        
        /**
         * Allows user to control width of brush
         * 
         * @author Luke Weber 9/18/2019
        */
        EventHandler<MouseEvent> slider_action = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) { 
                slider_value = slider.getValue();        //reads value from slider and saves as new width
                String str = String.format("%.1f", slider_value);
                label.setText(str);         //sets label of slider to choosen value
                graphic.setLineWidth(slider_value);
            } 
        };
        
        /**
         * Allows user to control width of brush
         * 
         * @author Luke Weber 9/18/2019
        */
        EventHandler<ActionEvent> freehand_draw_action = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) { 
                canvas.setOnMousePressed((MouseEvent freeDrawAction) -> {
                    undoStack.push(saveUndoImage());
                    graphic.beginPath();     
                    graphic.setLineJoin(StrokeLineJoin.ROUND);  //makes pixels drawn round
                    graphic.setLineCap(StrokeLineCap.ROUND);    //so lines have soft edges
                    graphic.moveTo(freeDrawAction.getX(), freeDrawAction.getY());
                    graphic.stroke();
                });
                canvas.setOnMouseDragged((MouseEvent freeDrawAction) -> {
                    graphic.lineTo(freeDrawAction.getX(), freeDrawAction.getY());
                    graphic.stroke();
                });
                canvas.setOnMouseReleased(null);
            } 
        };
        
        EventHandler<ActionEvent> square_draw_action = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent me) {
                canvas.setOnMousePressed((MouseEvent squareAction) -> {
                    undoStack.push(saveUndoImage());
                    graphic.beginPath();
                    x = squareAction.getSceneX();       //saves current X/Y values
                    y = squareAction.getSceneY();
                });
                canvas.setOnMouseDragged(null);
                canvas.setOnMouseReleased((MouseEvent squareAction) -> {
                    //draws lines on all 4 sides of the square using the saved X/Y values as well as the current X/Y values
                    graphic.strokeLine(x, y-100, squareAction.getSceneX(), y-100);
                    graphic.strokeLine(x, squareAction.getSceneY()-100, squareAction.getSceneX(), squareAction.getSceneY()-100);
                    graphic.strokeLine(x, squareAction.getSceneY()-100, x, y-100);
                    graphic.strokeLine(squareAction.getSceneX(), squareAction.getSceneY()-100, squareAction.getSceneX(), y-100);
                    graphic.stroke();
                    graphic.setFill(color_picker.getValue());
                    //graphic.fillRect(x, y, x, x);
                });
            }
        };
        
        /**
        * This method is used to allow the user to draw a circle using their 
        * mouse or track pad.
        * 
        * @author Luke Weber 09/19/2019
        */
        EventHandler<ActionEvent> circle_draw_action = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent me) {
                Circle circle = new Circle();   //creation of new circle to be drawn
                canvas.setOnMousePressed((MouseEvent circleAction) -> {
                    undoStack.push(saveUndoImage());
                    circle.setCenterX(circleAction.getX());     //sets current X/Y as center 
                    circle.setCenterY(circleAction.getY());
                });
                canvas.setOnMouseDragged(null);
                canvas.setOnMouseReleased((MouseEvent circleAction) -> {
                    //on mouse released set radius depending on which direction the mouse was dragged
                    if(Math.abs(circleAction.getY()- circle.getCenterY()) > Math.abs(circleAction.getX() - circle.getCenterX())){
                        circle.setRadius(Math.abs(circleAction.getY() - circle.getCenterY()));
                    }else{
                        circle.setRadius(Math.abs(circleAction.getX() - circle.getCenterX()));
                    }
                    graphic.strokeOval(circle.getCenterX(), circle.getCenterY(), circle.getRadius(), circle.getRadius());
                    graphic.setFill(color_picker.getValue());
                    graphic.fillOval(circle.getCenterX(), circle.getCenterY(), circle.getRadius(), circle.getRadius());                
                });
            }
        };
        
        /**
        * Allows the user to close but be prompted with smart close
        * 
        * @author Luke Weber 09/19/2019
        */
        EventHandler<WindowEvent> smart_close = new EventHandler<WindowEvent>(){
        @Override
            public void handle(WindowEvent we){
                alert = new Alert(Alert.AlertType.CONFIRMATION);      //new alert is open when X is clicked
                //alert setup information
                alert.setTitle("Smart Close");
                alert.setHeaderText("Save/Cancel/Close");
                alert.setContentText("Are you sure you want to close? Save your work first!");
                //creation of three buttons in smartClose dialog
                ButtonType one = new ButtonType("Save");
                ButtonType two = new ButtonType("Cancel");
                ButtonType three = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
                ButtonType four = new ButtonType("Save as");
                alert.getButtonTypes().setAll(one, four, two, three);
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == one){       //if save is clicked
                    WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
                    WritableImage snapshot = canvas.snapshot(new SnapshotParameters(), writableImage);

                    if (file != null) {     //checks if file path exists
                        try {
                            ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", file);
                        }catch(IOException ex) {
                            //error
                        }
                    }
                    alert.close();
                } else if (result.get() == four) {
                    WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
                    WritableImage snapshot = canvas.snapshot(new SnapshotParameters(), writableImage);

                    FileChooser file_chooser = new FileChooser();        //creates new filechooser
                    FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");   //Set extension filters to jpg and png
                    FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
                    file_chooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);
                    file_chooser.setTitle("Save Image");

                    try {
                        ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", file_chooser.showSaveDialog(primaryStage));
                    }catch(IOException ex) {
                        //error
                    }

                    alert.close();
                } else if (result.get() == two) { //if cancel is clicked
                    alert.hide();       //clears alert
                    we.consume();       //closes dialog box
                }
            }
        };
        
        /**
        * Allows the user to change color by clicking on any pixel
        * 
        * @author Luke Weber 09/19/2019
        */
        EventHandler<ActionEvent> color_dropper_action = new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {  
                canvas.setOnMouseClicked((MouseEvent event) -> {
                    WritableImage snap = graphic.getCanvas().snapshot(null, null);
                    PixelReader pixel_reader = snap.getPixelReader();
                    double xC = event.getX();
                    double yC = event.getY();
                    Color c = pixel_reader.getColor((int)xC, (int)yC);
                    color_picker.setValue(c);
                    graphic.setStroke(color_picker.getValue());     //sets colorpicker to new value is changed
                });            
            }
        };
        
        /**
        * Allows the user to undo
        * 
        * @author Luke Weber 09/19/2019
        */
        EventHandler<ActionEvent> undo_action = new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {   
                if (!undoStack.isEmpty()) {     //if the undoStack is NOT empty
                    redoStack.push(saveUndoImage());             //Saves previous state to redoStack
                    graphic.drawImage(undoStack.pop(), 0, 0);    // Draws previous image onto canvas    
                }
            }
        };
        
        /**
        * Allows the user to undo
        * 
        * @author Luke Weber 09/19/2019
        */
        EventHandler<ActionEvent> elipse_draw_action = new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {   
                Ellipse ellipse = new Ellipse();   //creation of new circle to be drawn
                canvas.setOnMousePressed((MouseEvent ellipseAction) -> {
                    undoStack.push(saveUndoImage());
                    ellipse.setCenterX(ellipseAction.getX());     //sets current X/Y as center 
                    ellipse.setCenterY(ellipseAction.getY());
                });
                canvas.setOnMouseDragged(null);
                canvas.setOnMouseReleased((MouseEvent ellipseAction) -> {
                    //on mouse released set radius depending on which direction the mouse was dragged
                    if(Math.abs(ellipseAction.getY()- ellipse.getCenterY()) > Math.abs(ellipseAction.getX() - ellipse.getCenterX())){
                        ellipse.setRadiusX(Math.abs(ellipseAction.getX() - ellipse.getCenterX()));
                    }else{
                        ellipse.setRadiusY(Math.abs(ellipseAction.getY() - ellipse.getCenterY()));
                    }
                    graphic.strokeOval(ellipse.getCenterX(), ellipse.getCenterY(), ellipse.getRadiusX(), ellipse.getRadiusY());
                    graphic.setFill(color_picker.getValue());
                    graphic.fillOval(ellipse.getCenterX(), ellipse.getCenterY(), ellipse.getRadiusX(), ellipse.getRadiusY());                
                });
            }
        };
        
        
        
        
        open_file.setOnAction(open);
        save_as.setOnAction(saveas);
        save.setOnAction(savefile);
        help.setOnAction(help_me);
        snap_draw.setOnAction(snap_draw_action);
        freehand_draw.setOnAction(freehand_draw_action);
        square_draw.setOnAction(square_draw_action);
        circle_draw.setOnAction(circle_draw_action);
        elipse_draw.setOnAction(elipse_draw_action);
        color_picker.setOnAction(color_switch);
        color_select.setOnAction(color_dropper_action);
        slider.setOnMouseReleased(slider_action);
        undo.setOnAction(undo_action);
        
        primaryStage.setOnCloseRequest(smart_close); //sets smartclose on exit click
        
    }   

    public Image saveUndoImage(){
        WritableImage wimage = new WritableImage((int)graphic.getCanvas().getWidth(),(int)graphic.getCanvas().getHeight());
        graphic.getCanvas().snapshot(null, wimage); //Copying all that is in Canvas
        //gc is GraphicContext object from Canvas, it has drawing functions
        BufferedImage bi = SwingFXUtils.fromFXImage((Image)wimage, null); 
        return SwingFXUtils.toFXImage(bi, null); //returns an image of current state of canvas   
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    } 
    
}
