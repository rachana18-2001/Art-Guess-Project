from keras.preprocessing import image
import numpy as np
import sys

INPUT_IMAGE_SIZE = 28

img = image.load_img(sys.argv[1], target_size=(INPUT_IMAGE_SIZE,INPUT_IMAGE_SIZE), color_mode='grayscale')
img = image.img_to_array(img)
img = img.reshape(INPUT_IMAGE_SIZE**2)
img = np.round(img/255)
# for i in range(INPUT_IMAGE_SIZE**2):
# 	if img[i] != 255:
# 		img[i] = 0
# 	else:
# 		img[i] = 1
from keras.models import load_model
classifier = load_model('old-model.h5')
import matplotlib.pyplot as plt
plt.imshow(img.reshape(INPUT_IMAGE_SIZE,INPUT_IMAGE_SIZE), cmap='gray')
plt.show()
pred = classifier.predict_classes(img.reshape(1,INPUT_IMAGE_SIZE,INPUT_IMAGE_SIZE,1))
print(pred)
