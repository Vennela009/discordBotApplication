package io.mountblue.bot.discord.repository;

import io.mountblue.bot.discord.entity.BotUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Map;

public interface BotUserRepository extends JpaRepository<BotUser,Long> {

    BotUser findByUserId(String id);

    boolean existsByUserId(String id);
}
