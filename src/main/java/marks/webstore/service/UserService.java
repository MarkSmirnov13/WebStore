package marks.webstore.service;

import marks.webstore.domain.Role;
import marks.webstore.domain.User;
import marks.webstore.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private MailSender mailSender;

    public boolean findUserByUsername(String username) {
        return userRepo.findByUsername(username) == null;
    }

    public boolean addUser(User user) {
        User userFromDb = userRepo.findByUsername(user.getUsername());

        if (userFromDb != null) {
            return false;
        }

        user.setActive(false);
        user.setRoles(Collections.singleton(Role.USER));
        user.setActivationCode(UUID.randomUUID().toString());

        userRepo.save(user);

        sendMessage(user);

        return true;
    }

    private void sendMessage(User user) {
        if (!StringUtils.isEmpty(user.getEmail())) {
            String message = String.format(
                    "Dear, %s! \n" + "\n" +
                            "Welcome to WebStore. Please, visit the next link to confirm the registration: \n" +
                            "http://localhost:8080/activate/%s",
                    user.getUsername(),
                    user.getActivationCode()
            );

            mailSender.sendMessage(user.getEmail(), "Activation code", message);
        }
    }

    /**
     * Function that checks if the email is already taken
     *
     * @param email Our new email
     * @return true if email is free, false if the email is taken
     */
    public boolean loadUserByEmail(String email) {
        return userRepo.findByEmail(email) == null;
    }

    public boolean checkForEmail(User user) {
        return userRepo.findByEmail(user.getEmail()) == null;
    }

    public boolean activateUser(String code) {
        User user = userRepo.findByActivationCode(code);

        if (user == null) {
            return false;
        }

        user.setActive(true);
        user.setActivationCode(null);

        userRepo.save(user);

        return true;
    }

    public List<User> findAll() {
        return userRepo.findAll();
    }

    public void saveUser(User user, String username, Map<String, String> form) {
        user.setUsername(username);

        Set<String> roles = Arrays.stream(Role.values()).map(Role::name).collect(Collectors.toSet());

        user.getRoles().clear();

        for (String key : form.keySet()) {
            if (roles.contains(key)) {
                user.getRoles().add(Role.valueOf(key));
            }
        }

        userRepo.save(user);
    }

    public void deleteUser(User user) {
        userRepo.delete(user);
    }

    public void updateProfile(User user,
                              String username,
                              String password,
                              String name,
                              String surname,
                              String city,
                              String email) {
        String userEmail = user.getEmail();

        boolean isEmailChanged = (email != null && !email.equals(userEmail)) ||
                (userEmail != null && !userEmail.equals(email));

        if (isEmailChanged) {
            user.setEmail(email);

            if (!StringUtils.isEmpty(email)) {
                user.setActivationCode(UUID.randomUUID().toString());
            }
        }

        if (!StringUtils.isEmpty(password)) {
            user.setPassword(password);
        }

        if (!StringUtils.isEmpty(username)) {
            user.setUsername(username);
        }

        if (!StringUtils.isEmpty(name)) {
            user.setName(name);
        }

        if (!StringUtils.isEmpty(surname)) {
            user.setSurname(surname);
        }

        if (!StringUtils.isEmpty(city)) {
            user.setCity(city);
        }

        userRepo.save(user);

        if (isEmailChanged) {
            sendMessage(user);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByUsername(username);
    }
}