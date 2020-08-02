from google.cloud import firestore,storage
import numpy as np
import os
import cv2
import smtplib
from geopy.geocoders import Nominatim
from datetime import datetime

os.environ["GOOGLE_APPLICATION_CREDENTIALS"] = "../../Downloads/garbagedetection-711ee-firebase-adminsdk-5bex3-df40124202.json"
db = firestore.Client()
def GenerateRandomCoordinates():
	return [np.random.uniform(23.2219,23.3256), np.random.uniform(77.4063,77.4064)]
def Add_Coords(lon,lat):
	# Add a new document
	db = firestore.Client()
	doc_ref = db.collection(u'Managers').document(u'GaYbpcstLnf0uAcYfWSAJG7Rudt2').collection(u'Locations')
	doc_ref.add({
				u'co-ords': firestore.GeoPoint(lon,lat)
				})

def imgUpload(lon,lat,tmnow,img):
	client = storage.Client()
	bucket = client.get_bucket('garbagedetection-711ee.appspot.com')
	cloud_dir = str(lat)[:7].ljust(7,'0')+'-'+str(lon)[:7].ljust(7,'0')+"/"
	name = str(tmnow.month)+'-'+str(tmnow.day)+'-'+str(tmnow.hour)+'-'+str(tmnow.minute)+'.jpg'
	Blob = bucket.blob(cloud_dir+name)
	encoded, enimg = cv2.imencode('.jpg',img)
	if not Blob.exists():
		Blob.upload_from_string(enimg.tobytes(),content_type='image/jpeg')
		print("Image Uploaded")

geolocator = Nominatim(user_agent="specify_your_app_name_here")

def send_mail(sender_email,app_password,target,imCords):
	message="Garbage has been detected in new loactions in your area\n"
	for x in imCords:
		try:
			location = geolocator.reverse(str(x[0])+","+str(x[1]))
			message += location.address+":\t\n"+"https://www.google.com/maps/place/"+str(x[0])+","+str(x[1])+"\n"
		except:
			pass
	obj = smtplib.SMTP('smtp-mail.outlook.com',587)
	obj.ehlo()
	obj.starttls()
	obj.login(sender_email,app_password)
	from_add = sender_email
	to_address = target
	subject = "New Locations added to your area"
	msg= "Subject: "+subject+'\n\n'+message
	print(obj.sendmail(from_add,to_address,msg))
	print("Mail Sent")