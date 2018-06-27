import numpy as np
from scipy import signal
from scipy import stats



def main_algo(filename, patch_position, heart_rate, sample_rate, start, end):
    # Set heart rate to 60 if unknown.
    if heart_rate == 0:
        heart_rate = 60

    # Read the raw ACC data.
    time, x, y, z = read_acc(filename, sample_rate, start, end)


    # Make timestamps start at zero, and correct axes due to patch positioning.
    print time
    time = (time - time[0]) * 10 ** (-5)
    print time
    if patch_position == 1:
        x = y
    elif patch_position == 2:
        x = -x

    # Apply a low-pass and high-pass backwards/forwards filter to the Z and X axes.
    z_filtered = filter_signal(z, 0.2, 0.002)
    x_filtered = filter_signal(x, 0.04, 0.003)

    # Detect peaks.
    z_peak_list, z1_peak_list, z2_peak_list, x_peak_list = detect_peaks(z_filtered, x_filtered, sample_rate, heart_rate)

    # Calculate the mass transit time (MTT).
    mtt = calculate_mtt(x_peak_list, z1_peak_list, sample_rate)

    return mtt


def read_truth(filename):
    patch_positions = []
    bp_sys = []
    bp_dia = []
    hr = []
    with open(filename) as f:
        lines = f.readlines()[1:]
        for line in lines:
            contents = line.split(",")
            patch_positions.append(int(contents[0]))
            bp_sys.append(int(contents[1]))
            bp_dia.append(int(contents[2]))
            hr.append(int(contents[3][:-1]))

    return np.array(patch_positions), np.array(bp_sys), np.array(bp_dia), np.array(hr)


def read_acc(filename, sample_rate, start=0, end=None):
    with open(filename) as f:
        lines = f.readlines()
        data = np.zeros((len(lines), 4))
    time = []
    x = []
    y = []
    z = []
    last_time = 0
    for i in range(len(lines)):
        line = lines[i].split(";")
        if len(line) < 2:
            line = lines[i].split(",")
        current_time = int(line[1])
        if current_time - last_time > 0:
            time.append(current_time)
            x.append(int(line[2]))
            y.append(int(line[3]))
            z.append(int(line[4]))
            prev_time = current_time
    time = np.array(time[sample_rate * start:sample_rate * end])
    x = np.array(x[sample_rate * start:sample_rate * end])
    y = np.array(y[sample_rate * start:sample_rate * end])
    z = np.array(z[sample_rate * start:sample_rate * end])
    return time, x, y, z


def filter_signal(acc, low, high, order=4, pad=150):
    b, a = signal.butter(order, low)
    acc_filtered = signal.filtfilt(b, a, acc - np.mean(acc), padlen=pad)
    b, a = signal.butter(order, high)
    acc_filtered = acc_filtered - signal.filtfilt(b, a, acc_filtered, padlen=pad)



    return acc_filtered


