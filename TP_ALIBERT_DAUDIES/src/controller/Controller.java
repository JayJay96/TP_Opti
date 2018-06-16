package controller;

import model.Circuit;
import model.Customer;
import model.Warehouse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

public class Controller {
    private List<Circuit> allCircuits;
    private List<Circuit> optimizedCircuit;
    private List<Customer> allCustomers;
    private Customer warehouse;
    private Integer nbTrucks;
    private Integer allQuantities;
    private LinkedList<List<Circuit>> tabouList;
    private Map<List<Circuit>, Double> allNeighbor;
    private Integer tabouListSize;
    private Integer nbMaxIteration;
    private String fileName;

    public Controller(){}

    private List<Circuit> tabouMethod(List<Circuit> circuits, Double fitness) throws Exception{
        allNeighbor = new HashMap<>();
        List<Circuit> cloneCircuit;
        List<Circuit> optimizedNeighbor = null;
        Double optimizedFitness = null;
        Circuit circuit;
        Double calculatedFitness;
        Integer customerIndex;
        for(int i = 0; i < circuits.size(); ++i){
            circuit = circuits.get(i);
            for(Customer customer : circuit.getCustomers()){
                if(customer instanceof Warehouse){
                    continue;
                }
                cloneCircuit = cloneList(circuits);
                customerIndex = cloneCircuit.get(i).getCustomers().indexOf(customer);
                cloneCircuit.get(i).removeCustomer(customer);
                for(Circuit clonedCircuit : cloneCircuit){
                    for(int j = 1; j < clonedCircuit.getCustomers().size(); ++j){
                        if(clonedCircuit.getQuantity() + customer.getQuantities() <= 100) {
                            clonedCircuit.addCustomerAt(customer, j);
                            List<Circuit> keyCircuit = cloneList(cloneCircuit);
                            calculatedFitness = getTotalFitness(keyCircuit);
                            allNeighbor.put(keyCircuit, calculatedFitness);
                            clonedCircuit.removeCustomer(customer);
                        }
                    }
                }
                cloneCircuit.get(i).addCustomerAt(customer, customerIndex);
            }
        }
        Map.Entry<List<Circuit>, Double> min;
        min = Collections.min(allNeighbor.entrySet(), Comparator.comparing(Map.Entry::getValue));
        while (tabouList.contains(min.getKey())){
            allNeighbor.remove(min.getKey());
            if(allNeighbor.size() == 0) return null;
            min = Collections.min(allNeighbor.entrySet(), Comparator.comparing(Map.Entry::getValue));
        }

        if(min.getValue() > fitness) {
            if (tabouList.size() == tabouListSize)
                tabouList.removeLast();
            if (!tabouList.contains(min.getKey()))
                tabouList.addFirst(min.getKey());
        }
        return min.getKey();
    }

    private List<Circuit> cloneList(List<Circuit> list) {
        try{
            List<Circuit> clone = new ArrayList<>(list.size());
            for (Circuit item : list) clone.add(item.clone());
            return clone;
        }catch (Exception e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private static Double getTotalFitness(List<Circuit> circuits){
        Double fitnessTotal = 0d;
        for(Circuit c : circuits) {
            c.setFitness(c.computeFitness());
            fitnessTotal += c.computeFitness();
        }
        return fitnessTotal;
    }

    public void initValue(String fileName){
        try{
            this.fileName = fileName;
            allCustomers = new ArrayList<>();
            optimizedCircuit = new ArrayList<>();
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
        initValue(fileName);
        allCircuits = new ArrayList<>();
        Circuit circuit;
        LinkedList<Customer> customers;
        Customer customer;
        Random r = new Random();
        Integer index;

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
        Double distance;
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

    public void tabou(){
        tabouList = new LinkedList<>();
        List<Circuit> c1 = allCircuits;
        Integer countClient = 0;
        for(int i = 0; i < c1.size(); ++i){
            countClient += c1.get(i).getCustomers().size() - 1;
        }
        System.out.println("Nombre de client : "  + countClient);

        try {
            for (int i = 0; i < nbMaxIteration; ++i) {
                c1 = tabouMethod(c1, getTotalFitness(c1));
            }
            System.out.println(c1);
            System.out.println(getTotalFitness(c1));
        }catch (Exception e){
            e.printStackTrace();
            System.out.println(c1);
            System.out.println(getTotalFitness(c1));
        }

        countClient = 0;
        for(int i = 0; i < c1.size(); ++i){
            countClient += c1.get(i).getCustomers().size() - 1;
        }
        System.out.println("Nombre de client après optimissation : " + countClient);
        optimizedCircuit = c1;
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

    public void setTabouListSize(Integer tabouListSize) {
        this.tabouListSize = tabouListSize;
    }

    public void setNbMaxIteration(Integer nbMaxIteration) {
        this.nbMaxIteration = nbMaxIteration;
    }
}
