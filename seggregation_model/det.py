import cv2
import numpy as np
from threading import Thread
import os
import sys

COLORS = [
			(0, 0, 0),
			(0, 255, 0),
			(255, 255, 0),
			(128, 128, 0),
			(0, 255, 0),
			(0, 0, 200),
			(255, 0, 0),
			(128, 0, 128)
		]
labels = {
	1 : 'Plastic',
	2 : 'Unlabeled_litter',
	3 : 'Metal',
	4 : 'Glass',
	5 : 'Paper',
	6 : 'Bottle',
	7 : 'Food_waste',
}
ssdnet = cv2.dnn.readNetFromTensorflow('frozen_inference_graph.pb','graph.pbtxt')

# cam = cv2.VideoCapture('/home/archer/machine_learning/garbage_detection/vid5.mp4')
# cam = cv2.VideoCapture(0)
fps = 12#round(cam.get(cv2.CAP_PROP_FPS))
# out = cv2.VideoWriter('out5.mp4',cv2.VideoWriter_fourcc(*'mp4v'), fps,(600,600))

cv2.namedWindow("output", cv2.WINDOW_NORMAL)
cv2.resizeWindow("output", 900,600)

IMDIR = "/home/archer/machine_learning/garbage_detection/garbage_dataset/"
images = os.listdir(IMDIR)
images = ["5a0b5618-679c-11e5-aa4a-40f2e96c8ad8.jpg"]

for imname in images:
	# ret, img = cam.read()
	img = cv2.imread(IMDIR + imname)
	img = cv2.rotate(img,cv2.ROTATE_90_CLOCKWISE)
	rows,cols,channels = img.shape
	ssdnet.setInput(cv2.dnn.blobFromImage(img,size=(1000,1000),swapRB=True,crop=False))
	netout = ssdnet.forward()

	scores=[]
	for detection in netout[0,0]:
		scores.append(float(detection[2]))

	INC_DET=False
	for idx in range(len(scores)):
		detection=netout[0,0][idx]
		obj_nm = detection[1]
		score = float(detection[2])
		if score >0.5:
			INC_DET=True
			left=int(detection[3]*cols)
			top=int(detection[4]*rows)
			right=int(detection[5]*cols)
			bottom=int(detection[6]*rows)

			cv2.rectangle(img, (left, top), (right, bottom), COLORS[int(obj_nm)], 2)
			cv2.rectangle(img, (left, top), (left+130, top+20), (*COLORS[int(obj_nm)],), -1)
			cv2.putText(img, str(score*100)[:5]+"% "+labels[obj_nm], (left, top+14),cv2.FONT_HERSHEY_SIMPLEX, 1.5, (0,0,0), 5)
	# img=cv2.resize(img, (600, 600))
	cv2.imshow("output", img)
	# out.write(img)
	while True:
		key = cv2.waitKey(1) & 0xff
		if key == ord('q'):
			break
		if key == ord('c'):
			sys.exit(0)
# out.release()
# cam.release()
cv2.destroyAllWindows()