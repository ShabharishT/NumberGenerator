Number Generator(Spring Boot)

A spring boot application that generates a sequence of numbers in the decreasing order till 0.

To run the APIs, follow the below steps:

1. Download 'numbergenerator-0.0.1-SNAPSHOT.jar.original' from target folder to a local folder.
2. Remove '.original' from the file name so that it becomes a executable jar.
3. Java JRE must be installed in the system.
4. Run the command 'java -jar <path where jar is located>'.
5. Open POSTMAN (or any API client ) to launch APIs.
6. Use the below API URL to generate a number sequence.

   Http Method: POST
   
   URL: http://localhost:8090/api/generate
   
   Request Body: 
   {
       "goal" : "40",
       "step" : "4"
   }
   
   Copy task ID generated in the response body. 
   
7. Use the below API URL to generate bulk number sequence.

   Http Method: POST
   
   URL: http://localhost:8090/api/bulkGenerate
   
   Request Body:
   [    
       {
       "goal" : "1000",
       "step" : "2"
       },
       {
       "goal" : "100",
       "step" : "2"
       }
   ]
   
   Copy task ID generated in the response body. 
   
8. Use the below API URL to get status of the task.

   Http Method: GET
 
   URL: http://localhost:8090/api/tasks/{UUID}/status
   
   where UUID is the task ID generated.
   
   Response shows result of the task -> SUCCESS/IN_PROGRESS/ERROR
   
9. Use the below API URL to get number sequence result.
    
   Http Method: GET  
   
   URL: http://localhost:8090/api/tasks/{UUID}?action=get_numlist
   
   where UUID is the task ID generated.
   
   Response shows number sequence result(s).
   
10. All required validations are handled.   