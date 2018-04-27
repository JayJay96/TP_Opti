package model;

public class Customer implements Cloneable{
    private Integer id;

    private Integer x;
    private Integer y;

    private Integer quantities;

    public Customer(Integer id, Integer x, Integer y, Integer quantities) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.quantities = quantities;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public Integer getQuantities() {
        return quantities;
    }

    public void setQuantities(Integer quantities) {
        this.quantities = quantities;
    }

    public Double getEuclidianDistance(Customer c){
        return Math.sqrt(Math.pow(x - c.getX(), 2) + Math.pow(y - c.getY(), 2));
    }

    @Override
    public String toString() {
        return "model.Customer{" +
                "id=" + id +
                ", x=" + x +
                ", y=" + y +
                ", quantities=" + quantities +
                '}';
    }

    public Customer clone() throws CloneNotSupportedException {
        return (Customer)super.clone();
    }
}
