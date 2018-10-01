# Bank rest service
Project featuring REST service with of Spring Boot as opposed to [same project without spring](https://github.com/chergey/bank-rest-service)

To run
```
mvn package 
java -jar <app-name> -port=<port>
curl http://localhost:<port>/api/account/transfer?from=1&to=2&amount=10
```

