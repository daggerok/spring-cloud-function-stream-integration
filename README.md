# spring-cloud-function-stream-integration [![CI](https://github.com/daggerok/spring-cloud-function-stream-integration/actions/workflows/ci.yaml/badge.svg)](https://github.com/daggerok/spring-cloud-function-stream-integration/actions/workflows/ci.yaml)

<!-- old Travis CI status:
[![Build Status](https://travis-ci.org/daggerok/spring-cloud-function-stream-integration.svg?branch=master)](https://travis-ci.org/daggerok/spring-cloud-function-stream-integration)
-->

This example was prepared for one of my [stackoverflow question: Functional programming model bean definition and spring-cloud-function + spring-cloud-stream integration](https://stackoverflow.com/questions/56517391/functional-programming-model-bean-definition-and-spring-cloud-function-spring)

```bash
docker run -d --rm --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3.7.15-management-alpine
./mvnw spring-boot:start

http :8080 string=1
http :8080 string=2
http :8080 string=0
# or
http :8080/process string=1
http :8080/process string=2
http :8080/process string=0

# and
echo '[0,1,1,2,3,5,8,13,21,34,55]' | http :8080/doubleIt
# or
curl -isS 0:8080/doubleIt -d '[0,1,1,2,3,5,8,13,21,34,55]' -H'Content-Type:application/json' ; echo

# and
echo 3 | http post :8080/produceIt
# or
curl -isS 0:8080/produceIt -d 3 -H'Content-Type:application/json' ; echo

# and
echo '{"integer":-123}' | http post :8080/logIt
# or
curl -isS 0:8080/logIt -d '{"integer":-123}' -H'Content-Type:application/json' ; echo

./mvnw spring-boot:stop
docker stop rabbitmq
```
