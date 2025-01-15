package MathCaptain.weakness.User.service;

import MathCaptain.weakness.User.domain.UserDetailsImpl;
import MathCaptain.weakness.User.repository.UserRepository;
import MathCaptain.weakness.User.domain.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Users users = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(email));
        return new UserDetailsImpl(users) {
        };
    }
}
