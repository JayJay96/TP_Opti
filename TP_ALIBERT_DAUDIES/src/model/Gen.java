package model;

public class Gen {
    private Integer nbPopulation;
    private Integer nbIteration;

    public Gen(Integer nbPopulation, Integer nbIteration) {
        this.nbPopulation = nbPopulation;
        this.nbIteration = nbIteration;
    }

    public Integer getNbPopulation() {
        return nbPopulation;
    }

    public void setNbPopulation(Integer nbPopulation) {
        this.nbPopulation = nbPopulation;
    }

    public Integer getNbIteration() {
        return nbIteration;
    }

    public void setNbIteration(Integer nbIteration) {
        this.nbIteration = nbIteration;
    }
}
