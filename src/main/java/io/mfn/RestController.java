package io.mfn;

import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.mfn.model.Alarm;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;

@Controller
public class RestController {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private AlarmRepository alarmRepo;

    private Flux<Alarm> globalFlux;
    private DirectProcessor<Alarm> directProcessor;

    @Autowired
    public RestController(AlarmRepository alarmRepo) {
        this.alarmRepo = alarmRepo;

        //Create global flux
        this.globalFlux = Flux.<Alarm>create(sink -> {
            Executors.newScheduledThreadPool(1).scheduleAtFixedRate(
                ()-> sink.next(new Alarm("alarm")), 
                0, 2, TimeUnit.SECONDS);
        }).publish().autoConnect();

        //Create processor to publish and broadcast alarms
        this.directProcessor = DirectProcessor.<Alarm>create();
    }

    @GetMapping(path = "/flux", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    @ResponseBody
    public Flux<Alarm> flux() {
        Flux<Alarm> alarms = Flux.from(alarmRepo.findAll()).log().delayElements(Duration.ofSeconds(1));

        // Subscribe on this flux server-side while also returning it to client through
        // JSON streaming
        alarms.subscribe(System.out::println);

        return alarms;
    }

    @GetMapping(path = "/connectableFlux", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    @ResponseBody
    public Flux<Alarm> connectableFlux() {
        // Subscribe on this flux server-side while also returning it to client through
        // JSON streaming
        globalFlux.subscribe(System.out::println);

        return globalFlux;
    }

    @GetMapping(path = "/raiseAlarm", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void raiseAlarm(@RequestParam("name") String name) {
        this.directProcessor.onNext(new Alarm(name));
    }

    @GetMapping(path = "/listenWithHistory", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    @ResponseBody
    public Flux<Alarm> listenWithHistory() {
        return this.directProcessor.replay().autoConnect(0);
    }

    @GetMapping(path = "/listen", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    @ResponseBody
    public Flux<Alarm> listen() {
        return this.directProcessor.publish().autoConnect();
    }
}