dict = {'The_Eiffel_Tower': 0, 'The_Mona_Lisa': 1, 'airplane': 2, 'ambulance': 3, 'angel': 4, 'ant': 5, 
		'apple': 6, 'axe': 7, 'banana': 8, 'basket': 9, 'beach': 10, 'bicycle': 11, 'birthday_cake': 12, 'book': 13, 
		'boomerang': 14, 'brain': 15, 'broccoli': 16, 'bulldozer': 17, 'butterfly': 18, 'cactus': 19, 'calculator': 20, 
		'camel': 21, 'castle': 22, 'ceiling_fan': 23, 'circle': 24, 'compass': 25, 'cow': 26, 'cruise ship': 27, 
		'cruise_ship': 28, 'cup': 29, 'dolphin': 30, 'dragon': 31, 'duck': 32, 'elephant': 33, 'envelope': 34, 
		'eye': 35, 'face': 36, 'feather': 37, 'fish': 38, 'flip_flops': 39, 'flower': 40, 'frog': 41, 'giraffe': 42,
		'grapes': 43, 'grass': 44, 'guitar': 45, 'hammer': 46, 'hand': 47, 'hat': 48, 'helicopter': 49, 'hospital': 50,
		'house': 51, 'ice_cream': 52, 'jacket': 53, 'jail': 54, 'kangaroo': 55, 'key': 56, 'keyboard': 57, 'ladder': 58,
		'leaf': 59, 'leg': 60, 'light_bulb': 61, 'lightning': 62, 'lion': 63, 'lipstick': 64, 'lollipop': 65, 'map': 66, 
		'mermaid': 67, 'mosquito': 68, 'mountain': 69, 'mouse': 70, 'mushroom': 71, 'necklace': 72, 'nose': 73, 'ocean': 74,
		'octopus': 75, 'onion': 76, 'pants': 77, 'parrot': 78, 'pencil': 79, 'piano': 80, 'pig': 81, 'pillow': 82, 'pizza': 83,
		'pond': 84, 'rabbit': 85, 'radio': 86, 'rain': 87, 'rainbow': 88, 'rifle': 89, 'rollerskates': 90, 'sandwich': 91, 
		'saw': 92, 'scissors': 93, 'sea_turtle': 94, 'see_saw': 95, 'shark': 96, 'skull': 97, 'spider': 98, 'stairs': 99, 
		'star': 100, 'stereo': 101, 'strawberry': 102, 'swan': 103, 'sword': 104, 't_shirt': 105, 'teddy_bear': 106, 'television': 107, 
		'tent': 108, 'tiger': 109, 'toothbrush': 110, 'tree': 111, 'umbrella': 112, 'underwear': 113, 'vase': 114, 'washing_machine': 115, 
		'whale': 116, 'wheel': 117, 'windmill': 118, 'wine_glass': 119, 'wristwatch': 120, 'zebra': 121, 'zigzag': 122}

output = ''
"""
for (key,value) in dict.items():
	output += (key.replace('_', ' ')).capitalize() + '\n'

"""
for (key,value) in dict.items():
	output += key + '\n'

open('labels', 'w').write(output)


