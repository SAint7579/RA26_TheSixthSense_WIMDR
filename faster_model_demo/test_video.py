import tflite_runtime.interpreter as tflite
#import tensorflow as tf
import numpy as np
import cv2
import matplotlib.pyplot as plt
from time import time

interpreter = tflite.Interpreter(model_path="./my_model_fp32_256x256.tflite")
interpreter.allocate_tensors()
input_info = interpreter.get_input_details()
output_info = interpreter.get_output_details()

input_shape = input_info[0]['shape']

start = time()
frame_count = 0

cap = cv2.VideoCapture('vid2.mp4')

while(cap.isOpened()):
    _,frame = cap.read()
    resized = cv2.resize(frame,(256,256))
    input_image = cv2.cvtColor(resized,cv2.COLOR_BGR2RGB).astype(np.float32)
    input_image = input_image/255
    interpreter.set_tensor(input_info[0]['index'], np.expand_dims(input_image,axis=0))
    interpreter.invoke()
    output_data = interpreter.get_tensor(output_info[0]['index'])

    #Making a mask

    A = output_data.squeeze()
    A[A<0.5] = 0
    A[A>=0.5] = 1
    A = A.astype(np.uint8)
    B = (output_data.squeeze()*255).astype(np.uint8)

    mask = cv2.resize(A,(256,256))
    temp = resized.copy()
    contours,_ = cv2.findContours(mask,cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)
    if len(contours) > 0:
        c = max(contours,key=cv2.contourArea)
        if cv2.contourArea(c)>2000:
            #Getting the bounding rectangle
            x,y,w,h = cv2.boundingRect(c)
            #Drawing the bounding rectangle
            cv2.rectangle(temp,(x,y),(x+w,y+h),(0,255,0),2)
            #Getting the moments
            m = cv2.moments(c)
            #moving mouse to the centroid
    #cv2.drawContours(temp,contours,-1,(255,0,0),2)
    heatmap_img = cv2.applyColorMap(cv2.resize(B,(256,256)), cv2.COLORMAP_HOT)
    added_image = cv2.addWeighted(heatmap_img,0.6,temp,1,0)            
    cv2.imshow("Frame",added_image)
    if cv2.waitKey(25) & 0xFF == ord('q'):
      break

    frame_count += 1

cap.release()
cv2.destroyAllWindows()
print("FPS: ",frame_count/(time()-start))
