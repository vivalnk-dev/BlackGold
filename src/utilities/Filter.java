package utilities;

import model.Complex;
import model.ZPK;

import java.util.ArrayList;
import java.util.Collections;

import static utilities.Calculations.*;

/**
 * Created by Huy on 7/2/18.
 */
public class Filter {

    public static ArrayList<Double> oddExt(ArrayList<Double> x, int n)
    {
        double leftEnd = x.get(0);
        double rightEnd = x.get(x.size()-1);
        ArrayList<Double> leftExt = new ArrayList<>();
        ArrayList<Double> rightExt = new ArrayList<>();

        for (int i = 1; i <= n; i++)
        {
            if (i < x.size())
                leftExt.add(x.get(i));
        }

        Collections.reverse(leftExt);

        for (int i = x.size()-2, j = n; j > 0; j--, i--)
        {
            if (i < x.size() && i >= 0)
                rightExt.add(x.get(i));
        }

        ArrayList<Double> ext = new ArrayList<>();

        ext.addAll(arrayDifference(leftExt, leftEnd*2));
        ext.addAll(x);
        ext.addAll(arrayDifference(rightExt, rightEnd*2));

        return ext;
    }

    public static ZPK butter (int N, int Wn)
    {
        ZPK zpk = buttap(N);
        double fs = 2.0;
        double warped = 2 * fs * Math.tan(Math.PI * Wn / fs);


    }

    public static ZPK buttap(int N)
    {
        ArrayList<Double> z = new ArrayList<>();
        ArrayList<Double> m = new ArrayList<>();
        ArrayList<Complex> p = new ArrayList<>();
        int k = 1;

        for (double i = -N + 1; i < N; i+=2)
        {
            m.add(i);
        }

        for (double num : m)
        {
            Complex comp = new Complex(0, Math.PI * num / (2 * N));
            p.add(comp.exp().times(new Complex(-1, 0)));
        }

        System.out.println(p);
        return new ZPK(z, p, k);

    }

    public static ZPK zpklp2lp (ArrayList<Double> z, ArrayList<Complex> p, int k) throws Exception {
        double wo = 1.0;
        int degree = relativeDegree(z, p);

        // Scale all points radially from origin to shift cutoff frequency.
        ArrayList<Double> z_lp = multiplyArray(z, wo);
        ArrayList<Complex> p_lp = multiplyComplexArray(p, wo);

        // Each shifted pole decreases gain by wo, each shifted zero increases it.
        // Cancel out the net change to keep overall gain the same.
        double k_lp = Math.pow(k * wo, degree);

    }

    public static int relativeDegree (ArrayList<Double> z, ArrayList<Complex> p) throws Exception {
        int degree = z.size() - p.size();
        if (degree < 0)
            throw new Exception("Improper transfer function. Must have at least as many poles as zeros.");
        else
            return degree;
    }

    public static ArrayList<Double> arrayDifference (ArrayList<Double> arr, double val)
    {
        for (int i = 0; i < arr.size(); i++)
        {
            double num = arr.get(i);
            arr.set(i, val - num);
        }

        return arr;
    }
}
