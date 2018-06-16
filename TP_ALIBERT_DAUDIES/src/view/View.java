package view;

import controller.Controller;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.*;

import java.util.*;

public class View extends Application{

    private static Integer multiplicatorValue = 5;
    private static Integer centerValue = 50;
    private static List<Node> addedNodes;
    private static Map<Color, Integer> circuitQuantity;

    @Override
    public void start(Stage stage) {
        Controller controller = new Controller();
        addedNodes = new ArrayList<>();
        Group root = new Group();

        Button buttonData1 = new Button("data01");
        buttonData1.setOnAction(actionEvent -> {
            initData(controller, root, "./data01.txt");
        });
        root.getChildren().add(buttonData1);
        Button buttonData2 = new Button("data02");
        buttonData2.setOnAction(actionEvent -> {
            initData(controller, root, "./data02.txt");
        });
        buttonData2.setLayoutX(70);
        root.getChildren().add(buttonData2);
        Button buttonData3 = new Button("data03");
        buttonData3.setOnAction(actionEvent -> {
            initData(controller, root, "./data03.txt");
        });
        buttonData3.setLayoutX(140);
        root.getChildren().add(buttonData3);
        Button buttonData4 = new Button("data04");
        buttonData4.setOnAction(actionEvent -> {
            initData(controller, root, "./data04.txt");
        });
        buttonData4.setLayoutX(210);
        root.getChildren().add(buttonData4);
        //Creating a Scene

        Button buttonCircuit = new Button("Init Circuit");
        buttonCircuit.setLayoutX(280);
        buttonCircuit.setOnAction(actionEvent -> {
            controller.initCircuit();
            root.getChildren().removeAll(addedNodes);
            addedNodes.clear();
            createAllCircuit(controller.getAllCircuits(), controller.getWarehouse());
            root.getChildren().addAll(addedNodes);
        });
        root.getChildren().add(buttonCircuit);

        Button buttonCompute = new Button("Tabou");
        buttonCompute.setLayoutX(375);
        buttonCompute.setOnAction(actionEvent -> {
            doTabou(controller, root);
        });
        root.getChildren().addAll(buttonCompute);

        Scene scene = new Scene(root, 900, 600);

        //Setting title to the scene
        stage.setTitle("Livraison");

        //Adding the scene to the stage
        stage.setScene(scene);

        //Displaying the contents of a scene
        stage.show();
    }
    public static void main(String args[]){
        launch(args);
    }

    private static void initData(Controller controller, Group root, String fileName){
        controller.initValue(fileName);
        List<Customer> customers = controller.getAllCustomers();
        root.getChildren().removeAll(addedNodes);
        addedNodes.clear();
        for(Customer c : customers){
            createCustomer(c, c.getId().toString(), Color.BLUE);
        }
        createCustomer(controller.getWarehouse(), "Entrepôt", Color.RED);

        root.getChildren().addAll(addedNodes);
    }

    private static void createCustomer(Customer customer, String title, Color color){
        Circle customerCicle;
        Text text;
        customerCicle = new Circle(customer.getX()*multiplicatorValue + centerValue,
                customer.getY()*multiplicatorValue + centerValue,
                5);
        customerCicle.setFill(color.deriveColor(1, 1, 1, 0.5));
        customerCicle.setStroke(color);
        customerCicle.setStrokeWidth(2);
        customerCicle.setStrokeType(StrokeType.OUTSIDE);

        text = new Text(title);
        text.setX(customer.getX()*multiplicatorValue + centerValue + 5);
        text.setY(customer.getY()*multiplicatorValue + centerValue - 5);
        addedNodes.add(customerCicle);
        addedNodes.add(text);
    }

    private static void createAllCircuit(List<Circuit> circuits, Customer warehouse){
        Color color;
        Customer customer;
        Label circuitQuantity;
        Integer acutalY = 5;
        Customer fakeCustomer1, fakeCustomer2;
        for(Circuit c : circuits){
            color = Color.color(Math.random(), Math.random(), Math.random());
            fakeCustomer1 = new Customer(0, 120, acutalY, 0);
            fakeCustomer2 = new Customer(0, 130, acutalY, 0);
            createLine(fakeCustomer1, fakeCustomer2, color);
            circuitQuantity = new Label("Quantité camion : " + c.getQuantity());
            circuitQuantity.setLayoutX(145 * multiplicatorValue);
            circuitQuantity.setLayoutY(acutalY * multiplicatorValue + 35);
            acutalY += 5;
            addedNodes.add(circuitQuantity);

            for(int i = 0; i < c.getCustomers().size() -1; ++i){
                customer = c.getCustomers().get(i);
                if(!(customer instanceof Warehouse))
                    createCustomer(customer, customer.getId().toString(), color);

                createLine(customer, c.getCustomers().get(i+1), color);
            }
            customer = c.getCustomers().getLast();
            createCustomer(customer, customer.getId().toString(), color);
            createLine(customer, warehouse, color);

        }
        createCustomer(warehouse, "Entrepôt", Color.RED);
    }

