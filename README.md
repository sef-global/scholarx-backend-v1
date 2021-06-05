# ScholarX - backend application and auth server 

Backend of the ScholarX project

### Setting up the project for development

**Prerequisites**
* Java 
* Maven
* MySQL
* Linkedin Social Login App
* Gmail Account with an App Password 

**Setup Linkedin Social Login App**

1. Create a new Linkedin App ([help?](https://docs.ultimatemember.com/article/142-social-login-linkedin-app-setup)) 
2. Click on `Auth` tab and add `http://localhost:8080/login/oauth2/code/linkedin` as an authorised redirect URL
3. Make Sure you have properly added the `Sign in with Linkedin product` under products tab

**Setup a gmail account with an app password**
1. Create a new gmail account if you don't have one already
2. Enable Two Factor Authorisation 
3. Generate a new `App Password` ([help?](https://support.google.com/mail/answer/185833?hl=en-GB)) 

**Steps**
1. Fork and clone the repository
```
git clone https://github.com/<your profile name>/scholarx
```
2. Open the cloned repo, Find and open the `application.yml` file
3. Replace the `client-id` and `client-secret` with the values from the above linkedin social app
<img width="592" alt="image" src="https://user-images.githubusercontent.com/45477334/116671426-8a0e3780-a9be-11eb-9b22-3de725ee3107.png">
4. Replace the mail username and password values with the generated `App Password` and the corresponding gmail address
<img width="422" alt="image" src="https://user-images.githubusercontent.com/45477334/116671690-dbb6c200-a9be-11eb-9882-7e4a649729b7.png">
5. Replace the datasource dummy values with your local mysql server instance credentials
<img width="1007" alt="image" src="https://user-images.githubusercontent.com/45477334/116671874-17ea2280-a9bf-11eb-84b4-a21355a9df73.png">
6. Run the application 

```
mvn spring-boot:run
```
