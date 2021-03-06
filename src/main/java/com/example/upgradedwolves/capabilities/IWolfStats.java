package com.example.upgradedwolves.capabilities;

import com.example.upgradedwolves.itemHandler.ItemStackHandlerWolf;

import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.item.ItemStack;

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
    public ItemStackHandlerWolf getInventory();
    public boolean addItemStack(ItemStack item);
    public WolfEntity getActiveWolf();
    public void setActiveWolf(WolfEntity entity);
    public void handleWolfGoals();
    public void addGoals();
    public void forceLevelUp(int amount);
    public void showParticle(int type);
}