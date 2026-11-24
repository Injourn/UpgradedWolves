package com.example.upgradedwolves.capabilities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import com.example.upgradedwolves.UpgradedWolves;

import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.IntTag;
import net.minecraft.core.Direction;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class TrainingHandler {
    private static final Map<ItemStack, ITraining> TRAINING_HANDLERS = new HashMap<ItemStack, ITraining>();

    public static final Capability<ITraining> CAPABILITY_TRAINING = CapabilityManager.get(new CapabilityToken<>() {});

    public interface ITraining{
        public void setAttribute(int value);        
        public void resetAttribute();
        public int getAttribute();
    }
    public static void register() {
        MinecraftForge.EVENT_BUS.register(new TrainingHandler());
    }
    @Nullable
    public static ITraining getHandler(ItemStack item)
    {
        if(item == null)
            return null;
        return TRAINING_HANDLERS.get(item);
    }
    @SubscribeEvent
    public void attachCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
        ItemStack stack = (ItemStack)event.getObject();
        if(stack.getComponents().has(DataComponents.FOOD)){
            if(stack.is(ItemTags.MEAT)){
                Provider provider = new Provider();
                TRAINING_HANDLERS.put(stack, provider.getCapability(CAPABILITY_TRAINING, Direction.DOWN).orElse(null));
            }
        }
        
    }

    public static class Provider implements ICapabilitySerializable<IntTag>
    {
        final ITraining INSTANCE;        
        final Capability<ITraining> capability = CAPABILITY_TRAINING;

        public Provider(){
            INSTANCE = new Training();
        }

        @Override
        public IntTag serializeNBT()
        {
            return IntTag.valueOf(INSTANCE.getAttribute());
            //return (IntTag) CAPABILITY_TRAINING.getStorage().writeNBT(CAPABILITY_TRAINING, INSTANCE, null);
        }

        @Override
        public void deserializeNBT(IntTag compound)
        {
            IntTag next = (IntTag)compound;
            INSTANCE.setAttribute(next.getAsInt());
            //CAPABILITY_TRAINING.getStorage().readNBT(CAPABILITY_TRAINING, INSTANCE, null, compound);
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
        {            
            if(cap == CAPABILITY_TRAINING){
                return (LazyOptional<T>) LazyOptional.of(() -> INSTANCE);
            }
            return LazyOptional.empty();
        }
    }
    public static class Training implements ITraining{
        int currentAttribute;

        @Override
        public void setAttribute(int value) {
            currentAttribute = value;

        }

        @Override
        public void resetAttribute() {        
            currentAttribute = 0;            
        }

        @Override
        public int getAttribute() {            
            return currentAttribute;
        }

    }
}