# RA26_TheSixthSense_WIMDR

# Smart Waste Locator

  We present our idea of Smart Waste Locator system which is an **Automated Waste Detection System** based on Computer Vision Algorithms.
  
# Modules:

## Waste Detection

We have created a custom model with modified MobileNetv3 as the backbone that finds the segmentation maps of the waste detected. This model works faster than most of the light weight image processing models like SSD MobileNetv2. Along with that, rather than just giving a bounding box, our model gives a full segmentation map that traces the waste.

### Screenshot:

- <b> Our Segmentation map model: </b>
<br>
<img src = "https://github.com/ShivamShrirao/RA26_TheSixthSense_WIMDR/blob/master/Sample_Images/seg_map.png" width="640" height="640"></img>

- <b> Optimized SSD MobileNetv3: </b>
<br>
<img src = "https://github.com/ShivamShrirao/RA26_TheSixthSense_WIMDR/blob/master/Sample_Images/ssd.png" width="640" height="640"></img>


We can see that the frame rate on our model is around 3 times faster than the conventional SSD model. You can check a full output video in the location "RA26_TheSixthSense_WIMDR/blob/master/much_faster_model/segout_bb.mp4".

### Requirements to run the model:
#### Tech Stack:
- Tensorflow 2.3
- Tflite
- OpenCV

#### Instructions:
In the repo directory "RA26_TheSixthSense_WIMDR/much_faster_model/", run the "proc_video.py" file with all the above libraries installed. The video source there can be replaced by any file or by the camera output by initializing the "cam" variable to cv2.VideoCapture(0).


## Waste Segregation

This model works on the server side. Using SSDMobileNetv2, we can identify different elements of the garbage dump. This will help the managers and collectors to identify the type of dump, therefore enabling them to facilitate the waste segregation during collection.

### Screenshot:
<img src = "https://github.com/ShivamShrirao/RA26_TheSixthSense_WIMDR/blob/master/Sample_Images/segregation.PNG"></img>

### Requirements to run the model:
#### Tech Stack:
- Tensorflow 2.3
- OpenCV

#### Instructions:
In the repo directory "RA26_TheSixthSense_WIMDR/segregation_model/", open the "proc_video.py" file and add an image source to the variable IMDIR. Running the code will show you the output of the image with the type of waste tagged. You can also check some created outputs in the directory "RA26_TheSixthSense_WIMDR/segregation_model/detect_out/".

## Manager and collector ecosystem

### Manager Website and app
This site and app allows the manager to see the detected waste and assign the tasks to ragpickers. These will be uploaded to our firebase DB.<br>
Manager's app : "RA26_TheSixthSense_WIMDR/SmartWasteDetector/"<br>
Manager's website : "RA26_TheSixthSense_WIMDR/SmartWasteWebsite"

### Tech Stack:
- Android and Andriod Studios
- Javascript
- CSS
- Google Maps API
- HTML
- Firebase

### Screenshot:
- <b>App:</b><br>
<img src = "https://github.com/ShivamShrirao/RA26_TheSixthSense_WIMDR/blob/master/Sample_Images/mngr.png" width="300" height="600"></img>
- <b>Website:</b>
<img src = "https://github.com/ShivamShrirao/RA26_TheSixthSense_WIMDR/blob/master/Sample_Images/mngr-web.png" ></img>

### Ragpicker's app
This app shows the assigned task to the ragpickers and help them navigate to it. This system also let's them mark the completion of the task. This can be done by clicking the picture of the area with the application and using computer vision to ensure that the picture has not garpage in it. If the picture is clean, then the ragpicker can upload the image. <br>
Ragpicker's app : "RA26_TheSixthSense_WIMDR/Ragpicker-App/"<br>

### Tech Stack:
- Flutter
- Tensorflow Lite
- Google Maps API
- Firebase

### Screenshot:
<img src = "https://github.com/ShivamShrirao/RA26_TheSixthSense_WIMDR/blob/master/Sample_Images/collector_app.png" width="300" height="600"></img>

## Others

### Device Render:
This is a basic render of the device made on rhinoceros 6. It represents the basic design of the device which will be mounted on the vehicles to detect garbage.<br>
File Location : "RA26_TheSixthSense_WIMDR/Device Render.3dm"
This file require rhinoceros 6 to view.
### The Device:
<img src = "https://github.com/ShivamShrirao/RA26_TheSixthSense_WIMDR/blob/master/Sample_Images/device_render.gif"  width="500" height="320"></img>


### Activity Monitoring System:
This is a simple webpage that tells the administrator about which devices are active. This is done by sending a ping every one minute to a central server. If a ping is not recived for 2 minutes, the device is declared offline. 
### Screenshot:
<img src = "https://github.com/ShivamShrirao/RA26_TheSixthSense_WIMDR/blob/master/Sample_Images/monitor.png"></img>

### Imagetagger App
This is the application which we used to make our custom dataset.

### Misc
- Firebase connectivity
- GPS location fetcher
- Waste Index calculator
- UDP Streaming between source and server for drones


