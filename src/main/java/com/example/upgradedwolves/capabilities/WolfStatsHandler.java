package com.example.upgradedwolves.capabilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.example.upgradedwolves.UpgradedWolves;
import com.example.upgradedwolves.entities.goals.IUpdateableGoal;
import com.example.upgradedwolves.entities.goals.WolfPlayWithPlushGoal;
import com.example.upgradedwolves.itemHandler.WolfItemStackHandler;
import com.example.upgradedwolves.itemHandler.WolfToysHandler;
import com.example.upgradedwolves.network.PacketHandler;
import com.example.upgradedwolves.network.message.SpawnLevelUpParticle;
import com.example.upgradedwolves.personality.Behavior;
import com.example.upgradedwolves.personality.PersonalitySerializer;
import com.example.upgradedwolves.personality.WolfPersonality;
import com.example.upgradedwolves.powerup.PowerUp;
import com.example.upgradedwolves.powerup.PowerUpList;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.PacketDistributor;

public class WolfStatsHandler {
    @CapabilityInject(IWolfStats.class)
    public static final Capability<IWolfStats> CAPABILITY_WOLF_STATS = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(IWolfStats.class, new Storage(), WolfStats::new);
        MinecraftForge.EVENT_BUS.register(new WolfStatsHandler());
    }

    @Nullable
    public static IWolfStats getHandler(WolfEntity entity) {
        IWolfStats stats = entity.getCapability(CAPABILITY_WOLF_STATS, Direction.DOWN).orElse(null);
        stats.setActiveWolf(entity);
        if(stats.getWolfPersonality() == null){
            Random rand = new Random();
            WolfPersonality personality = WolfPersonality.getRandomWolfPersonality();
            personality.subBehavior = Behavior.values()[rand.nextInt(Behavior.values().length)];
            stats.setWolfPersonality(personality);
            stats.getWolfPersonality().setWolfExpressions(entity);
        }
        return stats;
    }

    @SubscribeEvent
    public void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof WolfEntity) {
            event.addCapability(UpgradedWolves.getId("wolf_stats"), new Provider());            
        }
    }
    
    public static class WolfStats implements IWolfStats {

        int speedLvl, strengthLvl, intelligenceLvl, loveLvl, wolfType;
        int speedXp, strengthXp, intelligenceXp;
        double attackBonus,speedBonus,detectBonus;
        int wolfFur;
        //The wolves will have a maximum of 9 slots.       
        WolfItemStackHandler inventory;
        Entity ropeHolder;
        WolfEntity currentWolf;
        BlockPos location;
        List<Goal> allGoals = new ArrayList<Goal>();
        List<Goal> unaddedGoals = new ArrayList<Goal>();
        boolean tugOfWarActive = false;
        boolean deathRetrieval,lootAdder;
        WolfPersonality personality;

        private boolean LevelUpFunction(int level, int xp) {
            return xp > Math.pow(level,1.1) * 4;
        }
        public void InitLove(){
            if(loveLvl > 0)
                return;
            //Skewed Random function
            loveLvl = (int)(Math.pow(Math.random() * 1000,2) / 10000);
            if(loveLvl < 10)
            loveLvl = 10;
        }       
        
        private void clearGoals(){
            for(int i = 0; i < allGoals.size(); i++){
                currentWolf.goalSelector.removeGoal(allGoals.get(i));
                allGoals.remove(allGoals.get(i));
            }
        }

        private void resetGoals(){
            PowerUp[] wolfTypePowerUps = getRelevantPowerUps();
            for(int i = 0; i < allGoals.size();i++){
                if(allGoals.get(i) instanceof IUpdateableGoal)
                    ((IUpdateableGoal)allGoals.get(i)).Update(this, currentWolf);
            }
            for (PowerUp powerUp : wolfTypePowerUps) {
                speedBonus = 0;
                attackBonus = 0;
                detectBonus = 0;
                conditionallyAddPowerUp(powerUp);
            }
        }

        private void setInventorySize(){
            if(inventory != null)
                inventory.setSize(getInventorySize());
            else
                inventory = new WolfItemStackHandler(getInventorySize());
        }

        private int getInventorySize(){
            if(getWolfType() == WolfType.Scavenger.getValue()){
                return strengthLvl < 16 ? 3 + (strengthLvl * 3 / 5) : 9;
            }
            else if(getWolfType() != WolfType.NotSet.getValue()){
                return strengthLvl < 16 ? 1 + (strengthLvl * 4 / 15) : 5;
            }
            return 1;
        }

        // TODO: consider renaming to update Wolf and acting upon that.
        @Override
        public void handleWolfGoals(){
            if(allGoals.size() == 0)
                addGoals();
            else{
                setInventorySize();      
                resetGoals();
            }
        }

        @Override
        public void addGoals(){
            if(allGoals.size() > 0)
                clearGoals();
            PowerUp[] wolfTypePowerUps = getRelevantPowerUps();

            for (PowerUp powerUp : wolfTypePowerUps) {
                conditionallyAddPowerUp(powerUp);
            }

            Goal playGoal = new WolfPlayWithPlushGoal(currentWolf);
            allGoals.add(playGoal);
            currentWolf.goalSelector.addGoal(8, playGoal);
        }

        @Override
        public void addXp(WolfStatsEnum wolfStats, int amount) {
            
            switch (wolfStats) {
                case Strength:
                    if (LevelUpFunction(strengthLvl,strengthXp + amount)){                        
                        strengthXp -= Math.pow(strengthLvl++,1.1) * 4;
                        showParticle(1);
                        handleWolfGoals();
                    }
                    strengthXp += amount;
                    break;
                case Speed:
                    if (LevelUpFunction(speedLvl,speedXp + amount)){                        
                        speedXp -= Math.pow(speedLvl++,1.1) * 4;
                        showParticle(0);
                        handleWolfGoals();
                    }
                    speedXp += amount;
                    break;
                case Intelligence:
                    if (LevelUpFunction(intelligenceLvl,intelligenceXp + amount)){                        
                        intelligenceXp -= Math.pow(intelligenceLvl++,1.1) * 4;
                        showParticle(2);
                        handleWolfGoals();
                    }
                    intelligenceXp += amount;
                    break;
                case Love:
                    break;
            }

        }

        @Override
        public int getLevel(WolfStatsEnum wolfStats) {
            switch (wolfStats) {
                case Speed:
                    return speedLvl;
                case Strength:
                    return strengthLvl;
                case Intelligence:
                    return intelligenceLvl;
                case Love:
                    return loveLvl;
                default:
                    return 0;
            }
        }

        @Override
        public void setLevel(WolfStatsEnum wolfStats, int amount) {
            switch (wolfStats) {
                case Speed:
                    speedLvl = amount;
                    break;
                case Strength:
                    strengthLvl = amount;
                    break;
                case Intelligence:
                    intelligenceLvl = amount;
                    break;
                case Love:
                    loveLvl = amount;
                    break;
            }

        }

        @Override
        public int getWolfType() {            
            return wolfType;
        }

        @Override
        public void setWolfType(int type) {
            wolfType = type;            
        }

        @Override
        public int getXp(WolfStatsEnum wolfStats) {
            switch (wolfStats) {
                case Speed:
                    return speedXp;
                case Strength:
                    return strengthXp;
                case Intelligence:
                    return intelligenceXp;
                case Love:
                    return -4;
                default:
                    return 0;
            }            
        }

        @Override
        public double getWolfSpeed() {
            //Wolf Base Movement Speed is 0.3D
            return 0.3D + (speedLvl * .01) + speedBonus;
        }

        @Override
        public int getWolfStrength() {
            //Base wolf Damage is 4
            return 4 + (int)attackBonus;
        }

        @Override
        public double getDetectionBonus(){
            return detectBonus;
        }

        @Override
        public WolfItemStackHandler getInventory() {
            if(inventory == null)
                inventory = new WolfItemStackHandler(getInventorySize());
            return inventory;
        }
        
        @Override
        public WolfEntity getActiveWolf() {
            return currentWolf;
        }
        @Override
        public void setActiveWolf(WolfEntity entity) {
            if(currentWolf == null)
                currentWolf = entity;
        }
        @Override
        public float getStatRatio(WolfStatsEnum wolfStats) {
            switch (wolfStats) {
                case Speed:
                    return speedXp / ((float)Math.pow(speedLvl,1.1) * 4);
                case Strength:
                    return strengthXp / ((float)Math.pow(strengthLvl,1.1) * 4);
                case Intelligence:
                    return intelligenceXp / ((float)Math.pow(intelligenceLvl,1.1) * 4);
                case Love:
                    return -4;
                default:
                    return 0;
            }  
        }
        @Override
        public void forceLevelUp(int amount) {
            speedLvl += amount;
            strengthLvl += amount;
            intelligenceLvl += amount;
            speedXp = 0;
            strengthXp = 0;
            intelligenceXp = 0;         
            handleWolfGoals();
        }
        @Override
        public void showParticle(int type){
            PacketHandler.instance.send(PacketDistributor.TRACKING_ENTITY.with(() -> currentWolf), new SpawnLevelUpParticle( currentWolf.getEntityId(),type));
        }
        @Override
        public void setRopeHolder(Entity holder) {
            ropeHolder = holder;
            tugOfWarActive = true;
            currentWolf.setHeldItem(Hand.MAIN_HAND, new ItemStack(WolfToysHandler.TUFOFWARROPE));
        }
        @Override
        public Entity getRopeHolder() {            
            return ropeHolder;
        }
        @Override
        public void clearRopeHolder() {            
            ropeHolder = null;
            tugOfWarActive = false;
            currentWolf.setHeldItem(Hand.MAIN_HAND, ItemStack.EMPTY);
        }
        @Override
        public boolean getTugOfWarStatus() {            
            return tugOfWarActive;
        }
        @Override
        public void setSpeedBonus(double bonus) {
            speedBonus = bonus;
        }
        @Override
        public void setAttackBonus(double bonus) {
            attackBonus = bonus;
        }
        @Override
        public void setDetectionBonus(double bonus) {
            detectBonus = bonus;
        }
        @Override
        public boolean addItemStack(ItemStack item) {
            // TODO Auto-generated method stub
            return false;
        }

        private PowerUp[] getRelevantPowerUps(){            
            if(getWolfType() == WolfType.Fighter.getValue()){
                return PowerUpList.StrengthWolf;
            }
            if(getWolfType() == WolfType.Scavenger.getValue()){
                return PowerUpList.ScavengerWolf;
            }
            if(getWolfType() == WolfType.Show.getValue()){
                return PowerUpList.ShowWolf;
            }
            return PowerUpList.notSet;                    
        }

        private void conditionallyAddPowerUp(PowerUp powerUp){
            Goal conditionalGoal = powerUp.fetchRelevantGoal(currentWolf);
            if(conditionalGoal != null){
                for(Goal goal : allGoals){
                    if(goal.getClass() == conditionalGoal.getClass())
                        return;
                }
                addPendingGoal(powerUp.priority(),conditionalGoal);
                allGoals.add(conditionalGoal);
                
            }
        }

        public void addPendingGoal(int priority, Goal goal){
            if(goal instanceof TargetGoal){                
                if(currentWolf.targetSelector.getRunningGoals().count() == 0)
                    currentWolf.targetSelector.addGoal(priority,goal);
                else
                    unaddedGoals.add(new PrioritizedGoal(priority,goal));
            }
            else{
                if(currentWolf.goalSelector.getRunningGoals().count() == 0)
                    currentWolf.goalSelector.addGoal(priority,goal);
                else
                    unaddedGoals.add(new PrioritizedGoal(priority,goal));
            }
            allGoals.add(goal);
        }
        @Override
        public void addSpeedBonus(double bonus) {
            speedBonus += bonus;
            
        }
        @Override
        public void addAttackBonus(double bonus) {
            attackBonus += bonus;
            
        }
        @Override
        public void addDetectionBonus(double bonus) {
            detectBonus += bonus;
            
        }
        @Override
        public List<Goal> getUnaddedGoals() {
            List<Goal> retGoals = unaddedGoals;
            return retGoals;
        }
        
        @Override
        public void clearUnaddedGoals() {
            unaddedGoals.clear();
        }
        @Override
        public void setRetrievalFlag(boolean set) {
            deathRetrieval = set;            
        }
        @Override
        public boolean getRetrievalFlag() {
            return deathRetrieval;
        }
        @Override
        public void setLootFlag(boolean set) {
            lootAdder = set;            
        }
        @Override
        public boolean getLootFlag() {            
            return lootAdder;
        }
        @Override
        public BlockPos getRoamPoint() {            
            return location;
        }
        @Override
        public void setRoamPoint(BlockPos location) {
            this.location = location;
        }
        @Override
        public int getWolfFur() {
            
            return wolfFur;
        }
        @Override
        public void setWolffur(int color) {
            wolfFur = color;            
        }
        @Override
        public void setWolfPersonality(WolfPersonality personality) {

            this.personality = personality;
        }
        @Override
        public WolfPersonality getWolfPersonality() {

            return personality;
        }

    }

    public static class Storage implements Capability.IStorage<IWolfStats> {

        @Override
        public INBT writeNBT(Capability<IWolfStats> capability, IWolfStats instance, Direction side) {
            CompoundNBT nbt = new CompoundNBT();
            instance.InitLove();          
            nbt.putInt("SpeedLevel", instance.getLevel(WolfStatsEnum.Speed));
            nbt.putInt("StrengthLevel", instance.getLevel(WolfStatsEnum.Strength));
            nbt.putInt("IntelligenceLevel", instance.getLevel(WolfStatsEnum.Intelligence));
            nbt.putInt("LoveLevel", instance.getLevel(WolfStatsEnum.Love));
            nbt.putInt("SpeedXp", instance.getXp(WolfStatsEnum.Speed));
            nbt.putInt("StrengthXp", instance.getXp(WolfStatsEnum.Strength));
            nbt.putInt("IntelligenceXp", instance.getXp(WolfStatsEnum.Intelligence));
            nbt.putInt("WolfType",instance.getWolfType());
            nbt.putInt("WolfFur",instance.getWolfFur());
            nbt.put("Personality",PersonalitySerializer.serializeNbt(instance.getWolfPersonality()));
            nbt.put("Inventory",instance.getInventory().serializeNBT());
            if(instance.getRoamPoint() != null){
                nbt.put("RoamPosition",NBTUtil.writeBlockPos(instance.getRoamPoint()));
            }
            return nbt;
        }

        @Override
        public void readNBT(Capability<IWolfStats> capability, IWolfStats instance, Direction side, INBT nbt) {            
            CompoundNBT next = (CompoundNBT)nbt;            
            instance.setLevel(WolfStatsEnum.Speed, next.getInt("SpeedLevel"));
            instance.setLevel(WolfStatsEnum.Strength, next.getInt("StrengthLevel"));
            instance.setLevel(WolfStatsEnum.Intelligence, next.getInt("IntelligenceLevel"));
            instance.setLevel(WolfStatsEnum.Love, next.getInt("LoveLevel"));
            instance.addXp(WolfStatsEnum.Speed, next.getInt("SpeedXp"));
            instance.addXp(WolfStatsEnum.Strength, next.getInt("StrengthXp"));
            instance.addXp(WolfStatsEnum.Intelligence, next.getInt("IntelligenceXp"));
            instance.setWolfType(next.getInt("WolfType"));
            instance.setWolffur(next.getInt("WolfFur"));
            instance.getInventory().deserializeNBT(next.getCompound("Inventory"));
            instance.setRoamPoint(NBTUtil.readBlockPos(next.getCompound("RoamPosition")));
            instance.setWolfPersonality(PersonalitySerializer.deserializeNbt(next.getCompound("Personality")));
            instance.InitLove();
        }

    }
    public static class Provider implements ICapabilitySerializable<CompoundNBT>
    {
        final IWolfStats INSTANCE = CAPABILITY_WOLF_STATS.getDefaultInstance();

        @Override
        public CompoundNBT serializeNBT()
        {
            return (CompoundNBT) CAPABILITY_WOLF_STATS.getStorage().writeNBT(CAPABILITY_WOLF_STATS, INSTANCE, null);
        }

        @Override
        public void deserializeNBT(CompoundNBT compound)
        {
            CAPABILITY_WOLF_STATS.getStorage().readNBT(CAPABILITY_WOLF_STATS, INSTANCE, null, compound);
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
        {
            return CAPABILITY_WOLF_STATS.orEmpty(cap, LazyOptional.of(() -> INSTANCE));
        }
    }
}
