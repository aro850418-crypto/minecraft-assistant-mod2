package com.assistantmod.event;

import com.assistantmod.entity.AssistantEntity;
import com.assistantmod.registry.ModEntities;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;

import java.util.*;

public class AssistantServerEvents {

    private static final int SCAN_RADIUS = 15;
    private static final float HEALTH_THRESHOLD = 6.0f;
    private static final int SCAN_INTERVAL = 60;

    private final Map<UUID, Long> lastMobWarning = new HashMap<>();
    private final Map<UUID, Boolean> healthWarned = new HashMap<>();
    private final Set<UUID> spawnedAssistants = new HashSet<>();

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (!(player instanceof ServerPlayer serverPlayer)) return;

        serverPlayer.sendSystemMessage(Component.literal(
            "§6✦ §eПривет, §b" + player.getName().getString() + "§e! Я твой виртуальный помощник.§6 ✦"
        ));
        serverPlayer.sendSystemMessage(Component.literal(
            "§7Я буду предупреждать тебя об опасных мобах, следить за здоровьем и кружить рядом."
        ));
        serverPlayer.sendSystemMessage(Component.literal(
            "§7Нажми §aH§7, чтобы узнать свои координаты."
        ));

        ServerLevel level = serverPlayer.serverLevel();
        spawnAssistantForPlayer(level, serverPlayer);
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();
        if (!(player instanceof ServerPlayer serverPlayer)) return;

        ServerLevel level = serverPlayer.serverLevel();
        UUID playerUUID = player.getUUID();

        level.getEntities(ModEntities.ASSISTANT.get(), e -> {
            AssistantEntity ae = (AssistantEntity) e;
            return playerUUID.equals(ae.getOwnerUUID());
        }).forEach(Entity::discard);

        spawnedAssistants.remove(playerUUID);
        lastMobWarning.remove(playerUUID);
        healthWarned.remove(playerUUID);
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
            long tick = player.level().getGameTime();

            ensureAssistantExists(player);

            if (tick % SCAN_INTERVAL == 0) {
                scanForMobs(player);
            }

            checkHealth(player);
        }
    }

    private void ensureAssistantExists(ServerPlayer player) {
        UUID uuid = player.getUUID();
        if (spawnedAssistants.contains(uuid)) return;

        ServerLevel level = player.serverLevel();
        long existing = level.getEntities(ModEntities.ASSISTANT.get(), e -> {
            AssistantEntity ae = (AssistantEntity) e;
            return uuid.equals(ae.getOwnerUUID());
        }).stream().count();

        if (existing == 0) {
            spawnAssistantForPlayer(level, player);
        } else {
            spawnedAssistants.add(uuid);
        }
    }

    private void spawnAssistantForPlayer(ServerLevel level, ServerPlayer player) {
        AssistantEntity assistant = AssistantEntity.create(level, player);
        level.addFreshEntity(assistant);
        spawnedAssistants.add(player.getUUID());
    }

    private void scanForMobs(ServerPlayer player) {
        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();

        AABB searchBox = new AABB(
            x - SCAN_RADIUS, y - SCAN_RADIUS, z - SCAN_RADIUS,
            x + SCAN_RADIUS, y + SCAN_RADIUS, z + SCAN_RADIUS
        );

        List<Entity> hostiles = player.level().getEntities(player, searchBox, e ->
            e instanceof Creeper || e instanceof Zombie ||
            e instanceof Skeleton || e instanceof Spider ||
            e instanceof Witch || e instanceof Pillager
        );

        if (hostiles.isEmpty()) return;

        UUID playerUUID = player.getUUID();
        long currentTime = System.currentTimeMillis();
        long lastWarned = lastMobWarning.getOrDefault(playerUUID, 0L);

        if (currentTime - lastWarned < 5000) return;

        Map<String, Integer> mobCounts = new LinkedHashMap<>();
        for (Entity mob : hostiles) {
            String name = getMobName(mob);
            mobCounts.merge(name, 1, Integer::sum);
        }

        StringBuilder warning = new StringBuilder("§c⚠ §eОпасность! Рядом (±" + SCAN_RADIUS + " блоков): ");
        List<String> parts = new ArrayList<>();
        mobCounts.forEach((name, count) -> parts.add("§c" + name + " ×" + count));
        warning.append(String.join("§7, ", parts));

        player.sendSystemMessage(Component.literal(warning.toString()));
        lastMobWarning.put(playerUUID, currentTime);
    }

    private String getMobName(Entity mob) {
        if (mob instanceof Creeper)   return "Крипер";
        if (mob instanceof Skeleton)  return "Скелет";
        if (mob instanceof Zombie)    return "Зомби";
        if (mob instanceof Spider)    return "Паук";
        if (mob instanceof Witch)     return "Ведьма";
        if (mob instanceof Pillager)  return "Разбойник";
        return mob.getType().getDescription().getString();
    }

    private void checkHealth(ServerPlayer player) {
        float health = player.getHealth();
        UUID uuid = player.getUUID();
        boolean wasWarned = healthWarned.getOrDefault(uuid, false);

        if (health <= HEALTH_THRESHOLD && !wasWarned) {
            player.sendSystemMessage(Component.literal(
                "§4❤ §cОСТОРОЖНО! §eУ тебя мало здоровья (§c" + (int)(health / 2) + " сердец§e)! §aСрочно найди еду!"
            ));
            healthWarned.put(uuid, true);
        } else if (health > HEALTH_THRESHOLD + 2.0f && wasWarned) {
            healthWarned.put(uuid, false);
        }
    }
}
