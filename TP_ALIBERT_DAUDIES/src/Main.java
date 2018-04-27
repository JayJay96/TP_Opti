import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class Main {

    public static void main(String[] args) {
        try {
            LinkedList<Customer> allCustomer = new LinkedList<>();
            File f = new File("./data01.txt");
            InputStreamReader isr = new InputStreamReader(new FileInputStream(f));
            BufferedReader reader = new BufferedReader(isr);
            Warehouse w;
            String s = reader.readLine();
            String[] values;
            Integer allQuantites = 0;
            while (s != null) {
                if (!s.trim().equals("") && !s.startsWith("i")) {
                    values = s.split(";");
                    if(Integer.valueOf(values[0]) == 0)
                        w = new Warehouse(Integer.valueOf(values[0]),
                                Integer.valueOf(values[1]),
                                Integer.valueOf(values[2]));
                    else
                        allCustomer.add(new Customer(Integer.valueOf(values[0]),
                                                 Integer.valueOf(values[1]),
                                                 Integer.valueOf(values[2]),
                                                 Integer.valueOf(values[3])));
                    allQuantites += Integer.valueOf(values[3]);
                }
                s = reader.readLine();
            }
            Integer nbTrucks = (allQuantites/100+1);
            System.out.println("Nombre minimum de camion avec une capacité de 100 : " + nbTrucks);

            List<Circuit> allCircuit = new ArrayList<>();
            Circuit circuit;
            LinkedList<Customer> customers;
            for(int i = 0; i < 5; ++i){
                customers = new LinkedList<>();
                customers.add(allCustomer.removeFirst());
                circuit = new Circuit(customers);
                allCircuit.add(circuit);
            }

            Double minDistance = null;
            Double distance = null;
            Integer selectedCircuit = null;
            Circuit circuit1;
            for(Customer c : allCustomer){
                for(int i = 0; i < 5; i++){
                    circuit1 = allCircuit.get(i);
                    distance = c.getEuclidianDistance(circuit1.computeGravityCenter());
                    if((minDistance == null || minDistance > distance) && circuit1.getQuantity() + c.getQuantities() <= 100) {
                        minDistance = distance;
                        selectedCircuit = i;
                    }
                }
                allCircuit.get(selectedCircuit).addCustomer(c);
                minDistance = null;
                selectedCircuit = null;
            }

            /*
            Integer circuitNumber;
            Random r = new Random();
            for(Customer c : allCustomer){
                circuitNumber = r.nextInt(nbTrucks);
                while(allCircuit.get(circuitNumber).getQuantity() + c.getQuantities() > 100)
                    circuitNumber = r.nextInt(nbTrucks);
                allCircuit.get(circuitNumber).addCustomer(c);
                c.setCircuit(allCircuit.get(circuitNumber));
                System.out.println(circuitNumber);
            }
            */


            for(Circuit c : allCircuit)
                System.out.println(c.getQuantity() + " " + c);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
