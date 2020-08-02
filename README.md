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



