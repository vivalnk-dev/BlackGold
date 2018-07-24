package model;

import java.util.ArrayList;

/**
 * Created by Huy on 7/2/18.
 */
public class ZPK {
    private ArrayList<Double> z;
    private ArrayList<Complex> p;
    private int k;

    public ZPK(ArrayList<Double> z, ArrayList<Complex> p, int k)
    {
        setZ(z);
        setP(p);
        setK(k);
    }

    public void setZ(ArrayList<Double> z) {
        this.z = z;
    }

    public void setP(ArrayList<Complex> p) {
        this.p = p;
    }

    public void setK(int k) {
        this.k = k;
    }

    public ArrayList<Double> getZ() {
        return z;
    }

    public ArrayList<Complex> getP() {
        return p;
    }

    public int getK() {
        return k;
    }
}