    private static void createLine(Customer from, Customer to, Color color){
        Line line = new Line();
        line.setFill(color.deriveColor(1, 1, 1, 0.5));
        line.setStroke(color);
        line.setStrokeWidth(2);
        line.setStrokeType(StrokeType.OUTSIDE);
        line.setStartX(from.getX()*multiplicatorValue + centerValue + 5);
        line.setStartY(from.getY()*multiplicatorValue + centerValue - 5);
        line.setEndX(to.getX()*multiplicatorValue + centerValue + 5);
        line.setEndY(to.getY()*multiplicatorValue + centerValue - 5);
        addedNodes.add(line);
    }

    public void doTabou(Controller c, Group root){
        Dialog<Tabou> dialog = new Dialog<>();
        dialog.setTitle("Méthode Tabou");
        dialog.setHeaderText("Veuillez renseigner les données de l'algorithme Tabou.");
        dialog.setResizable(true);

        Label label1 = new Label("Taille liste Tabou : ");
        Label label2 = new Label("Itération max : ");
        TextField text1 = new TextField("5");
        TextField text2 = new TextField("500");

        GridPane grid = new GridPane();
        GridPane.setMargin(grid, new Insets(5, 5, 5, 5));
        grid.add(label1, 1, 1);
        grid.add(text1, 2, 1);
        grid.add(label2, 1, 2);
        grid.add(text2, 2, 2);
        dialog.getDialogPane().setContent(grid);

        ButtonType buttonTypeOk = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);

        dialog.setResultConverter(new Callback<ButtonType, Tabou>() {
            @Override
            public Tabou call(ButtonType b) {
                Integer tSize;
                Integer maxIt;
                if (b == buttonTypeOk) {
                    try{
                        tSize = Integer.parseInt(text1.getText());
                        maxIt = Integer.parseInt(text2.getText());
                        return new Tabou(tSize, maxIt);
                    }catch(Exception e){
                        alertFormat();
                        return null;
                    }

                }
                return null;
            }
        });

        Optional<Tabou> result = dialog.showAndWait();

        if (result.isPresent()) {
            c.setNbMaxIteration(result.get().getMaxIteration());
            c.setTabouListSize(result.get().getTabouSize());
            c.tabou();
            root.getChildren().removeAll(addedNodes);
            addedNodes.clear();
            createAllCircuit(c.getOptimizedCircuit(), c.getWarehouse());
            root.getChildren().addAll(addedNodes);
        }
    }

    public void doGen(Controller c, Group root){
        Dialog<Gen> dialog = new Dialog<>();
        dialog.setTitle("Méthode Génétique");
        dialog.setHeaderText("Veuillez renseigner les données de l'algorithme génétique.");
        dialog.setResizable(true);

        Label label1 = new Label("Taille population : ");
        Label label2 = new Label("Itération max : ");
        TextField text1 = new TextField("50");
        TextField text2 = new TextField("500");

        GridPane grid = new GridPane();
        GridPane.setMargin(grid, new Insets(5, 5, 5, 5));
        grid.add(label1, 1, 1);
        grid.add(text1, 2, 1);
        grid.add(label2, 1, 2);
        grid.add(text2, 2, 2);
        dialog.getDialogPane().setContent(grid);

        ButtonType buttonTypeOk = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);

        dialog.setResultConverter(new Callback<ButtonType, Gen>() {
            @Override
            public Gen call(ButtonType b) {
                Integer pop;
                Integer maxIt;
                if (b == buttonTypeOk) {
                    try{
                        pop = Integer.parseInt(text1.getText());
                        maxIt = Integer.parseInt(text2.getText());
                        return new Gen(pop, maxIt);
                    }catch(Exception e){
                        alertFormat();
                        return null;
                    }

                }
                return null;
            }
        });

        Optional<Gen> result = dialog.showAndWait();

        if (result.isPresent()) {
            c.tabou();//TODO change with gen
            root.getChildren().removeAll(addedNodes);
            addedNodes.clear();
            createAllCircuit(c.getOptimizedCircuit(), c.getWarehouse());
            root.getChildren().addAll(addedNodes);
        }
    }

    public void alertFormat(){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Donnée invalide");
        alert.setHeaderText("Erreur de parsing");
        alert.setContentText("Les données attendues sont des nombres");
        alert.showAndWait();
    }

    /*
    *
    * Integer tabouListSize;
            Integer tabouIteration;
            try{
                tabouListSize = Integer.parseInt(tabouSize.getText());
                tabouIteration = Integer.parseInt(tabouIterationValue.getText());
            }catch (Exception e){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Donnée invalide");
                alert.setHeaderText("Erreur de parsing");
                alert.setContentText("La taille de la liste tabou doit être un nombre");
                alert.showAndWait();
                return;
            }
            controller.setNbMaxIteration(tabouIteration);
            controller.setTabouListSize(tabouListSize);
            //controller.searchOptimizedNeighbor();
            controller.tabou();
            root.getChildren().removeAll(addedNodes);
            addedNodes.clear();
            createAllCircuit(controller.getOptimizedCircuit(), controller.getWarehouse());
            root.getChildren().addAll(addedNodes);
    * */
}   