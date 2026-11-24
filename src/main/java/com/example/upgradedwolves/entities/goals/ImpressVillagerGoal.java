package com.example.upgradedwolves.entities.goals;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import com.example.upgradedwolves.entities.utilities.AbilityEnhancer;
import com.example.upgradedwolves.entities.utilities.EntityFinder;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.resources.ResourceKey;
import net.minecraft.Util;
import net.minecraft.server.level.ServerLevel;

public class ImpressVillagerGoal extends CoolDownGoal {
    public final Wolf wolf;
    public final EntityFinder<AbstractVillager> entityFinder;
    public AbstractVillager target;
    int jumpTime;
    private static final Map<VillagerProfession, ResourceKey<LootTable>> GIFTS = Util.make(Maps.newHashMap(), (giftMap) -> {
        giftMap.put(VillagerProfession.ARMORER, BuiltInLootTables.ARMORER_GIFT);
        giftMap.put(VillagerProfession.BUTCHER, BuiltInLootTables.BUTCHER_GIFT);
        giftMap.put(VillagerProfession.CARTOGRAPHER, BuiltInLootTables.CARTOGRAPHER_GIFT);
        giftMap.put(VillagerProfession.CLERIC, BuiltInLootTables.CLERIC_GIFT);
        giftMap.put(VillagerProfession.FARMER, BuiltInLootTables.FARMER_GIFT);
        giftMap.put(VillagerProfession.FISHERMAN, BuiltInLootTables.FISHERMAN_GIFT);
        giftMap.put(VillagerProfession.FLETCHER, BuiltInLootTables.FLETCHER_GIFT);
        giftMap.put(VillagerProfession.LEATHERWORKER, BuiltInLootTables.LEATHERWORKER_GIFT);
        giftMap.put(VillagerProfession.LIBRARIAN, BuiltInLootTables.LIBRARIAN_GIFT);
        giftMap.put(VillagerProfession.MASON, BuiltInLootTables.MASON_GIFT);
        giftMap.put(VillagerProfession.SHEPHERD, BuiltInLootTables.SHEPHERD_GIFT);
        giftMap.put(VillagerProfession.TOOLSMITH, BuiltInLootTables.TOOLSMITH_GIFT);
        giftMap.put(VillagerProfession.WEAPONSMITH, BuiltInLootTables.WEAPONSMITH_GIFT);
     });

    public ImpressVillagerGoal(Wolf wolf){
        this.wolf = wolf;
        entityFinder = new EntityFinder<AbstractVillager>(wolf,AbstractVillager.class);
        setCoolDownInSeconds(1800);
        this.setFlags(EnumSet.of(Goal.Flag.MOVE,Goal.Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        if(active()){
            List<AbstractVillager> villagers = entityFinder.findWithinRange(5, 5);
            if(villagers.size() > 0){
                target = villagers.get(0);
                wolf.getNavigation().moveTo(target, 1);
                jumpTime = wolf.getRandom().nextInt(140) + 60;
                startCoolDown(AbilityEnhancer.increaseMin(wolf, 20) * 10);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        if(jumpTime-- > 0){
            wolf.getJumpControl().jump();
            return true;
        }
        giveItemToWolf();
        target = null;
        return false; 
    }

    private void giveItemToWolf(){
        List<ItemStack> stack = getItem(target);
        for (ItemStack itemStack : stack) {
            wolf.spawnAtLocation(itemStack);
        }
    }

    private List<ItemStack> getItem(AbstractVillager villagerEntity){
        if (villagerEntity.isBaby()) {
            return ImmutableList.of(new ItemStack(Items.POPPY));
        } else {
            if(villagerEntity instanceof Villager){
                Villager villager = (Villager)villagerEntity;
                VillagerProfession villagerprofession = villager.getVillagerData().getProfession();
                if (GIFTS.containsKey(villagerprofession)) {
                    LootTable loottable = villager.level().getServer().reloadableRegistries().getLootTable(GIFTS.get(villagerprofession));
                    LootParams.Builder lootcontext$builder = (new LootParams.Builder((ServerLevel)villager.level())).withParameter(LootContextParams.ORIGIN, villager.getPosition(1)).withParameter(LootContextParams.THIS_ENTITY, villager).withLuck(villager.getRandom().nextFloat());
                    return loottable.getRandomItems(lootcontext$builder.create(LootContextParamSets.GIFT));
                } else {
                    return ImmutableList.of(new ItemStack(Items.WHEAT_SEEDS));
                }
            } else {
                return ImmutableList.of(new ItemStack(Items.BONE));
            }
        }
    }
    
}
