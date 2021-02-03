# ScholarX - backend application and auth server 

Backend of the ScholarX project

### Setting up minikube cluster

**Prerequisites**
* Java 
* Maven
* Docker 
* Minikube

1. Install minikube ([help?](https://minikube.sigs.k8s.io/docs/start/))
2. Fork and clone the repo 
```
git clone https://github.com/<github_profile_name>/scholarx.git
```
3. Configure `app` resources files
```
cd scholarx
vim app/src/main/resources/application.yml
```
paste the code 
```
spring:
  jpa:
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL5InnoDBDialect
    hibernate:
      ddl-auto: update
  datasource:
    url: jdbc:mysql://${DATABASE_HOST}:${DATABASE_PORT}/${DATABASE_NAME}?allowPublicKeyRetrieval=true&useSSL=false&useUnicode=true&characterEncoding=UTF-8
    username: ${DATABASE_USER}
    password: ${DATABASE_PASSOWRD}
  jmx:
    unique-names: true
server:
  error:
    include-message: always
```
press `:wq` to write and quit

4. Configure `auth-server` resources files
```
vim auth-server/src/main/resources/application.yaml 
```
paste the code 
```
spring:
  thymeleaf:
    cache: false
  security:
    oauth2:
      client:
        registration:
          linkedin:
            client-id: ${CLIENT_ID}
            client-secret: ${CLIENT_SECRET}
            client-authentication-method: post
            authorization-grant-type: authorization_code
            redirect-uri: "{baseUrl}/login/oauth2/code/{registrationId}"
            scope: r_liteprofile, r_emailaddress
            client-name: Linkedin
        provider:
          linkedin:
            authorization-uri: https://www.linkedin.com/oauth/v2/authorization
            token-uri: https://www.linkedin.com/oauth/v2/accessToken
            user-info-uri: https://api.linkedin.com/v2/me?projection=(id,localizedFirstName,localizedLastName,profilePicture(displayImage~:playableStreams))
            user-name-attribute: id
            email-address-uri: https://api.linkedin.com/v2/emailAddress?q=members&projection=(elements*(handle~))
  jpa:
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL5InnoDBDialect
    hibernate:
      ddl-auto: update
  datasource:
    url: jdbc:mysql://${DATABASE_HOST}:${DATABASE_PORT}/${DATABASE_NAME}?allowPublicKeyRetrieval=true&useSSL=false&useUnicode=true&characterEncoding=UTF-8
    username: ${DATABASE_USER}
    password: ${DATABASE_PASSWORD}
  jmx:
    unique-names: true
```
*note: ask from a member in dev-team for ${CLIENT_ID} and ${CLIENT_SECRET}*


press `:wq` to write and quit

5. Build jar files for both modules
```
cd app
mvn clean install
cd ../auth-server
mvn clean install
cd ../
```

6. Start minikube
```
minikube start
```
7. Change docker environment from local to minikube 
```
eval $(minikube docker-env)
```
8. Build docker images for both modules
```
docker build -t scholarx:latest app/
docker build -t auth-server:latest auth-server/
```

6. Enable minikube ingress addon
```
minikube addons enable ingress
```

7. Set up the kuberntes cluster
```
cd kube-config
kubectl apply -f mysql-db-deployment.yaml
kubectl apply -f mysql-db-service.yaml
kubectl apply -f scholarx-app-deployment.yaml
kubectl apply -f scholarx-app-service.yaml
kubectl apply -f auth-server-deployment.yaml
kubectl apply -f auth-server-service.yaml
kubectl apply -f ingress.yaml
```

8. Access the application
Now the auth-server is exposed and can be accessed through `minikube-ip`

* Find the minikube ip
```
minikube ip
```

