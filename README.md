# ScholarX - backend application and auth server 

Backend of the ScholarX project

## Setting up the project for development

### Prerequisites
* Java 
* Maven
* PostgreSQL
* Linkedin Social Login App
* Gmail Account with an App Password 

### Setup Linkedin Social Login App

1. Create a new Linkedin App ([help?](https://docs.ultimatemember.com/article/142-social-login-linkedin-app-setup)) 
2. Click on `Auth` tab and add `http://localhost:8080/login/oauth2/code/linkedin` as an authorised redirect URL
3. Make Sure you have properly added the `Sign in with Linkedin product` under products tab

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
3. Replace the `${client-id}` and `${client-secret}` with the values from the above linkedin social app  
example:
```yaml
          linkedin:
            client-id: 324780jdsfg2u4
            client-secret: MsdfsdfggsqPFh
            client-authentication-method: post
            authorization-grant-type: authorization_code
```  

4. Replace the mail `username` and `password` values with the generated `App Password` and the corresponding gmail address  
example:
```yaml
  mail:
    host: smtp.gmail.com
    port: 587
    username: samplemail@gmail.com
    password: jhdfklsdjjadskt
    properties:
```

5. Replace the datasource dummy values with your local mysql server instance credentials  
example:
```yaml
  datasource:
    url: jdbc:postgresql://localhost:5432/scholarx_DB?allowPublicKeyRetrieval=true&useSSL=false&useUnicode=true&characterEncoding=UTF-8
    username: rootuser
    password: rootpassword
    platform: postgres
```

6. Run the application
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
