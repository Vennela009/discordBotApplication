package io.mountblue.bot.discord.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name="user_interaction")
@Data
public class Interaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id")
    private String userId;

    private String question;

    private String response;

    @Column(name="time_stamp")
    private LocalDateTime timeStamp;

    @ManyToOne
    @JoinColumn(name = "bot_user_id")
    private BotUser botUser;

    @Column(name = "is_morning_conversation")
    private boolean morningConversation;
}
