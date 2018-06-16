package model;

public class Tabou {
    private Integer tabouSize;
    private Integer maxIteration;

    public Tabou(Integer tabouSize, Integer maxIteration) {
        this.tabouSize = tabouSize;
        this.maxIteration = maxIteration;
    }

    public Integer getTabouSize() {
        return tabouSize;
    }

    public void setTabouSize(Integer tabouSize) {
        this.tabouSize = tabouSize;
    }

    public Integer getMaxIteration() {
        return maxIteration;
    }

    public void setMaxIteration(Integer maxIteration) {
        this.maxIteration = maxIteration;
    }
}
