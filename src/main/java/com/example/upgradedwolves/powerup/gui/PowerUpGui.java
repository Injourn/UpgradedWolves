package com.example.upgradedwolves.powerup.gui;

import java.util.ArrayList;
import java.util.List;

import com.example.upgradedwolves.UpgradedWolves;
import com.example.upgradedwolves.capabilities.WolfStatsEnum;
import com.example.upgradedwolves.capabilities.WolfType;
import com.example.upgradedwolves.powerup.PowerUp;
import com.example.upgradedwolves.powerup.PowerUpList;
import com.example.upgradedwolves.powerup.PowerUpListBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.ChatFormatting;

public class PowerUpGui {
   Minecraft minecraft;
   private double scrollX;
   private double scrollY;
   private int minX = -141;
   private int minY = -93;
   private int maxX = 141;
   private int maxY = 93;
   private boolean centered;
   private float fade;
   private Font font;
   private CompoundTag nbt;
   private static ResourceLocation background = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/advancements/backgrounds/stone.png");
   private static final ResourceLocation POWERUP = UpgradedWolves.getId("gui/wolf_powerup_gui.png");
   public List<PowerUp> powerUps;
   public Wolf wolf;


    
   public PowerUpGui(Minecraft minecraft,Wolf wolf,CompoundTag nbt) {
      this.minecraft = minecraft;      
      this.wolf = wolf;
      this.nbt = nbt;
      powerUps = setPowerups();
      font = minecraft.font;
      
   }

   private List<PowerUp> setPowerups(){
      try {
         return PowerUpListBuilder.buildOrRetrieve(WolfType.values()[nbt.getInt("wolfType")]);
      } catch (Exception e){
         UpgradedWolves.LOGGER.error(e.getMessage() + e.getStackTrace());
         return null;
      }
   }

   public void drawTabBackground(GuiGraphics guiGraphics) {
      if (!this.centered) {
         this.scrollX = (double)0;
         this.scrollY = (double)0;
         this.centered = true;
      }


      guiGraphics.enableScissor(0, 0, 0 + 300, 0 + 200);
      guiGraphics.pose().pushPose();
      guiGraphics.pose().translate((float)0, (float)0, 0.0F);
      int i = Mth.floor(this.scrollX);
      int j = Mth.floor(this.scrollY);
      int k = i % 16;
      int l = j % 16;   

      // RenderSystem.setShaderTexture(0,background);

      for(int i1 = -1; i1 <= 15; ++i1) {
         for(int j1 = -1; j1 <= 8; ++j1) {
            guiGraphics.blit(background, k + 16 * i1, l + 16 * j1, 0.0F, 0.0F, 16, 16, 16, 16);
         }
      }

      // RenderSystem.setShaderTexture(0,POWERUP);
      
      displayPowerUps(guiGraphics,i,j);
      
      guiGraphics.pose().popPose();
      guiGraphics.disableScissor();
   }
  
