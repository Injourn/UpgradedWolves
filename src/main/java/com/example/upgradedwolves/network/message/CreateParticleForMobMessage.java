package com.example.upgradedwolves.network.message;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class CreateParticleForMobMessage implements IMessage<CreateParticleForMobMessage> {
    protected int entityId;
    protected IParticleData particleData;
    protected int count;

    public CreateParticleForMobMessage(){
        entityId = 0;
        count = 0;
    }

    public CreateParticleForMobMessage(int entityId,IParticleData particleType,int count){
        this.entityId = entityId;
        this.particleData = particleType;
        this.count = count;
    }


    @Override
    public void encode(CreateParticleForMobMessage message, PacketBuffer buffer) {     
        DataSerializers.PARTICLE_DATA.write(buffer, message.particleData);           
        buffer.writeInt(message.entityId);
        buffer.writeInt(message.count);
    }

    @Override
    public CreateParticleForMobMessage decode(PacketBuffer buffer) {
        IParticleData particleData = DataSerializers.PARTICLE_DATA.read(buffer);
        int entityId = buffer.readInt();
        int count = buffer.readInt();        
        return new CreateParticleForMobMessage(entityId,particleData,count);
    }

    @Override
    public void handle(CreateParticleForMobMessage message, Supplier<Context> supplier) {
        supplier.get().enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            Entity entity = (Entity)mc.world.getEntityByID(message.entityId);
            Random r = new Random();

            for(int i = 0; i < message.count; i++)
                mc.world.addParticle(message.particleData, false, entity.getPosition().getX() + r.nextDouble(), entity.getPosition().getY() + r.nextDouble(), entity.getPosition().getZ() + r.nextDouble(), r.nextDouble()/5, r.nextDouble()/5, r.nextDouble()/5);
        });
        
    }
    
}
