import socket
from struct import pack,unpack
import numpy as np
from time import time,sleep

class tcp_handler(object):
	def __init__(self):
		self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

	def make_listener(self,ip,port):
		self.sock.bind((ip,port))
		self.sock.listen()
		self.conn,addr=self.sock.accept()

	def connect(self,ip,port):
		self.sock.connect((ip,port))

	def send_data(self,img,ip,port):
		data=img.tobytes()
		dln=pack('f',np.float32(len(data)))
		self.sock.send(dln+data)

	def get_data(self):
		bdln = self.conn.recv(4)
		dln=int(unpack('f', bdln)[0])
		return self.conn.recv(dln)

	def close(self):
		self.sock.close()