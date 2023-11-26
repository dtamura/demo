package com.dtamura.demo;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;

@RestController
public class GreetingController {

   private static final String template = "Hello, %s!";
   private final AtomicLong counter = new AtomicLong();

   private Tracer tracer;

   private NormalClass normal;

   public GreetingController(OpenTelemetry openTelemetry) {
      this.tracer = openTelemetry.getTracer(GreetingController.class.getName(), "0.1.0");
      this.normal = new NormalClass(openTelemetry);
   }

   @GetMapping("/greeting")
   public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {

      Span span = tracer.spanBuilder("hello").startSpan();
      try (Scope scope = span.makeCurrent()) {
         // span
         hoge();
      } finally {
         span.end();
      }
      return new Greeting(counter.incrementAndGet(), String.format(template, name));
   }

   public void hoge() {
      Span span = tracer.spanBuilder("hoge").startSpan();
      try (Scope scope = span.makeCurrent()) {
         long id = Thread.currentThread().getId();
         span.setAttribute("ThreadID", id);
         System.out.println("Thread ID: "+ id);
         fuga();
         piyo();
      } finally {
         span.end();
      }
      span.end();
   }

   public void fuga() {
      Span span = tracer.spanBuilder("fuga").startSpan();
      span.end();
   }

   public void piyo() {
      Span span = tracer.spanBuilder("piyo").startSpan();
      try (Scope scope = span.makeCurrent()) {
         normal.piyo();
      }
      span.end();
   }

}
