package carSim;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import vector.Vector;
import vector.VectorCalc;

import javax.sound.sampled.Line;

/**
 * Created by Jonas on 22.06.2017.
 */
public class LineCol {
    private double x, y;
    private double rot;

    private double length;

    public static LineCol FromPoints(Vector v0, Vector v1){
        Vector len = VectorCalc.distVec(v0, v1);
        double rot = Math.atan2(len.get(1), len.get(0)) / Math.PI * 180;

        return new LineCol(v0.get(0), v0.get(1), rot, len.length());
    }


    public LineCol(double x, double y, double rot, double length){
        this.x = x;
        this.y = y;
        this.rot = rot;
        this.length = length;
    }

    public double lengthTo(LineCol b){
        double vax = Math.cos(Math.PI * rot / 180 + Math.PI);
        double vay = Math.sin(Math.PI * rot / 180 + Math.PI);
        double vbx = Math.cos(Math.PI * b.rot / 180 + Math.PI);
        double vby = Math.sin(Math.PI * b.rot / 180 + Math.PI);

        double abx = x - b.x;
        double aby = y - b.y;

        double vaxvb = vax * vby - vay * vbx;

        double t = (abx * vby - aby * vbx) / vaxvb;
        double u = (abx * vay - aby * vax) / vaxvb;


        return t >= 0 && t < length && u >= 0 && u < b.length ? t : length;
    }

    public static boolean intersects(LineCol a, LineCol b){
        double vax = Math.cos(Math.PI * a.rot / 180 + Math.PI);
        double vay = Math.sin(Math.PI * a.rot / 180 + Math.PI);
        double vbx = Math.cos(Math.PI * b.rot / 180 + Math.PI);
        double vby = Math.sin(Math.PI * b.rot / 180 + Math.PI);

        double abx = a.x - b.x;
        double aby = a.y - b.y;

        double vaxvb = vax * vby - vay * vbx;

        double t = (abx * vby - aby * vbx) / vaxvb;
        double u = (abx * vay - aby * vax) / vaxvb;

        return t >= 0 && t < a.length && u >= 0 && u < b.length;
    }

    public void draw(Canvas canvas){

        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.save();
        gc.translate(x, y);
        gc.rotate(rot);

        gc.beginPath();
        //gc.moveTo(x, y);

        gc.setFill(Paint.valueOf("#000000"));
        //gc.moveTo(0, 0);
        gc.fillRect(0, 0, length, 1);
        //gc.lineTo(length, 0);
        //gc.lineTo(x + length * Math.cos(Math.PI * rot / 180), y + length * Math.sin(Math.PI * rot / 180));
        gc.stroke();
        gc.closePath();

        gc.restore();
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getRot() {
        return rot;
    }

    public void setRot(double rot) {
        this.rot = rot;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }
}
