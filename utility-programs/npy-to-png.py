"""
'npy-to-png.py' converts randomly selected 1000 (28,28) numpy array to png image file
and categorises it according to the npy file name
"""
import numpy as np
from matplotlib import image
from random import sample
import os
import sys

dir__ = 'dataset-6000'
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
        print('use: python npy-to-png.py destination-folder-name 1000')
        exit()

def extract_n_image(filename, n=1000):
    """Function that randomly selects n (default=1000) numpy array to png formatted images,
    and saves in the 'filename' named category folder
    """
    print('Pulling ' + str(n) + ' images from ' + filename + '...', end=' ')
    img_array = np.load(dir__ + '/' + filename)
    imgs_available = img_array.shape[0]
    if not os.path.exists(__dir + '/' + filename[:-4]):
        os.mkdir(__dir + '/' + filename[:-4])
    for i in sample(range(0, imgs_available), n):
        image.imsave(__dir + '/' + filename[:-4]+ '/' +str(i) + '.png', img_array[i].reshape(28,28), cmap='Greys', format='png')
    print('Done')

# create the dataset folder if not existing
if not os.path.exists(__dir):
    os.mkdir(__dir)

# selecting npy files from raw-dataset folder and passing into the extract_n_image function
for file in os.listdir(dir__):
    extract_n_image(file, n)

print('Extracted ' + str(len(os.listdir(dir__))) + ' categories in \'' + __dir + '\' folder.')
# end
