package io.mfn;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import io.mfn.model.Alarm;
import reactor.core.publisher.Flux;

@Component
public class AlarmRepository implements Repository<Alarm> {

    @Override
    public Flux<Alarm> findAll() {
        List<Alarm> alarms = 
            Arrays.asList(new Alarm("alarm1"), new Alarm("alarm2"), new Alarm("alarm3"));

        return Flux.fromIterable(alarms);
    }
    
}