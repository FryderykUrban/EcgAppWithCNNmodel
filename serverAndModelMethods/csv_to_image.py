import os
import cv2
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from biosppy.signals import ecg as biosppy_ecg

def segmentation(path):
    csv = pd.read_csv(path)
    data = np.array(csv['Sample Value'])
    signals = []
    count = 1
    peaks = biosppy_ecg.christov_segmenter(signal=data, sampling_rate=200)[0]
    for i in (peaks[1:-1]):
        diff1 = abs(peaks[count - 1] - i)
        diff2 = abs(peaks[count + 1] - i)
        x = peaks[count - 1] + diff1 // 2
        y = peaks[count + 1] - diff2 // 2
        signal = data[x:y]
        signals.append(signal)
        count += 1
    return signals

def signal_to_img(array, directory):
    if not os.path.exists(directory):
        os.makedirs(directory)

    for count, i in enumerate(array):
        fig = plt.figure(frameon=False)
        plt.plot(i)
        plt.xticks([]), plt.yticks([])
        for spine in plt.gca().spines.values():
            spine.set_visible(False)

        filename = os.path.join(directory, f'{count}.png')
        fig.savefig(filename)
        plt.close(fig)  # Close the figure to avoid memory issues
        im_gray = cv2.imread(filename, cv2.IMREAD_GRAYSCALE)
        im_gray = cv2.resize(im_gray, (128, 128), interpolation=cv2.INTER_LANCZOS4)
        cv2.imwrite(filename, im_gray)

def main(path, directory):
    signals = segmentation(path)
    signal_to_img(signals, directory)

if __name__ == "__main__":
    path = input("Enter the path of the CSV file: ")
    directory = input("Enter the directory where you want to save the images: ")
    main(path, directory)
