package com.clinic.clinic_personnel_system.services;

import com.clinic.clinic_personnel_system.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        return userRepository.findByPhone(phone)
                .map(user -> User.builder()
                        .username(user.getPhone())
                        .password(user.getPassword())
                        .roles(user.getRoles().split(","))
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
}
}