import tensorflow as tf
import sys

converter = tf.lite.TFLiteConverter.from_keras_model_file(sys.argv[1])
tflite_model = converter.convert()
open('cnn-model.tflite', 'wb').write(tflite_model)
