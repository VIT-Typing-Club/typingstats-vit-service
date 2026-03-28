## Typing Stats VIT API

Backend API service for TypingStatsVIT — Leaderboards & Typing Resources.

- Spring-based service architecture setup
- Discord OAuth2 authentication
- Leaderboard API for tracking user performance
- Verification service for user validation
- Daily competition service (TypeGG Daily Quotes)
- Sync Service - Scheduled and manual (leaderboards)

### Run Locally

Make sure you have Java and Maven installed.

```bash
# install dependencies
mvn clean install

# run with dev profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Maintained by VIT Typing Club.

### License

MIT License © VIT Typing Club
