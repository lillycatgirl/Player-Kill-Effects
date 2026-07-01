package com.lillycatt.playerkillfx.client.rendering;

import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class OrbitalRenderer {
    private static List<OrbitalStrikeEffect> EFFECTS =  new ArrayList<>();

    public static void AddEffect(Vec3d position){
        EFFECTS.add(new OrbitalStrikeEffect(position));
    }


    public static void Render(WorldRenderContext context){
        MatrixStack matrices = context.matrices();

        if (context.gameRenderer() == null){
            return;
        }
        Camera camera = context.gameRenderer().getCamera();
        Vec3d camPos = camera.getCameraPos();
        float yaw = camera.getYaw();
        VertexConsumerProvider consumers = context.consumers();

        for (OrbitalStrikeEffect Effect : EFFECTS){
            matrices.push();
            Vec3d pos = Effect.position;
            matrices.translate(
                    pos.x - camPos.x,
                    pos.y - camPos.y,
                    pos.z - camPos.z
            );

            Vec3d toCamera = camPos.subtract(pos);
            float angle = (float)Math.atan2(toCamera.x, toCamera.z);


            RenderBeam(matrices, consumers, Effect);


            matrices.pop();
        }
    }

    private static void RenderAura(MatrixStack matrices, VertexConsumerProvider consumers, OrbitalStrikeEffect Effect){
        VertexConsumer vc = consumers.getBuffer(RenderLayers.entityTranslucent(OrbitalStrikeEffect.TEXTURE));
        Matrix4f model = matrices.peek().getPositionMatrix();
    }

    private static void RenderBeam(MatrixStack matrices, VertexConsumerProvider consumers, OrbitalStrikeEffect Effect) {
        VertexConsumer vc = consumers.getBuffer(RenderLayers.entityTranslucent(OrbitalStrikeEffect.TEXTURE));
        Matrix4f model = matrices.peek().getPositionMatrix();

        int sides = 24;
        float radius = Effect.getWidthFromAge() * 1.4f;
        float alpha = Effect.getAlpha();
        float height = 90f;

        for (int i = 0; i < sides; i++) {

            float a0 = (float)(2.0 * Math.PI * i / sides);
            float a1 = (float)(2.0 * Math.PI * (i + 1) / sides);

            float x0 = MathHelper.cos(a0) * radius;
            float z0 = MathHelper.sin(a0) * radius;

            float x1 = MathHelper.cos(a1) * radius;
            float z1 = MathHelper.sin(a1) * radius;

            Vec3i rgb = Effect.getColorFromAgeAndSegment(i);

            vc.vertex(model, x1, 0, z1).color(rgb.getX(), rgb.getY(), rgb.getZ(), (int) (180 * alpha))
                    .texture(1, 1)
                    .overlay(OverlayTexture.DEFAULT_UV)
                    .light(0xF000F0)
                    .normal(0, 1, 0);
            vc.vertex(model, x0, 0, z0).color(rgb.getX(), rgb.getY(), rgb.getZ(), (int) (180 * alpha))
                    .texture(0, 1)
                    .overlay(OverlayTexture.DEFAULT_UV)
                    .light(0xF000F0)
                    .normal(0, 1, 0);
            vc.vertex(model, x0, height, z0).color(rgb.getX(), rgb.getY(), rgb.getZ(), (int) (180 * alpha))
                    .texture(0, 0)
                    .overlay(OverlayTexture.DEFAULT_UV)
                    .light(0xF000F0)
                    .normal(0, 1, 0);
            vc.vertex(model, x1, height, z1).color(rgb.getX(), rgb.getY(), rgb.getZ(), (int) (180 * alpha))
                    .texture(1, 0)
                    .overlay(OverlayTexture.DEFAULT_UV)
                    .light(0xF000F0)
                    .normal(0, 1, 0);
        }

        radius *= 0.98f;

        for (int i = 0; i < sides; i++) {

            float a0 = (float)(2.0 * Math.PI * i / sides);
            float a1 = (float)(2.0 * Math.PI * (i + 1) / sides);

            float x0 = MathHelper.cos(a0) * radius;
            float z0 = MathHelper.sin(a0) * radius;

            float x1 = MathHelper.cos(a1) * radius;
            float z1 = MathHelper.sin(a1) * radius;

            vc.vertex(model, x1, 0, z1).color(255,255,255, (int) (255 * alpha))
                    .texture(1, 1)
                    .overlay(OverlayTexture.DEFAULT_UV)
                    .light(0xF000F0)
                    .normal(0, 1, 0);
            vc.vertex(model, x0, 0, z0).color(255,255,255, (int) (255 * alpha))
                    .texture(0, 1)
                    .overlay(OverlayTexture.DEFAULT_UV)
                    .light(0xF000F0)
                    .normal(0, 1, 0);
            vc.vertex(model, x0, height, z0).color(255,255,255, (int) (255 * alpha))
                    .texture(0, 0)
                    .overlay(OverlayTexture.DEFAULT_UV)
                    .light(0xF000F0)
                    .normal(0, 1, 0);
            vc.vertex(model, x1, height, z1).color(255,255,255, (int) (255 * alpha))
                    .texture(1, 0)
                    .overlay(OverlayTexture.DEFAULT_UV)
                    .light(0xF000F0)
                    .normal(0, 1, 0);
        }
    }
}
