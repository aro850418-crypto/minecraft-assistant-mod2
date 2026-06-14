package com.assistantmod.event;

import com.assistantmod.client.KeyBindings;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class AssistantClientEvents {

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        while (KeyBindings.SHOW_COORDS.consumeClick()) {
            showCoordinates(mc.player);
        }
    }

    private void showCoordinates(Player player) {
        BlockPos pos = player.blockPosition();
        String biome = player.level()
            .getBiome(pos)
            .unwrapKey()
            .map(k -> k.location().getPath())
            .orElse("неизвестно");

        player.sendSystemMessage(Component.literal(
            "§6✦ §eКоординаты: §aX: §f" + pos.getX() +
            " §aY: §f" + pos.getY() +
            " §aZ: §f" + pos.getZ() +
            "  §7| §aBiome: §f" + biome
        ));
    }
}
