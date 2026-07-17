package com.lillycatt.playerkillfx.client;

import com.lillycatt.playerkillfx.client.rendering.OrbitalRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class PlayerKillFXClient implements ClientModInitializer {

    private List<AbstractClientPlayerEntity> deadPlayers = new ArrayList<>();

    public static final Logger LOGGER = LoggerFactory.getLogger("PlayerKillFX");

    @Override
    public void onInitializeClient() {
        WorldRenderEvents.AFTER_ENTITIES.register(OrbitalRenderer::Render);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world == null) return;

            for (AbstractClientPlayerEntity player : client.world.getPlayers()) {
                if (player.isDead() && !deadPlayers.contains(player)){
                    deadPlayers.add(player);
                    OrbitalRenderer.AddEffect(new  Vec3d(player.lastX, client.world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (int) player.lastX, (int) player.lastZ) - 1.5f,player.lastZ));
                } else if (!player.isDead()){
                    deadPlayers.remove(player);
                }
            }
        });

    }
}
