# Participant-Selection-tool

This it the repository of a participant selection tool written in java with the help of JExcel API [1].

The tool implements two heuristics and mix integer programming formulations to solve the participant selection problem.

Definition of the participant selection problem can be cound in [2].

It is a great tool that helps project manager to decide how to select and deploy his/her volunteers.

It is designed for the need of project managers on our volunteer management system (VMS)[3].

On VMS a project manager can generate a spreadsheet of registered volunteers.

With this tool, he/she can better select and deploy volunteers.

# Usage

## Using Eclipse IDE
Import the repository as a project.

Include the jxl.jar file.

Compile and run Main.java and pass in the the input file name.

## Using command line

Simply complie the Main.java file. 

javac -classpath jxl.jar; Main.java

Then run the Main.class file and pass in the input file name.

There is a PSP_input.xls which is a example of a input file.

java -classpath jxl.jar; Main PSP_input.xls

# Reference
[1] http://jexcelapi.sourceforge.net/

[2] http://ppt.cc/mPAX0

[3] VMS frontend repository : https://github.com/OpenISDM/VMS-frontend
    VMS backend reposirtory : https://github.com/OpenISDM/VMS
