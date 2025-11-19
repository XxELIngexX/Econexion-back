package io.econexion.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
public class GoogleTokenVerifier {

    private final GoogleIdTokenVerifier verifier;
    private final String clientId;

    public GoogleTokenVerifier(@Value("${google.client-id}") String clientId) {
        this.clientId = clientId;
        log.info("Inicializando GoogleTokenVerifier con clientId: {}", clientId);

        this.verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                new GsonFactory()
        )
        .setAudience(Collections.singletonList(clientId))
        .build();
    }

    public GoogleUser verify(String idTokenString) throws Exception {
        log.info("Verificando ID Token...");
        GoogleIdToken idToken = verifier.verify(idTokenString);
        
        if (idToken == null) {
            log.error("❌ Token inválido - verification returned null");
            return null;
        }

        GoogleIdToken.Payload payload = idToken.getPayload();
        log.info("✅ Token verificado para: {}", payload.getEmail());

        return new GoogleUser(
                payload.getEmail(),
                (String) payload.get("name"),
                (String) payload.get("picture")
        );
    }

    public record GoogleUser(String email, String name, String picture) {}
}