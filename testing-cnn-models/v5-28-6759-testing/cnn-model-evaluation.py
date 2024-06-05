import sys
import os
from keras.models import load_model
from keras.preprocessing import image
import numpy as np

label = ''
for char in open('labels.txt', 'r'):
    label += char
label = label.split('\n')

if len(sys.argv) != 4:
    print('python cnn-model-evaluation.py model-name folder-name image-input-size')
else:
    model_name = sys.argv[1]
    root_folder = sys.argv[2]
    image_input_size = int(sys.argv[3])

    model = load_model(model_name)

    correct_call = 0
    for folder in os.listdir(root_folder):
        temp_correct_call = 0
        index = label.index(folder)
        for immg in os.listdir(root_folder + '/' + folder):
            img = image.load_img(root_folder+'/'+folder+'/'+immg, color_mode='grayscale', target_size=(image_input_size, image_input_size))
            img = image.img_to_array(img)
            img = img / 255
            img = np.where(img < 0.3, 0, 1)
            img = img.reshape(1, image_input_size, image_input_size, 1)
            prediction = model.predict_classes(img)
            if prediction == index:
                temp_correct_call += 1

        print(folder + ':', temp_correct_call, ' out of 50')
        correct_call += temp_correct_call
    print('total correct call: ', correct_call, 'out of', 50*88, 'i.e', (correct_call*100)/(50*88), '%')














# end
