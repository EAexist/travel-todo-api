package com.matchalab.trip_todo_api.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.matchalab.trip_todo_api.DTO.UserAccountDTO;
import com.matchalab.trip_todo_api.mapper.UserAccountMapper;
import com.matchalab.trip_todo_api.model.UserAccount.GoogleProfile;
import com.matchalab.trip_todo_api.model.UserAccount.KakaoProfile;
import com.matchalab.trip_todo_api.model.UserAccount.UserAccount;
import com.matchalab.trip_todo_api.repository.UserAccountRepository;
import com.matchalab.trip_todo_api.service.UserAccountService;
import com.matchalab.trip_todo_api.utils.Utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private UserAccountMapper userAccountMapper;

    @Autowired
    private UserAccountService userAccountService;

    @Value("${google.client-id}")
    private String GOOGLE_CLIENT_ID;

    /**
     * Provide the details of an Trip with the given id.
     */
    @PostMapping(value = "/guest")
    public ResponseEntity<UserAccountDTO> guestLogin() {
        UserAccount userAccount = userAccountRepository.save(new UserAccount());

        UserAccountDTO userAccountDTO = userAccountService.createInitialEmptyTrip(userAccount);

        return ResponseEntity.created(Utils.getLocation((userAccountDTO.id())))
                .body(userAccountDTO);
    }

    @PostMapping(value = "/browser-cache")
    public ResponseEntity<UserAccountDTO> webBrowserCacheLogin() {
        UserAccount userAccount = userAccountRepository.save(new UserAccount());

        UserAccountDTO userAccountDTO = userAccountService.createInitialSampleTrip(userAccount);

        return ResponseEntity.created(Utils.getLocation((userAccountDTO.id())))
                .body(userAccountDTO);
    }

    /**
     * Provide the details of an Trip with the given id.
     */
    @PostMapping("/kakao")
    public ResponseEntity<UserAccountDTO> kakaoLogin(@RequestBody String idToken,
            @RequestBody KakaoProfile kakaoProfile) {
        try {

            Boolean isCreated = true;
            Optional<UserAccount> userOptional = userAccountRepository.findByKakaoId(kakaoProfile.id());

            if (userOptional.isPresent()) {
                isCreated = false;
            }

            UserAccount userAccount = userOptional
                    .orElse(userAccountRepository.save(new UserAccount(idToken, kakaoProfile)));

            UserAccountDTO userAccountDTO = userAccountService.createInitialEmptyTrip(userAccount);

            return ResponseEntity.status(isCreated ? HttpStatus.CREATED : HttpStatus.SEE_OTHER)
                    .location(Utils.getLocation((userAccountDTO.id())))
                    .body(userAccountDTO);
        } catch (HttpClientErrorException e) {
            throw e;
        }
    }

    /**
     * Provide the details of an Trip with the given id.
     */
    @PostMapping(value = "/google")
    public ResponseEntity<UserAccountDTO> googleLogin(@RequestBody GoogleProfile googleUserDTO) {
        try {

            Boolean isCreated = false;
            Optional<UserAccount> userOptional = userAccountRepository.findByGoogleId(googleUserDTO.id());
            UserAccount userAccount;

            if (userOptional.isPresent()) {
                // log.info(String.format("[Found User] %s", userOptional));
                userAccount = userOptional.get();
            } else {
                userAccount = userAccountRepository.save(new UserAccount(googleUserDTO));
                isCreated = true;
            }

            // UserAccount user = userOptional.orElse(userAccountRepository.save(new
            // UserAccount(googleUserDTO)));

            userAccountService.createInitialEmptyTrip(userAccount);

            return ResponseEntity.status(isCreated ? HttpStatus.CREATED : HttpStatus.OK)
                    .location(Utils.getLocation((userAccount.getId())))
                    .body(userAccountMapper.mapToUserAccountDTO(userAccount));
        } catch (HttpClientErrorException e) {
            throw e;
        }
    }

    /**
     * Provide the details of an Trip with the given id.
     */
    @PostMapping(value = "/google", params = "idToken")
    public ResponseEntity<UserAccountDTO> googleLoginWithIdToken(@RequestParam String idToken)
            throws GeneralSecurityException, IOException {
        try {
            GoogleIdTokenVerifier googleIdTokenVerifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(),
                    new GsonFactory())
                    .setAudience(Collections.singletonList(GOOGLE_CLIENT_ID))
                    .build();
            GoogleIdToken verifiedIdToken = googleIdTokenVerifier.verify(idToken);

            if (verifiedIdToken != null) {
                GoogleIdToken.Payload payload = verifiedIdToken.getPayload();
                return googleLogin(GoogleProfile.builder().id(payload.getSubject()).email(payload.getEmail()).build());
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (HttpClientErrorException e) {
            throw e;
        }
    }

}
