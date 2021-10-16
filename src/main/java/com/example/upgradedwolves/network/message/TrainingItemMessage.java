package com.example.upgradedwolves.network.message;

import java.util.function.Supplier;

import com.example.upgradedwolves.capabilities.TrainingHandler;
import com.example.upgradedwolves.capabilities.TrainingHandler.ITraining;
import com.example.upgradedwolves.common.TrainingEventHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.api.distmarker.Dist;

public class TrainingItemMessage implements IMessage<TrainingItemMessage> {
    int wolfValue;
    int playerId;
    public TrainingItemMessage(){
        wolfValue = 0;
        playerId = 0;
    }
    public TrainingItemMessage(int val,int id){
        wolfValue = val;
        playerId = id;
    }
    @Override
    public void encode(TrainingItemMessage message, PacketBuffer buffer) {                
        
        buffer.writeInt(message.wolfValue);        
        buffer.writeInt(message.playerId);
    }

    @Override
    public TrainingItemMessage decode(PacketBuffer buffer) {    
        return new TrainingItemMessage(buffer.readInt(),buffer.readInt());
    }

    @Override
    public void handle(TrainingItemMessage message, Supplier<Context> supplier) {
        supplier.get().enqueueWork(() -> {
            setItemAttribute(message);
        });
        supplier.get().setPacketHandled(true);

    }

    @OnlyIn(Dist.CLIENT)
    private void setItemAttribute(TrainingItemMessage message){
        Minecraft mc = Minecraft.getInstance();
            ClientPlayerEntity player = (ClientPlayerEntity)mc.world.getEntityByID(message.playerId);
            ItemStack foodItem = TrainingEventHandler.getFoodStack(player);
            ITraining handler = TrainingHandler.getHandler(foodItem);
            handler.setAttribute(message.wolfValue);
    }
}