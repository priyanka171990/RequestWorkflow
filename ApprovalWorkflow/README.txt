Steps to Execute:

1. Open Command Prompt.
2. Go to project directory. (cd C:\JavaProject\Workspace\ApprovalWorkflow) 
3. Start the H2 server with:
    $java -jar bin/h2-1.4.196.jar
4. Execute the DB script.
5. Execute Program

    From command prompt,
      a. Open new command prompt session.
      b. Go to project directory. (cd C:\JavaProject\Workspace\ApprovalWorkflow) .
      c. mvn -q exec:java 
     
     Or, Run as Java Application by importing project in Eclipse.