package com.assistantmod;

import com.assistantmod.client.KeyBindings;
import com.assistantmod.event.AssistantClientEvents;
import com.assistantmod.event.AssistantServerEvents;
import com.assistantmod.registry.ModEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(AssistantMod.MODID)
public class AssistantMod {

    public static final String MODID = "assistantmod";

    public AssistantMod(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        ModEntities.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            modEventBus.addListener(this::clientSetup);
        });

        MinecraftForge.EVENT_BUS.register(new AssistantServerEvents());

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
            MinecraftForge.EVENT_BUS.register(new AssistantClientEvents())
        );
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        KeyBindings.register();
    }
}
