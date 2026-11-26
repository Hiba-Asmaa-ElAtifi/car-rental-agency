package ma.uir.awd.metier;

import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import ma.uir.awd.dao.IUserDao;
import ma.uir.awd.model.Role;
import ma.uir.awd.model.User;

/** Gestion des utilisateurs par l'administrateur. Ne connaît que IUserDao (injection par le constructeur). */
public class UserMetierImpl implements IUserMetier {

    private final IUserDao userDao;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserMetierImpl(IUserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public List<User> findAll() {
        return userDao.findAll();
    }

    @Override
    public List<User> findByRole(Role role) {
        return userDao.findByRole(role);
    }

    @Override
    public User findById(Long id) {
        return userDao.findById(id);
    }

    @Override
    public User create(String fullName, String email, String rawPassword, String phone, Role role) {
        String normalizedEmail = AuthenticationMetierImpl.normalize(email);
        if (userDao.findByEmail(normalizedEmail) != null) {
            throw new BusinessException("error.email.taken");
        }
        User user = new User(fullName.trim(), normalizedEmail, encoder.encode(rawPassword), phone.trim(), role);
        userDao.save(user);
        return user;
    }

    @Override
    public void changeRole(Long userId, Role role, Long currentAdminId) {
        if (userId.equals(currentAdminId)) {
            throw new BusinessException("error.user.self");   // l'admin ne peut pas se retirer ses propres droits
        }
        User user = userDao.findById(userId);
        if (user == null) {
            throw new BusinessException("error.user.notFound");
        }
        user.setRole(role);
        userDao.update(user);
    }

    @Override
    public boolean delete(Long userId, Long currentAdminId) {
        if (userId.equals(currentAdminId)) {
            return false;
        }
        return userDao.delete(userId);   // le DAO refuse s'il existe un historique
    }
}
