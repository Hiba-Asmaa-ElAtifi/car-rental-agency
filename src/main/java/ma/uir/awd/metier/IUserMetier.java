package ma.uir.awd.metier;

import java.util.List;

import ma.uir.awd.model.Role;
import ma.uir.awd.model.User;

/** Contrat figé (01_Decisions/Contrat_interfaces.md §3.1). */
public interface IUserMetier {

    List<User> findAll();

    User findById(Long id);

    /** Crée un utilisateur de ce rôle (l'admin crée les agents). BusinessException si l'e-mail existe. */
    User create(String fullName, String email, String rawPassword, String phone, Role role);

    /** Change le rôle. BusinessException("error.user.self") si l'admin modifie son propre rôle. */
    void changeRole(Long userId, Role role, Long currentAdminId);

    /** false si l'utilisateur a un historique ou s'il s'agit de l'admin connecté. */
    boolean delete(Long userId, Long currentAdminId);
}
