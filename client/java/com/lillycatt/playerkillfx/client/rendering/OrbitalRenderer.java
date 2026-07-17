package com.lillycatt.playerkillfx.client.rendering;

import com.lillycatt.playerkillfx.client.PlayerKillFXClient;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class OrbitalRenderer {
    private static List<OrbitalStrikeEffect> EFFECTS =  new ArrayList<>();
    private static List<OrbitalStrikeEffect> SCHEDULEDREMOVEEFFECTS =  new ArrayList<>();

    public static void AddEffect(Vec3d position){
        EFFECTS.add(new OrbitalStrikeEffect(position));
    }

    public static void ScheduleRemoveEffect(OrbitalStrikeEffect effect){
        SCHEDULEDREMOVEEFFECTS.add(effect);
    }


    public static void Render(WorldRenderContext context){
        MatrixStack matrices = context.matrices();

        if (context.gameRenderer() == null){
            return;
        }
        Camera camera = context.gameRenderer().getCamera();
        Vec3d camPos = camera.getCameraPos();
        VertexConsumerProvider consumers = context.consumers();

        for (OrbitalStrikeEffect Effect : EFFECTS){
            matrices.push();
            Vec3d pos = Effect.position;
            matrices.translate(
                    pos.x - camPos.x,
                    pos.y - camPos.y,
                    pos.z - camPos.z
            );

            Vec2f pos2D = new Vec2f((float) pos.x, (float) pos.z);
            Vec2f camPos2D = new Vec2f((float) camPos.x, (float) camPos.z);

            float alphaMultiplier = Effect.getAlphaFromDistanceSquared(pos2D.distanceSquared(camPos2D));

            RenderBeam(matrices, consumers, Effect, alphaMultiplier);
            RenderShockwave(matrices, consumers, Effect, alphaMultiplier);


            matrices.pop();
        }

        for (OrbitalStrikeEffect Effect : SCHEDULEDREMOVEEFFECTS){
            EFFECTS.remove(Effect);
        }
        SCHEDULEDREMOVEEFFECTS.clear();
    }

    private static void RenderShockwave(MatrixStack matrices, VertexConsumerProvider consumers, OrbitalStrikeEffect Effect, float alphaMultiplier){
        VertexConsumer vc = consumers.getBuffer(RenderLayers.entityTranslucent(OrbitalStrikeEffect.SHOCKWAVETEXTURE));
        Matrix4f model = matrices.peek().getPositionMatrix();

        // Shockwave thingy
        int waves = OrbitalStrikeEffect.SHOCKWAVECOUNT;
        float shockwaveRadiusMax = Effect.ShockwaveRadiusMax();
        for  (int i = 0; i < waves; i++) {
            float shockWaveRadius = Effect.ShockwaveRadiusAtTime(shockwaveRadiusMax, i * 0.3f) * 6;
            float shockwaveAlpha = Effect.getAlpha() * 0.3f * alphaMultiplier;
            vc.vertex(model, shockWaveRadius, 0.1f, shockWaveRadius)
                    .color(255,255,255, (int)(255 * shockwaveAlpha))
                    .texture(1, 1)
                    .overlay(OverlayTexture.DEFAULT_UV)
                    .light(0xF000F0)
                    .normal(0, -1, 0);
            vc.vertex(model, -shockWaveRadius, 0.1f, shockWaveRadius)
                    .color(255,255,255, (int)(255 * shockwaveAlpha))
                    .texture(0, 1)
                    .overlay(OverlayTexture.DEFAULT_UV)
                    .light(0xF000F0)
                    .normal(0, -1, 0);
            vc.vertex(model, -shockWaveRadius, 0.1f, -shockWaveRadius)
                    .color(255,255,255, (int)(255 * shockwaveAlpha))
                    .texture(0, 0)
                    .overlay(OverlayTexture.DEFAULT_UV)
                    .light(0xF000F0)
                    .normal(0, -1, 0);
            vc.vertex(model, shockWaveRadius, 0.1f, -shockWaveRadius)
                    .color(255,255,255, (int)(255 * shockwaveAlpha))
                    .texture(1, 0)
                    .overlay(OverlayTexture.DEFAULT_UV)
                    .light(0xF000F0)
                    .normal(0, -1, 0);
            model.rotate(Effect.rotations[i], new Vector3f(0, 1, 0));
        }
    }

    private static void RenderBeam(MatrixStack matrices, VertexConsumerProvider consumers, OrbitalStrikeEffect Effect, float alphaMultiplier) {
        VertexConsumer vc = consumers.getBuffer(RenderLayers.entityTranslucent(OrbitalStrikeEffect.BLANKTEXTURE));
        Matrix4f model = matrices.peek().getPositionMatrix();

        int sides = 24;
        float radius = Effect.getWidthFromAge() * 1.4f;
        int alpha = (int)(Effect.getAlpha() * alphaMultiplier * 255);

        float height = 190f;
        float base = Effect.getHeightFromAge();

        int[][] colors = new int[sides][4];

        for (int i = 0; i < sides; i++) {
            Vec3i rgb = Effect.getColorFromAgeAndSegment(i);
            colors[i] = new int[] {rgb.getX(), rgb.getY(), rgb.getZ(), alpha};
        }
        RenderCylinder(sides, radius, height, vc, model, base, colors, false);

        for (int i = 0; i < sides; i++) {
            colors[i] = new int[] {255, 255, 255, (int) (255 * alphaMultiplier)};
        }

        radius *= 0.95f;
        RenderCylinder(sides, radius, height, vc, model, base, colors, false);

        radius *= 0.99f;
        RenderCylinder(sides, radius, height, vc, model, base, colors, true);
    }

    private static void RenderCylinder(int sides, float radius, float height, VertexConsumer vc, Matrix4f model, float base, int[][] colors, boolean interior){
        float yNormal = 1f;
        if (interior) yNormal = -1f;

        for (int i = 0; i < sides; i++) {

            float a0 = (float)(2.0 * Math.PI * i / sides);
            float a1 = (float)(2.0 * Math.PI * (i + 1) / sides);

            float x0 = MathHelper.cos(a0) * radius;
            float z0 = MathHelper.sin(a0) * radius;

            float x1 = MathHelper.cos(a1) * radius;
            float z1 = MathHelper.sin(a1) * radius;

            vc.vertex(model, x1, base, z1)
                    .color(colors[i][0], colors[i][1], colors[i][2], colors[i][3])
                    .texture(1, 1)
                    .overlay(OverlayTexture.DEFAULT_UV)
                    .light(0xF000F0)
                    .normal(0, yNormal, 0);
            vc.vertex(model, x0, base, z0)
                    .color(colors[i][0], colors[i][1], colors[i][2], colors[i][3])
                    .texture(0, 1)
                    .overlay(OverlayTexture.DEFAULT_UV)
                    .light(0xF000F0)
                    .normal(0, yNormal, 0);
            vc.vertex(model, x0, base + height, z0)
                    .color(colors[i][0], colors[i][1], colors[i][2], colors[i][3])
                    .texture(0, 0)
                    .overlay(OverlayTexture.DEFAULT_UV)
                    .light(0xF000F0)
                    .normal(0, yNormal, 0);
            vc.vertex(model, x1, base + height, z1)
                    .color(colors[i][0], colors[i][1], colors[i][2], colors[i][3])
                    .texture(1, 0)
                    .overlay(OverlayTexture.DEFAULT_UV)
                    .light(0xF000F0)
                    .normal(0, yNormal, 0);
        }
    }
}
