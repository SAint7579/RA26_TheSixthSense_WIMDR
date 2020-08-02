import json
import random
from PIL import Image, ExifTags

dataset_path = '../TACO/data/'
anns_file_path = dataset_path + 'annotations.json'

for orientation in ExifTags.TAGS.keys():
	if ExifTags.TAGS[orientation] == 'Orientation':
		break

# Read annotations
with open(anns_file_path, 'r') as f:
	dataset = json.loads(f.read())

images=dataset['images']
categories=dataset['categories']

shrtr={
	'Plastic_bag_&_wrapper':	'Plastic',
	'Plastic_glooves':			'Plastic',
	'Plastic_container':		'Plastic',
	'Plastic_utensils':			'Plastic',
	'Other_plastic':			'Plastic',
	'Cup':						'Plastic',
	'Lid':						'Plastic',
	'Straw':					'Plastic',
	'Bottle_cap':				'Plastic',
	'Squeezable_tube':			'Plastic',
	'Styrofoam_piece':			'Plastic',

	'Unlabeled_litter':			'Unlabeled_litter',

	'Pop_tab':					'Metal',
	'Aluminium_foil':			'Metal',
	'Battery':					'Metal',
	'Blister_pack':				'Metal',
	'Can':						'Metal',
	'Scrap_metal':				'Metal',

	'Broken_glass':				'Glass',
	'Glass_jar':				'Glass',

	'Carton':					'Paper',
	'Paper':					'Paper',
	'Paper_bag':				'Paper',
	'Carton':					'Paper',
	'Cigarette':				'Paper',

	'Rope_&_strings':			None,
	'Shoe':						None,

	'Bottle':					'Bottle',

	'Food_waste':				'Food_waste',
}

labels=[]
for ann in dataset['annotations']:
	img=images[ann['image_id']]
	fname=img['file_name']
	width=img['width']
	height=img['height']
	clas=categories[ann['category_id']]['supercategory'].replace(' ','_')
	clas=shrtr[clas]
	if clas == None:
		continue
	x, y, w, h = ann['bbox']
	xmin=x
	ymin=y
	xmax=x+w
	ymax=y+h
	im = Image.open(dataset_path+fname)
	if xmin<0:
		xmin=0
	if ymin<0:
		ymin=0
	# if im._getexif():
	# 	exif = dict(im._getexif().items())
	# 	# Rotate portrait and upside down images if necessary
	# 	if orientation in exif:
	# 		if exif[orientation] == 3:
	# 			im = im.rotate(180,expand=True)
	# 		if exif[orientation] == 6:
	# 			im = im.rotate(270,expand=True)
	# 		if exif[orientation] == 8:
	# 			im = im.rotate(90,expand=True)
	# 		im.save(dataset_path+fname)
	w,h=im.size
	if w!=width:
		print("width",w,[fname,width,height,clas,xmin,ymin,xmax,ymax])
	if h!=height:
		print("height",h,[fname,width,height,clas,xmin,ymin,xmax,ymax])
	labels.append([fname,width,height,clas,xmin,ymin,xmax,ymax])

for i in range(10):
	random.shuffle(labels)

cut=4500
print(f'train_labels_cls.csv:\t{cut}')
print(f'test_labels_cls.csv:\t{len(labels)-cut}')

with open('train_labels_cls.csv','w') as f:
	f.write("filename,width,height,class,xmin,ymin,xmax,ymax\n")
	for i in labels[:cut]:
		f.write(','.join(map(str,i))+'\n')

with open('test_labels_cls.csv','w') as f:
	f.write("filename,width,height,class,xmin,ymin,xmax,ymax\n")
	for i in labels[cut:]:
		f.write(','.join(map(str,i))+'\n')

lblmap={}
idx=1
for sct in shrtr.values():
	if sct!=None:
		try:
			lblmap[sct]
		except KeyError:
			lblmap[sct]=idx
			idx+=1

with open('label_map_cls.pbtxt','w') as f:
	for i in lblmap:
		f.write("item {\n\tid: %d\n\tname: '%s'\n}\n\n" % (lblmap[i],i.replace(' ','_')))

print(f'label_map_cls.pbtxt\t{idx-1}')
print("Files generated.")