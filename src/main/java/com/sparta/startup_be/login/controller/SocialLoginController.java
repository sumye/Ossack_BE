<<<<<<< HEAD:src/main/java/com/sparta/startup_be/controller/SocialLoginController.java
//package com.sparta.startup_be.controller;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.sparta.startup_be.dto.KakaoUserInfoDto;
//import com.sparta.startup_be.service.GoogleUserService;
//import com.sparta.startup_be.service.KakaoUserService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.servlet.http.HttpServletResponse;
//
//@RestController
//@RequiredArgsConstructor
//public class SocialLoginController {
//    private final KakaoUserService kakaoUserService;
//    private final GoogleUserService googleUserService;
//
//    // 카카오 로그인인
//   @GetMapping("/user/kakao/callback")
//    public KakaoUserInfoDto kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
//        return kakaoUserService.kakaoLogin(code, response);
//    }
//
////    // 구글 로그인
////    @GetMapping("/user/google/callback")
////    public void googleLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
////        googleUserService.googleLogin(code, response);
////    }
////
////    // 네이버 로그인
////    @GetMapping("/user/naver/callback")
////    public void naverLogin(@RequestParam String code, @RequestParam String state, HttpServletResponse response) throws JsonProcessingException{
////        naverUserService.naverLogin(code, state, response);
////    }
//}
=======
package com.sparta.startup_be.login.controller;

import com.fasterxml.jackson.core.JsonProcessingException;


import com.sparta.startup_be.login.dto.SocialUserInfoDto;
import com.sparta.startup_be.login.service.GoogleUserService;

import com.sparta.startup_be.login.service.KakaoUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
public class SocialLoginController {
    private final KakaoUserService kakaoUserService;
    private final GoogleUserService googleUserService;

    // 카카오 로그인
   @GetMapping("/user/kakao/callback")
    public SocialUserInfoDto kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        return kakaoUserService.kakaoLogin(code, response);
    }

    // 구글 로그인
    @GetMapping("/user/google/callback")
    public SocialUserInfoDto googleLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        return googleUserService.googleLogin(code, response);
    }
//
//    // 네이버 로그인
//    @GetMapping("/user/naver/callback")
//    public void naverLogin(@RequestParam String code, @RequestParam String state, HttpServletResponse response) throws JsonProcessingException{
//        naverUserService.naverLogin(code, state, response);
//    }
}
>>>>>>> 00535908658a1be52294b715abbcaad2e869eaf7:src/main/java/com/sparta/startup_be/login/controller/SocialLoginController.java
