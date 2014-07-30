elf.map.android
===============

An offline map viewer for android.

Features:
 1. Offline map based on GPS data
 2. Filter objects by type
 3. You can remove some type files that you don't need, except "points.llu" and "points.str".
 4. GPS location Map data converted from osm xml file (http://www.openstreetmap.org).
 
Use "MapMaker" to convert map.
example:
 "./MapMaker greater-london.osm . -0.95 51.28 0.5 0.5"
 
"MapMaker" based on Qt-5.3.1 and linux-x64. 

Apk and "MapMaker" can be download from "http://elf-map.sourceforge.net/".

For test, extract "map-demo.zip" to sdcard root dir. Absolute path is "/sdcard/elf/map/*".

You can download source from "https://github.com/elf0/elf.map.android"

Note: It supply small region, and slow performance now! Better in future. 
