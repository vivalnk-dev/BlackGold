package model;

import java.util.ArrayList;

/**
 * Created by Huy on 6/25/18.
 */
public class RawACC {
    private ArrayList<Double> time;
    private ArrayList<Integer> x;
    private ArrayList<Integer> y;
    private ArrayList<Integer> z;

    public RawACC(ArrayList<Double> time, ArrayList<Integer> x, ArrayList<Integer> y, ArrayList<Integer> z)
    {
        setTime(time);
        setX(x);
        setY(y);
        setZ(z);
    }

    public void setTime(ArrayList<Double> time) {
        this.time = time;
    }

    public void setX(ArrayList<Integer> x) {
        this.x = x;
    }

    public void setY(ArrayList<Integer> y) {
        this.y = y;
    }

    public void setZ(ArrayList<Integer> z) {
        this.z = z;
    }

    public ArrayList<Double> getTime() {
        return time;
    }

    public ArrayList<Integer> getX() {
        return x;
    }

    public ArrayList<Integer> getY() {
        return y;
    }

    public ArrayList<Integer> getZ() {
        return z;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public void zeroOutTime() {
        if (time.get(0) == null)
            return;

        double firstNum = time.get(0);

        for (int i = 0; i < time.size(); i++)
        {
            time.set(i, (time.get(i)-firstNum) * (Math.pow(10, -5)));
        }
    }
}
