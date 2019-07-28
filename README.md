# Bank rest service
Project featuring REST service with of Spring Boot as opposed to [same project without spring](https://github.com/chergey/bank-rest-service)

[![Build Status](https://travis-ci.com/chergey/bank-rest-service-spring.svg?branch=master)](https://travis-ci.com/chergey/bank-rest-service-spring)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=chergey_bank-rest-service-spring&metric=alert_status)](https://sonarcloud.io/dashboard?id=chergey_bank-rest-service-spring)

Features
* HATEOAS (Spring HATEOAS)
* Spring Security
* DAO (Spring Data)
* [Apache Ignite (for caching)](https://apacheignite.readme.io/docs)


To run
```
mvn package
sudo docker-compose up --force-recreate
```

Sample requests
```
curl -X POST http://localhost:<port>/api/accounts/transfer?from=1&to=2&amount=10
curl http://localhost:<port>/api/accounts
curl http://localhost:<port>/api/accounts/somename?page=&size=20
curl -X DELETE http://localhost:<port>/api/accounts/2
```
