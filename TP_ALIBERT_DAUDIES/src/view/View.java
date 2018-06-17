package view;

import controller.Controller;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.scene.layout.BorderPane;

public class View extends Application{

    private static Integer multiplicatorValue = 5;
    private static Integer centerValue = 50;
    private static List<Node> addedNodes;

    @Override
    public void start(Stage stage) {
        Controller controller = new Controller();
        addedNodes = new ArrayList<>();
        BorderPane borderpane = new BorderPane();
        
        Group root = new Group();

        MenuBar menuBar = new MenuBar();

        // --- Menu File
        Menu menuFile = new Menu("Données");
        MenuItem data1 = new MenuItem("Data01");
        data1.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                initData(controller, root, "./data01.txt");
            }
        });

        MenuItem data2 = new MenuItem("Data02");
        data2.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                initData(controller, root, "./data02.txt");
            }
        });

        MenuItem data3 = new MenuItem("Data03");
        data3.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                initData(controller, root, "./data03.txt");
            }
        });

        MenuItem data4 = new MenuItem("Data04");
        data4.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                initData(controller, root, "./data04.txt");
            }
        });

        menuFile.getItems().addAll(data1, data2, data3, data4);

        // --- Menu Edit
        Menu menuAlgo = new Menu("Algorithme");

        MenuItem circuit = new MenuItem("Init circuit");
        circuit.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                if(controller.getAllCustomers() == null || controller.getAllCustomers().size() == 0){
                    Alert alert = warningAlert("Problème dans l'algorithme",
                            "Donnée non générées",
                            "Merci de sélectionner des données dans le menu 'Données'");
                    alert.showAndWait();
                }
                else {
                    controller.initCircuit();
                    root.getChildren().removeAll(addedNodes);
                    addedNodes.clear();
                    createAllCircuit(controller.getAllCircuits(), controller.getWarehouse(), controller.getInitialFitness(), controller.getOptimizedFitness());
                    root.getChildren().addAll(addedNodes);
                }
            }
        });

        MenuItem tabou = new MenuItem("Tabou");
        tabou.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                if(controller.getAllCircuits() == null || controller.getAllCircuits().size() == 0){
                    Alert alert = warningAlert("Problème dans l'algorithme",
                            "Circuit non générées",
                            "Merci de générer les circuit dans le menu 'Algorithme'");
                    alert.showAndWait();
                }
                else
                    doTabou(controller, root);
            }
        });

        MenuItem gen = new MenuItem("Algo génétique");
        gen.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                if(controller.getAllCustomers() == null || controller.getAllCustomers().size() == 0){
                    Alert alert = warningAlert("Problème dans l'algorithme",
                            "Donnée non générées",
                            "Merci de sélectionner des données dans le menu 'Données'");
                    alert.showAndWait();
                }
                else
                    doGen(controller, root);
            }
        });
        menuAlgo.getItems().addAll(circuit, tabou, gen);

        menuBar.getMenus().addAll(menuFile, menuAlgo);
        root.getChildren().add(menuBar);

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

    private static void createAllCircuit(List<Circuit> circuits, Customer warehouse, Double inilFitness, Double optiFitness){
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
        Label initialFitness = new Label("Fitness initiale : " + String.format("%5.2f" , inilFitness));
        Label optimizedFitness = new Label("Fitness optimisée : " + (optiFitness == null? "" : String.format("%5.2f" , optiFitness)));
        acutalY += 10;
        initialFitness.setLayoutX(120 * multiplicatorValue);
        initialFitness.setLayoutY(acutalY * multiplicatorValue + 35);
        acutalY += 5;
        optimizedFitness.setLayoutX(120 * multiplicatorValue);
        optimizedFitness.setLayoutY(acutalY * multiplicatorValue + 35);
        addedNodes.add(initialFitness);
        addedNodes.add(optimizedFitness);
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
            createAllCircuit(c.getOptimizedCircuit(), c.getWarehouse(), c.getInitialFitness(), c.getOptimizedFitness());
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
            try {
                c.algoGen(result.get().getNbPopulation(), result.get().getNbIteration());//TODO change with gen
                root.getChildren().removeAll(addedNodes);
                addedNodes.clear();
                createAllCircuit(c.getOptimizedCircuit(), c.getWarehouse(), c.getInitialFitness(), c.getOptimizedFitness());
                root.getChildren().addAll(addedNodes);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void alertFormat(){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Donnée invalide");
        alert.setHeaderText("Erreur de parsing");
        alert.setContentText("Les données attendues sont des nombres");
        alert.showAndWait();
    }

    public Alert warningAlert(String title, String headerText, String contentText){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        return alert;
    }
}   