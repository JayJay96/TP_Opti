public class Customer {
    private Integer id;

    private Integer x;
    private Integer y;

    private Integer quantities;
    private Circuit circuit;

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

    public Circuit getCircuit() {
        return circuit;
    }

    public void setCircuit(Circuit circuit) {
        this.circuit = circuit;
    }

    public Double getEuclidianDistance(Customer c){
        return Math.sqrt((x - c.getX())^2 + (y - c.getY())^2);
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
}
