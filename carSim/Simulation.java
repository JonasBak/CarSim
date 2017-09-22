package carSim;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import vector.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Jonas on 22.06.2017.
 */
public class Simulation {
    private final int popSize = 250;

    private ArrayList<Car> cars;

    private Canvas canvas;

    private Canvas netCanv;

    private Timer timer;

    private final long dt;

    //private LineCol walls[];

    Path path;

    private int bestCar = 0;

    //private double speed = 2;


    private boolean allStopped = false;

    private SimStats stats;

    public Simulation(Canvas canvas, Canvas netCanv, SimStats stats){
        this.canvas = canvas;
        this.netCanv = netCanv;

        cars = new ArrayList<>();

        this.stats = stats;

        for (int i = 0; i < popSize; i++)
            cars.add(new Car());

        dt = 25;

        /*walls = new LineCol[]{
                new LineCol(50, 125, 90, 150),
                new LineCol(50, 125, 0, 1050),
                new LineCol(50, 275, 0, 1000),
                new LineCol(1100, 125, 45, 1000),
                new LineCol(1050, 275, 45, 1000),
                new LineCol(1000 + Math.sqrt(2) / 2 * 1000 + 50, 275 + Math.sqrt(2) / 2 * 1000, 0, 1000),
                new LineCol(1000 + Math.sqrt(2) / 2 * 1000 + 75, 125 + Math.sqrt(2) / 2 * 1000, 0, 1050),
                new LineCol(1000 + Math.sqrt(2) / 2 * 1000 + 50 + 1000, 275 + Math.sqrt(2) / 2 * 1000, -90, 150),

        };*/

        path = new Path(150,
                new Vector(0, 200),
                new Vector(1000, 200),
                new Vector(1800, 1500),
                new Vector(2500, 1500),
                new Vector(3300, 0),
                new Vector(4000, 0));

    }

    public void startTimer(){
        Timeline fiveSecondsWonder = new Timeline(new KeyFrame(javafx.util.Duration.millis(dt), event -> loop()));
        fiveSecondsWonder.setCycleCount(Timeline.INDEFINITE);
        fiveSecondsWonder.play();

        /*if (timer != null)
            timer.cancel();

        timer = new Timer(true);
        timer.schedule(
                new TimerTask() {

                    @Override
                    public void run() {
                        loop();
                    }
                }, 0, dt);*/
    }

    private void loop(){
        int lastBest = bestCar;
        allStopped = true;

        double speed = stats.getSpeed();

        stats.tick(dt * speed / 1000);


        LineCol walls[] = path.getLines();

        for (int i = 0; i < popSize; i++) {
            for (int n = 0; n < speed; n++)
                cars.get(i).update((double) dt / 1000 * (Math.min(1, speed - n)), walls);
            if (cars.get(i).getScore() > cars.get(bestCar).getScore())
                bestCar = i;
            if (!cars.get(i).getStopped())
                allStopped = false;
        }

        if (speed < 5) {
            if (bestCar != lastBest)
                drawNetwork();

            draw();
        }
        stats.updateText();

        if (allStopped || stats.getSimTime() > 25){
            stats.resetGeneration();
            newGeneration();
        }
    }


    public void draw(){
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.save();
        Vector best = getBestPos();
        gc.translate(-best.get(0) + canvas.getWidth() / 2, -best.get(1) + canvas.getHeight() / 2);


        for (Car car : cars)
            car.draw(canvas);

        LineCol walls[] = path.getLines();


        for (LineCol wall : walls)
            wall.draw(canvas);

        gc.restore();
    }

    private void drawNetwork(){
        getBestCar().drawNetwork(netCanv);
    }

    public Vector getCarPos(int i){
        return cars.get(i).getPos();
    }

    public Car getBestCar(){
        return cars.get(bestCar);
    }

    public Vector getBestPos(){
        return getCarPos(bestCar);
    }

    private void newGeneration(){
        cars.sort((a, b) -> a.getScore() == b.getScore() ? 0 : a.getScore() < b.getScore() ? 1 : -1);

        int kept = (int)(popSize * 0.4);

        int nSafe = (int)(kept * 0.3);

        while (cars.size() > kept)
            cars.remove((int)(Math.random() * (cars.size() - nSafe)) + nSafe);

        repopulate();
        mutate();

        for (Car car : cars)
            car.reset();

    }


    private void mutate(){
        for (Car car : cars)
            if (Math.random() > 0.5)
                car.mutate();
    }


    private void repopulate(){
        int i = 0;
        int n = cars.size();
        while (cars.size() < popSize) {
            cars.add(new Car(cars.get(i++ % n)));
            cars.get(cars.size() - 1).mutate();
        }
    }

}
