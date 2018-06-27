import numpy as np
import matplotlib.pyplot as plt


# Read the ECG/ACC signal from the patch.
def read_acc_raw(file):
    time = []
    acc_x = []
    acc_y = []
    acc_z = []
    ecg = []
    acc = False
    with open(file) as f:
        for line in f:
            # Grab the ACC data.
            if acc:
                for str in line.split(", "):
                    xyz = str.split(" ")
                    acc_x.append(int(xyz[0]))
                    acc_y.append(int(xyz[1]))
                    acc_z.append(int(xyz[2]))
            acc = False
            # Grab the timestamp.
            if line[:6] == "Sample":
                time.append(line.split(",")[0].split("-")[-1])
            # Grab ECG data.
            elif line[:3] == "ECG":
                for val in line[5:].split(", "):
                    ecg.append(int(val))
            # Grab XYZ accelerometer data.
            elif line[:3] == "XYZ":
                samples = int(line.split(" = ")[1])
                acc = True
    return time, ecg, acc_x, acc_y, acc_z

def read_acc_csv(file):
    acc_x = []
    acc_y = []
    acc_z = []
    with open(file) as f:
        for line in f:
            if line[:1] != "x":
                xyz = line.split(",")
                acc_x.append(int(xyz[0]))
                acc_y.append(int(xyz[1]))
                acc_z.append(int(xyz[2]))

    return acc_x, acc_y, acc_z


# Location of ACC data.
path = "/Users/BigMac/Downloads/"
filename = "1hz-6-22-18.txt"

# Read the raw ECG and ACC data.
time, ecg, x, y, z = read_acc_raw(path + filename)


plt.figure()
plt.subplot(411)
plt.plot(ecg)
plt.subplot(412)
plt.plot(x)
plt.subplot(413)
plt.plot(y)
plt.subplot(414)
plt.plot(z)
plt.show()
