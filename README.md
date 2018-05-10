# SaxTP
A commandline based client for a simple file retrieval protocol on top of UDP / IP.

# Installation
## User
1. Make sure you have java installed
  a guide to install java can be found here: https://www.java.com/en/download/help/download_options.xml
2. Make sure you can run java in the command prompt.
3. Download the SaxTP.jar file, located at: out\artifacts\SaxTP
4. Open a command prompt in the folder the SaxTP.jar file is located in.
5. Use "java -jar SaxTP.jar" to run the executable.

## Developer
1. Make sure you have java installed.
2. Make sure you have a capable IDE.
3. Download the complete project.
4. Run the program by running the Main.class

# Usage
When running the program you have to specify a hostname and a file, this can be done either of two ways.

1. Add them as program variables (when running from the command prompt add them at the end of the run statement for example: 'java -jar SaxTP.jar hostname.net file.zip'
2. When you don't specify the variables like method 1 you will be asked when it needs them.

When the program has the 2 needed variables it will download the files automatically in the current folder

# Developer specific information
* The source code is located in the src folder, this includes the unit tests.
* To run the project the Main.java is used, located at \src\main\java
* There are no extra libraries needed to run and develop the project.
* When running the project as developer the downloaded files will get downloaded to the root of the project.
