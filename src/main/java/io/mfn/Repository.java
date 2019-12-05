package io.mfn;

import reactor.core.publisher.Flux;

public interface Repository<T> {
    Flux<T> findAll();
}