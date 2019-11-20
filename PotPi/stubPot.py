#!/usr/bin/env python

import socket, sys, time
import serial

debug = True

hubIP = '192.168.0.190'

# Setting up UDP listener
s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
port = 1854
server_address = ('localhost', port)
s.bind(server_address)

# Reads current sensor data from file and sends data to hub
def reqPotData():
    print("INFO: Data requested")
    dataFile = open("data.txt", 'r')
    data = dataFile.read()
    dataFile.close()
    message = "ReportPotData " + data
    
    if debug:
        print ("DEBUG: Data message: " + message)
    
    s.sendto(message, (hubIP, port))
    print ("INFO: Data sent to hub")

# Requests Arduino triggers water pump to water plant by sending 'w' char
# each 'w' sent turns the pump on for one second
def waterPlant(length):
    print ("INFO: Water requested")
    message = ""
    for i in range(length):
        message += "w"
        
    if debug:
        print ("DEBUG: Water message: " + message)
    
    print ("STUB: Simulated message to Arduino: " + message)
    s.sendto("WaterAck", (hubIP, port))
    print ("INFO: Plant watered")


while True:
    print ("\nINFO: Waiting to receive request from hub at: " + str(hubIP) + " port " + str(port) + "\n")
    
    buf, address = s.recvfrom(port)
    if not len(buf):
        print("ERROR: Received empty data" )
        break
    
    print ("INFO: Received request from hub")
    
    if debug:
        print ("DEBUG: request: " + buf)

    if buf.split()[0] == "RequestPotData":
        reqPotData()
    elif buf.split()[0] == "WaterPlant":
        waterPlant(int(buf.split()[1]))
    else:
        print("ERROR: Unknown request: " + buf)

s.shutdown(1)