   public void drawTabTooltips(GuiGraphics graphics, int mouseX, int mouseY, int width, int height,PowerUp powerUp) {
      graphics.pose().pushPose();
      graphics.pose().translate(0.0F, 0.0F, 200.0F);
      graphics.fill(0, 0, 234, 113, Mth.floor(this.fade * 255.0F) << 24);
      boolean flag = false;
      ArrayList<Component> textBoxInfo = new ArrayList<Component>();
      Style redStyle = Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.RED)).withItalic(true);
      if(levelDistance(powerUp) <= 0){
         textBoxInfo.add(powerUp.getName());
         textBoxInfo.add(powerUp.getDescription(wolf));
      } else if(levelDistance(powerUp) <= 3){
         textBoxInfo.add(powerUp.getName());
         textBoxInfo.add(Component.literal("???").setStyle(Style.EMPTY.withItalic(true)));
         textBoxInfo.add(Component.translatable("powerup.required.level",powerUp.levelType().toString(),powerUp.requiredLevel()).setStyle(redStyle));
      } else {
         Style style = Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.BLUE)).withItalic(true);
         textBoxInfo.add(Component.literal("???").setStyle(style));
         textBoxInfo.add(Component.literal("???").setStyle(Style.EMPTY.withItalic(true)));
         textBoxInfo.add(Component.translatable("powerup.required.level",powerUp.levelType().toString(),powerUp.requiredLevel()).setStyle(redStyle));
      }
      graphics.renderComponentTooltip(font, textBoxInfo, mouseX, mouseY);
      // this.minecraft.screen.renderWithTooltip(graphics, mouseY, mouseX, mouseY);//(graphics, textBoxInfo,);
      graphics.pose().popPose();
      if (flag) {
         this.fade = Mth.clamp(this.fade + 0.02F, 0.0F, 0.3F);
      } else {
         this.fade = Mth.clamp(this.fade - 0.04F, 0.0F, 1.0F);
      }

   }
   public void dragSelectedGui(double dragX, double dragY) {
      if (this.maxX - this.minX > 141) {
         this.scrollX = Mth.clamp(this.scrollX + dragX, (double)(-(this.maxX - 141)), 0.0D);
      }

      if (this.maxY - this.minY > 93) {
         this.scrollY = Mth.clamp(this.scrollY + dragY, (double)(-(this.maxY - 93)), 0.0D);
      }
   }

   public void drawTooltips(GuiGraphics graphics, int mouseX, int mouseY, int width, int height,int xOffset, int yOffset) {
      if(xOffset < mouseX && mouseX < xOffset + 141 && yOffset < mouseY && mouseY < yOffset + 93){
         for(int i = 0; i < powerUps.size(); i++){
            int x = 30 * (i % 4) + 13 + Mth.floor(this.scrollX) + xOffset;
            int y = 7 * i + Mth.floor(this.scrollY) + yOffset;
            if(levelDistance(powerUps.get(i)) < 7 &&x< mouseX && mouseX < x + 26 &&
               y < mouseY && mouseY < y + 26)
               drawTabTooltips(graphics, mouseX, mouseY, width, height,powerUps.get(i));
         }
      }
   }

   private void displayPowerUps(GuiGraphics graphics,int xOffset,int yOffset){
      for(int i = 0; i < powerUps.size(); i++){
         int x = 30 * (i % 4) + 13;
         int y = 7 * i + 3;
         PowerUp powerUp = powerUps.get(i);
         int id = powerUp.iconType(getNbtData(powerUp.levelType()));
         if(levelDistance(powerUp) < 7){
            displayIcon(graphics, id, x + xOffset, y + yOffset);
            if(y > this.maxY - 30){
               this.maxY += 7;
            }
         }
         if(levelDistance(powerUp) < 3)
            graphics.blit(POWERUP, x + xOffset + 4, y + yOffset + 5, powerUp.uLocation, powerUp.vLocation, 16, 16);
         
      }
   }

   private void displayIcon(GuiGraphics graphics,int id,int x,int y){
      //Too lazy to learn the delegate equivalent of JAVA this'll do
      switch(id){
         case 0:
            graphics.blit(POWERUP, x, y, 0, 178, 25, 25);
         break;
         case 1:
            graphics.blit(POWERUP, x - 1, y, 25, 178, 26, 26);
         break;
         case 2:
            graphics.blit(POWERUP, x, y, 52, 178, 26, 26);
         break;
         case 3:
            graphics.blit(POWERUP, x, y, 0, 204, 25, 25);
         break;
         case 4:
            graphics.blit(POWERUP, x - 1, y, 25, 204, 26, 26);
         break;
         case 5:
            graphics.blit(POWERUP, x, y, 52, 204, 26, 26);
         break;
      }
   }

   private int levelDistance(PowerUp powerUp){
      return powerUp.requiredLevel() - getNbtData(powerUp.levelType());
   }

   private int getNbtData(WolfStatsEnum statType){
      switch(statType){
         case Speed:
            return nbt.getInt("spdLevel");
         case Strength:
            return nbt.getInt("strLevel");
         case Intelligence:
            return nbt.getInt("intLevel");
         default:
            return Integer.MAX_VALUE;
      }
   }
}
