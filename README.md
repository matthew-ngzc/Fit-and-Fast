# Fast-Fit
Codebase for CS206 Project Fast&amp;Fit, an AI powered fitness app for busy women

# To run backend
1. Setup databse in mySQL - create the schema called "fastnfit_app". Set the username and password both to "root". Details for the database can be found in "backend\src\main\resources\application-dev.properties". Set the username and password using this code in the mySQL workbench
   
      ALTER USER 'root'@'localhost' IDENTIFIED BY 'root';
      FLUSH PRIVILEGES;
   

3. Store the openai api key in your environment variables. API key can be found in the pinned messages in the telegram group. Can do this by putting it into your OS env variables (only need to set up once), or each time you startup vscode use one of the following commands
   
      bash : export OPENAI_API_KEY=your_api_key_here
      
      cmd : set OPENAI_API_KEY=your_api_key_here
      
      powershell : $env:OPENAI_API_KEY="your_api_key_here"

4. mvn spring-boot:run "-Dspring-boot.run.profiles=dev" or ./mvnw spring-boot:run "-Dspring-boot.run.profiles=dev"
