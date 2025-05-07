package com.seba.payconiqintegration.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service garantissant l'idempotence des traitements de webhook
 */
@Service
public class IdempotencyService {

    private static final Logger logger = LoggerFactory.getLogger(IdempotencyService.class);

    // Cache d'idempotence stocke les IDs de webhook déjà traités
    // Dans une application réelle, ceci devrait être un cache distribué ou une table de base de données
    private final Map<String, Instant> processedEvents = new ConcurrentHashMap<>();

    // Durée pendant laquelle garder l'entrée dans le cache (24 heures par défaut)
    private final Duration retentionDuration = Duration.ofHours(24);

    /**
     * Vérifie si un événement a déjà été traité
     * @param eventId Identifiant unique de l'événement
     * @return true si c'est un nouvel événement, false si déjà traité
     */
    public boolean isNewEvent(String eventId) {
        // Nettoyer les entrées périmées
        cleanExpiredEntries();

        // Vérifier si l'événement est nouveau
        if (processedEvents.containsKey(eventId)) {
            logger.info("Événement {} déjà traité, idempotence appliquée", eventId);
            return false;
        }

        // Marquer l'événement comme traité
        processedEvents.put(eventId, Instant.now());
        logger.info("Nouvel événement {} enregistré pour traitement", eventId);
        return true;
    }

    /**
     * Nettoie les entrées périmées du cache
     */
    private void cleanExpiredEntries() {
        Instant now = Instant.now();
        processedEvents.entrySet().removeIf(entry ->
                Duration.between(entry.getValue(), now).compareTo(retentionDuration) > 0);
    }
}