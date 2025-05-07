package com.seba.payconiqintegration.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

/**
 * Service gérant les retentatives pour les opérations critiques
 */
@Service
@EnableRetry
public class RetryService {

    private static final Logger logger = LoggerFactory.getLogger(RetryService.class);

    /**
     * Exécute une opération avec retentative en cas d'exception
     * @param operationName Nom de l'opération (pour logging)
     * @param operation La fonction à exécuter
     * @param <T> Type de retour de l'opération
     * @return Le résultat de l'opération
     */
    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public <T> T executeWithRetry(String operationName, Supplier<T> operation) {
        try {
            logger.info("Exécution de l'opération: {}", operationName);
            return operation.get();
        } catch (Exception e) {
            logger.error("Échec de l'opération {}: {}. Tentative de retentative...", operationName, e.getMessage());
            throw e; // L'annotation @Retryable interceptera cette exception et retentera
        }
    }

    /**
     * Exécute une opération sans valeur de retour avec retentative en cas d'exception
     */
    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void executeWithRetry(String operationName, Runnable operation) {
        try {
            logger.info("Exécution de l'opération: {}", operationName);
            operation.run();
        } catch (Exception e) {
            logger.error("Échec de l'opération {}: {}. Tentative de retentative...", operationName, e.getMessage());
            throw e; // L'annotation @Retryable interceptera cette exception et retentera
        }
    }

    /**
     * Méthode de récupération appelée lorsque toutes les tentatives ont échoué
     */
    @Recover
    public <T> T recover(Exception e, String operationName) {
        logger.error("Échec définitif de l'opération {} après plusieurs tentatives: {}", operationName, e.getMessage());

        // En production, vous pourriez enregistrer cet échec pour un traitement ultérieur
        // failedOperationsRepository.save (new FailedOperation (operationName, e.getMessage()));

        return null;
    }

    /**
     * Méthode de récupération pour les opérations sans valeur de retour
     */
    @Recover
    public void recoverVoid(Exception e, String operationName) {
        logger.error("Échec définitif de l'opération {} après plusieurs tentatives: {}", operationName, e.getMessage());

        // En production, vous pourriez enregistrer cet échec pour un traitement ultérieur
        // failedOperationsRepository.save (new FailedOperation (operationName, e.getMessage()));
    }
}
