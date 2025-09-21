package blog.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import blog.exceptions.UserNotFoundException;
import blog.repositories.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        try {
            UserDetails user = userRepository.findByUsernameOrEmail(identifier);

            if (user == null) {
                throw new UserNotFoundException("User not found with identifier: " + identifier);
            }

            return user;
        } catch (UsernameNotFoundException e) {
            throw new UsernameNotFoundException("User not found by identifier: " + identifier);
        }
    }
}
