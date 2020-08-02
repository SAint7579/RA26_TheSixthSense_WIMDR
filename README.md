# RA26_TheSixthSense_WIMDR

## Smart Waste Locator

  We present our idea of Smart Waste Locator system which is an **Automated Waste Detection System** based on Computer Vision Algorithms.
  
  There are various aspects associated with it that include - 
  
  1. Our Customised fully convoluted Architecture, which has provened to be superior than the existing Comp Vision Algorithms that can be used for Garbage Detecton. 
  It works at 36 FPS with more than 96.5% Accuracy on the other hand, the SSD Mobile Net 2 which is being used for this task earlier has 13.5 FPS with lower precision 
  on the same device at same resolution. Instead of just getting a rectagular bounding box, our model also generate a segmentation map accurately depicting area and layout of garbage.
  Our model is more faster and lightweight, accurate than any existing system and we are planning to initiate the Patenting process for our System Architecture after the competition.
  
  2. The algorithm is embedded in our Customised Device which we are planning to make by ourself. This device shall be mounted over any moving vehicle and as the vehicles like City Buses, 
  Taxi, etc moves through out the city, the device would capture the garbage along with its Geolocations on the map and send the data to the central cloud server.
  NOTE - We are just uploading the Images and Location of the detected garbage and not the Video feed, so it won't violate any privacy of the citizens.
  
  3. The central server which we are going with is Gogle Firebase, it is connected with a Website and Mobile App of Garbage Pickers.
