package utilities;

import model.GroundTruth;
import model.RawACC;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Huy on 6/25/18.
 */
public class Utils {

    public static RawACC readACC(String fn, int sr, int st, int ed)
    {
        ArrayList<Double> time = new ArrayList<>();
        ArrayList<Integer> x = new ArrayList<>();
        ArrayList<Integer> y = new ArrayList<>();
        ArrayList<Integer> z = new ArrayList<>();

        try {
            File file = new File(fn);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            int count = 0;
            String line;

            while (count < st)
            {
                bufferedReader.readLine();
                count++;
            }

            while ((line = bufferedReader.readLine()) != null && count < ed)
            {
                String[] contents = line.split(";");
                time.add(Double.parseDouble(contents[1]));
                x.add(Integer.parseInt(contents[2]));
                y.add(Integer.parseInt(contents[3]));
                z.add(Integer.parseInt(contents[4]));

                count++;
            }

            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new RawACC(time, x, y, z);
    }

    public static RawACC readACC(String fn, int sr)
    {
        ArrayList<Double> time = new ArrayList<>();
        ArrayList<Integer> x = new ArrayList<>();
        ArrayList<Integer> y = new ArrayList<>();
        ArrayList<Integer> z = new ArrayList<>();

        try {
            File file = new File(fn);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;


            while ((line = bufferedReader.readLine()) != null)
            {
                String[] contents = line.split(";");
                time.add(Double.parseDouble(contents[1]));
                x.add(Integer.parseInt(contents[2]));
                y.add(Integer.parseInt(contents[3]));
                z.add(Integer.parseInt(contents[4]));
            }

            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new RawACC(time, x, y, z);
    }

    public static GroundTruth readTruth(String fileName)
    {
        ArrayList<Integer> pp = new ArrayList<>();
        ArrayList<Integer> s = new ArrayList<>();
        ArrayList<Integer> d= new ArrayList<>();
        ArrayList<Integer> hr = new ArrayList<>();

        try {
            File file = new File(fileName);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;

            bufferedReader.readLine(); // skip first line

            while ((line = bufferedReader.readLine()) != null)
            {
                String[] contents = line.split(",");
                pp.add(Integer.parseInt(contents[0]));
                s.add(Integer.parseInt(contents[1]));
                d.add(Integer.parseInt(contents[2]));
                hr.add(Integer.parseInt(contents[3]));
            }


            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new GroundTruth(pp, s, d, hr);
    }
}
