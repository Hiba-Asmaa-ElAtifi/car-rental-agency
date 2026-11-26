package ma.uir.awd.metier;

/**
 * Refus d'une règle métier. getMessage() est une clé de package.properties :
 * l'Action fait addActionError(getText(e.getMessage())) (Lab 8).
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String messageKey) {
        super(messageKey);
    }
}
