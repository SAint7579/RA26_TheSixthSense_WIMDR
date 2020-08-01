import pynmea2
import serial
import time
import string


def GetLocation():
	port="/dev/ttyAMA0"
	ser=serial.Serial(port, baudrate=9600, timeout=0.5)
	dataout = pynmea2.NMEAStreamReader()
	newdata=ser.readline()

	if newdata[0:6] == "$GPRMC":
		newmsg=pynmea2.parse(newdata)
		lat=newmsg.latitude
		lng=newmsg.longitude
		#gps = "Latitude=" + str(lat) + "and Longitude=" + str(lng)
		return((lat,lng))

if __name__ == "__main__":
	while True:
		print(GetLocation())
