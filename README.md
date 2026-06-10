Per i test basta avviare `./mvnw test` e non servono variabili d'ambiente.

Per avviare l'applicazione invece bisogna settare le variabili d'ambiente per il db richieste da `application.properties`, ovvero:
1. `SKAKKI_DB_URL`
2. `SKAKKI_DB_USER`
3. `SKAKKI_DB_PASSWORD`