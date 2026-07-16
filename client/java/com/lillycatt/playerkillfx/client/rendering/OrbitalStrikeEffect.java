package com.lillycatt.playerkillfx.client.rendering;

import com.lillycatt.playerkillfx.client.PlayerKillFXClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.Heightmap;

public class OrbitalStrikeEffect {
    public static final float DURATION = 4f;
    public static final int SHOCKWAVECOUNT = 4;
    public static final float FADEDISTANCE = 2f;
    public float[] rotations =  new float[SHOCKWAVECOUNT];

    public final Vec3d position;
    public final long startTime;

    private final static Vec3i PINK = new Vec3i(209, 98, 164);
    private final static Vec3i DARK_PINK = new Vec3i(181, 86, 144);
    private final static Vec3i WHITE = new Vec3i(255, 255, 255);
    private final static Vec3i ORANGE = new Vec3i(255, 154, 86);
    private final static Vec3i DARK_ORANGE = new Vec3i(239, 118, 39);

    private final static Vec3i[] Colors = new Vec3i[]
            {
                    DARK_PINK, PINK, WHITE, ORANGE, DARK_ORANGE, ORANGE, WHITE, PINK
            };

    public OrbitalStrikeEffect(Vec3d position) {
        this.startTime = System.currentTimeMillis();

        ClientWorld world = MinecraftClient.getInstance().world;



        if (world == null) {
            this.position =  position;
            for(int i = 0; i < SHOCKWAVECOUNT; i++) {
                rotations[i] = (float) (Math.random() * Math.PI);
            }
            return;
        }

        for(int i = 0; i < SHOCKWAVECOUNT; i++) {
            rotations[i] = world.getRandom().nextFloat() * 360f;
        }

        BlockPos surface = world.getTopPosition(
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                new BlockPos((int) Math.floor(position.x), 0, (int) Math.floor(position.z))
        );
        this.position = surface.toCenterPos().add(new Vec3d(0, -0.5, 0));
    }

    public float getAgeSeconds() {
        if (((System.currentTimeMillis() - startTime) / 1000f) > DURATION) {
            // OrbitalRenderer.ScheduleRemoveEffect(OrbitalStrikeEffect.this);
        }
        return (System.currentTimeMillis() - startTime) / 1000f;
    }

    public float getAlphaFromDistanceSquared(float distanceSquared) {
        return Math.clamp(distanceSquared / (FADEDISTANCE * FADEDISTANCE), 0f, 1f);
    }

    public float getWidthFromAge() {
        float ageSeconds = getAgeSeconds();
        float t = ((4 * ageSeconds / DURATION) % 4) / 3;
        float d = 2.3f;
        return ((float)Math.min(Math.cbrt(((d * t) - 1.5f) / (d * 4.0f)) * d * t, 0.8d) + 0.38717f) / 1.18717f;
    }

    public float getHeightFromAge() {
        float ageSeconds = getAgeSeconds();
        float t = ((4 * ageSeconds / DURATION) % 4) / 3;

        return (float) Math.max(-Math.log((2*t) + 0.1), 0f);
    }

    public Vec3i getColorFromAgeAndSegment(int segment) {
        return Colors[segment % Colors.length];
    }

    public float getAlpha() {
        float ageSeconds = getAgeSeconds();
        float t = ((4 * ageSeconds / DURATION) % 4) / 3;
        return Math.min(Math.min(20f * t * Math.abs(t - 0.45f), 1) * t, 1);
    }

    public float ShockwaveRadiusMax() {
        float ageSeconds = getAgeSeconds();
        float t = ((4 * ageSeconds / DURATION) % 4) / 3;

        return (float) ((Math.exp((1.5d * t) - 1) - 0.36788d) / 2.36788d);
    }

    public float ShockwaveRadiusAtTime(float maxSize, float offset) {
        float ageSeconds = getAgeSeconds() + offset;
        float t = ((4 * ageSeconds / DURATION) % 4) / 3;

        return ((6 * t) % 1) *  maxSize;
    }

    public float ShockwaveAlphaAtTime(float offset) {
        float ageSeconds = getAgeSeconds() + offset;
        float t = ((4 * ageSeconds / DURATION) % 4) / 3;

        return (float) Math.clamp((Math.exp((t) + 0.8) - 4d), 0, 1);
    }

    public static final Identifier BLANKTEXTURE =
            Identifier.of("playerkillfx", "textures/effect/blank.png");
    public static final Identifier PATTERNTEXTURE =
            Identifier.of("playerkillfx", "textures/effect/ground_overlay.png");
    public static final Identifier SHOCKWAVETEXTURE =
            Identifier.of("playerkillfx", "textures/effect/shockwave.png");
}
