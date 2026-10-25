package com.example.demo.web;

import com.example.demo.web.dto.Greeting;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Greetings", description = "Greeting operations")
public class GreetingController {

    @GetMapping("/greet")
    @Operation(summary = "Get a greeting", description = "Returns a greeting for the supplied name.")
    public ResponseEntity<Greeting> greet(
            @Parameter(description = "Name to include in the greeting", example = "Ada")
            @RequestParam(name = "name", defaultValue = "World") String name) {
        Greeting g = new Greeting(String.format("Hello, %s!", name));
        return ResponseEntity.ok(g);
    }
}
