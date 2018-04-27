package controller;

import model.Circuit;
import model.Customer;
import model.Warehouse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Controller {
    private List<Circuit> allCircuits;
    private List<Circuit> optimizedCircuit;
    private List<Customer> allCustomers;
    private Customer warehouse;
    private Integer nbTrucks;
    private Integer allQuantities;

    public Controller(){}

    public void run() {
        try {
            initValue("./data01.txt");
            initCircuit();


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public List<Circuit> getOptimizedNeighbor(List<Circuit> circuits, Double fitness) throws Exception{
        List<Circuit> cloneCircuit;
        List<Circuit> optimizedNeighbor = null;
        Double optimizedFitness = fitness;
        Circuit circuit;
        for(int i = 0; i < circuits.size(); ++i){
            circuit = circuits.get(i);
            for(Customer customer : circuit.getCustomers()){
                if(customer instanceof Warehouse){
                    continue;
                }
                cloneCircuit = cloneList(circuits);
                cloneCircuit.get(i).removeCustomer(customer);
                for(Circuit clonedCircuit : cloneCircuit){
                    for(int j = 1; j < clonedCircuit.getCustomers().size(); ++j){
                        if(clonedCircuit.getQuantity() + customer.getQuantities() > 100) {
                            clonedCircuit.addCustomerAt(customer, j);
                            if (optimizedFitness > getTotalFitness(cloneCircuit)) {
                                optimizedFitness = getTotalFitness(cloneCircuit);
                                optimizedNeighbor = cloneCircuit;
                            }
                            clonedCircuit.removeCustomer(customer);
                        }
                    }
                }
            }
        }
        if(fitness == optimizedFitness) throw new Exception("Valeur optimisée");
        fitness = optimizedFitness;
        return optimizedNeighbor;
    }

    public List<Circuit> cloneList(List<Circuit> list) {
        try{
            List<Circuit> clone = new ArrayList<Circuit>(list.size());
            for (Circuit item : list) clone.add(item.clone());
            return clone;
        }catch (Exception e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static Double getTotalFitness(List<Circuit> circuits){
        Double fitnessTotal = 0d;
        for(Circuit c : circuits) {
            c.setFitness(c.computeFitness());
            fitnessTotal += c.computeFitness();
        }
        return fitnessTotal;
    }

    public void initValue(String fileName){
        try{
            allCustomers = new ArrayList<>();
            File f = new File(fileName);
            InputStreamReader isr = new InputStreamReader(new FileInputStream(f));
            BufferedReader reader = new BufferedReader(isr);
            String s = reader.readLine();
            String[] values;
            allQuantities = 0;
            while (s != null) {
                if (!s.trim().equals("") && !s.startsWith("i")) {
                    values = s.split(";");
                    if(Integer.valueOf(values[0]) == 0)
                        warehouse = new Warehouse(Integer.valueOf(values[0]),
                                Integer.valueOf(values[1]),
                                Integer.valueOf(values[2]));
                    else
                        allCustomers.add(new Customer(Integer.valueOf(values[0]),
                                Integer.valueOf(values[1]),
                                Integer.valueOf(values[2]),
                                Integer.valueOf(values[3])));
                    allQuantities += Integer.valueOf(values[3]);
                }
                s = reader.readLine();
            }
            nbTrucks = (allQuantities/100+1);
            System.out.println("Nombre minimum de camion avec une capacité de 100 : " + nbTrucks);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void initCircuit(){
        allCircuits = new ArrayList<>();
        Circuit circuit;
        LinkedList<Customer> customers;
        Customer customer;
        Random r = new Random();
        Integer index = 0;

        //Init all circuit with one customer selected randomly
        for(int i = 0; i < nbTrucks; ++i){
            customers = new LinkedList<>();
            index = r.nextInt(allCustomers.size());
            customer = allCustomers.get(index);
            allCustomers.remove(customer);
            customers.add(warehouse);
            customers.add(customer);
            circuit = new Circuit(customers);
            allCircuits.add(circuit);
        }

        Double minDistance = null;
        Double distance = null;
        Integer selectedCircuit = null;
        Circuit circuit1;
        for(Customer c : allCustomers){
            for(int i = 0; i < allCircuits.size(); i++){
                circuit1 = allCircuits.get(i);
                distance = c.getEuclidianDistance(circuit1.computeGravityCenter());
                if((minDistance == null || minDistance > distance) && circuit1.getQuantity() + c.getQuantities() <= 100) {
                    minDistance = distance;
                    selectedCircuit = i;
                }
            }
            if(selectedCircuit == null){
                Circuit newCircuit = new Circuit(new LinkedList<>());
                newCircuit.addCustomer(c);
                allCircuits.add(newCircuit);
            }
            else
                allCircuits.get(selectedCircuit).addCustomer(c);
            minDistance = null;
            selectedCircuit = null;
        }

            /* Random method
            Integer circuitNumber;
            Random r = new Random();
            for(model.Customer c : allCustomer){
                circuitNumber = r.nextInt(nbTrucks);
                while(allCircuit.get(circuitNumber).getQuantity() + c.getQuantities() > 100)
                    circuitNumber = r.nextInt(nbTrucks);
                allCircuit.get(circuitNumber).addCustomer(c);
                c.setCircuit(allCircuit.get(circuitNumber));
                System.out.println(circuitNumber);
            }
            */

        Double circuitDistance;
        Double fitnessTotal = 0d;
        for(Circuit c : allCircuits) {
            circuitDistance = c.computeFitness();
            c.setFitness(circuitDistance);
            fitnessTotal += circuitDistance;
            System.out.println(c.getQuantity() + " " + c);
        }
        System.out.println("Fitness total : " + fitnessTotal);
        System.out.println("Quantité totale : " + allQuantities);
    }

    public void searchOptimizedNeighbor(){
        List<Circuit> c1 = allCircuits;
        try {
            for (int i = 0; i < 20; ++i) {
                c1 = getOptimizedNeighbor(c1, getTotalFitness(c1));
            }
            System.out.println(c1);
            System.out.println(getTotalFitness(c1));
        }catch (Exception e){
            System.out.println(c1);
            System.out.println(getTotalFitness(c1));
        }
        optimizedCircuit = c1;
    }

    public static void main(String[] args) {
        Controller controller = new Controller();
        controller.run();
    }

    public List<Circuit> getAllCircuits() {
        return allCircuits;
    }

    public List<Customer> getAllCustomers() {
        return allCustomers;
    }

    public Customer getWarehouse() {
        return warehouse;
    }

    public Integer getNbTrucks() {
        return nbTrucks;
    }

    public Integer getAllQuantities() {
        return allQuantities;
    }

    public List<Circuit> getOptimizedCircuit() {
        return optimizedCircuit;
    }
}
