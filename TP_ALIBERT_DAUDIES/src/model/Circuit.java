package model;

import java.util.LinkedList;

public class Circuit implements Cloneable{
    private LinkedList<Customer> customers;
    private Integer quantity;
    private Double fitness;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Circuit{" + "customers={");
        for(Customer c : customers)
            sb.append(c);
        sb.append("}, quantity=")
            .append(quantity)
            .append(", fitness=")
            .append(fitness)
            .append('}');
        return sb.toString();
    }

    public Circuit(LinkedList<Customer> customers) {
        this.customers = customers;
        this.quantity = 0;
        for(Customer c : customers)
            this.quantity += c.getQuantities();
    }

    public LinkedList<Customer> getCustomers() {
        return customers;
    }

    public void setFitness(Double fitness) {
        this.fitness = fitness;
    }

    public void addCustomerAt(Customer c, Integer index){
        quantity += c.getQuantities();
        customers.add(index, c);
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

    public Double computeFitness(){
        Double fitness = 0d;
        for(int i = 0; i < customers.size()-1; ++i){
            fitness += customers.get(i).getEuclidianDistance(customers.get(i+1));
        }
        fitness += customers.getLast().getEuclidianDistance(customers.getFirst());
        return fitness;
    }

    public Circuit clone() throws CloneNotSupportedException {
        Circuit c = (Circuit)super.clone();
        c.customers = (LinkedList<Customer>)customers.clone();
        return c;
    }
}
