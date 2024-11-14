package io.mountblue.bot.discord.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="user_details")
@Data
public class BotUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id")
    private String userId;

    @Column(name="display_name")
    private String displayName;

    @Column(name = "morning_step")
    private int morningStep = 1;

    @Column(name = "evening_step")
    private int eveningStep = 100;

    @Column(name = "in_morning_conversation")
    private boolean inMorningConversation;

    @Column(name = "in_evening_conversation")
    private boolean inEveningConversation;
}
