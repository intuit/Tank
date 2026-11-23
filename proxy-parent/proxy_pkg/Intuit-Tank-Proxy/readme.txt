Intuit Tank Proxy
=================

Records a browser session as Tank XML, which you upload to Tank as a new script.
Full documentation: https://intuit.github.io/Tank/supplemental-tools/#tank-proxy-recorder

Requires Java 21 or later.

1. Start the proxy from this directory, so it keeps its configuration and
   certificate authority here:

       java -jar Tank-Proxy-all.jar      (or ./startProxy.sh)

2. Set your browser's HTTP and HTTPS proxy to localhost and the proxy port
   (8888 by default). File > Settings changes the port and the output file
   (outputFile.xml by default).

3. Click Start Recording. The first time, the proxy creates a certificate
   authority for this installation: auto_generated_ca.p12 (its private key)
   and auto_generated_ca.pem (the certificate). To record HTTPS sites, import
   auto_generated_ca.pem into your browser or operating system as a trusted
   certificate authority ("OWASP Custom CA for Tank"). On Windows:

       certutil -user -addstore Root auto_generated_ca.pem

   Keep auto_generated_ca.p12 private: anyone who has it can intercept HTTPS
   traffic from a browser that trusts the CA.

   Packages from Tank 5.0.7 and earlier shipped the same CA to everyone, and
   its private key is public. If you trusted that CA, remove it from your trust
   stores and delete the old auto_generated_ca.* files.

4. Use the application in the browser, then click Stop Recording and Save.

5. Upload the saved XML to Tank as a new script.
