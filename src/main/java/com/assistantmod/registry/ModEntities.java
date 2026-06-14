package com.assistantmod.registry;

import com.assistantmod.AssistantMod;
import com.assistantmod.entity.AssistantEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, AssistantMod.MODID);

    public static final RegistryObject<EntityType<AssistantEntity>> ASSISTANT =
            ENTITY_TYPES.register("assistant", () ->
                EntityType.Builder.<AssistantEntity>of(AssistantEntity::new, MobCategory.MISC)
                    .sized(0.6f, 0.8f)
                    .clientTrackingRange(64)
                    .build("assistant")
            );

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
