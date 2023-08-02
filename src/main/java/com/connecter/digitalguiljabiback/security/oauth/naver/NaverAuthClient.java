package com.connecter.digitalguiljabiback.security.oauth.naver;

import com.connecter.digitalguiljabiback.config.properties.NaverProperties;
import com.connecter.digitalguiljabiback.exception.KakaoClientException;
import com.connecter.digitalguiljabiback.exception.NaverClientException;
import com.connecter.digitalguiljabiback.security.dto.AuthRequest;
import com.connecter.digitalguiljabiback.security.dto.AuthResponse;
import com.connecter.digitalguiljabiback.security.dto.KakaoUserResponse;
import com.connecter.digitalguiljabiback.security.dto.NaverUserResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

/**
 * KakaoAuthClient
 * 카카오 API와의 통신을 담당하는 클라이언트 클래스입니다.
 * 카카오 인증에 관련된 액세스 토큰 요청과 사용자 정보를 가져오는 기능을 제공합니다.
 * 작성자: hyunjin
 * 버전: 1.0.0
 * 작성일: 2023-07-30
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NaverAuthClient {

    private final NaverProperties naverProperties;
    private final RestTemplate restTemplate;

    /**
     * 네이버 API로 액세스 토큰을 요청합니다.
     * @param request KakaoAuthRequest 객체로부터 인증 정보를 받아옵니다.
     * @return 요청한 액세스 토큰 문자열
     * @throws KakaoClientException 네이버 API 호출 중 예외가 발생했을 경우
     */
    public String requestAccessToken(AuthRequest request) throws UnsupportedEncodingException {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        UriComponents uriBuilder = UriComponentsBuilder.fromHttpUrl(naverProperties.getTokenUri())
          .queryParam("grant_type", naverProperties.getGrantType())
          .queryParam("client_id", naverProperties.getClientId())
          .queryParam("client_secret", naverProperties.getClientSecret())
          .queryParam("code", request.getAuthorizationCode())
          .queryParam("state", URLEncoder.encode(request.getState(), StandardCharsets.UTF_8))
          .build(true);

        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);

        try {
            ResponseEntity<AuthResponse> respEntity = restTemplate.exchange(uriBuilder.toString(), HttpMethod.GET, entity, AuthResponse.class);

            AuthResponse response = respEntity.getBody();

            if (response == null || response.getAccessToken() == null) {
                throw new KakaoClientException("네이버 API에서 액세스 토큰을 가져오지 못했습니다.");
            }
            return response.getAccessToken();
        } catch (RestClientException e) {
            throw new KakaoClientException("네이버 API에서 액세스 토큰 요청 중 예외가 발생했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 네이버 API로부터 사용자 정보를 요청합니다.
     * @param accessToken 요청할 사용자의 액세스 토큰 문자열
     * @return 사용자 정보를 담고 있는 KakaoUserInfoResponse 객체
     * @throws KakaoClientException 카카오 API 호출 중 예외가 발생했을 경우
     */
    public NaverUserResponse requestUserInfo(String accessToken) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("Authorization", "Bearer " + accessToken);

        HttpEntity<?> httpEntity = new HttpEntity<>(httpHeaders);

        try {

            return restTemplate.postForObject(naverProperties.getUserInfoUri(), httpEntity, NaverUserResponse.class);
        } catch (RestClientException e) {
            throw new NaverClientException("네이버 API에서 사용자 정보 요청 중 예외가 발생했습니다: " + e.getMessage(), e);
        }
    }
}

