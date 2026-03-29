package com.bedfordshire.recipenest.security;

import com.bedfordshire.recipenest.repository.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;


@Service
public class AppUserDetailsService implements UserDetailsService {

    // Repository used to fetch users from the database
    private final UserRepository userRepository;

    // Constructed for AppUserDetailsService
    // Spring uses this to inject the UserRepository dependency
    public AppUserDetailsService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Username is the Users email address
        // Spring secruity will call this method during login
        return userRepository.findByEmail(username)
                .orElseThrow(() ->new  UsernameNotFoundException("User not found with email: "+ username));
    }
}
