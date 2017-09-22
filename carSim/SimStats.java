package carSim;

import javafx.scene.text.Text;

import java.util.Collection;

/**
 * Created by Jonas on 05.08.2017.
 */
public class SimStats {
    private double simTime;
    private int generation;
    private int population;
    private double speed;

    private Text text;

    public void resetGeneration(){
        simTime = 0;
        generation++;

    }

    public SimStats(int population, Text text){
        generation = 0;
        this.population = population;
        this.text = text;
        speed = 1;

        resetGeneration();

    }

    public void tick(double dt){
        simTime += dt;

    }

    public void updateText(){
        text.setText("Simulation time: " + ((int)simTime) +
                "\nGeneration: " + generation +
                "\nSpeed: " + speed);

    }

    public double getSimTime() {
        return simTime;
    }

    public int getGeneration() {
        return generation;
    }

    public int getPopulation() {
        return population;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}
