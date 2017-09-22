package carSim;

import matrix.MatrixCalc;
import vector.Vector;
import vector.VectorCalc;

/**
 * Created by Jonas on 23.07.2017.
 */
public class Path {

    private LineCol[] lines;

    public Path(double width, Vector ... vecs){
        lines = new LineCol[2 * vecs.length];

        Vector offsets [] = new Vector[vecs.length];


        Vector lastOffset = VectorCalc.getNormal(VectorCalc.distVec(vecs[0], vecs[1]));

        offsets[0] = VectorCalc.multiply(lastOffset, width / 2);

        for (int i = 1; i < vecs.length - 1; i++){
            Vector offset = VectorCalc.getNormal(VectorCalc.distVec(vecs[i], vecs[i + 1]));

            double deg = Math.acos(VectorCalc.dot(offset, lastOffset) / (offset.length() * lastOffset.length()));

            deg = (Math.PI - deg) / 2;

            double len = width / (Math.sin(deg) * 2);

            offsets[i] = VectorCalc.multiply(VectorCalc.normalize(VectorCalc.add(offset, lastOffset)), len);

            lastOffset = offset;
        }

        offsets[offsets.length - 1] = VectorCalc.multiply(lastOffset, width / 2);

/*
        Vector last = VectorCalc.distVec(vecs[0], vecs[1]);

        offsets[0] = VectorCalc.multiply(VectorCalc.getNormal(last), width / 2);

        Vector lastOffset = offsets[0];

        for (int i = 1; i < vecs.length - 1; i++){
            Vector v = VectorCalc.distVec(vecs[i], vecs[i + 1]);

            Vector offset = VectorCalc.getNormal(v);

            offset = VectorCalc.multiply(offset, width / 2);

            Vector u = VectorCalc.add(lastOffset, VectorCalc.multiply(VectorCalc.proj(lastOffset, offset), -1));

            offsets[i] = VectorCalc.add(offset, u);

            lastOffset = offset;
        }

        offsets[offsets.length - 1] = lastOffset;
*/

        for (int i = 0; i < vecs.length - 1; i++){
            lines[2 * i] = LineCol.FromPoints(VectorCalc.add(vecs[i], offsets[i]), VectorCalc.add(vecs[i + 1], offsets[i + 1]));
            lines[2 * i + 1] = LineCol.FromPoints(VectorCalc.add(vecs[i], VectorCalc.multiply(offsets[i], -1)), VectorCalc.add(vecs[i + 1], VectorCalc.multiply(offsets[i + 1], -1)));
        }


        lines[lines.length - 2] = LineCol.FromPoints(VectorCalc.add(vecs[0], offsets[0]), VectorCalc.add(vecs[0], VectorCalc.multiply(offsets[0], -1)));
        lines[lines.length - 1] = LineCol.FromPoints(VectorCalc.add(vecs[vecs.length - 1], offsets[vecs.length - 1]), VectorCalc.add(vecs[vecs.length - 1], VectorCalc.multiply(offsets[vecs.length - 1], -1)));

    }

    public LineCol[] getLines() {
        return lines;
    }
}
