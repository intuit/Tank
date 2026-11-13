Intuit Tank Proxy
=================

Records a browser session as Tank XML, which you upload to Tank as a new script.
Full documentation: https://intuit.github.io/Tank/supplemental-tools/#tank-proxy-recorder

Requires Java 21 or later.

1. Trust the proxy's certificate authority in your browser or operating system:
   import auto_generated_ca.pem as a trusted certificate authority
   ("OWASP Custom CA for Tank"). On Windows you can import the .p12 instead:

       certutil -f -p password -importpfx auto_generated_ca.p12

2. Start the proxy from this directory, so it finds its configuration and CA files:

       java -jar Tank-Proxy-all.jar      (or ./startProxy.sh)

3. Set your browser's HTTP and HTTPS proxy to localhost and the proxy port
   (8888 by default). File > Settings changes the port and the output file
   (outputFile.xml by default).

4. Click Start Recording, use the application in the browser, then click
   Stop Recording and Save.

5. Upload the saved XML to Tank as a new script.
