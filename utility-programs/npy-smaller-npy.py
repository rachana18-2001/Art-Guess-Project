"""
'npy-to-smaller-npy.py' converts randomly selected n (28,28) numpy array to and saves them to
destined folder with n arrays
"""
import numpy as np
from matplotlib import image
from random import sample
import os
import sys

dir__ = '/media/mr_paul/ubuntu-files/deep-learning-datasets/doodle-dataset'
__dir = 'dataset'
n = 1000

if len(sys.argv) == 1:
    # no extra input, go with the default values
    pass
else:
    try:
        __dir = sys.argv[1]
        n = int(sys.argv[2])
    except:
        print('use: python npy-to-smaller-npy.py destination-folder-name 1000')
        exit()


# create the dataset folder if not existing
if not os.path.exists(__dir):
    os.mkdir(__dir)

# selecting npy files from raw-dataset folder and passing into the extract_n_image function
for file in os.listdir(dir__):
    img_array = np.load(dir__ + '/' + file)
    images_available = img_array.shape[0]
    arr = []
    for i in sample(range(0, images_available), n):
        arr.append(img_array[i])
    np.save(__dir + '/' + file, np.array(arr))


print('Extracted ' + str(len(os.listdir(dir__))) + ' categories in \'' + __dir + '\' folder.')

















# end
