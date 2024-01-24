import os
import cv2
import numpy as np
import matplotlib.pyplot as plt
import shutil
from biosppy.signals import ecg
from keras.models import load_model
from collections import Counter
from flask import Flask, request, jsonify
import glob

app = Flask(__name__)

# Ścieżka do modelu
MODEL_PATH = r'C:/Users/cukro/Desktop/ECG-arrhythmia-classification-using-a-2-D-convolutional-neural-network.-master/ecg_model_own.hdf5'
model = load_model(MODEL_PATH)

# Definicje etykiet dla klas arytmii
class_labels = {
    0: 'Atrial premature contraction beat (APC)',
    1: 'Normal beat',
    2: 'Left bundle branch block beat (LBB)',
    3: 'Paced beat (PAB)',
    4: 'Premature ventricular contraction beat (PVC)',
    5: 'Right bundle branch block beat (RBB)',
    6: 'Ventricular escape beat (VEB)'
}

@app.route('/analyze', methods=['POST'])
def analyze_ecg():
    data = request.get_json()
    signal = np.array(data['signal'])
    sampling_rate = 360
    signals, rpeaks = segmentation(signal, sampling_rate)

    image_directory = 'temp_images'
    if not os.path.exists(image_directory):
        os.makedirs(image_directory)
    
    signal_to_img(signals, image_directory)

    predictions, arrhythmia_intervals = load_and_predict(image_directory, rpeaks, sampling_rate)

    diagnosis = interpret_prediction(predictions)

    shutil.rmtree(image_directory)

    result = {
        'diagnosis': diagnosis,
        'arrhythmia_intervals': arrhythmia_intervals
    }

    return jsonify(result)

def segmentation(signal, sampling_rate):
    signals = []
    count = 1
    peaks = ecg.christov_segmenter(signal=signal, sampling_rate=sampling_rate)[0]
    for i in (peaks[1:-1]):
        diff1 = abs(peaks[count - 1] - i)
        diff2 = abs(peaks[count + 1] - i)
        x = peaks[count - 1] + diff1 // 2
        y = peaks[count + 1] - diff2 // 2
        segment = signal[x:y]
        signals.append(segment)
        count += 1
    return signals, peaks

def signal_to_img(signals, directory):
    if not os.path.exists(directory):
        os.makedirs(directory)

    for count, signal in enumerate(signals):
        fig = plt.figure(frameon=False)
        plt.plot(signal)
        plt.xticks([]), plt.yticks([])
        for spine in plt.gca().spines.values():
            spine.set_visible(False)

        filename = os.path.join(directory, f'signal_{count}.png')
        fig.savefig(filename)

        im_gray = cv2.imread(filename, cv2.IMREAD_GRAYSCALE)
        im_gray = cv2.resize(im_gray, (128, 128), interpolation=cv2.INTER_LANCZOS4)
        cv2.imwrite(filename, im_gray)

        plt.close(fig)

def load_and_predict(directory, rpeaks, sampling_rate):
    files = sorted(glob.glob(directory + '/*.png'))
    predictions = []
    arrhythmia_intervals = []

    for file in files:
        image = cv2.imread(file, cv2.IMREAD_GRAYSCALE)
        image = cv2.resize(image, (128, 128))
        image = np.stack((image,)*3, axis=-1)
        pred = model.predict(image.reshape((1, 128, 128, 3)))
        y_classes = pred.argmax(axis=-1)
        predictions.append(y_classes[0])

    for i, prediction in enumerate(predictions):
        if prediction != 1:  # Assuming '1' indicates a normal heartbeat
            start_time = rpeaks[i] / sampling_rate
            end_time = rpeaks[i+1] / sampling_rate if i+1 < len(rpeaks) else start_time + 1
            arrhythmia_intervals.append((start_time, end_time))

    return predictions, arrhythmia_intervals

def interpret_prediction(predictions):
    most_common = predictions[0]
    return class_labels.get(most_common, 'Unknown condition')

if __name__ == '__main__':
    app.run(debug=True)
