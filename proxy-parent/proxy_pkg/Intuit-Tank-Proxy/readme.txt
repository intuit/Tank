run the following command on windows to import the .p12 file to your trusted certificates.

Windows

1.  Open the command prompt.
2.  Navigate to the folder which has the .p12 file
3.  And run the following command.  Also you will need to know the password which was used to create the certificate (currently is 'password').

certutil -f -p password -importpfx auto_generated_ca.p12

I still need to test this thing....


MACOS

We are still researching on how to import it automatically...



Manual steps for importing stuff to firefox.

1.  Open firefox preferences
2.  Go to Advanced tab
3.  Open the Encryption tab in the advanced section.
4.  Click view certificates
5.  Go to authorities tab.
6.  Look for OWASP Custom CA for and delete it if it exists.
7.  Click on import and choose the auto_generated_ca.pem file that comes with the application and select all the check boxes and click ok.
8.  Verify that the certificate OWASP Custom CA exists.



Steps to update the proxy settings in firefox.

1.  Open the firefox preferences.
2.  Go to Advanced tab.
3.  Open the Network tab in the advanced section.
4.  Click on settings.
5.  Select Manual Proxy Settings option.
5.1 Enter "localhost" in HTTP Proxy field
5.2 Enter the port number that the proxy server is listening to.
6.  Click on okay and then you should be good to go.


Steps to run the proxy
1. Run the client proxy application by double clicking the 'Tank-Proxy-all.jar file'
2. Optinally set the output file location. (default is recordedOutput.xml0
3. Optionally set the port. (default is 8888)
4. Set up firefox and click Start Session
5. Perform Test steps in firefox.
6. Click Stop Session to finalize output.
7. Import into Intuit Tank!

