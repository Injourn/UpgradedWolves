package com.example.upgradedwolves.loot_table.modifier;

import com.google.common.base.Suppliers;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.util.GsonHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

public class ChestLootModifier extends LootModifier
{
    private final List<ChestItem> chestItems;

    public static final Supplier<MapCodec<ChestLootModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.mapCodec(instance -> codecStart(instance).and(ChestItem.CODEC.listOf().fieldOf("additions").forGetter(modifier -> modifier.chestItems)).apply(instance, ChestLootModifier::new)));

    /**
     * Constructs a LootModifier.
     *
     * @param conditionsIn the ILootConditions that need to be matched before the loot is modified.
     */
    public ChestLootModifier(LootItemCondition[] conditionsIn, List<ChestItem> chestItems) {
        super(conditionsIn);
        this.chestItems = chestItems;
    }


    @Nonnull
    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot,
            LootContext context) {
        // TODO Auto-generated method stub
        for (ChestItem chestItem : chestItems) {
            generatedLoot.removeIf(itemStack -> itemStack.getItem() == chestItem.item);
            float rand = context.getRandom().nextFloat();
            float chance = chestItem.chance + Float.parseFloat("0." + context.getLootingModifier());
            if (chance > 1f) {
                chance = 1f;
            }
            if (rand <= chance) {
                generatedLoot.add(new ItemStack(chestItem.item));
            }
        }
        return generatedLoot;
    }

    public static class ChestItem {
        public IntRange range;
        public Item item;
        public float chance;
        public int quantity;

        public static final Codec<ChestItem> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ForgeRegistries.ITEMS.getCodec().fieldOf("item").forGetter(chestItem -> chestItem.item),
            Codec.FLOAT.fieldOf("chance").forGetter(chestItem -> chestItem.chance),
            Codec.INT.fieldOf("quantity").forGetter(chestItem -> chestItem.quantity)
            ).apply(instance, ChestItem::new));

        public ChestItem(Item itemIn, float chanceIn, int quantity) {
            this.item = itemIn;
            this.quantity = quantity;
            this.chance = chanceIn;
        }
    }


    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }


    
}