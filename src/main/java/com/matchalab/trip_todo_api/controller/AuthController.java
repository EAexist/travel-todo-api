package com.matchalab.trip_todo_api.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
import com.matchalab.trip_todo_api.exception.NotFoundException;
import com.matchalab.trip_todo_api.mapper.UserAccountMapper;
import com.matchalab.trip_todo_api.model.UserAccount.GoogleProfile;
import com.matchalab.trip_todo_api.model.UserAccount.KakaoProfile;
import com.matchalab.trip_todo_api.model.UserAccount.UserAccount;
import com.matchalab.trip_todo_api.repository.UserAccountRepository;
import com.matchalab.trip_todo_api.service.TripService;
import com.matchalab.trip_todo_api.service.UserAccountService;
import com.matchalab.trip_todo_api.utils.Utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    @Autowired
    private TripService tripService;

    @Value("${app.google.client-id}")
    private String googleClientId;

    @Value("${app.google.web-client-id}")
    private String googleWebClientId;

    @Value("#{'${app.security.admin-emails}'.split(',')}")
    private List<String> adminEmails;

    /**
     * Provide the details of an Trip with the given id.
     */
    @PostMapping(value = "/google", params = "idToken")
    public ResponseEntity<UserAccountDTO> googleLoginWithIdToken(@RequestParam String idToken)
            throws GeneralSecurityException, IOException {
        try {
            GoogleIdTokenVerifier googleIdTokenVerifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(),
                    new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
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

            UserAccountDTO userAccountDTO = userAccountMapper
                    .mapToUserAccountDTO(userAccountService.createInitialEmptyTrip(userAccount));

            return ResponseEntity.status(isCreated ? HttpStatus.CREATED : HttpStatus.SEE_OTHER)
                    .location(Utils.getLocation((userAccountDTO.id())))
                    .body(userAccountDTO);
        } catch (HttpClientErrorException e) {
            throw e;
        }
    }

    @PostMapping(value = "/web-browser")
    public ResponseEntity<UserAccountDTO> webBrowserLogin(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) {

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            UUID userId = UUID.fromString(username);
            UserAccount userAccount = userAccountRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException(userId));

            return ResponseEntity.created(Utils.getLocation((userAccount.getId())))
                    .body(userAccountMapper.mapToUserAccountDTO(userAccount));
        }

        else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * Provide the details of an Trip with the given id.
     */
    @PostMapping(value = "/admin", params = "idToken")
    public ResponseEntity<UserAccountDTO> adminGoogleLoginWithIdToken(@RequestParam String idToken,
            HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            UUID userId = UUID.fromString(username);
            UserAccount userAccount = userAccountRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException(userId));

            return ResponseEntity.ok()
                    .body(userAccountMapper.mapToUserAccountDTO(userAccount));
        }

        else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    private ResponseEntity<UserAccountDTO> googleLogin(GoogleProfile googleUserDTO) {

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
    }

    /**
     * Provide the details of an Trip with the given id.
     */
    // @PostMapping(value = "/guest")
    // public ResponseEntity<UserAccountDTO> guestLogin() {
    // UserAccount userAccount = userAccountRepository.save(new UserAccount());

    // UserAccountDTO userAccountDTO =
    // userAccountService.createInitialEmptyTrip(userAccount);

    // return ResponseEntity.created(Utils.getLocation((userAccountDTO.id())))
    // .body(userAccountDTO);
    // }

    /**
     * Provide the details of an Trip with the given id.
     */
    @PostMapping(value = "/google")
    public ResponseEntity<UserAccountDTO> _googleLogin(@RequestBody GoogleProfile googleUserDTO) {
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

}
