package com.fitpal.service.auth;

import com.fitpal.api.AuthService;
import com.fitpal.api.User;
import com.fitpal.service.db.UserRepository;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = AuthService.class)
public class AuthServiceImpl implements AuthService {

    @Reference
    private TokenService tokenService;

    @Reference
    private UserRepository userRepository;

    @Reference
    private PasswordService passwordService;

    @Override
    public String login(String email, String password, Boolean reactivate) throws Exception {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid Credentials"));

        if (!passwordService.checkPassword(password, user.getPassword())) {
            throw new RuntimeException("Invalid Credentials");
        }

        if (user.isDeactivated()) {
            if (Boolean.TRUE.equals(reactivate)) {
                user.setDeactivated(false);
                userRepository.save(user);
            } else {
                throw new RuntimeException("Account is deactivated. Please reactivate to continue.");
            }
        }
        return tokenService.generateToken(user.getId());
    }
}