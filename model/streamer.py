import cv2
import sys
from udp_streamer import *
from imutils.video import VideoStream

IP=sys.argv[1]
PORT=int(sys.argv[2])
DIV_FACT=int(sys.argv[3])

handler = udp_handler()

cam = cv2.VideoCapture(0)

# handler.connect(IP,PORT)

f=0
while True:
	ret,img = cam.read()
	f+=1
	if not f%DIV_FACT:
		f=0
		try:
			ret,img = cam.read()
			img = cv2.resize(img, (300, 300))
			encoded, enimg = cv2.imencode('.jpg',img)
			handler.send_data_small(enimg,IP,PORT)
		except KeyboardInterrupt:
			cam.release()
			cv2.destroyAllWindows()
			break

handler.close()