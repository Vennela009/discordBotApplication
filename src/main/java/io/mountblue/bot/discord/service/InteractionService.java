package io.mountblue.bot.discord.service;

import io.mountblue.bot.discord.entity.BotUser;
import io.mountblue.bot.discord.entity.Interaction;
import io.mountblue.bot.discord.repository.InteractionRepository;
import io.mountblue.bot.discord.repository.BotUserRepository;
import jakarta.annotation.PostConstruct;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InteractionService extends ListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(InteractionService.class);

    @Autowired
    private InteractionRepository interactionRepository;

    @Autowired
    private BotUserRepository botUserRepository;

    private final Map<String, Integer> morningConversationStep = new HashMap<>();
    private final Map<String, Integer> eveningConversationStep = new HashMap<>();
    private final Map<Integer, String> interactionQuestion = new HashMap<>();

    @Value("${discord.bot.token}")
    private String token;

    @Value("${discord.guild.id}")
    private Long guildId;

    private JDA jda;

    private static final String TIMEZONE = "Asia/Kolkata";

    @PostConstruct
    public void initializeBot() {
        try {
            logger.info("Initializing Discord bot...");
            setupInteractionQuestions();

            jda = JDABuilder.createDefault(token)
                    .addEventListeners(this)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS)
                    .build();
            jda.awaitReady();
            saveAllGuildMembersToDatabase();
            logger.info("Bot initialization completed successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize bot: ", e);
            throw new RuntimeException("Bot initialization failed", e);
        }
    }

    private void saveAllGuildMembersToDatabase() {
        Guild guild = jda.getGuildById(guildId);
        if (guild == null) {
            logger.error("Guild not found with ID: {}", guildId);
            return;
        }
        guild.loadMembers().onSuccess(members -> {
            for (Member member : members) {
                if (!botUserRepository.existsByUserId(member.getId())) {
                    BotUser botUser = new BotUser();
                    botUser.setUserId(member.getId());
                    botUser.setDisplayName(member.getEffectiveName());
                    botUserRepository.save(botUser);
                }
            }
        }).onError(error -> {
            logger.error("Failed to retrieve members from guild", error);
        });
    }

    private void setupInteractionQuestions() {
        interactionQuestion.put(1, "Hi, there! How are you feeling today?");
        interactionQuestion.put(2, "What did you work on yesterday?");
        interactionQuestion.put(3, "What is your plan for today?");
        interactionQuestion.put(4, "Do you have any doubts or is there anything blocking your progress?");
        interactionQuestion.put(5, "Thank you! Have a Great day");
        interactionQuestion.put(100, "Hi, Hope you are doing good! How was your day?");
        interactionQuestion.put(101, "Thank you for sharing! Have a great evening!");
        logger.info("Interaction questions initialized");
    }

    @Scheduled(cron = "0 30 10 * * ?", zone = TIMEZONE)
    public void sendMorningMessage() {
        logger.info("Initiating morning message routine at {}", LocalDateTime.now(ZoneId.of(TIMEZONE)));
        botUserRepository.findAll().forEach(botUser -> {
            sendScheduledMessage(botUser, true);
        });
    }

    @Scheduled(cron = "0 30 18 * * ?", zone = TIMEZONE)
    public void sendEveningMessage() {
        logger.info("Initiating evening message routine at {}", LocalDateTime.now(ZoneId.of(TIMEZONE)));
        botUserRepository.findAll().forEach(botUser -> {
            sendScheduledMessage(botUser, false);
        });
    }

    private void sendScheduledMessage(BotUser botUser, boolean isMorningConversation) {
        jda.retrieveUserById(botUser.getUserId()).queue(
                user -> {
                    if (user != null && !((user.getId()).equals(jda.getSelfUser().getId()))) {
                        logger.info("{} message: Found user {}", isMorningConversation ? "Morning" : "Evening", botUser.getUserId());
                        sendMessageToUser(user, botUser, isMorningConversation);
                    } else {
                        logger.error("{} message: BotUser {} not found", isMorningConversation ? "Morning" : "Evening", botUser.getUserId());
                    }
                },
                error -> logger.error("{} message: Failed to retrieve user {}: ", isMorningConversation ? "Morning" : "Evening", botUser.getUserId(), error)
        );
    }

    private int getAndIncrementMorningStep(String userId) {
        int step = morningConversationStep.getOrDefault(userId, 1);
        morningConversationStep.put(userId, step + 1);
        return step;
    }

    private int getAndIncrementEveningStep(String userId) {
        int step = eveningConversationStep.getOrDefault(userId, 100);
        eveningConversationStep.put(userId, step + 1);
        return step;
    }

    private void sendMessageToUser(User user, BotUser botUser, boolean isMorningConversation) {
        user.openPrivateChannel().queue(
                channel -> {
                    String message = interactionQuestion.get(isMorningConversation ? getAndIncrementMorningStep(botUser.getUserId()) : getAndIncrementEveningStep(botUser.getUserId()));
                    channel.sendMessage(message).queue(
                            success -> logger.info("{} message sent successfully: {}", isMorningConversation ? "Morning" : "Evening", message),
                            error -> logger.error("{} message: Failed to send message: ", isMorningConversation ? "Morning" : "Evening", error)
                    );
                },
                error -> logger.error("{} message: Failed to open private channel: ", isMorningConversation ? "Morning" : "Evening", error)
        );
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        User user = event.getAuthor();

        if (!event.isFromType(ChannelType.PRIVATE) || user.isBot()) {
            return;
        }

        try {
            BotUser botUser = botUserRepository.findByUserId(user.getId());
            if (botUser == null) {
                botUser = new BotUser();
                botUser.setUserId(user.getId());
                botUser.setDisplayName(user.getName());
                botUserRepository.save(botUser);
            }

            handleUserResponse(event, botUser);
        } catch (Exception e) {
            logger.error("Error processing user response: ", e);
            notifyUserOfError(user);
        }
    }

    private void handleUserResponse(MessageReceivedEvent event, BotUser botUser) {
        String reply = event.getMessage().getContentDisplay();
        User user = event.getAuthor();
        int currentStep ;
        String question ;

        if (isMorningConversationActive(botUser.getUserId()) && getMorningConversationStep(botUser.getUserId()) <= 5) {
            currentStep = getMorningConversationStep(botUser.getUserId());
            question = interactionQuestion.get(currentStep -1 );
            if (currentStep != 6){
                saveInteraction(user.getId(), question, reply, botUser, true);
            }
            logger.info("Processing morning conversation step: {}", currentStep);

            if (getMorningConversationStep(botUser.getUserId()) <= 5) {
                incrementMorningConversationStep(botUser.getUserId());
                sendNextQuestionIfAvailable(user, currentStep, botUser, true);
            } else {
                incrementMorningConversationStep(botUser.getUserId());
                botUser.setInMorningConversation(false);
                botUserRepository.save(botUser);
                logger.info("Morning conversation completed for user: {}", botUser.getUserId());
            }
        } else if (isEveningConversationActive(botUser.getUserId()) && getEveningConversationStep(botUser.getUserId()) <= 101) {
            currentStep = getEveningConversationStep(botUser.getUserId());
            question = interactionQuestion.get(currentStep -1);
            if (currentStep != 102 && currentStep>100){
                saveInteraction(user.getId(), question, reply, botUser, false);
            }
            logger.info("Processing evening conversation step: {}", currentStep);


            if (getEveningConversationStep(botUser.getUserId()) <= 101) {
                incrementEveningConversationStep(botUser.getUserId());
                sendNextQuestionIfAvailable(user, currentStep, botUser, false);
            } else {
                setEveningConversationStep(botUser.getUserId(), 101);
                botUser.setInEveningConversation(false);
                botUserRepository.save(botUser);
                logger.info("Evening conversation completed for user: {}", botUser.getUserId());
            }
        } else {
            logger.info("No active conversation for user: {}", botUser.getUserId());
        }


        botUserRepository.save(botUser);
    }

    private void saveInteraction(String userId, String question, String reply, BotUser botUser, boolean isMorningConversation) {
        try {
            Interaction interaction = new Interaction();
            interaction.setUserId(userId);
            interaction.setQuestion(question);
            interaction.setResponse(reply);
            interaction.setTimeStamp(LocalDateTime.now(ZoneId.of(TIMEZONE)));
            interaction.setBotUser(botUser);
            interaction.setMorningConversation(isMorningConversation);

            interactionRepository.save(interaction);
            logger.info("Successfully saved interaction to database");
        } catch (Exception e) {
            logger.error("Failed to save interaction to database: ", e);
        }
    }

    private void sendNextQuestionIfAvailable(User user, int currentStep, BotUser botUser, boolean isMorningConversation) {
        String nextQuestion = interactionQuestion.get(currentStep);
        if (nextQuestion != null) {
            user.openPrivateChannel().queue(
                    channel -> {
                        channel.sendMessage(nextQuestion).queue(
                                success -> logger.info("Successfully sent next question: {}", nextQuestion),
                                error -> logger.error("Failed to send next question: ", error)
                        );
                    },
                    error -> logger.error("Failed to open private channel for next question: ", error)
            );
        } else {
            logger.info("No next question available for step {}", currentStep);
        }
    }

    private void notifyUserOfError(User user) {
        user.openPrivateChannel().queue(
                channel -> {
                    channel.sendMessage("Sorry, there was an error processing your response. Please try again later.")
                            .queue(
                                    success -> logger.info("Sent error notification to user"),
                                    error -> logger.error("Failed to send error notification: ", error)
                            );
                },
                error -> logger.error("Failed to open private channel for error notification: ", error)
        );
    }

    private boolean isMorningConversationActive(String userId) {
        return morningConversationStep.containsKey(userId) && morningConversationStep.get(userId) <= 5;
    }

    private boolean isEveningConversationActive(String userId) {
        return eveningConversationStep.containsKey(userId) && eveningConversationStep.get(userId) <= 101;
    }

    private int getMorningConversationStep(String userId) {
        return morningConversationStep.getOrDefault(userId, 1);
    }

    private int getEveningConversationStep(String userId) {
        return eveningConversationStep.getOrDefault(userId, 100);
    }

    private void incrementMorningConversationStep(String userId) {
        morningConversationStep.put(userId, morningConversationStep.getOrDefault(userId, 1) + 1);
    }

    private void incrementEveningConversationStep(String userId) {
        eveningConversationStep.put(userId, eveningConversationStep.getOrDefault(userId, 100) + 1);
    }

    private void setEveningConversationStep(String userId, int step) {
        eveningConversationStep.put(userId, step);
    }

    public List<Interaction> fetchAllInteraction() {
        return interactionRepository.findAll();
    }
}