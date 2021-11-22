package com.example.upgradedwolves.personality;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import com.example.upgradedwolves.UpgradedWolves;
import com.example.upgradedwolves.capabilities.WolfStatsEnum;
import com.example.upgradedwolves.personality.expressions.Expressions;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.entity.passive.WolfEntity;

public abstract class WolfPersonality {    
    public Behavior subBehavior;
    public static List<Class<? extends WolfPersonality>> addedWolfPersonalities = new ArrayList<Class<? extends WolfPersonality>>();

    protected final Behavior mainBehavior;

    public WolfPersonality(Behavior mainBehavior){
        this.mainBehavior = mainBehavior;
    }

    public static WolfPersonality getRandomWolfPersonality(){        
        Random rand = new Random();
        Class<? extends WolfPersonality> selectedClass = (Class<? extends WolfPersonality>)addedWolfPersonalities.get(rand.nextInt(addedWolfPersonalities.toArray().length));
        try{
            WolfPersonality personality = (WolfPersonality)selectedClass.getConstructors()[0].newInstance();
            return personality;
        } catch(Exception ignored){
            UpgradedWolves.LOGGER.error("failed to load wolf personality: " + selectedClass.getName());
        }
        return null;
    }

    public static void addGoals(){
        addedWolfPersonalities.add(SocialPersonality.class);
        addedWolfPersonalities.add(AffectionatePersonality.class);
        addedWolfPersonalities.add(AggressivePersonality.class);
        addedWolfPersonalities.add(DominantPersonality.class);
        addedWolfPersonalities.add(PassivePersonality.class);
        addedWolfPersonalities.add(PlayfulPersonality.class);
    }
    
    public void setWolfExpressions(WolfEntity wolf) {
        getExpressions().forEach(x -> assignExpressions(wolf,x));        
    }

    public ITextComponent getNameComponent(){
        return new TranslationTextComponent("personality.upgradedwolves." + getResourceName());
    }

    public abstract int levelUpStatBonus(WolfStatsEnum stats);

    public abstract Stream<Class<? extends Expressions>> getExpressions();

    protected abstract String getResourceName();

    protected void assignExpressions(WolfEntity wolf, Class<? extends Expressions> clazz){
        try{
            wolf.goalSelector.addGoal(8,(Expressions)clazz.getConstructors()[0].newInstance(wolf,subBehavior));
        } catch (Exception ignored){
            UpgradedWolves.LOGGER.error("Failed to add Expression:" + clazz.getName());
        }
    }
}
