import javafx.util.Pair;

import java.util.LinkedList;

public class Circuit {
    private LinkedList<Customer> customers;
    private Integer quantity;
    private Integer fitness;

    @Override
    public String toString() {
        String toString = "Circuit{" + "customers={";
        for(Customer c : customers)
            toString += c;
        toString += "}, quantity=" + quantity +
                ", fitness=" + fitness +
                '}';
        return toString;
    }

    public Circuit(LinkedList<Customer> customers) {
        this.customers = customers;
        this.quantity = 0;
    }

    public LinkedList<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(LinkedList<Customer> customers) {
        this.customers = customers;
    }

    public Integer getFitness() {
        return fitness;
    }

    public void setFitness(Integer fitness) {
        this.fitness = fitness;
    }

    public void addCustomer(Customer c){
        quantity += c.getQuantities();
        customers.add(c);
    }

    public void removeCustomer(Customer c){
        quantity -= c.getQuantities();
        customers.remove(c);
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Customer computeGravityCenter(){
        Integer X = 0;
        Integer Y = 0;
        for(Customer c : customers){
            X += c.getX();
            Y += c.getY();
        }
        return new Customer(0, X/customers.size(), Y/customers.size(), 0);
    }
}
