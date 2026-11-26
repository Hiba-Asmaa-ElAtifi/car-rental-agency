package ma.uir.awd.dao;

import java.util.List;

import ma.uir.awd.model.Role;
import ma.uir.awd.model.User;

/** Contrat figé (01_Decisions/Contrat_interfaces.md §2.1) : ne pas changer sans accord du groupe. */
public interface IUserDao {

    /** Insère u ; ensuite u.getId() est l'id généré. */
    void save(User u);

    /** L'utilisateur, ou null. */
    User findById(Long id);

    /** L'utilisateur qui a exactement cet e-mail, ou null (paramètre nommé :email). */
    User findByEmail(String email);

    /** Tous les utilisateurs, triés par fullName (en HQL). */
    List<User> findAll();

    /** Les utilisateurs de ce rôle, triés par fullName. */
    List<User> findByRole(Role role);

    /** u existe déjà : écrit fullName, phone, role (et passwordHash s'il a changé). */
    void update(User u);

    /**
     * Supprime l'utilisateur. false, et rien ne change, s'il n'existe pas ou s'il a une réservation,
     * une location ou une décision enregistrée.
     */
    boolean delete(Long id);
}
