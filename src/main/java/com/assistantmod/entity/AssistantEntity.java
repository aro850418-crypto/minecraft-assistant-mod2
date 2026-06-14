package com.assistantmod.entity;

import com.assistantmod.registry.ModEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.UUID;

public class AssistantEntity extends PathfinderMob {

    private static final EntityDataAccessor<String> OWNER_NAME =
            SynchedEntityData.defineId(AssistantEntity.class, EntityDataSerializers.STRING);

    private UUID ownerUUID;
    private int hoverTick = 0;

    public AssistantEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
        this.setInvulnerable(true);
        this.setPersistenceRequired();
    }

    public static AssistantEntity create(Level level, Player player) {
        AssistantEntity entity = new AssistantEntity(ModEntities.ASSISTANT.get(), level);
        entity.ownerUUID = player.getUUID();
        entity.entityData.set(OWNER_NAME, player.getName().getString());
        entity.moveTo(
            player.getX() + 1.5,
            player.getY() + 1.5,
            player.getZ() + 1.5,
            0f, 0f
        );
        return entity;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(OWNER_NAME, "");
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.5)
                .add(Attributes.FLYING_SPEED, 0.5);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FollowOwnerGoal(this));
        this.goalSelector.addGoal(2, new HoverGoal(this));
    }

    @Override
    public void tick() {
        super.tick();
        hoverTick++;

        if (!this.level().isClientSide) {
            Player owner = getOwner();
            if (owner == null || !owner.isAlive()) {
                this.discard();
                return;
            }

            double hoverOffset = Math.sin(hoverTick * 0.08) * 0.15;
            double targetX = owner.getX() + Math.cos(hoverTick * 0.03) * 1.8;
            double targetY = owner.getY() + 2.2 + hoverOffset;
            double targetZ = owner.getZ() + Math.sin(hoverTick * 0.03) * 1.8;

            double dx = targetX - this.getX();
            double dy = targetY - this.getY();
            double dz = targetZ - this.getZ();

            this.setDeltaMovement(dx * 0.25, dy * 0.25, dz * 0.25);
            this.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(owner.getX(), owner.getEyeY(), owner.getZ()));
        }
    }

    public Player getOwner() {
        if (ownerUUID == null) return null;
        if (this.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            return serverLevel.getPlayerByUUID(ownerUUID);
        }
        return null;
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (ownerUUID != null) {
            tag.putUUID("OwnerUUID", ownerUUID);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.hasUUID("OwnerUUID")) {
            ownerUUID = tag.getUUID("OwnerUUID");
        }
    }

    static class FollowOwnerGoal extends Goal {
        private final AssistantEntity assistant;

        public FollowOwnerGoal(AssistantEntity assistant) {
            this.assistant = assistant;
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return assistant.getOwner() != null;
        }

        @Override
        public void tick() {
        }
    }

    static class HoverGoal extends Goal {
        private final AssistantEntity assistant;

        public HoverGoal(AssistantEntity assistant) {
            this.assistant = assistant;
        }

        @Override
        public boolean canUse() {
            return true;
        }

        @Override
        public void tick() {
        }
    }
}
