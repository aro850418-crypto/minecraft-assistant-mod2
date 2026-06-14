package com.assistantmod.event;

import com.assistantmod.entity.AssistantEntity;
import com.assistantmod.registry.ModEntities;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "assistantmod", bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEntityAttributeEvent {

    @SubscribeEvent
    public static void onAttributeCreate(EntityAttributeCreationEvent event) {
        event.put(ModEntities.ASSISTANT.get(), AssistantEntity.createAttributes().build());
    }
}
