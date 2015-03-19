These certs are used for signing the applets used by Tank. The jar files signed are the Tank-Debugger-all.jar and the Tank-Script-Runner-all.jar
This is provided so that the user can launch tools as applets and still have access to local resources. 
The p12 cert is added so users can import the cert to the java keystore.

If you wish to use your own certs, you can generate them using keytool or you can use your own. 

Keytool example:
keytool -genkey -alias [MYALIAS] -keyalg RSA -keysize 2048 -keystore [KEYSTORE].ks


You will need to override the default cert properties on the command line. The properties are:

cert.dir - the directory where you keep your certificates - default=${project.parent.parent.basedir}/certs
cert.keystore - the keystore to use - default=self_signed_certs.ks
cert.alias - the alias of the certificate to use - default tank
cert.store.password - the password for the keystore  - detault=tanktank
cert.password - the password for the certificate - detault=tanktank
cert.p12 - the p12 file to use. Will be copied to the tools directory for users to import - default=self_signed_tank.p12

supply these options on the command line using the -D e.g. -Dcert.dir=/myCerts -Dcert.keystore=MyKeystore.ks etc.
