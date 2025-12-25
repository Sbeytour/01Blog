package blog.services;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import blog.entity.User;
import blog.exceptions.UserNotFoundException;
import blog.repositories.UserRepository;
import blog.util.BanMessageFormatter;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        try {
            User user = userRepository.findByUsernameOrEmail(identifier);

            if (user == null) {
                throw new UserNotFoundException("User not found with identifier: " + identifier);
            }

            if (user.isActiveBan()) {
                String banMessage = BanMessageFormatter.formatBanMessage(user.getBannedUntil(), user.getBanReason());
                throw new DisabledException(banMessage);
            }

            return user;
        } catch (UsernameNotFoundException e) {
            throw new UsernameNotFoundException("User not found by identifier: " + identifier);
        }
    }
}
