package model;

import java.util.ArrayList;

/**
 * Created by Huy on 6/25/18.
 */
public class GroundTruth {
    private ArrayList<Integer> patchPositions = new ArrayList<>();
    private ArrayList<Integer> systolics = new ArrayList<>();
    private ArrayList<Integer> diastolics = new ArrayList<>();
    private ArrayList<Integer> heartRates = new ArrayList<>();

    public GroundTruth (ArrayList<Integer> pp, ArrayList<Integer> s, ArrayList<Integer> d, ArrayList<Integer> hr)
    {
        setPatchPositions(pp);
        setSystolics(s);
        setDiastolics(d);
        setHeartRates(hr);

    }
    
    public void setPatchPositions(ArrayList<Integer> patchPositions)
    {
        this.patchPositions = patchPositions;
    }

    public void setSystolics(ArrayList<Integer> systolics)
    {
        this.systolics = systolics;
    }

    public void setDiastolics(ArrayList<Integer> diastolics)
    {
        this.diastolics = diastolics;
    }

    public void setHeartRates(ArrayList<Integer> heartRates)
    {
        this.heartRates = heartRates;
    }

    public ArrayList<Integer> getPatchPositions()
    {
        return patchPositions;
    }

    public ArrayList<Integer> getSystolics()
    {
        return systolics;
    }

    public ArrayList<Integer> getDiastolics()
    {
        return diastolics;
    }

    public ArrayList<Integer> getHeartRates()
    {
        return heartRates;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
