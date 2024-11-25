package com.example.elaundryproject;

public class ModelResults {
    private String vicinity;
    private String name;

    // Constructor
    public ModelResults(String name, String vicinity) {
        this.name = name;
        this.vicinity = vicinity;
    }

    // Getter for Name
    public String getName() {
        return name;
    }

    // Getter for Vicinity
    public String getVicinity() {
        return vicinity;
    }

    // Setter for Name
    public void setName(String name) {
        this.name = name;
    }

    // Setter for Vicinity
    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }
}
