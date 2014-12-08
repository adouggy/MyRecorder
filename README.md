MyRecorder
==========
Command 'screenrecord' is wonderful recording screen tool after Android 4.4(KitKat), We can use this command via ADB shell, 
but we may use it in application sometimes.

This Project encapsulate this command and make it runnable under normal Android application( root is necessary, due to AID_GRAPHICS privilege).

The source code is neat and simple, we run 'screenrecord' by super user shell (Java Process Runtime) and stop it by calling kill -2 (equals Ctrl+c)
