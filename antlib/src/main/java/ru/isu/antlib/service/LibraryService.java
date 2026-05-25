package ru.isu.antlib.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.isu.antlib.model.Library;
import ru.isu.antlib.model.User;
import ru.isu.antlib.repository.LibraryRepository;
import ru.isu.antlib.repository.UserLibraryRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

@Service
public class LibraryService {
    @Autowired
    private LibraryRepository libraryRepository;

    @Autowired
    private UserLibraryRepository userLibraryRepository;

    private final Random random = new Random();

    @Transactional
    public Library save(Library library, boolean generateCode){
        if(generateCode) library.setInviteCode(generateInviteCode());
        return libraryRepository.save(library);
    }

    private String generateInviteCode() {
        String code;
        do {
            code = generateHexString(10);
        } while (libraryRepository.existsByInviteCode(code));
        return code;
    }

    private String generateHexString(int length) {
        String HEX = "0123456789ABCDEF";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(HEX.charAt(random.nextInt(HEX.length())));
        }
        return sb.toString();
    }

    public Library getById(Integer id){
        return libraryRepository.findById(id).get();
    }

    public Library getByInviteCode(String inviteCode){
        Optional<Library> library =libraryRepository.findByInviteCode(inviteCode);
        return library.orElse(null);
    }

    public void deleteById(Integer id){
        libraryRepository.deleteById(id);
    }

    public boolean existsByNameAndOwner(String name, User owner) {
        return libraryRepository.findByNameAndOwnerId(name.trim(), owner.getId()).isPresent();
    }


}
