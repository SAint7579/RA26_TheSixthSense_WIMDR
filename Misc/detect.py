import cv2
import numpy as np
from threading import Thread
# from db_utils import *

COLOR = (0,255,0)

ssdnet = cv2.dnn.readNetFromTensorflow('trained_model/frozen_inference_graph.pb','trained_model/graph.pbtxt')

# ssdnet.setPreferableBackend(cv2.dnn.DNN_BACKEND_CUDA)
# ssdnet.setPreferableTarget(cv2.dnn.DNN_TARGET_CUDA)

cam = cv2.VideoCapture('vid2.mp4')
# cam = cv2.VideoCapture(0)
fps = 12#round(cam.get(cv2.CAP_PROP_FPS))
out = cv2.VideoWriter('out2.mp4',cv2.VideoWriter_fourcc(*'mp4v'), fps,(600,600))

cv2.namedWindow("output", cv2.WINDOW_NORMAL)
cv2.resizeWindow("output", 900,600)

# with open("../appswd","r") as f:
# 	EMAIL,PSWD,TRGT=f.read().split('\n')[0].split(',')

imCords = []

def handleUpload(img):
	global imCords
	lat,lon=GenerateRandomCoordinates()
	imCords.append((lat,lon))
	tmnow = datetime.now()
	imgUpload(lon,lat,tmnow,img)

DET_COUNT=0
while cam.isOpened():
	ret, img = cam.read()
	if img is None:
		break
	# try:
	# 	img = img[:,:-900]
	# 	img = cv2.resize(img,(600,600))
	# except:
	# 	break
	# img = np.rot90(img)
	# img = np.rot90(img)
	# img = np.ascontiguousarray(np.rot90(img))
	rows,cols,channels = img.shape
	ssdnet.setInput(cv2.dnn.blobFromImage(img,size=(300,300),swapRB=True,crop=False))
	netout = ssdnet.forward()

	scores=[]
	for detection in netout[0,0]:
		scores.append(float(detection[2]))

	if len(scores)>200:
		first=np.argmax(scores)
		scores.pop(first)
		second=np.argmax(scores)
		idtxs=[first,second]
	else:
		idtxs = range(len(scores))

	INC_DET=False
	for idx in idtxs:
		detection=netout[0,0][idx]
		score = float(detection[2])
		if score >0.5:
			INC_DET=True
			left=int(detection[3]*cols)
			top=int(detection[4]*rows)
			right=int(detection[5]*cols)
			bottom=int(detection[6]*rows)

			cv2.rectangle(img, (left, top), (right, bottom), COLOR, 2)
			cv2.rectangle(img, (left, top), (left+130, top+20), (*COLOR,), -1)
			cv2.putText(img, str(score*100)[:5]+"% garbage", (left, top+14),cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0,0,0), 1)
	# img=cv2.resize(img, (600, 600))
	cv2.imshow("output", img)
	# if INC_DET:
	# 	DET_COUNT+=1
	# if DET_COUNT>=20:
	# 	DET_COUNT=0
	# 	t1=Thread(target=handleUpload,args=(img,))
	# 	t1.setDaemon(True)
	# 	t1.start()
	# 	if len(imCords)>5:
	# 		print("Sending mail")
	# 		t2=Thread(target=send_mail,args=(EMAIL,PSWD,TRGT,imCords,))
	# 		t2.setDaemon(True)
	# 		t2.start()
	# 		imCords=[]
	out.write(img)
	key = cv2.waitKey(1) & 0xff
	if key == ord('q'):
		break

out.release()
cam.release()
cv2.destroyAllWindows()