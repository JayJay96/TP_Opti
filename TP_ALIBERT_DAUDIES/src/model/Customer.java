package model;

public class Customer implements Cloneable{
    private Integer id;

    private Integer x;
    private Integer y;

    private Integer quantities;
    
    private Integer listPosition;

    public Customer(Integer id, Integer x, Integer y, Integer quantities) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.quantities = quantities;
    }

    public Integer getId() {
        return id;
    }

    public Integer getX() {
        return x;
    }

    public Integer getY() {
        return y;
    }

    public Integer getQuantities() {
        return quantities;
    }

    public Integer getListPosition() {
        return listPosition;
    }

    public void setListPosition(Integer listPosition) {
        this.listPosition = listPosition;
    }

    public Double getEuclidianDistance(Customer c){
        return Math.sqrt(Math.pow(x - c.getX(), 2) + Math.pow(y - c.getY(), 2));
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", x=" + x +
                ", y=" + y +
                ", quantities=" + quantities +
                '}';
    }

    public Customer clone() throws CloneNotSupportedException {
        return (Customer)super.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Customer customer = (Customer) o;

        return id.equals(customer.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
