package com.example.upgradedwolves.powerup;


public class PowerUpList {
    public static final PowerUp[] StrengthWolf = {
        new EnhanceDetectionPowerUp(4, 1, 2D), 
        new AutoAttackPowerUp(5),
        new EnhanceAttackPowerUp(5, 1, 1),
        new FleeHealthPowerUp(6),
        new EnhanceSpeedPowerUp(7, 1, 0.05D),
        new FleeCreeperPowerUp(8),
        new UseSwordPowerUp(9),
        new PickUpItemPowerUp(10),
        new EnhanceAttackPowerUp(10, 2, 1),
        new BarkStunPowerUp(13),
        new EnhanceDetectionPowerUp(14, 1, 2D),
        new DisarmEnemyPowerUp(14),
        new EnhanceAttackPowerUp(15, 3, 1),
        new SelfPreservationPowerUp(15),
        new ArrowInterceptPowerUp(18),
        new EnhanceSpeedPowerUp(18, 2, 0.05D),
        new FishForItemPowerUp(18),
        new EnhanceAttackPowerUp(20,4,1),
        new WolfTossArrowPowerUp(21)
    };
    public static final PowerUp[] ScavengerWolf = {
        new FleeHealthPowerUp(2),
        new PickUpItemPowerUp(2),
        new EnhanceDetectionPowerUp(4, 1, 2D),
        new EnhanceSpeedPowerUp(5,1,0.05D),
        new EnhanceAttackPowerUp(6, 1, 1),
        new FleeCreeperPowerUp(7),
        new DetectEnemyPowerUp(8),
        new EnhanceSpeedPowerUp(10,2,0.05D),
        new SelfPreservationPowerUp(11),
        new RetrieveOnDeathPowerUp(12),
        new EnhanceDetectionPowerUp(14, 2, 1D),
        new EnhanceSpeedPowerUp(15,3,0.05D), 
        new DigForItemPowerUp(17),
        new FishForItemPowerUp(18),
        new EnhanceAttackPowerUp(18, 2, 1),
        new EnhanceSpeedPowerUp(20,4,0.05D),
        new LootBonusPowerUp(22),
        new EnhanceSpeedPowerUp(25,5,0.05D)
    };
    public static final PowerUp[] ShowWolf = {
        new ShareItemPowerUp(4),
        new FishForItemPowerUp(7),
        new EnhanceSpeedPowerUp(5, 1, 0.05D),
        new EnhanceDetectionPowerUp(6, 1, 1D),
        new EnhanceAttackPowerUp(6, 1, 1),
        new PickUpItemPowerUp(10),
        new ThrowPotionPowerUp(11),
        new EnhanceDetectionPowerUp(12, 2, 2D),
        new SelfPreservationPowerUp(15),
        new EnhanceSpeedPowerUp(16, 2, 0.05D),
        new EnhanceDetectionPowerUp(16, 3, 3D),
        new EnhanceAttackPowerUp(18, 2, 1),
        new ImpressVillagerPowerUp(19),
        new EnhanceDetectionPowerUp(21, 4, 3D),
        new EnhanceDetectionPowerUp(25, 5, 3D),
    };
    public static final PowerUp[] notSet = {
        new FleeHealthPowerUp(5),
        new FleeCreeperPowerUp(10),
    };

}
