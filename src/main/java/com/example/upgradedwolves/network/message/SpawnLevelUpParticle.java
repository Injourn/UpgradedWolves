package com.example.upgradedwolves.network.message;

import java.util.Random;

import com.example.upgradedwolves.capabilities.WolfStatsEnum;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.network.FriendlyByteBuf;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class SpawnLevelUpParticle implements IMessage<SpawnLevelUpParticle> {
    int wolfId;
    int statId;

    public SpawnLevelUpParticle(){
        wolfId = 0;
        statId = 0;
    }

    public SpawnLevelUpParticle(int wolf, int stat) {
        wolfId = wolf;
        statId = stat;
    }

    @Override
    public SpawnLevelUpParticle encode(SpawnLevelUpParticle message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.wolfId);
        buffer.writeInt(message.statId);
        return message;
    }

    @Override
    public SpawnLevelUpParticle decode(FriendlyByteBuf buffer) {
        
        return new SpawnLevelUpParticle(buffer.readInt(),buffer.readInt());
    }

    @Override
    public SpawnLevelUpParticle handle(SpawnLevelUpParticle message, CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            Wolf wolf = (Wolf)mc.level.getEntity(message.wolfId);
            WolfStatsEnum stat = WolfStatsEnum.values()[message.statId];
            if(wolf.getOwner() == mc.player && message.statId != 3){
                mc.player.sendSystemMessage(Component.translatable("chat.upgradedwolves.level_up",wolf.getName(),stat.toString()));
            }
            Random r = new Random();
            SimpleParticleType pt = ParticleTypes.FLASH;
            switch(stat){
                case Strength:
                pt = ParticleTypes.FLAME;
                break;
                case Intelligence:
                pt = ParticleTypes.EFFECT;
                break;
                case Speed:
                pt = ParticleTypes.FIREWORK;
                break;
                case Love:
                pt = ParticleTypes.HEART;
                break;
            }
            for(int i = 0; i < 15; i++)
                mc.level.addParticle(pt, false, wolf.getPosition(1).x() + r.nextDouble(), wolf.getPosition(1).y() + r.nextDouble(), wolf.getPosition(1).z() + r.nextDouble(), r.nextDouble()/5, r.nextDouble()/5, r.nextDouble()/5);
        });
        context.setPacketHandled(true);
        return message;
    }
    
}
