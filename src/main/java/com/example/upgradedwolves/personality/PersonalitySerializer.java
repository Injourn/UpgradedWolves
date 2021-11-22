package com.example.upgradedwolves.personality;

import com.example.upgradedwolves.UpgradedWolves;

import net.minecraft.nbt.CompoundNBT;

public class PersonalitySerializer {

    public static CompoundNBT serializeNbt(WolfPersonality personality){
        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("ClassName", getClassName(personality));
        nbt.putInt("SubBehavior", personality.subBehavior.ordinal());
        return nbt;
    }

    public static WolfPersonality deserializeNbt(CompoundNBT nbt){
        WolfPersonality personality = getWolfPersonalityType(nbt.getString("ClassName"));
        personality.subBehavior = Behavior.values()[nbt.getInt("SubBehavior")];
        return personality;
    }

    private static String getClassName(WolfPersonality personality){
        return personality.getClass().getName();
    }

    private static WolfPersonality getWolfPersonalityType(String name){
        WolfPersonality personality;
        try{
            Class<? extends WolfPersonality> personalityClass = Class.forName(name).asSubclass(WolfPersonality.class);
            personality = (WolfPersonality)personalityClass.getConstructors()[0].newInstance();
            
            return personality;
        } catch (ClassNotFoundException ignored){
            UpgradedWolves.LOGGER.error("Failed to load Wolf Personality Class");
        } catch (Exception ignored){
            UpgradedWolves.LOGGER.error("Failed to load Wolf Personality Constructor");
        }
        return null;
    }
}
