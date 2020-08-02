from google.cloud import storage
import os
from datetime import datetime
import firebase_admin
from firebase_admin import credentials
from firebase_admin import db
import googlemaps
import numpy as np
# Fetch the service account key JSON file contents
try:
    cred = credentials.Certificate('../../smart-waste-locator-firebase-adminsdk-ljjzx-495a7e327a.json')
    # Initialize the app with a service account, granting admin privileges
    firebase_admin.initialize_app(cred, {
        'databaseURL': 'https://smart-waste-locator.firebaseio.com/'
    })
except:
    print("Already Initialized")
    
gmaps_keys = googlemaps.Client(key = "AIzaSyDeuccZuXIp4Ncemlzgqs8YoKfg3xixJ-c")
    

def cloud_upload_image(image):
    try:
        os.environ["GOOGLE_APPLICATION_CREDENTIALS"]="../../smart-waste-locator-firebase-adminsdk-ljjzx-495a7e327a.json"
        # Enable firebase Storage
        client = storage.Client()
        # Reference an existing bucket.
        bucket = client.get_bucket('smart-waste-locator.appspot.com')
        # Upload a local file to a new file to be created in your bucket. 
        path = "./"+image
        Blob = bucket.blob(image)
        Blob.upload_from_filename(filename=path)
        #returning the url
        return 'https://firebasestorage.googleapis.com/v0/b/smart-waste-locator.appspot.com/o/'+image+'?alt=media'
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
                "Notified": False,
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
    return [float("%1.4f"%(np.random.uniform(18.4311,18.5995))), float("%1.4f"%(np.random.uniform(73.7469,73.9474)))]


if __name__ == "__main__":
    image = "../../img_waste.jpg"
    coords = GenerateRandomCoordinates()
    print(coords)
    print(add_data(image,coords[0],coords[1]))
