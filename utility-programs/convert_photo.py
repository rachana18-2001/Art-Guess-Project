import sys
import numpy as np

from keras.preprocessing import image
img = image.load_img(sys.argv[1], color_mode='grayscale', target_size=(28,28))
img = image.img_to_array(img)
img = img.reshape(28*28)
img = img/255
img = np.where(img < float(sys.argv[2]), 0, 1)
# for i in range(28*28):
# 	if img[i] < 0.3:
# 		img[i] = 0
# 	else:
# 		img[i] = 1
import matplotlib.pyplot as plt
plt.imshow(img.reshape(28,28), cmap='gray')
plt.show()

