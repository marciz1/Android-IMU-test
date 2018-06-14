# Android-IMU-test
 Application that retrieves the IMU sensor indication in the phone.
 
## Configuration
The application uses TCP connection to start data retrieving. So firstly you should modify the SERVER_IP value in MainActivity class.
It uses default port 4444 but you can change it also there. 

## Running application
To test connection you could use simple netcat server, by typing:

```
nc -l -p 4444
```
Then type:
```
START
```
If the screen freezes, connection is allright and data starts to record. To stop recording you must close terminal or end server process.
Application will return the error, but data will be saved. Data is deleted from this localization every time you start application so that you can save new data.
Every time you want to get new data, you should restart the application and server, then type "START" again.

App could be used with other servers, runned by ros launcher, for example comm_tcp. 
You should modify the code to send "START" message when server starts work. 
https://github.com/abhinavjain241/comm_tcp

## Data localization
```
Environment.getExternalStorageDirectory() + "/IMU/quaternion.txt"
```

## Data structure

Short fragment of collected data:

```
Q 0,16018329560756683000, 0,07420799881219864000, -0,03303821384906769000, -0,98373925685882570000, 205782459127505 
A -0,81163024902343750000, 0,60812377929687500000, 12,52645874023437500000, 205782460409244
G -0,68760681152343750000, -0,79010009765625000000, 0,01678466796875000000, 205782450429996 
M -3,50010681152343750000, -14,40376281738281200000, -38,71870422363281000000, 205782459127505 

Q 0,15873500704765320000, 0,06795410811901093000, -0,03259175270795822000, -0,98444056510925290000, 205782459127505 
A -0,93852233886718750000, 1,15161132812500000000, 11,72918701171875000000, 205782470388492
G -0,47508239746093750000, -0,65321350097656250000, 0,15313720703125000000, 205782470388492 
M -3,50010681152343750000, -14,40376281738281200000, -38,71870422363281000000, 205782459127505 

Q 0,16024686396121980000, 0,06643336266279220000, -0,03329329565167427000, -0,98427593708038330000, 205782479299625 
A -0,93852233886718750000, 1,15161132812500000000, 11,72918701171875000000, 205782470388492
G -0,47508239746093750000, -0,65321350097656250000, 0,15313720703125000000, 205782470388492 
M -1,34747314453125000000, -13,73606872558593800000, -40,20213317871094000000, 205782479299625

...

```

Q - quaternion [w, x, y, z, timestamp], Sensor.TYPE_ROTATIONAL_VECTOR - gyro + acc + magn

A - accelerometer [x, y, z, timestamp], Sensor.TYPE_ACCELEROMETER

G - gyroscope [x, y, z, timestamp], Sensor.TYPE_GYROSCOPE

M - magnetometer [x, y, z, timestamp], Sensor.TYPE_MAGNETOMETER


