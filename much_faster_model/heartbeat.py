from time import sleep
import requests

API_ENDPOINT = "http://annotate.ret2rop.com:31896/monitoring/"
uid = "first"
data = {"uid": uid, "auth-token": "ENTER_AUTH_TOKEN"}


while True:
	r = requests.post(url = API_ENDPOINT, data = data)
	print(r)
	sleep(60)