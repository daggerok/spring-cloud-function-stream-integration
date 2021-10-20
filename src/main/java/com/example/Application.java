package com.example;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;
import java.util.function.Function;

@Data
@NoArgsConstructor
class StringPayload {
  private String string;
}

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of", onConstructor_ = @JsonCreator)
class IntegerPayload {
  private Integer integer;
}

@Log4j2
@SpringBootApplication
public class Application {

  @Bean
  Consumer<Flux<StringPayload>> process(StreamBridge streamBridge) {
    return payloads -> payloads.map(StringPayload::getString)
                               .doOnNext(str -> log.info("process => {}", str))
                               .subscribe(str -> streamBridge.send("idestination", str/*
                                  (Object) str, org.springframework.util.MimeType.valueOf("application/json")*/));
  }

  @Bean
  Function<Flux<String>, Flux<Integer>> doubleIt() {
    return integers -> integers.map(Integer::valueOf)
                               .map(integer -> integer * 2)
                               .doOnNext(o -> log.info("doubleIt => {}", o));
  }

  @Bean
  Function<Flux<Integer>, Flux<IntegerPayload>> produceIt() {
    return integers -> integers.flatMap(integer -> Flux.range(0, integer))
                               .doOnNext(o -> log.info("produceIt => {}", o))
                               .map(IntegerPayload::of)
                               .window(100)
                               .flatMap(flux -> flux);
  }

  @Bean
  Consumer<Flux<IntegerPayload>> logIt() {
    return payloads -> payloads.subscribe(payload -> log.info("logIt => {}", payload));
  }

  public static void main(String[] args) {
    SpringApplication.run(
        Application.class,
        "--spring.main.lazy-initialization=false",
        "--spring.cloud.function.definition=process",
        "--spring.cloud.stream.function.definition=doubleIt|produceIt|logIt",
        "--spring.cloud.stream.bindings.doubleIt|produceIt|logIt-in-0.destination=idestination",
        "--spring.cloud.stream.bindings.doubleIt|produceIt|logIt-in-0.group=igroup",
        "--spring.cloud.stream.rabbit.bindings.doubleIt|produceIt|logIt-in-0.consumer.durable-subscription=true"
    );
  }
}
