# ktor-twitch-bot

Twitch bot for commands using Ktor websockets and HTTP client.

The software is provided "as is", without warranty of any kind. Please use with great care.

---

## Setup

**Step 1:**

Add Twitch user and channel to HOCON configuration file (`src/main/resources/application.conf`):

```
bot {
    user = <twitch-bot-username>
    channel = <twitch-channel>
}
```


**Step 2:**

Register application on Twitch developer console: https://dev.twitch.tv/console

Use the user from the previous step to register a new application at: https://dev.twitch.tv/console/apps/create

For testing purposes, use `http://localhost` for OAuth redirect URL when registering a new application.

A client ID, which is used in the next step, is added when the application is created.


**Step 3:**

Request a new access token using the new client ID:
```
https://id.twitch.tv/oauth2/authorize?response_type=token&redirect_uri=http://localhost&scope=chat%3Aread+chat%3Aedit&client_id=<ADD_TWITCH_CLIENT_ID>
```

You will be redirected to a new URL containing the access token like so:
```
http://localhost/#access_token=<ACCESS_TOKEN>&scope=chat%3Aread+chat%3Aedit&token_type=bearer
```

Read more about Twitch OAuth here: https://dev.twitch.tv/docs/authentication/getting-tokens-oauth/


**Step 4:**

Add your new access token as an environment variable when running this project.

You can read more about running Ktor applications here: https://ktor.io/docs/running.html
