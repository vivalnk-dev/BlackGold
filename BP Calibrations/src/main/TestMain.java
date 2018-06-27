package main;
import model.GroundTruth;
import model.TestParameters;

import java.util.ArrayList;

import static utilities.Calculations.*;
import static utilities.Utils.*;


/**

Driver class

*/

public class TestMain {

    /**
     * Global test variables
     */
    static String mainFolder; //Location of model.
    static String subjectFolder;  // The particular subject.
    static int start; // Start time (in seconds).
    static int end; // End time (in seconds).
    static int sampleRate; // ACC sampling rate (in Hertz).
    static int[] calibrationPoints; // Tests to use for calibration.
    static int test;
    static GroundTruth truth;

    /**
     *
     * Start test
     */
    public static void main(String[] args) {

        initParameters(); // Initialize variables
        calibrate();

    }

    /**
     * Initialize test parameters
     */
    public static void initParameters()
    {
        TestParameters param = new TestParameters();
        mainFolder = param.getMainFolder();
        subjectFolder = param.getSubjectFolder();
        start = param.getStart();
        end = param.getEnd();
        sampleRate = param.getSampleRate();
        calibrationPoints = param.getCalibrationPoints();
        test = param.getTest();
        //Read the ground truth model
        String truthFileName = mainFolder + "/" + subjectFolder + "truth.csv";
        truth = readTruth(truthFileName);
    }

    /**
     * Calculate MTT
     */
    public static void calibrate()
    {
        ArrayList<Integer> mttCal = new ArrayList<>();
        for (int cal : calibrationPoints)
        {
            //Set some variables
            int patchPosition = truth.getPatchPositions().get(cal-1);
            int heartRate = truth.getHeartRates().get(cal-1);

            //Calculate the mass transit time (MTT)
            String mttFileName = mainFolder + "/" + subjectFolder + "test" + cal + ".csv";
            int mtt = mainAlgo(mttFileName, patchPosition, heartRate, sampleRate, start, end);
            mttCal.add(mtt);
        }
    }



}
