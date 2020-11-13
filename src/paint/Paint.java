/**
 * Paint Project
 * CS 250 Object Oriented Programming
 * @author Luke Weer
 */
package paint;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.PixelReader;
import javafx.scene.shape.*;
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
import javafx.application.HostServices;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.image.*;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
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
            square_draw, circle_draw, elipse_draw, help, undo, color_select,
            add_text, redo, no_tool, eraser, move, zoom, rounded_rect_draw;
    private File file;
    private Image image;
    private ImageView image_view;
    private GraphicsContext graphic;
    private VBox top_menu, imagePane;
    private Scene scene;
    private Alert alert;
    private Line line;
    private StackPane stack_pane;
    private FileChooser file_chooser;
    private Stage primaryStage;
    private Label label;
    private Canvas canvas, new_canvas;
    private WritableImage writeable_img, wi;
    private FlowPane modification_pane, color_pane;
    private StackPane image_pane;
    private BorderPane full_pane;
    private ColorPicker color_picker, color_dropper;
    private double x, y;
    private Slider slider;
    private double slider_value;
    private Popup popup;
    private Rectangle rect;

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
        redo = new MenuItem("Redo");
        zoom = new MenuItem("Zoom");
        save.setAccelerator(KeyCombination.keyCombination("Ctrl+Shift+S"));
        help.setAccelerator(KeyCombination.keyCombination("Ctrl+Shift+h"));
        redo.setAccelerator(KeyCombination.keyCombination("Ctrl+Y"));
        undo.setAccelerator(KeyCombination.keyCombination("Ctrl+z"));
        
        file_menu.getItems().addAll(open_file, save, save_as); 
        help_menu.getItems().add(help);
        edit_menu.getItems().addAll(undo, redo, zoom);
        
    //Line layout
        slider = new Slider();  //creation of slider for width adjustment
        slider.setMin(1);   //allows value to be set 1-15
        slider.setMax(15);  
        label = new Label("1.0"); //default value of slider is 1
        
    //Line selection
        MenuButton line_choice = new MenuButton("Draw Choice");
        snap_draw = new MenuItem("Snap Draw");
        freehand_draw = new MenuItem("Freehand Draw");
        square_draw = new MenuItem("Rectangle Draw");
        circle_draw = new MenuItem("Circle Draw");
        elipse_draw = new MenuItem("Elipse Draw");
        rounded_rect_draw = new MenuItem("Rounded Rectangle Draw");
        
        line_choice.getItems().addAll(snap_draw, freehand_draw, square_draw, circle_draw, elipse_draw, rounded_rect_draw);
        
        MenuButton draw_options = new MenuButton("Draw Options");
        add_text = new MenuItem("Add Text");
        no_tool = new MenuItem("No Tool");
        eraser = new MenuItem("Eraser");
        move = new MenuItem("Move");
        color_select = new MenuItem("Color Dropper");

        draw_options.getItems().addAll(color_select, add_text, no_tool, eraser, move);

    //Scene layout
        canvas = new Canvas(500,500);   //cavas to be drawn on 
        graphic = canvas.getGraphicsContext2D();
        
        ToolBar tool_bar = new ToolBar();
        color_picker = new ColorPicker();
        color_picker.setValue(Color.BLACK);
        tool_bar.getItems().addAll(color_picker, draw_options, line_choice, slider);
        
        menu_bar.getMenus().addAll(file_menu, edit_menu, help_menu);
        
        ScrollPane scroll_pane = new ScrollPane();
        scroll_pane.setFitToHeight(true);
        scroll_pane.setFitToWidth(true);
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
    
        
        
        
        
        
        //FILE MENU
    
        /**
         * Allows user to open an image of their choosing
         * 
         * @author Luke Weber 9/3/2019
         */
        EventHandler<ActionEvent> open = new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent t){              
                try{
                    img_container.getChildren().clear();
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
                    canvas.setWidth(image.getWidth());
                    canvas.setHeight(image.getHeight());
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
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        //HELP MENU//
        
        /**
         * Allows user to open a pop-up for help/about
         * 
         * @author Luke Weber 9/11/2019
        */
        EventHandler<ActionEvent> help_me = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) { 
                StackPane root2 = new StackPane();
                Button view_pdf = new Button("Release Notes");
                root2.getChildren().add(view_pdf);
                Scene secondScene = new Scene(root2, 400,300);
                Stage secondStage = new Stage();
                
                view_pdf.setOnAction((ActionEvent event) -> {
                    FileChooser fileChooser = new FileChooser();
                    // Set Initial Directory to Desktop
                    fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "\\Documents/Paint"));
                    // Set extension filter, only PDF files will be shown
                    FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf");
                    fileChooser.getExtensionFilters().add(extFilter);
                    // Show open file dialog
                    File file1 = fileChooser.showOpenDialog(primaryStage);
                    //Open PDF file
                    HostServices hostServices1 = getHostServices();
                    hostServices1.showDocument(file1.getAbsolutePath());
                });
    
                secondStage.setScene(secondScene); // set the scene
                secondStage.setTitle("Paint Replica - v0.0.0.3");
                secondStage.show();
                //primaryStage.close(); // close the first stage (Window)
            } 
        };
        
        
        
        
        
        
        
        
        
        
        //SLIDER AND COLOR//
        
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
        
        
        
        
        
        
        
        
        
        
        
        //DRAW CHOICE//

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
                line_choice.setText("Snap Draw");
            }
        };

        /**
         * Allows user to freehand draw
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
                line_choice.setText("Freehand Draw");
            } 
        };
        
        EventHandler<ActionEvent> square_draw_action = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent me) {
                line_choice.setText("Square Draw");
                canvas.setOnMousePressed((MouseEvent rectAction) -> {
                    undoStack.push(saveUndoImage());
                    rect = new Rectangle();
                    graphic.setStroke(color_picker.getValue());
                    graphic.setFill(color_picker.getValue());
                    rect.setX(rectAction.getX());
                    rect.setY(rectAction.getY());

                });
                canvas.setOnMouseDragged(null);
                canvas.setOnMouseReleased((MouseEvent rectAction) -> {
                    rect.setWidth(Math.abs((rectAction.getX() - rect.getX())));
                    rect.setHeight(Math.abs((rectAction.getY() - rect.getY())));

                    // Size the rectangle based on where the mouse is released.
                    if (rect.getX() > rectAction.getX()) {
                        rect.setX(rectAction.getX());
                    }
                    if (rect.getY() > rectAction.getY()) {
                        rect.setY(rectAction.getY());
                    }

                    // Fill the rectangle and outline it.
                    graphic.fillRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
                    graphic.strokeRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
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
                line_choice.setText("Circle Draw");
            }
        };
        
        /**
        * Allows the user to draw an Ellipse
        * 
        * @author Luke Weber 09/19/2019
        */
        EventHandler<ActionEvent> elipse_draw_action = new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {   
                Ellipse ellipse = new Ellipse();   //creation of new circle to be drawn
                canvas.setOnMousePressed((MouseEvent ellipseAction) -> {
                    undoStack.push(saveUndoImage());
                    graphic.setStroke(color_picker.getValue());
                    graphic.setFill(color_picker.getValue());
                    ellipse.setCenterX(ellipseAction.getX());
                    ellipse.setCenterY(ellipseAction.getY());

                });
                canvas.setOnMouseDragged(null);
                canvas.setOnMouseReleased((MouseEvent ellipseAction) -> {
                    ellipse.setRadiusX(Math.abs(ellipseAction.getX() - ellipse.getCenterX()));
                    ellipse.setRadiusY(Math.abs(ellipseAction.getY() - ellipse.getCenterY()));

                    // Size the circle based on where the mouse is released.
                    if (ellipse.getCenterX() > ellipseAction.getX()) {
                        ellipse.setCenterX(ellipseAction.getX());
                    }
                    if (ellipse.getCenterY() > ellipseAction.getY()) {
                        ellipse.setCenterY(ellipseAction.getY());
                    }

                    // Fill the ellipse and outline it.
                    graphic.strokeOval(ellipse.getCenterX(), ellipse.getCenterY(), ellipse.getRadiusX(), ellipse.getRadiusY());
                    graphic.fillOval(ellipse.getCenterX(), ellipse.getCenterY(), ellipse.getRadiusX(), ellipse.getRadiusY());
                
                });
                line_choice.setText("Ellipse Draw");
            }
        };
        
        /**
        * Allows the user to draw a triangle
        * 
        * @author Luke Weber 09/26/2019
        */
        EventHandler<ActionEvent> rounded_rect_draw_action = new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {   
                Rectangle roundrect  = new Rectangle();
                line_choice.setText("Rounded Rectangle Draw");
                canvas.setOnMousePressed((MouseEvent rectAction) -> {
                    undoStack.push(saveUndoImage());
                    graphic.setStroke(color_picker.getValue());
                    graphic.setFill(color_picker.getValue());
                    roundrect.setX(rectAction.getX());
                    roundrect.setY(rectAction.getY());
                });
                canvas.setOnMouseDragged(null);
                canvas.setOnMouseReleased((MouseEvent rectAction) -> {
                    roundrect.setWidth(Math.abs((rectAction.getX() - roundrect.getX())));
                    roundrect.setHeight(Math.abs((rectAction.getY() - roundrect.getY())));
                    
                    // Size the rectangle based on where the mouse is released.
                    if (roundrect.getX() > rectAction.getX()) {
                        roundrect.setX(rectAction.getX());
                    }
                    if (roundrect.getY() > rectAction.getY()) {
                        roundrect.setY(rectAction.getY());
                    }

                    // Fill the rectangle and outline it.
                    graphic.fillRoundRect(roundrect.getX(), roundrect.getY(), roundrect.getWidth(), roundrect.getHeight(), 20.0, 20.0);
                    graphic.strokeRoundRect(roundrect.getX(), roundrect.getY(), roundrect.getWidth(), roundrect.getHeight(), 20.0, 20.0);
                    
                    
                });
            }
        };
        
        /**
        * Allows the user to draw a triangle
        * 
        * @author Luke Weber 09/26/2019
        */
        EventHandler<ActionEvent> polygon_draw_action = new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {   
                Polygon poly = new Polygon();
                line_choice.setText("Rounded Rectangle Draw");
                canvas.setOnMousePressed((MouseEvent polyAction) -> {
                    undoStack.push(saveUndoImage());
                    
                });
                canvas.setOnMouseDragged(null);
                canvas.setOnMouseReleased((MouseEvent polyAction) -> {
                    
                    
                });
            }
        };
        
      
        
        
        
        
        
        
        
        
        
        
        //EDIT MENU ITEMS
        
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
        * This method allows the user to redo any modification that has been undone
        * to the image.
        * 
        * @author Luke Weber 09/23/2019
        */
        EventHandler<ActionEvent> redo_action = new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {   
                if (!redoStack.isEmpty()) {     //if the redoStack is NOT empty
                    undoStack.push(saveUndoImage());            //Saves previous state to undoStack
                    graphic.drawImage(redoStack.pop(), 0, 0);   //Draws previous image onto canvas
                }
            }
        };
        
        /**
        * This method allows the user to zoom in and out
        * 
        * @author Luke Weber 09/26/2019
        */
        EventHandler<ActionEvent> zoom_action = new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) { 
                
                canvas.setOnMousePressed((e) -> {
                    undoStack.push(saveUndoImage());
                    double scaler = 0.1;
                    double xScale = img_container.getScaleX();
                    double yScale = img_container.getScaleY();

                    // Zoom in on left click, out on right.
                    if (e.getButton() == MouseButton.PRIMARY) {
                        img_container.setScaleX(scaler + xScale);
                        img_container.setScaleY(scaler + yScale);
                    }
                    else if (e.getButton() == MouseButton.SECONDARY) {
                        img_container.setScaleX(xScale - scaler);
                        img_container.setScaleY(yScale - scaler);
                    }
                
                });
            }
        };
        
                

        
      
        
        
        
        
        
        
        
        
        
        
        
        
        //DRAW OPTIONS//
        
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
         * Allows user to add text to canvas
         * 
         * @author Luke Weber 09/23/2019
         */
        EventHandler<ActionEvent> add_text_action = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                line_choice.setText("Add Text");
                Dialog<Results> textBox = new Dialog<>();   //setting up the popup box
                textBox.setTitle("Image Text");
                textBox.setHeaderText("Add text to your image!");
                GridPane grid = new GridPane();  //gridPane to be used inside the popup box
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(20, 150, 10, 10));
                ButtonType doneButton = new ButtonType("Done", ButtonData.OK_DONE);     //allows user to click done/close
                textBox.getDialogPane().getButtonTypes().addAll(doneButton, ButtonType.CANCEL);
                TextField userText = new TextField();       //setting up the text fields
                userText.setPromptText("Text");
                TextField fontSize = new TextField();
                fontSize.setPromptText("Size");
                TextField xPixel = new TextField();
                xPixel.setPromptText("Pixel");
                TextField yPixel = new TextField();
                yPixel.setPromptText("Pixel");
                grid.add(new Label("Text: "), 0, 0);        //positioning the text fields
                grid.add(userText, 1, 0);
                grid.add(new Label("Font Size: "), 0, 1);
                grid.add(fontSize, 1, 1);
                grid.add(new Label("Pixel (X Value): "), 0, 2);
                grid.add(xPixel, 1, 2);
                grid.add(new Label("Pixel (Y Value): "), 0, 3);
                grid.add(yPixel, 1, 3);
                textBox.getDialogPane().setContent(grid);
                Optional<Results> result = textBox.showAndWait();      //shows dialog box and waits for user input
                undoStack.push(saveUndoImage());                       //adds changes to undoStack
                graphic.setStroke(color_picker.getValue());
                graphic.setFont(new Font("Verdana", Integer.parseInt(fontSize.getText())));
                graphic.fillText(userText.getText(), Integer.parseInt(xPixel.getText()), Integer.parseInt(yPixel.getText()));
                
            }
        };
        
        /**
         * Allows user to clear selection tool
         * 
         * @author Luke Weber 09/23/2019
         */
        EventHandler<ActionEvent> no_tool_action = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                canvas.setOnMousePressed(null); //clears mouse actions
                canvas.setOnMouseReleased(null);
                canvas.setOnMouseDragged(null);
                line_choice.setText("Draw Choice");
            }
        };
        
        /**
        * This method is used to allow the user to erase
        * 
        * @author Luke Weber 09/26/2019
        */
        EventHandler<ActionEvent> erase_action = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent me) {
                line_choice.setText("Eraser");
                canvas.setOnMouseDragged((MouseEvent eraseDrag) -> {    
                    graphic.setLineJoin(StrokeLineJoin.ROUND);  //sets eraser tip as round
                    graphic.setLineCap(StrokeLineCap.ROUND);
                    graphic.clearRect(eraseDrag.getX(), eraseDrag.getY(), slider.getValue(), slider.getValue());    //clears current pixels
                    //graphic.lineTo(freeDrawAction.getX(), freeDrawAction.getY());

                });
                canvas.setOnMousePressed(null);
                canvas.setOnMouseReleased(null);
            }
        };
        
        
        
        /**
        * This method is used to allow the user to move the selected cut image, 
        * however, this can only be used after the selection method has been used.
        * 
        * @author Luke Weber 09/26/2019
        */
        EventHandler<ActionEvent> move_action = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent me) {
                undoStack.push(saveUndoImage());        //pushes current image state onto undoStack
                wi = new WritableImage((int)rect.getWidth(), (int)rect.getHeight());
                PixelWriter pix = wi.getPixelWriter();  //creation of a pixelWriter used in moveAction

                Image i = imagePane.snapshot(null ,null);       //takes snapShot of current state of canvas
                PixelReader pixread = i.getPixelReader();

                //filters through EVERY pixel
                for(int p = 1; p < (int)rect.getWidth() - 1 ; p++){
                    for(int q = 1; q < (int)rect.getHeight() - 1; q++){
                        pix.setArgb(p, q, pixread.getArgb(((int)rect.getX())+p, ((int)rect.getY())+q)); 
                    }
                }

                graphic.setFill(Color.WHITE);       //leaves a white space behind cut selection
                graphic.fillRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());

                new_canvas = new Canvas(rect.getWidth(), rect.getHeight());     //newCanvas = the cut out image
                GraphicsContext newGraphic = new_canvas.getGraphicsContext2D();
                newGraphic.drawImage(wi, 0, 0);
                imagePane.getChildren().add(new_canvas);     //adds cut out image to the imagePane

                //mouse pressed action for moveAction
                canvas.setOnMousePressed((MouseEvent event) -> {
                    new_canvas.setTranslateX(event.getSceneX() - (scene.getWidth() - canvas.getLayoutX())/2);
                    new_canvas.setTranslateY(event.getSceneY() - (scene.getHeight() - canvas.getLayoutY())/2);
                });
                //mouse dragged action for moveAction
                canvas.setOnMouseDragged((MouseEvent event) -> {
                    new_canvas.setTranslateX(event.getSceneX() - (scene.getWidth() - canvas.getLayoutX())/2);
                    new_canvas.setTranslateY((event.getSceneY() - (scene.getHeight() - canvas.getLayoutY())/2) - 50);
                });
                //mouse released action for moveAction
                canvas.setOnMouseReleased((MouseEvent event) -> {
                    graphic.drawImage(wi, event.getX() - (rect.getWidth()/2), event.getY()- (rect.getHeight()/2));
                    imagePane.getChildren().remove(new_canvas);
                    canvas.setOnMousePressed(null);
                    canvas.setOnMouseReleased(null);
                    canvas.setOnMouseDragged(null);
                });

            }
        };

        
        
        
        
        
        
        
        
        
        
        //SMART CLOSE
        
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
        
        
        
        
        open_file.setOnAction(open);
        save_as.setOnAction(saveas);
        save.setOnAction(savefile);
        help.setOnAction(help_me);
        snap_draw.setOnAction(snap_draw_action);
        freehand_draw.setOnAction(freehand_draw_action);
        square_draw.setOnAction(square_draw_action);
        circle_draw.setOnAction(circle_draw_action);
        elipse_draw.setOnAction(elipse_draw_action);
        rounded_rect_draw.setOnAction(rounded_rect_draw_action);
        color_picker.setOnAction(color_switch);
        color_select.setOnAction(color_dropper_action);
        slider.setOnMouseReleased(slider_action);
        undo.setOnAction(undo_action);
        add_text.setOnAction(add_text_action);
        redo.setOnAction(redo_action);
        no_tool.setOnAction(no_tool_action);
        eraser.setOnAction(erase_action);
        move.setOnAction(move_action);
        zoom.setOnAction(zoom_action);
        
        primaryStage.setOnCloseRequest(smart_close); //sets smartclose on exit click
        
    }   
    
    private static class Results {
        /**
         * Text that the user wants to add to the image.
         */
        String text;
        /**
         *  Font size to be used for text.
         */
        String size;
        /**
         * X value for start of text.
         */
        String xpix;
        /**
         * Y value for start of text.
         */
        String ypix;
        public Results(String text, String size, String xpix, String ypix) {
            this.text = text; 
            this.size = size; 
            this.xpix = xpix;
            this.ypix = ypix;
        }
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
