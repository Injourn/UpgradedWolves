package com.example.upgradedwolves.network.message;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.InteractionHand;

public class SyncWolfHandMessage implements IMessage<SyncWolfHandMessage> {    
    int wolfId;
    ItemStack item;

    public SyncWolfHandMessage(){
        this.wolfId = 0;
        this.item = null;
    }

    public SyncWolfHandMessage(int wolfId,ItemStack item){
        this.wolfId = wolfId;
        this.item = item;
    }

    @Override
    public SyncWolfHandMessage encode(SyncWolfHandMessage message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.wolfId);
        EntityDataSerializers.ITEM_STACK.codec().encode((RegistryFriendlyByteBuf) buffer, message.item);
        return message;
    }

    @Override
    public SyncWolfHandMessage decode(FriendlyByteBuf buffer) {
        int id = buffer.readInt();
        ItemStack item = EntityDataSerializers.ITEM_STACK.codec().decode((RegistryFriendlyByteBuf) buffer);
        return new SyncWolfHandMessage(id,item);
    }

    @Override
    public SyncWolfHandMessage handle(SyncWolfHandMessage message, CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            Wolf wolf = (Wolf)mc.level.getEntity(message.wolfId);            
            wolf.setItemInHand(InteractionHand.MAIN_HAND, message.item);
        });
        return message;
    }
}
