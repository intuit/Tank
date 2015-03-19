Intuit Tank Debugger
-------------------------------------------------
The Intuit Tank debugger allows you to run a test plan in debug mode to ensure that 
it runs as expected. Scripts to be run can be obtained in one of two ways:
    1. From the script edit view of Intuit Tank - Simply click the "Harness XML" button in the 
        upper right corner of the UI. This will contain only the script in question.
    2. From the Project scripts tab in TurbosScale - Click the "Download Harness XML" icon
        next to the "Add Test Plan" Button. This will download the entire collections of scripts as
        a job will get it.

Locations:
    scripts - where you should place your scripts.
    datafiles - where you should place your datafiles .csv files. Note, if you only have one datafile, 
        you may wish to copy the file as "csv-data.txt" if you use the getCSV() function without specifying the name.

Windows

1.  Open the command prompt.
2.  Navigate to the folder that this file is in.
3.  Run the following command substituting the correct harness xml file (xxx_H.xml)
        
        run_debugger.bat scripts\<##TestFile_H.xml>


MACOS

1.  Open the terminal.
2.  Navigate to the folder that this file is in.
3.  Run the following command substituting the correct harness xml file for <##TestFile_H.xml>
        
        ./run_debugger.sh  scripts/<##TestFile_H.xml>

The run_debugger script deletes the previous log files and runs the command
    java -jar apiharness-1.0-all.jar -users=1 -d -tp=$1

General Debugger Usage:
You can run the debugger manually using the the appropriate arguments from the list below

Options:
    -tp=<file name>:  The test plan file to execute (Required)
    -ramp=<time>:  The time (min) to get to the ideal concurrent users specified (Optional default 0)
    -time=<time>:  The time (min) of the simulation (Optional default 0)
    -users=<# of total users>:  The number of total users to run concurrently(Optional default 1)
    -start=<# of users to start with>:  The number of users to run concurrently when test begins (Optional default 0)
    -http=<controller_base_url>:  The url of the controller to get test info from (Optional, if present overrides all other values)
    -d:  Turns debug on to step through each request (Optional if present debugging is enabled)



