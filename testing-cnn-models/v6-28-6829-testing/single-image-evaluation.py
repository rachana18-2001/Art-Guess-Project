from keras.preprocessing import image
import numpy as np
import sys

if len(sys.argv) != 3:
	print('use: python single-image-evaluation.py image_name model_name')

INPUT_IMAGE_SIZE = 28

img = image.load_img(sys.argv[1], target_size=(INPUT_IMAGE_SIZE,INPUT_IMAGE_SIZE), color_mode='grayscale')
img = image.img_to_array(img)
img = img.reshape(INPUT_IMAGE_SIZE**2)
img = np.where(img < 0.3, 0, 1)

from keras.models import load_model
classifier = load_model(sys.argv[2])
import matplotlib.pyplot as plt
plt.imshow(img.reshape(INPUT_IMAGE_SIZE,INPUT_IMAGE_SIZE), cmap='gray')
plt.show()
pred = classifier.predict_classes(img.reshape(1,INPUT_IMAGE_SIZE,INPUT_IMAGE_SIZE,1))
print(pred)
