package com.example.upgradedwolves.capabilities;

import java.util.List;

import com.example.upgradedwolves.itemHandler.WolfItemStackHandler;
import com.example.upgradedwolves.personality.WolfPersonality;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public interface IWolfStats {
    public void addXp(WolfStatsEnum wolfStats,int amount);
    public int getXp(WolfStatsEnum wolfStats);
    public int getLevel(WolfStatsEnum wolfStats);
    public float getStatRatio(WolfStatsEnum wolfStats);
    public void setLevel(WolfStatsEnum wolfStats,int amount);
    public int getWolfType();
    public void setWolfType(int type);
    public void InitLove();
    public double getWolfSpeed();
    public int getWolfStrength();
    public double getDetectionBonus();
    public void setSpeedBonus(double bonus);
    public void setAttackBonus(double bonus);
    public void setDetectionBonus(double bonus);
    public void addSpeedBonus(double bonus);
    public void addAttackBonus(double bonus);
    public void addDetectionBonus(double bonus);
    public WolfItemStackHandler getInventory();
    public boolean addItemStack(ItemStack item);
    public Wolf getActiveWolf();
    public void setActiveWolf(Wolf entity);
    public void handleWolfGoals();
    public void addGoals();
    public void forceLevelUp(int amount);
    public void showParticle(int type);
    public void setRopeHolder(Entity holder);
    public Entity getRopeHolder();
    public void clearRopeHolder();
    public boolean getTugOfWarStatus();
    public List<Goal> getUnaddedGoals();
    public void clearUnaddedGoals();
    public void addPendingGoal(int priority, Goal goal);
    public void setRetrievalFlag(boolean set);
    public boolean getRetrievalFlag();
    public void setLootFlag(boolean set);
    public boolean getLootFlag();
    public Vec3 getRoamPoint();
    public void setRoamPoint(Vec3 location);
    public int getWolfFur();
    public void setWolffur(int color);
    public void setWolfPersonality(WolfPersonality behavior);
    public WolfPersonality getWolfPersonality();
}