package com.example.upgradedwolves.powerup.gui;

import java.util.ArrayList;

import com.example.upgradedwolves.UpgradedWolves;
import com.example.upgradedwolves.powerup.ExamplePowerUp;
import com.example.upgradedwolves.powerup.PowerUp;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class PowerUpGui extends AbstractGui {
   Minecraft minecraft;
   private double scrollX;
   private double scrollY;
   private int minX = -141;
   private int minY = -93;
   private int maxX = 141;
   private int maxY = 93;
   private boolean centered;
   private float fade;
   private static ResourceLocation background = new ResourceLocation("minecraft:textures/gui/advancements/backgrounds/stone.png");
   private static final ResourceLocation POWERUP = UpgradedWolves.getId("gui/wolf_powerup_gui.png");
   public static ArrayList<PowerUp> powerUps;
   public WolfEntity wolf;

    
   public PowerUpGui(Minecraft minecraft,WolfEntity wolf) {
      this.minecraft = minecraft;      
      this.wolf = wolf;
      powerUps = setPowerups();
   }

   private ArrayList<PowerUp> setPowerups(){
      ArrayList<PowerUp> powerUpList = new ArrayList<PowerUp>();
      for(int i = 0; i < 30; i++){
         powerUpList.add(new ExamplePowerUp(2 * i));
      }
      this.maxY = (7 * powerUpList.size() + 20);
      return powerUpList;
   }

   public void drawTabBackground(MatrixStack matrixStack) {
      if (!this.centered) {
         this.scrollX = (double)0;
         this.scrollY = (double)0;
         this.centered = true;
      }

      RenderSystem.pushMatrix();
      RenderSystem.enableDepthTest();
      RenderSystem.translatef(0.0F, 0.0F, 950.0F);
      RenderSystem.colorMask(false, false, false, false);
      fill(matrixStack, 4680, 2260, -4680, -2260, -16777216);
      RenderSystem.colorMask(true, true, true, true);
      RenderSystem.translatef(0.0F, 0.0F, -950.0F);
      RenderSystem.depthFunc(518);
      fill(matrixStack, 141, 93, 0, 0, -16777216);
      RenderSystem.depthFunc(515);

      int i = MathHelper.floor(this.scrollX);
      int j = MathHelper.floor(this.scrollY);
      int k = i % 16;
      int l = j % 16;   

      this.minecraft.getTextureManager().bindTexture(background);

      for(int i1 = -1; i1 <= 15; ++i1) {
         for(int j1 = -1; j1 <= 8; ++j1) {
            blit(matrixStack, k + 16 * i1, l + 16 * j1, 0.0F, 0.0F, 16, 16, 16, 16);
         }
      }

      this.minecraft.getTextureManager().bindTexture(POWERUP);
      
      displayPowerUps(matrixStack,i,j);
      
      RenderSystem.depthFunc(518);
      RenderSystem.translatef(0.0F, 0.0F, -950.0F);
      RenderSystem.colorMask(false, false, false, false);
      fill(matrixStack, 4680, 2260, -4680, -2260, -16777216);
      RenderSystem.colorMask(true, true, true, true);
      RenderSystem.translatef(0.0F, 0.0F, 950.0F);
      RenderSystem.depthFunc(515);
      RenderSystem.popMatrix();
   }
  
   public void drawTabTooltips(MatrixStack matrixStack, int mouseX, int mouseY, int width, int height) {
      RenderSystem.pushMatrix();
      RenderSystem.translatef(0.0F, 0.0F, 200.0F);
      fill(matrixStack, 0, 0, 234, 113, MathHelper.floor(this.fade * 255.0F) << 24);
      boolean flag = false;
      int i = MathHelper.floor(this.scrollX);
      int j = MathHelper.floor(this.scrollY);

      RenderSystem.popMatrix();
      if (flag) {
         this.fade = MathHelper.clamp(this.fade + 0.02F, 0.0F, 0.3F);
      } else {
         this.fade = MathHelper.clamp(this.fade - 0.04F, 0.0F, 1.0F);
      }

   }
   public void dragSelectedGui(double dragX, double dragY) {
      if (this.maxX - this.minX > 141) {
         this.scrollX = MathHelper.clamp(this.scrollX + dragX, (double)(-(this.maxX - 141)), 0.0D);
      }

      if (this.maxY - this.minY > 93) {
         this.scrollY = MathHelper.clamp(this.scrollY + dragY, (double)(-(this.maxY - 93)), 0.0D);
      }
   }

   private void displayPowerUps(MatrixStack matrixStack,int xOffset,int yOffset){
      for(int i = 0; i < powerUps.size(); i++){
         int x = 30 * (i % 4) + 13;
         int y = 7 * i;
         int id = powerUps.get(i).iconType(wolf);
         displayIcon(matrixStack, id, x + xOffset, y + yOffset);
      }
   }

   private void displayIcon(MatrixStack matrixStack,int id,int x,int y){
      //Too lazy to learn the delegate equivalent of JAVA this'll do
      switch(id){
         case 0:
            blit(matrixStack, x, y, 0, 178, 25, 25);
         break;
         case 1:
            blit(matrixStack, x, y, 25, 178, 26, 26);
         break;
         case 2:
            blit(matrixStack, x, y, 51, 178, 26, 26);
         break;
         case 3:
            blit(matrixStack, x, y, 0, 205, 25, 25);
         break;
         case 4:
            blit(matrixStack, x, y, 25, 205, 26, 26);
         break;
         case 5:
            blit(matrixStack, x, y, 51, 205, 26, 26);
         break;
      }
   }
}