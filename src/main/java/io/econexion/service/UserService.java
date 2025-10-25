package io.econexion.service;

import java.util.*;
import io.econexion.repository.UserRepository;
import io.econexion.model.User;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;      // <-- import necesario
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile; // <-- import necesario

@Service
 
public class UserService {
    private final UserRepository repository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository) throws Exception {
        this.repository = repository;
    }

    @PostConstruct
    public void init() {
        try {
            User newUser = new User(
                    "Econexia",
                    "administrativo",
                    "123456789",
                    "admin@econexia.admin",
                    passwordEncoder.encode("admin1234"),
                    "admin"
            );
            repository.save(newUser);
        }catch (Exception e){
            System.err.println("Error al crear usuario admin: " + e.getMessage());
        }
    }

    public List<User> findAll() {

        return repository.findAll();
    }

    public Optional<User> findById(UUID id) throws Exception {
        if (!repository.findById(id).isPresent()) {
            throw new Exception("Usuario no encontrado");

        }
        return repository.findById(id);
    }

    public User create(User user) throws Exception {
        if (findByEmail(user.getEmail()).isPresent()) {
            throw new Exception("El usuario ya existe");
        }

        return repository.save(user);
    }

    public Optional<User> update(UUID id, User newUser) throws Exception {
        if (!repository.findById(id).isPresent()) {
            throw new Exception("Usuario no encontrado");
        }
        newUser.setId(id);
        return Optional.of(repository.save(newUser));
    }

    public boolean delete(UUID id) throws Exception {
        if (!repository.findById(id).isPresent()) {
            throw new Exception("EL Id no esta registrado a ningun usuario");
        }
        repository.deleteById(id);
        return true;
    }

    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    public User update(User user){
        return repository.save(user);
    }
}
