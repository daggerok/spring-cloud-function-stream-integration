package com.example;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.support.MessageBuilder;
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
@AllArgsConstructor(staticName = "of")
class IntegerPayload {
  private Integer integer;
}

@Log4j2
@SpringBootApplication
@EnableBinding(Source.class)
public class Application {

  @Bean
  Consumer<Flux<StringPayload>> process(Source source) {
    return payloads -> payloads.map(StringPayload::getString)
                               .doOnNext(o -> log.info("process => {}", o))
                               .map(MessageBuilder::withPayload)
                               .map(MessageBuilder::build)
                               .subscribe(source.output()::send);
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
        "--spring.cloud.function.definition=process",
        "--spring.cloud.stream.function.definition=doubleIt|produceIt|logIt"
    );
  }
}