def detect_peaks(z_filtered, x_filtered, sample_rate, hr):
    # Integrate the derivative of the signal.
    z_mag = np.abs(z_filtered[1:] - z_filtered[:len(z_filtered) - 1])
    np.insert(z_mag, 0, 0)
    z_cu_mag = np.zeros(len(z_filtered))
    width = int(sample_rate / 25)
    for i in range(len(z_filtered)):
        if i < width:
            adj_width = i
        elif i > len(z_filtered) - width:
            adj_width = len(z_filtered) - i
        else:
            adj_width = width
        z_cu_mag[i] = np.sum(z_mag[i - adj_width:i + adj_width])

    # Find all Z-peaks (systolic and diastolic).
    z_peak = np.full(len(z_filtered), np.nan)
    z_peak_list = []
    width1 = int(60 * sample_rate / hr / 4)
    width2 = int(sample_rate / 50)
    for i in range(width1, len(z_filtered) - width1):
        if z_cu_mag[i] >= np.max(z_cu_mag[i - width1:i + width1]):
            idx = np.where(z_filtered == np.max(z_filtered[i - width2:i + width2]))[0][0]
            z_peak[idx] = np.max(z_filtered[i - width2:i + width2]) + 5
            z_peak_list.append(idx)

    # Weed out the diastolic peaks.
    z_peak_list_clean = [z_peak_list[0]]
    for i in range(1, len(z_peak_list)):
        dist_prev_peak = z_peak_list[i] - z_peak_list[i - 1]
        if dist_prev_peak > 60 * sample_rate / hr / 2:
            z_peak_list_clean.append(z_peak_list[i])
        else:
            z_peak[z_peak_list[i]] = np.nan
    z_peak_list = z_peak_list_clean

    # Find the Z1/Z2 peaks (systolic).
    z1_peak = np.full(len(z_filtered), np.nan)
    z1_peak_list = []
    z2_peak = np.full(len(z_filtered), np.nan)
    z2_peak_list = []
    width2 = int(sample_rate / 25)
    for i in range(width2, len(z_filtered) - width2):
        if not np.isnan(z_peak[i]):
            idx = np.where(z_filtered == np.min(z_filtered[i - width2:i]))[0][0]
            z1_peak[idx] = np.min(z_filtered[i - width2:i]) - 5
            z1_peak_list.append(idx)
            idx = np.where(z_filtered == np.min(z_filtered[i:i + width2]))[0][0]
            z2_peak[idx] = np.min(z_filtered[i:i + width2]) - 5
            z2_peak_list.append(idx)

    # Find the X peaks (systolic).
    x_peak = np.full(len(z_filtered), np.nan)
    x_peak_list = []
    width = int(sample_rate / 5)
    for i in range(len(z_filtered)):
        if not np.isnan(z_peak[i]):
            idx = np.where(x_filtered == np.max(x_filtered[i:i + width]))[0][0]
            x_peak[idx] = np.max(x_filtered[i:i + width]) + 5
            x_peak_list.append(idx)

    return z_peak_list, z1_peak_list, z2_peak_list, x_peak_list


def calculate_mtt(x_peak_list, z1_peak_list, sample_rate, predictor=""):
    dist = (np.array(x_peak_list) - np.array(z1_peak_list)) / float(sample_rate) * 1000
    avg_dist = np.mean(dist)
    std_dist = np.std(dist)
    mtt = np.mean(dist[np.abs(dist - avg_dist) < 2 * std_dist])

    return mtt



######################
##  INITIALIZATION  ##
######################

# Initialize variables.
mainfolder = "/Users/BigMac/Documents/PycharmProjects/test/data"  # Location of data.
subjectfolder = "phase3_subject2/"  # The particular subject.
start = 0  # Start time (in seconds).
end = 35  # End time (in seconds).
sample_rate = 500  # ACC sampling rate (in Hertz).
#   (We may get bad results if the
#    sampling rate is variable.)
cal_points = [1, 2, 3]  # Tests to use for calibration.
test = 4  # Test to use for prediction.

# Read the ground truth data.
filename = mainfolder + "/" + subjectfolder + "truth.csv"
patch_positions, bp_sys, bp_dia, hr = read_truth(filename)

#####################
##   CALIBRATION   ##
#####################

mtt_cal = []
for cal in cal_points:
    # Set some variables.
    patch_position = patch_positions[cal - 1]
    heart_rate = hr[cal - 1]

    # Calculate the mass transit time (MTT).
    filename = mainfolder + "/" + subjectfolder + "test" + str(cal) + ".csv"
    mtt = main_algo(filename, patch_position, heart_rate, sample_rate, start, end)
    mtt_cal.append(mtt)

    # Print results.
    print("Calibration on test #{}:\tBP={:.2f}\tMTT={:.2f}".format(cal, bp_sys[cal - 1], mtt))

slope, intercept, r_value, p_value, std_err = stats.linregress(mtt_cal, bp_sys[np.array(cal_points)-1])

##################
##   ANALYSIS   ##
##################

# Set some variables.
patch_position = patch_positions[test - 1]
heart_rate = hr[test - 1]

# Calculate the mass transit time (MTT).
filename = mainfolder + "/" + subjectfolder + "test" + str(test) + ".csv"
mtt = main_algo(filename, patch_position, heart_rate, sample_rate, start, end)

# Use MTT to predict BP, and quantify the error (if available).
pred = slope * mtt + intercept
error = bp_sys[test - 1] - pred

# Print results.
print("Prediction for test #{}:\tBP={:.2f}\tPred={:.2f}\tError={:.2f}".format(test, bp_sys[test - 1], pred, error))
