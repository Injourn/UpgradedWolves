package com.example.upgradedwolves.network.message;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class CreateParticleForMobMessage implements IMessage<CreateParticleForMobMessage> {
    protected int entityId;
    protected ParticleOptions particleData;
    protected int count;

    public CreateParticleForMobMessage(){
        entityId = 0;
        count = 0;
    }

    public CreateParticleForMobMessage(int entityId,ParticleOptions particleType,int count){
        this.entityId = entityId;
        this.particleData = particleType;
        this.count = count;
    }

    @Override
    public CreateParticleForMobMessage encode(CreateParticleForMobMessage message, FriendlyByteBuf buffer) {     
        EntityDataSerializers.PARTICLE.codec().encode((RegistryFriendlyByteBuf) buffer, message.particleData);
        buffer.writeInt(message.entityId);
        buffer.writeInt(message.count);
        return message;
    }

    @Override
    public CreateParticleForMobMessage decode(FriendlyByteBuf buffer) {
        
        ParticleOptions particleData = EntityDataSerializers.PARTICLE.codec().decode((RegistryFriendlyByteBuf) buffer);
        int entityId = buffer.readInt();
        int count = buffer.readInt();        
        return new CreateParticleForMobMessage(entityId,particleData,count);
    }

    @Override
    public CreateParticleForMobMessage handle(CreateParticleForMobMessage message, CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            Entity entity = (Entity)mc.level.getEntity(message.entityId);
            Random r = new Random();

            for(int i = 0; i < message.count; i++)
                mc.level.addParticle(message.particleData, false, entity.getPosition(1).x + r.nextDouble(), entity.getPosition(1).y + r.nextDouble(), entity.getPosition(1).z + r.nextDouble(), r.nextDouble()/5, r.nextDouble()/5, r.nextDouble()/5);
        });
        return message;
    }

}