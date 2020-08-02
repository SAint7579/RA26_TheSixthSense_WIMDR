import cv2
import numpy as np
from udp_streamer import *
from threading import Thread
# from db_utils import *

COLOR = (0,255,0)

ssdnet = cv2.dnn.readNetFromTensorflow('trained_model/frozen_inference_graph.pb','trained_model/graph.pbtxt')

handler = udp_handler()
handler.make_listener('0.0.0.0',5555)

with open("../appswd","r") as f:
	EMAIL,PSWD,TRGT=f.read().split('\n')[0].split(',')

imCords = []

def handleUpload(img):
	global imCords
	lat,lon=GenerateRandomCoordinates()
	imCords.append((lat,lon))
	tmnow = datetime.now()
	imgUpload(lon,lat,tmnow,img)

def get_img():
	global handler,img
	buffer = handler.get_data_small()
	if buffer is not None:
		try:
			npimg = np.frombuffer(buffer, dtype=np.uint8)
			img = cv2.imdecode(npimg, 1)
		except Exception as e:
			print(e)

buffer = handler.get_data_small()
img=prev_img=None
try:
	npimg = np.frombuffer(buffer, dtype=np.uint8)
	img = cv2.imdecode(npimg, 1)
except:
	pass
DET_COUNT=0
threads=[]
while True:
	# thread=Thread(target=get_img)
	# thread.setDaemon(True)
	# thread.start()
	get_img()
	if id(img)==id(prev_img):
		continue
	if img is None:
		continue
	rows,cols,channels = img.shape
	ssdnet.setInput(cv2.dnn.blobFromImage(img,size=(300,300),swapRB=True,crop=False))
	netout = ssdnet.forward()
	prev_img=img
	scores=[]
	for detection in netout[0,0]:
		scores.append(float(detection[2]))

	if len(scores)>2:
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
		if score >0.9:
			INC_DET=True
			left=int(detection[3]*cols)
			top=int(detection[4]*rows)
			right=int(detection[5]*cols)
			bottom=int(detection[6]*rows)

			cv2.rectangle(img, (left, top), (right, bottom), COLOR, 2)
			cv2.rectangle(img, (left, top), (left+130, top+20), COLOR, -1)
			cv2.putText(img, str(score*100)[:5]+"% garbage", (left, top+14),cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0,0,0), 1)
	try:
		cv2.imshow("output", cv2.resize(img, (600, 600)))
	except:
		pass
	# if INC_DET:
	# 	DET_COUNT+=1
	# 	if DET_COUNT>=20:
	# 		DET_COUNT=0
	# 		t1=Thread(target=handleUpload,args=(img,))
	# 		t1.setDaemon(True)
	# 		t1.start()
	# 		if len(imCords)>10:
	# 			print("Sending mail")
	# 			t2=Thread(target=send_mail,args=(EMAIL,PSWD,TRGT,imCords,))
	# 			t2.setDaemon(True)
	# 			t2.start()
	# 			imCords=[]
	key = cv2.waitKey(1) & 0xff
	if key == ord('q'):
		break
	# thread.join()

cv2.destroyAllWindows()
handler.close()