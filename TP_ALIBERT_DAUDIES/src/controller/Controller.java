package controller;

import model.Circuit;
import model.Customer;
import model.Warehouse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class Controller {

    private List<Circuit> allCircuits;
    private Double initialFitness;
    private List<Circuit> optimizedCircuit;
    private Double optimizedFitness;
    private List<Customer> allCustomers;
    private Customer warehouse;
    private Integer nbTrucks;
    private Integer allQuantities;
    private LinkedList<List<Circuit>> tabouList;
    private Map<List<Circuit>, Double> allNeighbor;
    private Integer tabouListSize;
    private Integer nbMaxIteration;
    private String fileName;

    public Controller() {
    }

    private List<Circuit> tabouMethod(List<Circuit> circuits, Double fitness) throws Exception {
        allNeighbor = new HashMap<>();
        List<Circuit> cloneCircuit;
        Circuit circuit;
        Double calculatedFitness;
        Integer customerIndex;
        for (int i = 0; i < circuits.size(); ++i) {
            circuit = circuits.get(i);
            for (Customer customer : circuit.getCustomers()) {
                if (customer instanceof Warehouse) {
                    continue;
                }
                cloneCircuit = cloneList(circuits);
                customerIndex = cloneCircuit.get(i).getCustomers().indexOf(customer);
                cloneCircuit.get(i).removeCustomer(customer);
                for (Circuit clonedCircuit : cloneCircuit) {
                    for (int j = 1; j < clonedCircuit.getCustomers().size(); ++j) {
                        if (clonedCircuit.getQuantity() + customer.getQuantities() <= 100) {
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
        while (tabouList.contains(min.getKey())) {
            allNeighbor.remove(min.getKey());
            if (allNeighbor.size() == 0) {
                return null;
            }
            min = Collections.min(allNeighbor.entrySet(), Comparator.comparing(Map.Entry::getValue));
        }

        if (min.getValue() >= fitness) {
            if (tabouList.size() == tabouListSize) {
                tabouList.removeLast();
            }
            if (!tabouList.contains(min.getKey())) {
                tabouList.addFirst(min.getKey());
            }
        }
        return min.getKey();
    }

    private List<Circuit> cloneList(List<Circuit> list) {
        try {
            List<Circuit> clone = new ArrayList<>(list.size());
            for (Circuit item : list) {
                clone.add(item.clone());
            }
            return clone;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private static Double getTotalFitness(List<Circuit> circuits) {
        Double fitnessTotal = 0d;
        for (Circuit c : circuits) {
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
                    if (Integer.valueOf(values[0]) == 0) {
                        warehouse = new Warehouse(Integer.valueOf(values[0]),
                                Integer.valueOf(values[1]),
                                Integer.valueOf(values[2]));
                        warehouse.setListPosition(0);
                    } else {
                        allCustomers.add(new Customer(Integer.valueOf(values[0]),
                                Integer.valueOf(values[1]),
                                Integer.valueOf(values[2]),
                                Integer.valueOf(values[3])));
                    }
                    allQuantities += Integer.valueOf(values[3]);
                }
                s = reader.readLine();
            }
            nbTrucks = (allQuantities / 100 + 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initCircuit() {
        initValue(fileName);
        optimizedFitness = null;
        allCircuits = new ArrayList<>();
        Circuit circuit;
        LinkedList<Customer> customers;
        Customer customer;
        Random r = new Random();
        Integer index;

        //Init all circuit with one customer selected randomly
        for (int i = 0; i < nbTrucks; ++i) {
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
        for (Customer c : allCustomers) {
            for (int i = 0; i < allCircuits.size(); i++) {
                circuit1 = allCircuits.get(i);
                distance = c.getEuclidianDistance(circuit1.computeGravityCenter());
                if ((minDistance == null || minDistance > distance) && circuit1.getQuantity() + c.getQuantities() <= 100) {
                    minDistance = distance;
                    selectedCircuit = i;
                }
            }
            if (selectedCircuit == null) {
                Circuit newCircuit = new Circuit(new LinkedList<>());
                newCircuit.addCustomer(c);
                allCircuits.add(newCircuit);
            } else {
                allCircuits.get(selectedCircuit).addCustomer(c);
            }
            minDistance = null;
            selectedCircuit = null;
        }

        Double circuitDistance;
        Double fitnessTotal = 0d;
        for (Circuit c : allCircuits) {
            circuitDistance = c.computeFitness();
            c.setFitness(circuitDistance);
            fitnessTotal += circuitDistance;
        }
        initialFitness = getTotalFitness(allCircuits);
    }

    public void tabou(){
        tabouList = new LinkedList<>();
        List<Circuit> c1 = allCircuits;
        try {
            for (int i = 0; i < nbMaxIteration; ++i) {
                c1 = tabouMethod(c1, getTotalFitness(c1));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        optimizedCircuit = c1;
        optimizedFitness = getTotalFitness(c1);
    }


    public void algoGen(Integer nbInitialIndividu, Integer nbGeneration) throws CloneNotSupportedException {

        Map<List<Circuit>, Double> initialPopulationAndFitness = new HashMap<>();
        Map<List<Circuit>, Map<Integer, Integer>> codagePopInit = new HashMap<>();

        Map<List<Circuit>, Double> selection;
        //initialize
        for (int i = 0; i < nbInitialIndividu; i++) {
            Map.Entry<List<Circuit>, Double> entry = initRouting();
            initialPopulationAndFitness.put(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<List<Circuit>, Double> entry : initialPopulationAndFitness.entrySet()) {
            List<Circuit> key = entry.getKey();
            Double value = entry.getValue();

            codagePopInit.put(key, new TreeMap<>());
            for (Circuit circuit : key) {
                for (Customer customer : circuit.getCustomers()) {
                    if (!(customer instanceof Warehouse)) {
                        codagePopInit.get(key).put(customer.getId(), Integer.parseInt(circuit.getId() + ""));
                    }

                }
            }
        }

        for (int b = 0; b < nbGeneration; b++) {
            //selection
            selection = initialPopulationAndFitness.entrySet().stream()
                    .sorted(Comparator.comparing(Map.Entry::getValue))
                    .limit(nbInitialIndividu / 2)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            List<List<Circuit>> toRemove = new ArrayList();
            for (Map.Entry<List<Circuit>, Map<Integer, Integer>> entry2 : codagePopInit.entrySet()) {
                if (!selection.containsKey(entry2.getKey())) {
                    toRemove.add(entry2.getKey());
                }
            }
            for (List<Circuit> list : toRemove) {
                codagePopInit.remove(list);
            }

            Map<List<Circuit>, Double> children = new HashMap<>();
            for (int a = 0; a < nbInitialIndividu / 2; a++) {

                //croisement
                List<Circuit> selectedTournee1 = selection.entrySet().stream().sorted((o1, o2) -> {
                    Random r = new Random();
                    return r.nextInt(2) - 1;
                }).findFirst().get().getKey();

                List<Circuit> selectedTournee2 = selection.entrySet().stream().sorted((o1, o2) -> {
                    Random r = new Random();
                    return r.nextInt(2) - 1;
                }).findFirst().get().getKey();

                Map<Integer, Integer> codageT1 = codagePopInit.get(selectedTournee1);
                Map<Integer, Integer> codageT2 = codagePopInit.get(selectedTournee2);

                //Creation d'un fils
                Random r = new Random();
                int section = r.nextInt(30);

                Map<Integer, Integer> codageFils1 = new TreeMap<>();

                Map<Integer, Integer> tmp1 = codageT1.entrySet().stream()
                        .limit(section)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                Map<Integer, Integer> tmp2 = codageT2.entrySet().stream()
                        .skip(section)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

                codageFils1.putAll(tmp1);
                codageFils1.putAll(tmp2);

                List<Circuit> newCircuits = new ArrayList<>();

                List<Customer> sortedCustormers = allCustomers.stream().sorted(Comparator.comparing(Customer::getId)).collect(Collectors.toList());

                Integer circuitQte;
                LinkedList<Customer> excludedCustomers = new LinkedList<>();
                Integer nbTrucks = codageFils1.values().stream().max(Integer::compareTo).get();
                for (int j = 1; j <= nbTrucks ; j++) {
                    circuitQte = 0;
                    LinkedList<Customer> circuitCustomers = new LinkedList<>();
                    circuitCustomers.add(warehouse);
                    for (Customer c : sortedCustormers) {
                        //Keep the same place in the circuit
                        c.setListPosition(findCusotmerInList(selectedTournee1, selectedTournee2, c, section));
                        if (codageFils1.get(c.getId()) == j) {
                            Customer cCopy = c.clone();
                            if (circuitQte + c.getQuantities() <= 100) {
                                if(circuitCustomers.getLast().getListPosition() > cCopy.getListPosition()){
                                    Integer listSize = circuitCustomers.size();
                                    Customer bigger = circuitCustomers.getLast();
                                    while(bigger.getListPosition() > c.getListPosition() && listSize < circuitCustomers.size()){
                                        bigger = circuitCustomers.get(listSize--);
                                    }
                                    circuitCustomers.add(listSize, cCopy);
                                }   
                                else if (c.getListPosition() > circuitCustomers.size())
                                    circuitCustomers.addLast(cCopy);
                                else {
                                    circuitCustomers.add(c.getListPosition(), cCopy);
                                }
                                circuitQte += c.getQuantities();
                            } else {
                                excludedCustomers.add(cCopy);
                            }

                        }
                    }
                    Circuit c = new Circuit(circuitCustomers);
                    c.setId(j);
                    newCircuits.add(c);
                }

                Double minDistance = null;
                Double distance;
                Integer selectedCircuit = null;
                Circuit circuit1;
                for (Customer c1 : excludedCustomers) {
                    Customer cClonnned = c1.clone();
                    for (int h = 0; h < newCircuits.size(); h++) {
                        circuit1 = newCircuits.get(h).clone();
                        distance = cClonnned.getEuclidianDistance(circuit1.computeGravityCenter());
                        if ((minDistance == null || minDistance > distance) && circuit1.getQuantity() + cClonnned.getQuantities() <= 100) {
                            minDistance = distance;
                            selectedCircuit = h;
                        }
                    }
                    if (selectedCircuit == null) {
                        Circuit newCircuit = new Circuit(new LinkedList<>());
                        newCircuit.addCustomer(warehouse);
                        newCircuit.addCustomer(cClonnned);
                        newCircuit.setId(newCircuits.size() + 1);
                        newCircuits.add(newCircuit);
                    } else {
                        newCircuits.get(selectedCircuit).addCustomer(cClonnned);
                    }
                    minDistance = null;
                    selectedCircuit = null;
                }

                Double circuitDistance;
                Double fitnessTotal = 0d;
                for (Circuit c : newCircuits) {
                    circuitDistance = c.computeFitness();
                    c.setFitness(circuitDistance);
                    fitnessTotal += circuitDistance;
                }

                children.put(newCircuits, fitnessTotal);

            }
            selection.putAll(children);
            initialPopulationAndFitness = selection;

            codagePopInit.clear();
            for (Map.Entry<List<Circuit>, Double> entry : initialPopulationAndFitness.entrySet()) {
                
                List<Circuit> key = entry.getKey();
                Double value = entry.getValue();

                codagePopInit.put(key, new TreeMap<>());
                for (Circuit circuit : key) {
                    for (Customer customer : circuit.getCustomers()) {
                        if (!(customer instanceof Warehouse)) {
                            codagePopInit.get(key).put(customer.getId(), Integer.parseInt(circuit.getId() + ""));
                        }

                    }
                }
            }

        }
        Map.Entry<List<Circuit>, Double> min;
        min = Collections.min(initialPopulationAndFitness.entrySet(), Comparator.comparing(Map.Entry::getValue));
        optimizedCircuit = min.getKey();
        optimizedFitness = getTotalFitness(optimizedCircuit);
    }

    public Map.Entry<List<Circuit>, Double> initRouting() throws CloneNotSupportedException {
        initValue(fileName);
        ArrayList<Circuit> allCircuits = new ArrayList<Circuit>();
        optimizedFitness = null;
        Circuit circuit;
        LinkedList<Customer> customers;
        Customer customer;
        Random r = new Random();
        Integer index;

        //Init all circuit with one customer selected randomly
        for (int i = 0; i < nbTrucks; ++i) {
            customers = new LinkedList<>();
            index = r.nextInt(allCustomers.size());
            customer = allCustomers.get(index);
            allCustomers.remove(customer);
            customers.add(warehouse);
            customers.add(customer);
            circuit = new Circuit(customers);
            circuit.setId(i + 1);
            allCircuits.add(circuit);
        }

        Double minDistance = null;
        Double distance;
        Integer selectedCircuit = null;
        Circuit circuit1;
        for (Customer c1 : allCustomers) {
            Customer cClonnned = c1.clone();
            for (int i = 0; i < allCircuits.size(); i++) {
                circuit1 = allCircuits.get(i).clone();
                distance = cClonnned.getEuclidianDistance(circuit1.computeGravityCenter());
                if ((minDistance == null || minDistance > distance) && circuit1.getQuantity() + cClonnned.getQuantities() <= 100) {
                    minDistance = distance;
                    selectedCircuit = i;
                }
            }
            if (selectedCircuit == null) {
                Circuit newCircuit = new Circuit(new LinkedList<>());
                newCircuit.addCustomer(warehouse);
                newCircuit.addCustomer(cClonnned);
                newCircuit.setId(allCircuits.size() + 1);
                allCircuits.add(newCircuit);
            } else {
                allCircuits.get(selectedCircuit).addCustomer(cClonnned);
            }
            minDistance = null;
            selectedCircuit = null;
        }

        Double circuitDistance;
        Double fitnessTotal = 0d;
        for (Circuit c : allCircuits) {
            circuitDistance = c.computeFitness();
            c.setFitness(circuitDistance);
            fitnessTotal += circuitDistance;
        }
        initialFitness = getTotalFitness(allCircuits);
        initValue(fileName);

        return new AbstractMap.SimpleEntry<>(allCircuits, fitnessTotal);
    }

    private Integer findCusotmerInList(List<Circuit> section1, List<Circuit> section2, Customer customer, Integer sectionCut){
        Integer index = allCustomers.size();
        if (customer.getId() <= sectionCut)
            for(Circuit circuit : section1){
                if(circuit.getCustomers().indexOf(customer) != -1)
                    index = circuit.getCustomers().indexOf(customer);
            }
        else {
            for(Circuit circuit : section2){
                if(circuit.getCustomers().indexOf(customer) != -1)
                    index = circuit.getCustomers().indexOf(customer);
            }
        }
        return index;
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

    public Double getInitialFitness() {
        return initialFitness;
    }

    public Double getOptimizedFitness() {
        return optimizedFitness;
    }
}
