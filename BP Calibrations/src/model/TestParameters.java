package model;

/**
 * Created by Huy on 6/25/18.
 */
public class TestParameters {
    private String mainFolder = "/Users/BigMac/Documents/BP Calibrations/test_data/data/"; //Location of data.
    private String subjectFolder = "phase3_subject2/";  // The particular subject.
    private int start = 0; // Start time (in seconds).
    private int end = 35; // End time (in seconds).
    private int sampleRate = 500; // ACC sampling rate (in Hertz).
    private int[] calibrationPoints = {1, 2, 3}; // Tests to use for calibration.
    private int test = 4; // Test to use for prediction.

    public String getMainFolder() {
        return mainFolder;
    }

    public String getSubjectFolder() {
        return subjectFolder;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public int[] getCalibrationPoints() {
        return calibrationPoints;
    }

    public int getTest() {
        return test;
    }

    public void setMainFolder(String mainFolder) {
        this.mainFolder = mainFolder;
    }

    public void setSubjectFolder(String subjectFolder) {
        this.subjectFolder = subjectFolder;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }

    public void setCalibrationPoints(int[] calibrationPoints) {
        this.calibrationPoints = calibrationPoints;
    }

    public void setTest(int test) {
        this.test = test;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
