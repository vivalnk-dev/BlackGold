package model;

import java.util.ArrayList;

/**
 * Created by BigMac on 6/28/18.
 */
public class PeakList {

    private ArrayList<Integer> zPeakList = new ArrayList<>();
    private ArrayList<Integer> z1PeakList = new ArrayList<>();
    private ArrayList<Integer> z2PeakList = new ArrayList<>();
    private ArrayList<Integer> xPeakList = new ArrayList<>();

    public PeakList(ArrayList<Integer> z, ArrayList<Integer> z1, ArrayList<Integer> z2, ArrayList<Integer> x)
    {
        setZPeakList(z);
        setZ1PeakList(z1);
        setZ2PeakList(z2);
        setxPeakList(x);
    }

    public void setZPeakList(ArrayList<Integer> zPeakList) {
        this.zPeakList = zPeakList;
    }

    public void setZ1PeakList(ArrayList<Integer> z1PeakList) {
        this.z1PeakList = z1PeakList;
    }

    public void setZ2PeakList(ArrayList<Integer> z2PeakList) {
        this.z2PeakList = z2PeakList;
    }

    public void setxPeakList(ArrayList<Integer> xPeakList) {
        this.xPeakList = xPeakList;
    }

    public ArrayList<Integer> getzPeakList() {
        return zPeakList;
    }

    public ArrayList<Integer> getZ1PeakList() {
        return z1PeakList;
    }

    public ArrayList<Integer> getZ2PeakList() {
        return z2PeakList;
    }

    public ArrayList<Integer> getxPeakList() {
        return xPeakList;
    }

}
