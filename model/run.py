import cv2
import numpy as np
from threading import Thread
import os
import sys
from time import time

ssdnet = cv2.dnn.readNetFromTensorflow('trained_model/frozen_inference_graph.pb','trained_model/graph.pbtxt')

cam = cv2.VideoCapture('vid2.mp4')
# cam = cv2.VideoCapture(0)
fps = 13#round(cam.get(cv2.CAP_PROP_FPS))
out = cv2.VideoWriter('ssdout.mp4',cv2.VideoWriter_fourcc(*'mp4v'), fps,(320,320))

cv2.namedWindow("output", cv2.WINDOW_NORMAL)
cv2.resizeWindow("output", 600,600)

start = time()
counter = 0
while cam.isOpened():
	ret, img = cam.read()
	if not ret:
		break
	# img = cv2.imread(IMDIR + imname)
	# img = cv2.rotate(img,cv2.ROTATE_90_CLOCKWISE)
	img = cv2.resize(img, (320, 320))
	rows,cols,channels = img.shape
	ssdnet.setInput(cv2.dnn.blobFromImage(img,size=(320,320),swapRB=True,crop=False))
	netout = ssdnet.forward()

	scores=[]
	for detection in netout[0,0]:
		scores.append(float(detection[2]))

	for idx in range(len(scores)):
		detection=netout[0,0][idx]
		obj_nm = detection[1]
		score = float(detection[2])
		if score >0.5:
			left=int(detection[3]*cols)
			top=int(detection[4]*rows)
			right=int(detection[5]*cols)
			bottom=int(detection[6]*rows)

			cv2.rectangle(img, (left, top), (right, bottom), (0,255,0), 2)
			cv2.rectangle(img, (left, top), (left+130, top+20), (0,255,0), -1)
			cv2.putText(img, str(score*100)[:5]+"% garbage", (left, top+14),cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0,0,0), 1)
	# img=cv2.resize(img, (600, 600))
	cv2.putText(img, str(fps)[:5]+" FPS", (0, 15),cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0,255,255), 1)
	cv2.imshow("output", img)
	counter+=1
	if (time() - start) > 1:
		fps = counter / (time() - start)
		# print("FPS: ", fps)
		counter = 0
		start = time()
	out.write(img)
	key = cv2.waitKey(1) & 0xff
	if key == ord('q'):
		break
out.release()
cam.release()
cv2.destroyAllWindows()