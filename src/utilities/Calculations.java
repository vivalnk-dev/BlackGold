package utilities;

import model.PeakList;
import model.RawACC;

import java.util.ArrayList;
import java.util.Arrays;
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
    public static PeakList detectPeaks(ArrayList<Double> zFiltered, ArrayList<Double> xFiltered, int sampleRate, int heartRate)
    {
        /** Integrate the derivative of the signal*/
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

        /** Find all Z-peaks (systolic and diastolic)*/
        ArrayList<Double> zPeak = new ArrayList<Double>(Collections.nCopies(zFiltered.size(), null));
        ArrayList<Integer> zPeakList = new ArrayList<>();
        int width1 = (60 * sampleRate / heartRate / 4);
        int width2 = sampleRate / 50;

        for (int i = width1; i < zFiltered.size() - width1; i++)
        {
            if (zCuMag.get(i) >= findMax(zCuMag, 1-width1, 1+width1))
            {
                double maxVal = findMax(zFiltered, Math.max(0, i-width2), Math.min(zFiltered.size(), i + width2));
                int idx = zFiltered.indexOf(maxVal);
                zPeak.set(idx, maxVal+5);
                zPeakList.add(idx);
            }
        }

        /** Weed out the diastolic peaks*/
        ArrayList<Integer> zPeakListClean = new ArrayList<>(Arrays.asList(zPeakList.get(0)));
        for (int i = 1; i < zPeakList.size(); i++)
        {
            int distPrevPeak = zPeakList.get(i) - zPeakList.get(i-1);
            if (distPrevPeak > 60 * sampleRate / heartRate / 2)
                zPeakListClean.add(zPeakList.get(i));
            else
                zPeak.set(zPeakList.get(i), null);
        }
        zPeakList = zPeakListClean;

        /** Find the Z1/Z2 peaks (systolic)*/
        ArrayList<Double> z1Peak = new ArrayList<Double>(Collections.nCopies(zFiltered.size(), null));
        ArrayList<Integer> z1PeakList = new ArrayList<>();
        ArrayList<Double> z2Peak = new ArrayList<Double>(Collections.nCopies(zFiltered.size(), null));
        ArrayList<Integer> z2PeakList = new ArrayList<>();
        width2 = sampleRate / 25;
        for (int i = width2; i < zFiltered.size() - width2; i++)
        {
            if (zPeak.get(i) != null)
            {
                double minValue = findMin(zFiltered, i - width2, i);
                int idx = zFiltered.indexOf(minValue);
                z1Peak.set(idx, findMin(zFiltered, i - width2, i) - 5);
                z1PeakList.add(idx);

                minValue = findMin(zFiltered, i, i + width2);
                idx = zFiltered.indexOf(minValue);
                z2Peak.set(idx, findMin(zFiltered, i, i + width2) - 5);
                z2PeakList.add(idx);
            }
        }
        
        /** Find the X peaks (systolic)*/
        ArrayList<Double> xPeak = new ArrayList<Double>(Collections.nCopies(zFiltered.size(), null));
        ArrayList<Integer> xPeakList = new ArrayList<>();
        width = sampleRate / 5;
        for (int i = 0; i < zFiltered.size(); i++)
        {
            if (zPeak.get(i) != null)
            {
                double maxVal = findMax(xFiltered, i, i + width);
                int idx = zFiltered.indexOf(maxVal);
                xPeak.set(idx, findMax(xFiltered, i, i + width) + 5);
                xPeakList.add(idx);
            }
        }

        System.out.println("peak list ");
        System.out.println(z1Peak.toString());
        System.out.println(z2Peak.toString());

        return new PeakList(zPeakList, z1PeakList, z2PeakList, xPeakList);

    }

    public static int calculateMTT (ArrayList<Integer> xPeakList, ArrayList<Integer> z1PeakList, int sr)
    {
        ArrayList<Integer> dist = divideArray(subtractArray(xPeakList, z1PeakList), sr * 1000);
        double avgDist = findMean(dist);
        double stdDist = findSTD(dist);

        return 0;

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

    public static double findMax (ArrayList<Double> arr, int start, int end)
    {
        double max = 0;

        for (int i = start; i < end; i++)
        {
            if (i < arr.size() && i >= 0)
            {
                if (arr.get(i) > max)
                    max = arr.get(i);
            }

        }

        return max;
    }

    public static double findMin (ArrayList<Double> arr, int start, int end)
    {
        double min = Double.MAX_VALUE;

        for (int i = start; i < end; i++)
        {
            if (i < arr.size() && i >= 0)
            {
                if (arr.get(i) < min)
                    min = arr.get(i);
            }

        }

        return min;
    }

    public static double findMean (ArrayList<Integer> arr)
    {
        double sum = 0;

        for (int num : arr)
        {
            sum += num;
        }

        if (sum == 0)
            return 0;

        return sum/(double) arr.size();
    }

    public static double findSTD (ArrayList<Integer> table)
    {
        double mean = findMean(table);
        double temp = 0;
        for (int i = 0; i < table.size(); i++)
        {
            int val = table.get(i);
            double squrDiffToMean = Math.pow(val - mean, 2);
            temp += squrDiffToMean;
        }
        double meanOfDiffs = (double) temp / (double) (table.size());
        return Math.sqrt(meanOfDiffs);
    }

    public static ArrayList<Integer> subtractArray (ArrayList<Integer> x, ArrayList<Integer> y)
    {
        ArrayList<Integer> z = new ArrayList<>();
        for (int i = 0; i < x.size(); i++)
        {
            z.add(x.get(i) - y.get(i));
        }

        return z;
    }

    public static ArrayList<Integer> divideArray (ArrayList<Integer> arr, int num)
    {
        ArrayList<Integer> ans = new ArrayList<>();
        for (int i = 0; i < arr.size(); i++)
        {
            ans.add(arr.get(i)/num);
        }

        return ans;
    }
}
