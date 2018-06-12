package view;

import controller.Controller;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Circuit;
import model.Customer;
import model.Warehouse;

import java.util.ArrayList;
import java.util.List;

public class View extends Application{

    private static Integer multiplicatorValue = 5;
    private static Integer centerValue = 50;
    private static List<Node> addedNodes;
    private static TextField tabouSize;
    private static TextField tabouIterationValue;

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

        Button buttonCompute = new Button("Compute");
        buttonCompute.setLayoutX(375);
        buttonCompute.setOnAction(actionEvent -> {
            Integer tabouListSize;
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
        });
        root.getChildren().addAll(buttonCompute);
        Label tabouList = new Label("Taille liste tabou : ");
        tabouList.setLayoutX(475);
        tabouSize = new TextField();
        tabouSize.setLayoutX(600);
        tabouSize.setMaxWidth(40);
        root.getChildren().addAll(tabouSize, tabouList);

        Label tabouIteration = new Label("Itération max tabou : ");
        tabouIteration.setLayoutX(650);
        tabouIterationValue = new TextField();
        tabouIterationValue.setLayoutX(800);
        tabouIterationValue.setMaxWidth(50);
        root.getChildren().addAll(tabouIteration, tabouIterationValue);

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

        for(Circuit c : circuits){
            color = Color.color(Math.random(), Math.random(), Math.random());
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
}   