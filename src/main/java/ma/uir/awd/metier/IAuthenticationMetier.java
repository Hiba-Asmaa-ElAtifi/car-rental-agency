package ma.uir.awd.metier;

import ma.uir.awd.model.User;

/** Contrat figé (01_Decisions/Contrat_interfaces.md §3.1). */
public interface IAuthenticationMetier {

    /** L'utilisateur si l'e-mail existe et que BCrypt valide le mot de passe, sinon null (même réponse dans les deux cas). */
    User authenticate(String email, String rawPassword);

    /** Crée un CUSTOMER avec le mot de passe haché. BusinessException("error.email.taken") si l'e-mail existe. */
    User register(String fullName, String email, String rawPassword, String phone);
}
