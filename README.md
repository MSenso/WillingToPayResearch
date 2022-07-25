# WillingToPayResearch

1. Install Java 8 or higher

Visit the page www.java.com/en/download/manual.jsp, choose java-installer and follow the instructions.

2. Check if JAVA_HOME environment variable is set

On Windows you can do it by command "echo %JAVA_HOME%" in command line. On Linux and MacOS you can do it by command "echo $JAVA_HOME". 
If it is not recognised, you should to set it in your environment variables. 

On Windows you can use search and find "Edit the system environment variables", then click "Environment variables...", choose "New..." in "User variables for admin"
or "System Variables", then set variable name is equal to "JAVA_HOME" and value to path that leads to directory that contains bin-directory of Java SDK. 
It is often located in ProgramFiles/Java or ProgramFiles (x86)/ Java.

On Linux/MacOS you can run command "export JAVA_HOME = Path/to/SDK-Folder" where SDK-Folder is a folder that contains your Java bin folder.

You can check if everything is fine by command "java" in command line.

3. Download the content from directory "production" with .jar file and "scripts" directory. You need to keep them in the same place.

4. Open command line in the directory than contains WillingToPayResearch.jar and scripts directory (or set the current working directory to this directory) and run
"java -jar WillingToPayResearch.jar"

5. If you have any troubles please contact me via email: mmakhaladze@edu.hse.ru
