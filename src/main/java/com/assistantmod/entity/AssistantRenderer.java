package com.assistantmod.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class AssistantRenderer extends MobRenderer<AssistantEntity, AssistantRenderer.AssistantModel> {

    public static final ModelLayerLocation LAYER_LOCATION =
        new ModelLayerLocation(new ResourceLocation("assistantmod", "assistant"), "main");

    private static final ResourceLocation TEXTURE =
        new ResourceLocation("assistantmod", "textures/entity/assistant.png");

    public AssistantRenderer(EntityRendererProvider.Context context) {
        super(context, new AssistantModel(context.bakeLayer(LAYER_LOCATION)), 0.3f);
    }

    @Override
    public ResourceLocation getTextureLocation(AssistantEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void setupRotations(AssistantEntity entity, PoseStack poseStack,
                                   float ageInTicks, float rotationYaw, float partialTicks, float scale) {
        super.setupRotations(entity, poseStack, ageInTicks, rotationYaw, partialTicks, scale);
        float bob = Mth.sin(ageInTicks * 0.1f) * 0.1f;
        poseStack.translate(0, bob, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(ageInTicks * 2.0f));
    }

    @Override
    public void render(AssistantEntity entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);

        poseStack.pushPose();
        VertexConsumer glowConsumer = buffer.getBuffer(RenderType.entityTranslucentEmissive(TEXTURE));
        this.model.renderToBuffer(poseStack, glowConsumer, 15728880,
            OverlayTexture.NO_OVERLAY, 0xFFFFCC44);
        poseStack.popPose();
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("body",
            CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(-3f, -3f, -3f, 6, 6, 6),
            PartPose.offset(0f, 16f, 0f)
        );

        root.addOrReplaceChild("eye_left",
            CubeListBuilder.create()
                .texOffs(24, 0)
                .addBox(-2f, -1.5f, -3.1f, 2, 2, 1),
            PartPose.offset(0f, 16f, 0f)
        );

        root.addOrReplaceChild("eye_right",
            CubeListBuilder.create()
                .texOffs(28, 0)
                .addBox(0.5f, -1.5f, -3.1f, 2, 2, 1),
            PartPose.offset(0f, 16f, 0f)
        );

        return LayerDefinition.create(mesh, 32, 16);
    }

    public static class AssistantModel extends EntityModel<AssistantEntity> {
        private final ModelPart body;
        private final ModelPart eyeLeft;
        private final ModelPart eyeRight;

        public AssistantModel(ModelPart root) {
            this.body = root.getChild("body");
            this.eyeLeft = root.getChild("eye_left");
            this.eyeRight = root.getChild("eye_right");
        }

        @Override
        public void setupAnim(AssistantEntity entity, float limbSwing, float limbSwingAmount,
                              float ageInTicks, float netHeadYaw, float headPitch) {
        }

        @Override
        public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer,
                                   int packedLight, int packedOverlay, int color) {
            body.render(poseStack, buffer, packedLight, packedOverlay, color);
            eyeLeft.render(poseStack, buffer, packedLight, packedOverlay, color);
            eyeRight.render(poseStack, buffer, packedLight, packedOverlay, color);
        }
    }
}
