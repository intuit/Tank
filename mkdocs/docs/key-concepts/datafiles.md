# Data Files
Data Files are external sources of data for your scripts. The lines will get evenly divided up among the agents for use.  


## Uploading Data Files    
Clicking the `Data Files` tab takes the user to the main screen, which lists all the previously created Data Files.

<figure markdown>
![Data files Main Page](https://tank.perf.a.intuit.com/docs/html_single/images/datafiles/datafiles_1.png){ width="600"}
<figcaption>Data files Main Page</figcaption>
</figure> 

!!! info "Note"
    Currently, Tank supports data files with `CSV` (Comma Separated Values), and `XML` format only.   

The data file is read into shared memory only once, even if it is read by multiple virtual users.   

Usually you will use one of the `functions` below only once in your script for each file you load.   
The following two functions can be used in Tank to read a file:

* `#{ioFunctions.getCSVData(COLUMN_NUMBER)` - This function is used to read a default data file associated with the project. A Data File is default if there is only one in a particular job.
* `#{ioFunctions.getCSVData('FILE_NAME', COLUMN_NUMBER)` - This function can be used to read a specific data file when you have to read from multiple data files used for the same script or multiple scripts in a project (i.e. separate data files for personal information and login credentials).

!!! abstract "Tip"
    * Column Number 0 represents the 1st column in the file.
    * It is important that all the user data files needed by your test script are added to your project.


Clicking on the `Magnify` icon will allow you to see the contents of the data file.
<figure markdown>
![View Data File Content](https://tank.perf.a.intuit.com/docs/html_single/images/datafiles/datafiles_2.png){ width="600"}
<figcaption>View Data File Content</figcaption>
</figure> 


## Steps to Data File Upload
1. In the `Data Files` Tab, click the `Upload` link to add data files. The `Upload Data Files` dialog box opens.
    <figure markdown>
        ![Data Files Home Page](https://tank.perf.a.intuit.com/docs/html_single/images/datafiles/datafiles_3.png){ width="600"}
    <figcaption>Data Files Home Page</figcaption>
    </figure> 
2. From the `Upload Data Files` dialog box, click `Choose` to navigate and select the data file(s) or zip archives to add to the project.  
If a zip archive is uploaded, Tank will recursively upload all files with the `.csv` extension.
    <figure markdown>
        ![Choose Data File to Upload](https://tank.perf.a.intuit.com/docs/html_single/images/datafiles/datafiles_4.png){ width="600"}
    <figcaption>Choose Data File to Upload</figcaption>
    </figure> 
3. Click either `Upload` or the `arrow` next to the box to upload the data file. You can also click the `Cancel` button if you choose not to upload the data file.
        <figure markdown>
        ![Upload Data File](https://tank.perf.a.intuit.com/docs/html_single/images/datafiles/datafiles_5.png){ width="600"}
    <figcaption>Upload Data File</figcaption>
    </figure> 







    

