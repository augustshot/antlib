package ru.isu.antlib.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.isu.antlib.model.User;
import ru.isu.antlib.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService implements UserDetailsService{

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(()->new UsernameNotFoundException("Failed to get user: "+username));
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Transactional
    public void save(User user){
        user.setRole("ROLE_USER");
        user.setPassword("{noop}"+user.getPassword());
        this.userRepository.save(user);
    }

    public Map<Integer, String> getAllUsers(){
        Map<Integer,String> users = new HashMap();
        for (User user : this.userRepository.findAll()) {
            users.put(
                    user.getId(),
                    user.getUsername()
            );
        }
        return users;
    }

    public User findById(Integer userId){
        User user = this.userRepository
                .findById(userId)
                .orElseThrow(()->new UsernameNotFoundException(
                        "Failed to retrieve user by id" + userId));
        return user;
    }

}
