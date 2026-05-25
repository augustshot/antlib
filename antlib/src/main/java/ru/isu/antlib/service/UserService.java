package ru.isu.antlib.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.isu.antlib.model.User;
import ru.isu.antlib.model.UserBookMark;
import ru.isu.antlib.repository.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService implements UserDetailsService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final UserBookMarkService userBookMarkService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       UserBookMarkService userBookMarkService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userBookMarkService = userBookMarkService;
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
//        user.setPassword("{noop}"+user.getPassword());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        this.userRepository.save(user);
    }

    @Transactional
    public void update(User user, String newPassword) {
        user.setRole("ROLE_USER");
        if (!newPassword.isBlank()) {
            user.setPassword(passwordEncoder.encode(newPassword));
        }
        userRepository.save(user);
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

    public User getByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Transactional
    public void delete(User user) {
        List<UserBookMark> marks = userBookMarkService.getAllByUser(user);

        // чтобы удалились все BookDescription с verified = 0, созданные и используемые только этим пользователем
        for (UserBookMark mark : marks) userBookMarkService.deleteBook(mark);
        userRepository.delete(user);
    }
}
