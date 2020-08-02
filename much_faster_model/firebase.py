from google.cloud import storage
import os
from datetime import datetime
import firebase_admin
from firebase_admin import credentials
from firebase_admin import db
import googlemaps
from time import time

# Fetch the service account key JSON file contents
try:
    cred = credentials.Certificate('../smart-waste-locator-firebase-adminsdk-ljjzx-495a7e327a.json')
    # Initialize the app with a service account, granting admin privileges
    firebase_admin.initialize_app(cred, {
        'databaseURL': 'https://smart-waste-locator.firebaseio.com/'
    })
except:
    print("Already Initialized")

with open("../appkey","r") as f:
    appkey = f.read()[:-1]

gmaps_keys = googlemaps.Client(key = appkey)
    

def cloud_upload_image(image):
    try:
        os.environ["GOOGLE_APPLICATION_CREDENTIALS"]="../smart-waste-locator-firebase-adminsdk-ljjzx-495a7e327a.json"
        # Enable firebase Storage
        client = storage.Client()
        # Reference an existing bucket.
        bucket = client.get_bucket('smart-waste-locator.appspot.com')
        # Upload a local file to a new file to be created in your bucket.
        imname = str(time())+'.jpg'
        # path = "./"+imname
        Blob = bucket.blob(imname)
        encoded, enimg = cv2.imencode('.jpg', image)
        if not Blob.exists():
            Blob.upload_from_string(enimg.tobytes(), content_type='image/jpeg')
            print("Image Uploaded")
        # Blob.upload_from_filename(filename=path)
        #returning the url
        return 'gs://smart-waste-locator.appspot.com/'+imname
    except:
        return None
    

def add_data(image,latitude,longitude):
    link = cloud_upload_image(image)
    if link != None:
        key = '-'.join([''.join(str(latitude).split('.')) , ''.join(str(longitude).split('.'))])
        ref = db.reference('Detected/')
        child = ref.child(key)
        date = datetime.now()
        date = str(date.day)+"/"+str(date.month)+"/"+str(date.year)
        result = gmaps_keys.reverse_geocode((latitude, longitude))
        area = result[0]['address_components'][0]['long_name']
        pincode = result[0]['address_components'][-1]['long_name']
        child.set(
            {
                "Cleaned":"False",
                "TimeStamp":date,
                "image":link,
                "Area":area,
                "Pincode":pincode,
                'latitude':latitude,
                'longitude':longitude,
                'collectorid':-1
            })
        return True
    else:
        return False

def GenerateRandomCoordinates():
    #Generate Random Coordiantes in Pune
    return [np.random.uniform(18.4311,18.5995), np.random.uniform(73.7469,73.9474)]
