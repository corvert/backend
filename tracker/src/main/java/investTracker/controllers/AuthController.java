package investTracker.controllers;

import investTracker.models.AppRole;
import investTracker.models.RefreshToken;
import investTracker.models.Role;
import investTracker.models.User;
import investTracker.repositories.RoleRepository;
import investTracker.repositories.UserRepository;
import investTracker.security.jwt.JwtUtils;
import investTracker.security.request.LoginRequest;
import investTracker.security.request.SignupRequest;
import investTracker.security.request.TokenRefreshRequest;
import investTracker.security.response.LoginResponse;
import investTracker.security.response.MessageResponse;
import investTracker.security.response.TokenRefreshResponse;
import investTracker.security.response.UserInfoResponse;
import investTracker.security.services.UserDetailsImpl;
import investTracker.services.RefreshTokenService;
import investTracker.services.UserService;
import investTracker.util.AuthUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    UserService userService;

    @Autowired
    RefreshTokenService refreshTokenService;

    @Autowired
    AuthUtil authUtil;



    @PostMapping("/public/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication;
        try {
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()));
        } catch (AuthenticationException exception) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Bad credentials");
            map.put("status", false);
            return new ResponseEntity<Object>(map, HttpStatus.NOT_FOUND);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwtToken = jwtUtils.generateTokenFromUsername(userDetails);

        // Generate refresh token
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        LoginResponse response = new LoginResponse(
                userDetails.getUsername(),
                roles,
                jwtToken,
                refreshToken.getToken()  // Add refresh token to response
        );

        return ResponseEntity.ok(response);
    }


    @PostMapping("/public/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUserName(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Role role;

        if (strRoles == null || strRoles.isEmpty()) {
            role = roleRepository.findByRoleName(AppRole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        } else {
            String roleStr = strRoles.iterator().next();
            if (roleStr.equals("admin")) {
                role = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            } else {
                role = roleRepository.findByRoleName(AppRole.ROLE_USER)
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            }

            user.setAccountNonLocked(true);
            user.setAccountNonExpired(true);
            user.setCredentialsNonExpired(true);
            user.setEnabled(true);
            user.setCredentialsExpiryDate(LocalDate.now().plusYears(1));
            user.setAccountExpiryDate(LocalDate.now().plusYears(1));
            user.setTwoFactorEnabled(false);
            user.setSignUpMethod("email");
        }
        user.setRole(role);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserDetails(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        UserInfoResponse response = new UserInfoResponse(
                user.getUserId(),
                user.getUserName(),
                user.getEmail(),
                user.isAccountNonLocked(),
                user.isAccountNonExpired(),
                user.isCredentialsNonExpired(),
                user.isEnabled(),
                user.getCredentialsExpiryDate(),
                user.getAccountExpiryDate(),
                user.isTwoFactorEnabled(),
                roles
        );

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/username")
    public String currentUserName(@AuthenticationPrincipal UserDetails userDetails){
        return (userDetails != null) ? userDetails.getUsername() : "";
    }

    @PostMapping("/public/refresh-token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String newAccessToken = jwtUtils.generateTokenFromUsername(user.getUserName());
                    // Optional: Generate a new refresh token (rotation)
                    RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user.getUserId());

                    return ResponseEntity.ok(new TokenRefreshResponse(
                            newAccessToken,
                            newRefreshToken.getToken()
                    ));
                })
                .orElseThrow(() -> new RuntimeException("Refresh token not found!"));
    }

    @PostMapping("/public/logout")
    public ResponseEntity<?> logoutUser() {
        Long userId = authUtil.loggedInUserId();
        refreshTokenService.deleteByUserId(userId);
        return ResponseEntity.ok(new MessageResponse("Logout successful!"));
    }

    @PostMapping("/public/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email){
        try {
            userService.gereratePasswordResetToken(email);
            return ResponseEntity.ok(new MessageResponse("Password reset email sent successfully!"));

        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error sending password resest email: " + e.getMessage()));
        }

    }

    @PostMapping("/public/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token,
                                           @RequestParam String newPassword){
        try {
            userService.resetPassword(token, newPassword);
            return ResponseEntity.ok(new MessageResponse("Password reset successful!"));
        }catch (RuntimeException e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error resetting password: " + e.getMessage()));
        }
    }


}
