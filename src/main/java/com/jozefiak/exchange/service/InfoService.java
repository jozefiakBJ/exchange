package com.jozefiak.exchange.service;

import com.jozefiak.exchange.domain.dto.InfoView;
import com.jozefiak.exchange.domain.dto.RateView;
import com.jozefiak.exchange.domain.mapper.InfoMapper;
import com.jozefiak.exchange.domain.mapper.InfoMapperImpl;
import com.jozefiak.exchange.domain.mapper.RateViewMapper;
import com.jozefiak.exchange.domain.mapper.RateViewMapperImpl;
import com.jozefiak.exchange.repository.InfoRepo;
import com.jozefiak.exchange.repository.RateRepo;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
public class InfoService {

    private final WebClient webClient;
    private final InfoMapper infoMapper;
    private final InfoRepo infoRepo;

    public InfoService(WebClient.Builder webClientBuilder, InfoRepo infoRepo) {

        this.webClient = webClientBuilder.baseUrl("http://api.nbp.pl/api/exchangerates/tables/").build();
        this.infoRepo = infoRepo;
        this.infoMapper = new InfoMapperImpl();

    }

    public List<InfoView> getLastRates(int days)
    {
        Mono<List<InfoView>> response = webClient.get()
                .uri("/C/last/"+days)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {});

        List<InfoView> r = response.block();

        infoRepo.deleteAll();

        infoRepo.saveAll(infoMapper.toInfo(r));

        return r;
    }



}