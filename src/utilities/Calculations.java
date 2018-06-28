package utilities;

import model.RawACC;

import java.util.ArrayList;
import java.util.Collections;

import static utilities.Utils.readACC;

/**
 * Created by Huy on 6/26/18.
 */
public class Calculations {

    /**
     * Calculation driver that runs through all the calculations
     * @param fn
     * @param pp
     * @param hr
     * @param sr
     * @param st
     * @param ed
     * @return Mass Transit Time
     */
    public static int mainAlgo(String fn, int pp, int hr, int sr, int st, int ed)
    {
        //Set heart rate to 60 if unknown
        if (hr == 0)
            hr = 60;

        //Read the raw ACC model
        RawACC acc = readACC(fn, sr, st, ed);
        ArrayList<Integer> x = acc.getX();
        ArrayList<Integer> z = acc.getZ();

        //Make timestamps start at zero, and correct axes due to patch positioning.
        acc.zeroOutTime();
        if (pp == 1)
            x = acc.getY();
        else if (pp == 2)
        {
            for (int i = 0; i < x.size(); i++)
            {
                x.set(i, -x.get(i));
            }
        }

        // Apply a low-pass and high-pass backwards/forwards filter to the Z and X axies.
        ArrayList<Double> zFiltered = filterSignal(z, 0.2, 0.002);
        ArrayList<Double> xFiltered = filterSignal(x, 0.04, 0.003);

        //Detect peaks
        //Peaks peaks = detectPeaks(zFiltered, xFiltered, sr, hr);

        //int mtt = calculateMTT(peaks);

        return 0;

    }

    /**
     * Main filter method; requires important filter algorithms (Butterworth and filtfilt(?))
     * @param acc
     * @param low
     * @param high
     * @return List of filtered ACC
     */
    public static ArrayList<Double> filterSignal (ArrayList<Integer> acc, double low, double high)
    {
        //default values
        int order = 4;
        int pad = 150;

        return null;

    }

    /**
     * Overloaded method without default values
     * @param acc
     * @param low
     * @param high
     * @param order
     * @param pad
     * @return List of filtered ACC
     */
    public static ArrayList<Double> filterSignal (ArrayList<Integer> acc, double low, double high, int order, int pad)
    {
        return null;
    }

    /**
     *
     * @param zFiltered
     * @param xFiltered
     * @param sampleRate
     * @param heartRate
     * @return Peaks object
     */
    public static void detectPeaks(ArrayList<Double> zFiltered, ArrayList<Double> xFiltered, int sampleRate, int heartRate)
    {
        // Integrate the derivative of the signal
        ArrayList<Double> zMag = integrate(zFiltered);
        ArrayList<Double> zCuMag = new ArrayList<Double>(Collections.nCopies(zFiltered.size(), 0.0));
        int width = sampleRate/25;
        int adjWidth = 0;

        for (int i = 0; i < zFiltered.size(); i++)
        {
            if (i < width)
                adjWidth = i;
            else if (i > zFiltered.size() - width)
                adjWidth = zFiltered.size() - i;
            else
                adjWidth = width;

            int start = i - adjWidth;
            int end = i + adjWidth;
            zCuMag.set(i, sumArray(zMag, start, end));
        }

        // Find all Z-peaks (systolic and diastolic)
        ArrayList<Double> zPeak = new ArrayList<Double>(Collections.nCopies(zFiltered.size(), null));
        ArrayList<Double> zPeakList = new ArrayList<>();
        int width1 = (60 * sampleRate / heartRate / 4);
        int width2 = sampleRate/50;





        System.out.println("mag");
        System.out.println(zCuMag.toString());


    }

    public static ArrayList<Double> integrate (ArrayList<Double> zFiltered)
    {
        ArrayList<Double> results = new ArrayList<>();
        results.add(0.0);

        for (int i = 1; i < zFiltered.size(); i++)
        {
            results.add(Math.abs(zFiltered.get(i)-zFiltered.get(i-1)));
        }

        return results;
    }

    public static double sumArray(ArrayList<Double> list, int start, int end)
    {
        double sum = 0;

        while (start <= end)
        {
            if (start >= 0 && start < list.size())
            {
                sum += list.get(start);
            }
            start++;
        }

        return sum;
    }
}
