#!/usr/bin/env python

import serial, time

debug = True

data = ""
discard = 5 # Number of initial data points to discard (when system is starting up)

data = ["{\"lowWater\": false, \"soilMoisture\": \"dry\", \"temperature\": 21.7, \"sunlight\": true, \"humidity\": 18.2}",
        "{\"lowWater\": false, \"soilMoisture\": \"wet\", \"temperature\": 22, \"sunlight\": true, \"humidity\": 21.0}",
        "{\"lowWater\": false, \"soilMoisture\": \"dry\", \"temperature\": 23, \"sunlight\": true, \"humidity\": 21.0}",
        "{\"lowWater\": false, \"soilMoisture\": \"dry\", \"temperature\": 21, \"sunlight\": true, \"humidity\": 24.0}",
        "{\"lowWater\": false, \"soilMoisture\": \"dry\", \"temperature\": 23, \"sunlight\": true, \"humidity\": 24.0}",
        "{\"lowWater\": false, \"soilMoisture\": \"dry\", \"temperature\": 18.1, \"sunlight\": true, \"humidity\": 21.0}",
        "{\"lowWater\": false, \"soilMoisture\": \"dry\", \"temperature\": 21, \"sunlight\": true, \"humidity\": 25.0}",
        "{\"lowWater\": false, \"soilMoisture\": \"wet\", \"temperature\": 21, \"sunlight\": true, \"humidity\": 26.0}",
        "{\"lowWater\": false, \"soilMoisture\": \"wet\", \"temperature\": 21, \"sunlight\": true, \"humidity\": 28.0}",
        "{\"lowWater\": false, \"soilMoisture\": \"dry\", \"temperature\": 21, \"sunlight\": true, \"humidity\": 29.0}",
        "{\"lowWater\": false, \"soilMoisture\": \"dry\", \"temperature\": 21, \"sunlight\": true, \"humidity\": 23.0}",
        "{\"lowWater\": false, \"soilMoisture\": \"dry\", \"temperature\": 21, \"sunlight\": true, \"humidity\": 26.0}",
        "{\"lowWater\": false, \"soilMoisture\": \"wet\", \"temperature\": 21, \"sunlight\": false, \"humidity\": 40}"]

count = 0

# Serial port read loop
while True:
    
    if count < 10:
        stubData = data[0]
    elif count < 20:
        stubData = data[1]
    elif count < 30:
        stubData = data[2]
    elif count < 40:
        stubData = data[3]
        count = -1

    # Write to file
    dataFile = open("data.txt", 'w+')
    timestamp = str(time.time()) + ""
    while len(timestamp) < 15:
        timestamp += "0"
    dataFile.write(timestamp + " " + stubData)
    dataFile.close()
    print("INFO: Sensor Data has been writen to file: " + stubData)
    
    time.sleep(1)
    count += 1
