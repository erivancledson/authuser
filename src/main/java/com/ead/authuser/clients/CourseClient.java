package com.ead.authuser.clients;

import com.ead.authuser.services.UtilsService;
import com.ead.dtos.CourseDto;
import com.ead.dtos.ResponsePageDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Log4j2
@Component //implementação de authUser com Course
public class CourseClient {
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    UtilsService utilsService;

    @Value("${ead.api.url.course}")
    String REQUEST_URL_COURSE;

    //@Retry(name = "retryInstance", fallbackMethod = "retryfallback") //circuit break resilience4j, nome da instancia que está no application. Não se utiliza desse
    @CircuitBreaker(name = "circuitbreakerInstance") //anotação para receber a instancia do circuitBreaker que vem do application.yaml
    public Page<CourseDto> getAllCoursesByUser(UUID userId, Pageable pageable){
        List<CourseDto> searchResult = null;
        String url = REQUEST_URL_COURSE + utilsService.createUrl(userId, pageable);

        log.debug("Request URL: {} ", url);
        log.info("Request URL: {} ", url);

        try{
            ParameterizedTypeReference<ResponsePageDto<CourseDto>> responseType = new ParameterizedTypeReference<ResponsePageDto<CourseDto>>() {};
            ResponseEntity<ResponsePageDto<CourseDto>> result = restTemplate.exchange(url, HttpMethod.GET, null, responseType);
            searchResult = result.getBody().getContent();
            log.debug("Response Number of Elements: {} ", searchResult.size());
        }catch (HttpStatusCodeException e){
            log.error("Error request /courses: {} ", e);
        }

        log.info("Ending request /courses: userId {} ", userId);
        return new PageImpl<>(searchResult); // crio a paginação
    }


    //metodo de fallback para as tentativas do resilienc4j realizar suas tentativas sem sucesso
    ///o metodo deve conter o mesmo retorno e parametros passados

    public Page<CourseDto> circuitBreakerfallback(UUID userId, Pageable pageable, Throwable t){
        log.error("Inside retry retryfallback, cause - {}", t.toString());
        List<CourseDto> searchResult = new ArrayList<>();
        return new PageImpl<>(searchResult); //não vai receber o erro, ele vai receber uma paginação vazia
    }
    public Page<CourseDto> retryfallback(UUID userId, Pageable pageable, Throwable t){
        log.error("Inside retry retryfallback, cause - {}", t.toString());
        List<CourseDto> searchResult = new ArrayList<>();
        return new PageImpl<>(searchResult);
    }

}
