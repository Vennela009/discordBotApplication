# Discord Bot Application

## Project Overview:
This is a Discord bot application that interacts with users via commands. It provides a platform for users to log in, send messages, interact with the bot, and perform tasks like managing their own interactions with the bot.

## Part 1: Core Features
### Technologies Used:
- Spring Boot
- Spring Data JPA
- Thymeleaf
- Discord API (JDA or Discord4J)
- Spring Security (for user authentication)

### Features:
- **User Authentication**: Users can log in to access the bot's functionalities.
- **Interacting with the Bot**: Users can send commands and receive responses.
- **Role-Based Access Control**: Admins can manage users, view interactions, and modify bot settings.

### Use Cases for Non-Logged-In Users:
- **View Bot Information**: Non-logged-in users can check basic bot info such as available commands and bot status.

### Use Cases for Logged-In Users:
- **Send Commands**: Users can interact with the bot using predefined commands.
- **Check Interaction History**: Users can view their past interactions with the bot.

### Admin Features:
- **Manage Users**: Admins can view, add, remove, and modify user data.
- **Manage Commands**: Admins can define new bot commands or update existing ones.

## Part 2: Authentication & Authorization
### Technologies Used:
- Spring Boot
- Spring Data JPA
- Spring Security
- Thymeleaf

### Use Cases for Non-Logged-In Users:
- **Access Basic Bot Commands**: Non-logged-in users can use some basic commands to interact with the bot.

### Use Cases for Logged-In Users:
- **Use Advanced Commands**: Logged-in users can use commands requiring authentication.
- **Manage Personal Interactions**: Users can view and manage their interactions with the bot.

### Admin Privileges:
- Admins can view and manage all user interactions.
- Admins can perform administrative tasks such as modifying bot behavior or settings.

---

## DiscordBotApplication Directory Structure

```plaintext
discordBotApplication
│
├── build.gradle
├── gradlew
├── gradlew.bat
├── HELP.md
├── README.md
├── settings.gradle
│
├── build
│   ├── classes
│   │   └── java
│   │       └── main
│   │           └── io
│   │               └── mountblue
│   │                   └── bot
│   │                       └── discord
│   │                           ├── DiscordApplication.class
│   │                           ├── auth
│   │                           │   └── Security.class
│   │                           ├── controller
│   │                           │   ├── DashBoardController.class
│   │                           │   └── LoginController.class
│   │                           ├── entity
│   │                           │   ├── BotUser.class
│   │                           │   └── Interaction.class
│   │                           ├── repository
│   │                           │   ├── BotUserRepository.class
│   │                           │   └── InteractionRepository.class
│   │                           └── service
│   │                               └── InteractionService.class
│   ├── resources
│   │   └── main
│   │       └── application.properties
│   │       └── templates
│   │           ├── dashBoard.html
│   │           └── login-form.html
│   └── tmp
│       └── compileJava
│           └── previous-compilation-data.bin
│           └── compileTransaction
│               └── stash-dir
│                   ├── DashBoardController.class.uniqueId0
│                   └── InteractionService.class.uniqueId1
│
├── gradle
│   └── wrapper
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
│
└── src
    ├── main
    │   ├── java
    │   │   └── io
    │   │       └── mountblue
    │   │           └── bot
    │   │               └── discord
    │   │                   ├── DiscordApplication.java
    │   │                   ├── auth
    │   │                   │   └── Security.java
    │   │                   ├── controller
    │   │                   │   ├── DashBoardController.java
    │   │                   │   └── LoginController.java
    │   │                   ├── entity
    │   │                   │   ├── BotUser.java
    │   │                   │   └── Interaction.java
    │   │                   ├── repository
    │   │                   │   ├── BotUserRepository.java
    │   │                   │   └── InteractionRepository.java
    │   │                   └── service
    │   │                       └── InteractionService.java
    │   └── resources
    │       ├── application.properties
    │       └── templates
    │           ├── dashBoard.html
    │           └── login-form.html
    └── test
        └── java
            └── io
                └── mountblue
                    └── bot
                        └── discord
                            └── DiscordApplicationTests.java
```
---

# Technologies Used

- **Java 17**
- **Spring Boot 3.x**
- **Spring Security**
- **Spring Data JPA**
- **PostgreSQL**
- **Thymeleaf** (optional)
- **Gradle**

---

## Installation

### Prerequisites
- **Java 17** or higher
- **PostgreSQL** (configured and running)
- **Gradle**

### Clone the Repository

```bash
git clone https://github.com/yourusername/discordBotApplication.git
cd discordBotApplication
