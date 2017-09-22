package carSim;


import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import matrix.IOVector;
import network.Network;
import network.NetworkHelper;
import vector.Vector;
import vector.VectorCalc;



/**
 * Created by Jonas on 21.06.2017.
 */
public class Car {

    private Vector pos;
    private double vel, acc, dir, rot;

    private Paint color;

    private LineCol sensors[];
    private double rotOffset[];
    private double sens[];

    private final double maxSensOffset = 60;

    private final int nSens = 5;
    private final int sensLen = 120;

    private double score;

    private boolean stopped = false;

    private Network n;

    private double mutRange;

    private String tmp(){
        return Integer.toHexString((int)(Math.random() * 16));
    }

    public Car(Car c){
        n = new Network(c.n);


        rotOffset = new double[nSens];
        sensors = new LineCol[nSens];
        sens = new double[nSens];


        color = c.color;

        mutRange = c.mutRange;// * (1 - 0.1 + Math.random() * 0.2);

        reset();
    }

    public Car(){

        //input er sensor + vel + acc + dir
        //nNoder er valgt "tilfeldig"
        //output er acc + dir

        Network.Config config = new Network.Config();


        int depth = (int)(Math.random() * 2) + 2;

        int [] nNeur = new int[depth];

        char [] neuTypes = new char[depth];

        for (int i = 0; i < depth - 1; i++){
            nNeur[i] = (int)(Math.random() * 8) + 2;
            neuTypes[i] = Math.random() < 0.5 ? 'R' : 'N';
        }

        nNeur[depth - 1] = 2;
        neuTypes[depth - 1] = 'N';

        config.setnNeuron(nNeur);
        config.setTypes(neuTypes);

        n = new Network(nSens + 3, config);

        mutRange = 0.2;

        rotOffset = new double[nSens];
        sensors = new LineCol[nSens];
        sens = new double[nSens];


        color = Paint.valueOf("#" + tmp() + tmp() + tmp() + tmp() + tmp() + tmp());

        reset();
    }

    public void reset(){

        pos = new Vector(100, 200);
        vel = 100;
        acc = 0;
        dir = 0;
        rot = -30 + 60 * Math.random();
        score = 0;

        stopped = false;


        for (int i = 0; i < nSens; i++){
            rotOffset[i] = -maxSensOffset + 2 * maxSensOffset * (i + 0.5) / nSens;
            sensors[i] = new LineCol(pos.get(0), pos.get(1), rotOffset[i], sensLen);
            sens[i] = sensLen;
        }


    }

    public void update(double dt, LineCol walls[]){
        if (stopped)
            return;


        rot += dir * dt;
        vel += acc * dt;

        score += vel * dt - dt;//pos.get(0) * pos.get(0) + pos.get(1) * pos.get(1);//vel * dt;

        pos = VectorCalc.add(pos, new Vector(dt * vel * Math.cos(Math.PI * rot / 180), dt * vel * Math.sin(Math.PI * rot / 180)));


        for (int i = 0; i < nSens; i++) {
            sensors[i].setRot(rot + rotOffset[i]);
            sensors[i].setX(pos.get(0));
            sensors[i].setY(pos.get(1));

            sensors[i].setLength(sensLen);

            sens[i] = sensLen;
            for (int u = 0; u < walls.length; u++){
                double len = sensors[i].lengthTo(walls[u]);
                if (len < sens[i])
                    sens[i] = len;
                if (len < 20)
                    stopped = true;
            }

            sensors[i].setLength(sens[i]);


        }

        double[] in = new double[nSens + 3];
        for (int i = 0; i < nSens; i++)
            in[i] = sens[i];// / sensLen;

        in[in.length - 3] = vel;
        in[in.length - 2] = acc;
        in[in.length - 1] = dir;

        IOVector out = n.giveInput(new IOVector(in));

        acc = out.get(0);
        dir = out.get(1);

        if (score < -20)
            stopped = true;

    }

    public void draw(Canvas canvas){
        GraphicsContext gc = canvas.getGraphicsContext2D();

        double width = 15;
        double length = 40;

        gc.save();
        gc.translate(pos.get(0), pos.get(1));
        gc.rotate(rot);

        gc.setFill(color);

        gc.fillRect(-length/2, -width/2, length, width);

        //gc.fillText(String.valueOf(score), 0, width);

        gc.restore();



        for (int i = 0; i < nSens; i++)
            sensors[i].draw(canvas);
    }

    public void mutate(){
        int u = (int)(Math.random() * 5 + 1);
        for (int i = 0; i < u; i++)
            NetworkHelper.mutate(n, mutRange);
    }

    public void drawNetwork(Canvas canvas){
        NetworkHelper.draw(n.getInfo(), canvas);
    }

    public double getScore() {
        return score;
    }

    public boolean getStopped(){
        return stopped;
    }
    public Vector getPos() {
        return pos;
    }
}
