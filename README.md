# ScholarX - backend application and auth server 

Backend of the ScholarX project

## Setting up the project for development

### Prerequisites
* Java 
* Maven
* PostgreSQL
* Google Developer Account
* Gmail Account with an App Password 

### Setup Google Auth Client

Here are the steps you need to follow to configure Google for social login:
- Go to [https://console.developers.google.com/](https://console.developers.google.com/) and register for a developer account.
- Create a Google API Console project.
- Once your Google App is open, click on the Credentials menu and then Create Credentials followed by Auth client ID.
- Select Web Application as the Application type.
- Give the client a name.
- Fill in the Authorized redirect URIs field to include the redirect URI to your app: `http://<your-domain>/login/oauth2/code/google`.
  - example: `http://localhost:8080/login/oauth2/code/google`
- Click Create.
Copy the client ID and client secret, as you'll need them later.

### Setup a gmail account with an app password
1. Create a new gmail account if you don't have one already
2. Enable Two Factor Authorisation 
3. Generate a new `App Password` ([help?](https://support.google.com/mail/answer/185833?hl=en-GB))  

### Run Locally
1. Fork and clone the repository
```shell
git clone https://github.com/<your profile name>/scholarx
```
2. Open the cloned repo, Find and open the `application.yml` file
3. Replace the `${CLIENT_ID}` and `${CLIENT-SECRET}` with the values from the above google auth client setup.  
example:
```yaml
        google:
            client-id: 123456789123-456rtyfghvbnyui.apps.googleusercontent.com
            client-secret: ABCDEF-qweqrtyuiopasdfghjklzxcv
```  


4. Replace the datasource dummy values with your local postgresql server instance credentials  
example:
```yaml
  datasource:
    url: jdbc:postgresql://localhost:5432/scholarx_DB?allowPublicKeyRetrieval=true&useSSL=false&useUnicode=true&characterEncoding=UTF-8
    username: rootuser
    password: rootpassword
    platform: postgres
```

5. Find and open the `simplejavamail.properties` file. Replace the mail `${EMAIL}` and `${APP_PASSWORD}` values with the generated `App Password` and the corresponding gmail address  
example:
```yaml
  simplejavamail.smtp.username = example@gmail.com
  simplejavamail.smtp.password = ytrewwqlkjmkolkj
```
6. Update the SecurityConfig.java to allow requests from the origin http://localhost:3000. The file path is scholarx/src/main/java/org/sefglobal/scholarx/config/SecurityConfig.java  
example:
```java
  configuration.setAllowedOrigins(ImmutableList.of("http://localhost:3000"));
```
7. Run the application
```shell
mvn spring-boot:run
```

### Configuring a MySQL Database (Optional)

1. Add the `mysql-connector-java` dependency to the `pom.xml` file.
```xml
<dependency>
  <groupId>mysql</groupId>
  <artifactId>mysql-connector-java</artifactId>
  <scope>runtime</scope>
</dependency>
```

2. Replace the `spring.jpa` and `spring.datasource` configurations in `application.yml` with the following configuration.
```yaml
  jpa:
    database: postgresql
    hibernate:
      ddl-auto: update
  datasource:
    url: jdbc:postgresql://${DB_URL}/${DB_NAME}?allowPublicKeyRetrieval=true&useSSL=false&useUnicode=true&characterEncoding=UTF-8
    username: ${DB_USER_NAME}
    password: ${DB_USER_PASSWORD}
    platform: postgres
```
