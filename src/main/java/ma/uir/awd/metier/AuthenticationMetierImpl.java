package ma.uir.awd.metier;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import ma.uir.awd.dao.IUserDao;
import ma.uir.awd.model.Role;
import ma.uir.awd.model.User;

/**
 * Connexion et inscription.
 * - Ne connaît que l'interface IUserDao, reçue par le constructeur (chapitre 1, rapport §9.4) :
 *   new AuthenticationMetierImpl(new UserDaoImpl()).
 * - BCrypt n'a pas été vu en TP (Lab 2 : comparaison en clair). L'énoncé l'exige : la vérification
 *   se fait ici, dans le métier, jamais dans le DAO ni dans la vue.
 * - BCryptPasswordEncoder accepte les hachages $2a$ (comptes de démo) et $2b$ (seed V15).
 */
public class AuthenticationMetierImpl implements IAuthenticationMetier {

    private final IUserDao userDao;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthenticationMetierImpl(IUserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public User authenticate(String email, String rawPassword) {
        if (email == null || rawPassword == null) {
            return null;
        }
        User user = userDao.findByEmail(normalize(email));
        if (user == null || !encoder.matches(rawPassword, user.getPasswordHash())) {
            return null;   // même réponse : on ne dit pas si c'est l'e-mail ou le mot de passe qui est faux
        }
        return user;
    }

    @Override
    public User register(String fullName, String email, String rawPassword, String phone) {
        String normalizedEmail = normalize(email);
        if (userDao.findByEmail(normalizedEmail) != null) {
            throw new BusinessException("error.email.taken");
        }
        User user = new User(fullName.trim(), normalizedEmail, encoder.encode(rawPassword), phone.trim(), Role.CUSTOMER);
        userDao.save(user);
        return user;
    }

    /** Un e-mail est comparé sans espaces et en minuscules. */
    static String normalize(String email) {
        return email.trim().toLowerCase();
    }
}
