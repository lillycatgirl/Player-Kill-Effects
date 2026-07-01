package com.lillycatt.playerkillfx.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.util.math.Vec3d;

public class PlayerKillFXClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        WorldRenderEvents.END_MAIN.register(OrbitalRenderer::Render);
        OrbitalRenderer.AddEffect(new Vec3d(0,64,-100));
    }
}
