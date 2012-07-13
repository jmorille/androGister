androGister
===========

TODO: http://android.cyrilmottier.com/

# Update Site
##############
m2e release : http://rgladwell.github.com/m2e-android/ 
m2e master : http://rgladwell.github.com/m2e-android/updates/master

# Wifi Deploy
##############
According to a post on xda-developers, you can enable ADB over WiFi from the device with the commands  
$ setprop service.adb.tcp.port 5555  
$ stop adbd  
$ start adbd  

And you can disable it and return ADB to listening on USB with  
$ setprop service.adb.tcp.port -1  
$ stop adbd  
$ start adbd  

If you have USB access already, it is even easier to switch to using WiFi. From a command line on the computer that has the device connected via USB, issue the commands  
$ adb tcpip 5555  
$ adb connect 192.168.0.101:5555  


To tell the ADB daemon return to listening over USB  
$ adb usb  
There are also several apps on the Android Market that automate this process.  


# Proxy
##############
$ adb shell am start -a andoid.intent.action.MAIN -n com.android.settings/.ProxySelector

Sinon, il existe une application dans le market pour y accéder :
https://market.android.com/details?id=org.credil.proxysettings&feature=search_result

# Support Library
##############
http://developer.android.com/tools/extras/support-library.html

# Free Icons
##############
svn checkout svn://svn.services.openoffice.org/ooo/trunk/default_images/chart2/res/ icons
mkdir types
cp icons/type*16.png types

# Graph Chart
##############
https://github.com/jjoe64/GraphView
http://androidplot.com/wiki/Home
http://code.google.com/p/afreechart/
http://code.google.com/p/droidcharts/

# Thread
http://stackoverflow.com/questions/424752/any-good-graphing-packages-for-android/1856369#1856369
http://stackoverflow.com/questions/424752/any-good-graphing-packages-for-android
##############
