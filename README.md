# spring-cloud-function-stream-integration [![Build Status](https://travis-ci.org/daggerok/spring-cloud-function-stream-integration.svg?branch=master)](https://travis-ci.org/daggerok/spring-cloud-function-stream-integration)

This example was prepared for one of my [stackoverflow question: Functional programming model bean definition and spring-cloud-function + spring-cloud-stream integration](https://stackoverflow.com/questions/56517391/functional-programming-model-bean-definition-and-spring-cloud-function-spring)

```bash
docker run --rm -it --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3.7.15-management-alpine

./mvnw spring-boot:run

http :8080 string=1
http :8080 string=2
http :8080 string=0
```
