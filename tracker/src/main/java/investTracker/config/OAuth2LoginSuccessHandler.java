package investTracker.config;

import investTracker.models.enums.AppRole;
import investTracker.models.Role;
import investTracker.models.User;
import investTracker.repositories.RoleRepository;
import investTracker.security.jwt.JwtUtils;
import investTracker.security.services.UserDetailsImpl;
import investTracker.services.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.*;


@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    @Autowired
    private final UserService userService;

    @Autowired
    private final JwtUtils jwtUtils;

    @Autowired
    private final RoleRepository roleRepository;

    @Value("${frontend.url}")
    private String frontendUrl;

    //	private String idAttributeKey;
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws ServletException, IOException {

        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;

        DefaultOAuth2User principal = (DefaultOAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = principal.getAttributes();

        String provider = token.getAuthorizedClientRegistrationId();

        String email = (String) attributes.get("email");
        String name = String.valueOf(attributes.getOrDefault("name", ""));
        String username;

        String idAttributeKey;

        // FIX APPLIED — Clean provider branching with correct braces
        if ("github".equals(provider)) {

            username = String.valueOf(attributes.getOrDefault("login", ""));
            idAttributeKey = "id";

            // FIX APPLIED — GitHub may not return email
            if (email == null || email.isBlank()) {
                email = username + "@github.local";
            }

        } else if ("google".equals(provider)) {

            username = email.split("@")[0];
            idAttributeKey = "sub";

        } else {

            username = "user";
            idAttributeKey = "id";
        }

        log.info("HELLO OAUTH: {} : {} : {}", email, name, username);

        String finalEmail = email;
        String finalUsername = username;
        String finalIdKey = idAttributeKey;

        userService.findByEmail(finalEmail)
                .ifPresentOrElse(user -> {

                    DefaultOAuth2User oauthUser =
                            new DefaultOAuth2User(
                                    List.of(new SimpleGrantedAuthority(user.getRole().getRoleName().name())),
                                    attributes,
                                    finalIdKey
                            );

                    Authentication newAuth =
                            new OAuth2AuthenticationToken(
                                    oauthUser,
                                    oauthUser.getAuthorities(),
                                    provider
                            );

                    SecurityContextHolder.getContext().setAuthentication(newAuth);

                }, () -> {

                    User newUser = new User();
                    Role role = roleRepository.findByRoleName(AppRole.ROLE_USER)
                            .orElseThrow(() -> new RuntimeException("Default role not found"));

                    newUser.setRole(role);
                    newUser.setEmail(finalEmail);
                    newUser.setUserName(finalUsername);
                    newUser.setSignUpMethod(provider);

                    userService.registerUser(newUser);

                    DefaultOAuth2User oauthUser =
                            new DefaultOAuth2User(
                                    List.of(new SimpleGrantedAuthority(role.getRoleName().name())),
                                    attributes,
                                    finalIdKey
                            );

                    Authentication newAuth =
                            new OAuth2AuthenticationToken(
                                    oauthUser,
                                    oauthUser.getAuthorities(),
                                    provider
                            );

                    SecurityContextHolder.getContext().setAuthentication(newAuth);
                });

        // FIX APPLIED — Always use updated authentication
        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
        DefaultOAuth2User oauth2User = (DefaultOAuth2User) currentAuth.getPrincipal();

        String jwtEmail = (String) oauth2User.getAttributes().get("email");

        if (jwtEmail == null || jwtEmail.isBlank()) {
                        jwtEmail = finalUsername + "@github.local";
        }

        User user = userService.findByEmail(jwtEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Set<SimpleGrantedAuthority> authorities =
                Set.of(new SimpleGrantedAuthority(user.getRole().getRoleName().name()));

        UserDetailsImpl userDetails = new UserDetailsImpl(
                user.getUserId(),
                finalUsername,
                jwtEmail,
                null,
                false,
                authorities
        );

        String jwtToken = jwtUtils.generateTokenFromUsername(userDetails);

        String targetUrl = UriComponentsBuilder
                .fromUriString(frontendUrl + "/auth/oauth2/redirect")
                .queryParam("token", jwtToken)
                .build()
                .toUriString();

                // Explicit redirect avoids SavedRequest fallback (which was causing /error redirects).
                getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}