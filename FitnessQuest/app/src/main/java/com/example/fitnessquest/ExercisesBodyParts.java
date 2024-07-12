package com.example.fitnessquest;

import java.util.ArrayList;
import java.util.List;

public class ExercisesBodyParts {
    private String bodyPart;
    private String equipment;
    private String gifUrl;
    private String id;
    private String name;
    private String target;

    public String getBodyPart() {
        return bodyPart;
    }

    public String getEquipment() {
        return equipment;
    }

    public String getGifUrl() {
        return gifUrl;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTarget() {
        return target;
    }

    public ArrayList<String> getSecondaryMuscles() {
        return (ArrayList<String>) secondaryMuscles;
    }

    public ArrayList<String> getInstructions() {
        return (ArrayList<String>) instructions;
    }

    private List<String> secondaryMuscles;
    private List<String> instructions;
}
