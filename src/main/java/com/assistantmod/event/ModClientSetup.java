package com.assistantmod.event;

import com.assistantmod.entity.AssistantRenderer;
import com.assistantmod.registry.ModEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "assistantmod", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModClientSetup {

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.ASSISTANT.get(), AssistantRenderer::new);
    }

    @SubscribeEvent
    public static void onRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(AssistantRenderer.LAYER_LOCATION, AssistantRenderer::createBodyLayer);
    }
}
