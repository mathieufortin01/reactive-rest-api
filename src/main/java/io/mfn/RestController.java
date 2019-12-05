package io.mfn;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import io.mfn.model.Alarm;
import reactor.core.publisher.Flux;

@Controller
public class RestController {

    @GetMapping(path = "/streamJson", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    @ResponseBody
    public Flux<Alarm> streamJson() {
        List<Alarm> alarms = 
            Arrays.asList(new Alarm("alarm1"), new Alarm("alarm2"), new Alarm("alarm3"));

        return Flux.fromIterable(alarms).delayElements(Duration.ofSeconds(1));
    }
}