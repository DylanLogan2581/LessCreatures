package xyz.dylanlogan.client;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.EntitySpellParticleFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import xyz.dylanlogan.MoCEntityData;
import xyz.dylanlogan.MoCProxy;
import xyz.dylanlogan.MoCTools;
import xyz.dylanlogan.MoCreatures;
import xyz.dylanlogan.client.audio.MoCSounds;
import xyz.dylanlogan.client.model.MoCModelAnt;
import xyz.dylanlogan.client.model.MoCModelBear;
import xyz.dylanlogan.client.model.MoCModelBee;
import xyz.dylanlogan.client.model.MoCModelBigCat1;
import xyz.dylanlogan.client.model.MoCModelBigCat2;
import xyz.dylanlogan.client.model.MoCModelBird;
import xyz.dylanlogan.client.model.MoCModelBoar;
import xyz.dylanlogan.client.model.MoCModelBunny;
import xyz.dylanlogan.client.model.MoCModelButterfly;
import xyz.dylanlogan.client.model.MoCModelCrab;
import xyz.dylanlogan.client.model.MoCModelCricket;
import xyz.dylanlogan.client.model.MoCModelCrocodile;
import xyz.dylanlogan.client.model.MoCModelDeer;
import xyz.dylanlogan.client.model.MoCModelDolphin;
import xyz.dylanlogan.client.model.MoCModelDragonfly;
import xyz.dylanlogan.client.model.MoCModelDuck;
import xyz.dylanlogan.client.model.MoCModelEgg;
import xyz.dylanlogan.client.model.MoCModelElephant;
import xyz.dylanlogan.client.model.MoCModelEnt;
import xyz.dylanlogan.client.model.MoCModelFirefly;
import xyz.dylanlogan.client.model.MoCModelFishBowl;
import xyz.dylanlogan.client.model.MoCModelFishy;
import xyz.dylanlogan.client.model.MoCModelFly;
import xyz.dylanlogan.client.model.MoCModelFox;
import xyz.dylanlogan.client.model.MoCModelGoat;
import xyz.dylanlogan.client.model.MoCModelGolem;
import xyz.dylanlogan.client.model.MoCModelJellyFish;
import xyz.dylanlogan.client.model.MoCModelKitty;
import xyz.dylanlogan.client.model.MoCModelKittyBed;
import xyz.dylanlogan.client.model.MoCModelKittyBed2;
import xyz.dylanlogan.client.model.MoCModelKomodo;
import xyz.dylanlogan.client.model.MoCModelLitterBox;
import xyz.dylanlogan.client.model.MoCModelMaggot;
import xyz.dylanlogan.client.model.MoCModelMediumFish;
import xyz.dylanlogan.client.model.MoCModelMiniGolem;
import xyz.dylanlogan.client.model.MoCModelMole;
import xyz.dylanlogan.client.model.MoCModelMouse;
import xyz.dylanlogan.client.model.MoCModelNewHorse;
import xyz.dylanlogan.client.model.MoCModelNewHorseMob;
import xyz.dylanlogan.client.model.MoCModelOgre;
import xyz.dylanlogan.client.model.MoCModelOstrich;
import xyz.dylanlogan.client.model.MoCModelPetScorpion;
import xyz.dylanlogan.client.model.MoCModelRaccoon;
import xyz.dylanlogan.client.model.MoCModelRat;
import xyz.dylanlogan.client.model.MoCModelRay;
import xyz.dylanlogan.client.model.MoCModelRoach;
import xyz.dylanlogan.client.model.MoCModelScorpion;
import xyz.dylanlogan.client.model.MoCModelShark;
import xyz.dylanlogan.client.model.MoCModelSilverSkeleton;
import xyz.dylanlogan.client.model.MoCModelSmallFish;
import xyz.dylanlogan.client.model.MoCModelSnail;
import xyz.dylanlogan.client.model.MoCModelSnake;
import xyz.dylanlogan.client.model.MoCModelTurkey;
import xyz.dylanlogan.client.model.MoCModelTurtle;
import xyz.dylanlogan.client.model.MoCModelWere;
import xyz.dylanlogan.client.model.MoCModelWereHuman;
import xyz.dylanlogan.client.model.MoCModelWolf;
import xyz.dylanlogan.client.model.MoCModelWraith;
import xyz.dylanlogan.client.model.MoCModelWyvern;
import xyz.dylanlogan.client.renderer.entity.MoCRenderBear;
import xyz.dylanlogan.client.renderer.entity.MoCRenderBigCat;
import xyz.dylanlogan.client.renderer.entity.MoCRenderBird;
import xyz.dylanlogan.client.renderer.entity.MoCRenderBoar;
import xyz.dylanlogan.client.renderer.entity.MoCRenderBunny;
import xyz.dylanlogan.client.renderer.entity.MoCRenderButterfly;
import xyz.dylanlogan.client.renderer.entity.MoCRenderCricket;
import xyz.dylanlogan.client.renderer.entity.MoCRenderCrocodile;
import xyz.dylanlogan.client.renderer.entity.MoCRenderDeer;
import xyz.dylanlogan.client.renderer.entity.MoCRenderDolphin;
import xyz.dylanlogan.client.renderer.entity.MoCRenderEgg;
import xyz.dylanlogan.client.renderer.entity.MoCRenderFirefly;
import xyz.dylanlogan.client.renderer.entity.MoCRenderFishBowl;
import xyz.dylanlogan.client.renderer.entity.MoCRenderGoat;
import xyz.dylanlogan.client.renderer.entity.MoCRenderGolem;
import xyz.dylanlogan.client.renderer.entity.MoCRenderHellRat;
import xyz.dylanlogan.client.renderer.entity.MoCRenderHorseMob;
import xyz.dylanlogan.client.renderer.entity.MoCRenderInsect;
import xyz.dylanlogan.client.renderer.entity.MoCRenderKitty;
import xyz.dylanlogan.client.renderer.entity.MoCRenderKittyBed;
import xyz.dylanlogan.client.renderer.entity.MoCRenderLitterBox;
import xyz.dylanlogan.client.renderer.entity.MoCRenderMoC;
import xyz.dylanlogan.client.renderer.entity.MoCRenderMouse;
import xyz.dylanlogan.client.renderer.entity.MoCRenderNewHorse;
import xyz.dylanlogan.client.renderer.entity.MoCRenderOstrich;
import xyz.dylanlogan.client.renderer.entity.MoCRenderPetScorpion;
import xyz.dylanlogan.client.renderer.entity.MoCRenderPlatform;
import xyz.dylanlogan.client.renderer.entity.MoCRenderRat;
import xyz.dylanlogan.client.renderer.entity.MoCRenderScorpion;
import xyz.dylanlogan.client.renderer.entity.MoCRenderShark;
import xyz.dylanlogan.client.renderer.entity.MoCRenderSnake;
import xyz.dylanlogan.client.renderer.entity.MoCRenderTRock;
import xyz.dylanlogan.client.renderer.entity.MoCRenderTurtle;
import xyz.dylanlogan.client.renderer.entity.MoCRenderWWolf;
import xyz.dylanlogan.client.renderer.entity.MoCRenderWerewolf;
import xyz.dylanlogan.client.renderer.entity.MoCRenderWraith;
import xyz.dylanlogan.client.renderer.texture.MoCTextures;
import xyz.dylanlogan.configuration.MoCConfiguration;
import xyz.dylanlogan.entity.IMoCEntity;
import xyz.dylanlogan.entity.ambient.MoCEntityAnt;
import xyz.dylanlogan.entity.ambient.MoCEntityBee;
import xyz.dylanlogan.entity.ambient.MoCEntityButterfly;
import xyz.dylanlogan.entity.ambient.MoCEntityCrab;
import xyz.dylanlogan.entity.ambient.MoCEntityCricket;
import xyz.dylanlogan.entity.ambient.MoCEntityDragonfly;
import xyz.dylanlogan.entity.ambient.MoCEntityFirefly;
import xyz.dylanlogan.entity.ambient.MoCEntityFly;
import xyz.dylanlogan.entity.ambient.MoCEntityMaggot;
import xyz.dylanlogan.entity.ambient.MoCEntityRoach;
import xyz.dylanlogan.entity.ambient.MoCEntitySnail;
import xyz.dylanlogan.entity.aquatic.MoCEntityDolphin;
import xyz.dylanlogan.entity.aquatic.MoCEntityFishy;
import xyz.dylanlogan.entity.aquatic.MoCEntityJellyFish;
import xyz.dylanlogan.entity.aquatic.MoCEntityMediumFish;
import xyz.dylanlogan.entity.aquatic.MoCEntityPiranha;
import xyz.dylanlogan.entity.aquatic.MoCEntityRay;
import xyz.dylanlogan.entity.aquatic.MoCEntityShark;
import xyz.dylanlogan.entity.aquatic.MoCEntitySmallFish;
import xyz.dylanlogan.entity.item.MoCEntityEgg;
import xyz.dylanlogan.entity.item.MoCEntityFishBowl;
import xyz.dylanlogan.entity.item.MoCEntityKittyBed;
import xyz.dylanlogan.entity.item.MoCEntityLitterBox;
import xyz.dylanlogan.entity.item.MoCEntityPlatform;
import xyz.dylanlogan.entity.item.MoCEntityThrowableRock;
import xyz.dylanlogan.entity.monster.MoCEntityFlameWraith;
import xyz.dylanlogan.entity.monster.MoCEntityGolem;
import xyz.dylanlogan.entity.monster.MoCEntityHellRat;
import xyz.dylanlogan.entity.monster.MoCEntityHorseMob;
import xyz.dylanlogan.entity.monster.MoCEntityMiniGolem;
import xyz.dylanlogan.entity.monster.MoCEntityOgre;
import xyz.dylanlogan.entity.monster.MoCEntityRat;
import xyz.dylanlogan.entity.monster.MoCEntityScorpion;
import xyz.dylanlogan.entity.monster.MoCEntitySilverSkeleton;
import xyz.dylanlogan.entity.monster.MoCEntityWWolf;
import xyz.dylanlogan.entity.monster.MoCEntityWerewolf;
import xyz.dylanlogan.entity.monster.MoCEntityWraith;
import xyz.dylanlogan.entity.passive.MoCEntityBear;
import xyz.dylanlogan.entity.passive.MoCEntityBigCat;
import xyz.dylanlogan.entity.passive.MoCEntityBird;
import xyz.dylanlogan.entity.passive.MoCEntityBoar;
import xyz.dylanlogan.entity.passive.MoCEntityBunny;
import xyz.dylanlogan.entity.passive.MoCEntityCrocodile;
import xyz.dylanlogan.entity.passive.MoCEntityDeer;
import xyz.dylanlogan.entity.passive.MoCEntityDuck;
import xyz.dylanlogan.entity.passive.MoCEntityElephant;
import xyz.dylanlogan.entity.passive.MoCEntityEnt;
import xyz.dylanlogan.entity.passive.MoCEntityFox;
import xyz.dylanlogan.entity.passive.MoCEntityGoat;
import xyz.dylanlogan.entity.passive.MoCEntityHorse;
import xyz.dylanlogan.entity.passive.MoCEntityKitty;
import xyz.dylanlogan.entity.passive.MoCEntityKomodo;
import xyz.dylanlogan.entity.passive.MoCEntityMole;
import xyz.dylanlogan.entity.passive.MoCEntityMouse;
import xyz.dylanlogan.entity.passive.MoCEntityOstrich;
import xyz.dylanlogan.entity.passive.MoCEntityPetScorpion;
import xyz.dylanlogan.entity.passive.MoCEntityRaccoon;
import xyz.dylanlogan.entity.passive.MoCEntitySnake;
import xyz.dylanlogan.entity.passive.MoCEntityTurkey;
import xyz.dylanlogan.entity.passive.MoCEntityTurtle;
import xyz.dylanlogan.entity.passive.MoCEntityWyvern;
import xyz.dylanlogan.network.MoCMessageHandler;
import xyz.dylanlogan.network.message.MoCMessageInstaSpawn;
import xyz.dylanlogan.utils.MoCLog;

public class MoCClientProxy extends MoCProxy {

    public static Minecraft mc = Minecraft.getMinecraft();
    public static MoCClientProxy instance;
    public static MoCTextures mocTextures = new MoCTextures();

    public MoCClientProxy()
    {
        instance = this;
    }

    @Override
    public void registerRenderers()
    {
    }

    @Override
    public void initTextures()
    {
        mocTextures.loadTextures();
    }

    @Override
    public ResourceLocation getTexture(String texture)
    {
        return mocTextures.getTexture(texture);
    }

    @Override
    public void registerRenderInformation()
    {
        // Register your custom renderers
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityBunny.class, new MoCRenderBunny(new MoCModelBunny(), 0.3F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityBird.class, new MoCRenderBird(new MoCModelBird(), 0.3F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityTurtle.class, new MoCRenderTurtle(new MoCModelTurtle(), 0.4F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityMouse.class, new MoCRenderMouse(new MoCModelMouse(), 0.1F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntitySnake.class, new MoCRenderSnake(new MoCModelSnake(), 0.0F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityTurkey.class, new MoCRenderMoC(new MoCModelTurkey(), 0.4F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityButterfly.class, new MoCRenderButterfly(new MoCModelButterfly()));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityHorse.class, new MoCRenderNewHorse(new MoCModelNewHorse()));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityHorseMob.class, new MoCRenderHorseMob(new MoCModelNewHorseMob()));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityBoar.class, new MoCRenderBoar(new MoCModelBoar(), 0.5F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityBear.class, new MoCRenderBear(new MoCModelBear(), 0.7F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityDuck.class, new MoCRenderMoC(new MoCModelDuck(), 0.3F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityBigCat.class, new MoCRenderBigCat(new MoCModelBigCat2(), new MoCModelBigCat1(), 0.7F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityDeer.class, new MoCRenderDeer(new MoCModelDeer(), 0.5F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityWWolf.class, new MoCRenderWWolf(new MoCModelWolf(), 0.7F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityWraith.class, new MoCRenderWraith(new MoCModelWraith(), 0.5F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityFlameWraith.class, new MoCRenderWraith(new MoCModelWraith(), 0.5F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityWerewolf.class, new MoCRenderWerewolf(new MoCModelWereHuman(), new MoCModelWere(), 0.7F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityFox.class, new MoCRenderMoC(new MoCModelFox(), 0.5F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityShark.class, new MoCRenderShark(new MoCModelShark(), 0.6F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityDolphin.class, new MoCRenderDolphin(new MoCModelDolphin(), 0.6F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityFishy.class, new MoCRenderMoC(new MoCModelFishy(), 0.1F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityEgg.class, new MoCRenderEgg(new MoCModelEgg(), 0.0F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityKitty.class, new MoCRenderKitty(new MoCModelKitty(0.0F, 15F), 0.4F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityKittyBed.class, new MoCRenderKittyBed(new MoCModelKittyBed(), new MoCModelKittyBed2(), 0.3F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityLitterBox.class, new MoCRenderLitterBox(new MoCModelLitterBox(), 0.3F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityRat.class, new MoCRenderRat(new MoCModelRat(), 0.2F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityHellRat.class, new MoCRenderHellRat(new MoCModelRat(), 0.4F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityScorpion.class, new MoCRenderScorpion(new MoCModelScorpion(), 0.6F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityCrocodile.class, new MoCRenderCrocodile(new MoCModelCrocodile(), 0.5F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityRay.class, new MoCRenderMoC(new MoCModelRay(), 0.4F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityJellyFish.class, new MoCRenderMoC(new MoCModelJellyFish(), 0.1F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityGoat.class, new MoCRenderGoat(new MoCModelGoat(), 0.3F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityFishBowl.class, new MoCRenderFishBowl(new MoCModelFishBowl(), 0.3F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityOstrich.class, new MoCRenderOstrich(new MoCModelOstrich(), 0.5F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityBee.class, new MoCRenderInsect(new MoCModelBee()));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityFly.class, new MoCRenderInsect(new MoCModelFly()));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityDragonfly.class, new MoCRenderInsect(new MoCModelDragonfly()));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityFirefly.class, new MoCRenderFirefly(new MoCModelFirefly()));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityCricket.class, new MoCRenderCricket(new MoCModelCricket()));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntitySnail.class, new MoCRenderMoC(new MoCModelSnail(), 0.0F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityGolem.class, new MoCRenderGolem(new MoCModelGolem(), 0.5F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityThrowableRock.class, new MoCRenderTRock());
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityPetScorpion.class, new MoCRenderPetScorpion(new MoCModelPetScorpion(), 0.6F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityPlatform.class, new MoCRenderPlatform());
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityElephant.class, new MoCRenderMoC(new MoCModelElephant(), 0.7F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityKomodo.class, new MoCRenderMoC(new MoCModelKomodo(), 0.3F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityWyvern.class, new MoCRenderMoC(new MoCModelWyvern(), 0.5F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityOgre.class, new MoCRenderMoC(new MoCModelOgre(), 0.6F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityRoach.class, new MoCRenderInsect(new MoCModelRoach()));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityMaggot.class, new MoCRenderMoC(new MoCModelMaggot(), 0F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityCrab.class, new MoCRenderMoC(new MoCModelCrab(), 0.2F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityRaccoon.class, new MoCRenderMoC(new MoCModelRaccoon(), 0.4F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityMiniGolem.class, new MoCRenderMoC(new MoCModelMiniGolem(), 0.5F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntitySilverSkeleton.class, new MoCRenderMoC(new MoCModelSilverSkeleton(), 0.6F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityAnt.class, new MoCRenderInsect(new MoCModelAnt()));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityMediumFish.class, new MoCRenderMoC(new MoCModelMediumFish(), 0.2F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntitySmallFish.class, new MoCRenderMoC(new MoCModelSmallFish(), 0.1F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityPiranha.class, new MoCRenderMoC(new MoCModelSmallFish(), 0.1F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityEnt.class, new MoCRenderMoC(new MoCModelEnt(), 0.5F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityMole.class, new MoCRenderMoC(new MoCModelMole(), 0.3F));
        
    }

    @Override
    //public EntityClientPlayerMP getPlayer()
    public EntityPlayer getPlayer()
    {
        return this.mc.thePlayer;
    }

    @Override
    public void UndeadFX(Entity entity)
    {
        //if (!((Boolean) MoCreatures.particleFX.get()).booleanValue()) return;
        int densityInt = (MoCreatures.proxy.getParticleFX());
        if (densityInt == 0) { return; }

        int i = (densityInt / 2) * (entity.worldObj.rand.nextInt(2) + 1);
        if (i == 0)
        {
            i = 1;
        }
        if (i > 10)
        {
            i = 10;
        }
        for (int x = 0; x < i; x++)
        {
            MoCEntityFXUndead FXUndead = new MoCEntityFXUndead(entity.worldObj, entity.posX, entity.posY + (double) (entity.worldObj.rand.nextFloat() * entity.height), entity.posZ);
            mc.effectRenderer.addEffect(FXUndead);

        }
    }

    @Override
    public void StarFX(MoCEntityHorse entity)
    {
        int densityInt = MoCreatures.proxy.getParticleFX();
        if (densityInt == 0) { return; }

        if ((entity.getType() >= 50 && entity.getType() < 60) || entity.getType() == 36)
        {

            float fRed = entity.colorFX(1, entity.getType());
            float fGreen = entity.colorFX(2, entity.getType());
            float fBlue = entity.colorFX(3, entity.getType());

            int i = densityInt * entity.worldObj.rand.nextInt(2);// + 2;
            for (int x = 0; x < i; x++)
            {
                MoCEntityFXStar FXStar = new MoCEntityFXStar(mc.theWorld, entity.posX, entity.posY + (double) (entity.worldObj.rand.nextFloat() * entity.height), entity.posZ, fRed, fGreen, fBlue);
                mc.effectRenderer.addEffect(FXStar);

            }

        }
    }

    @Override
    public void LavaFX(Entity entity)
    {
        int densityInt = (MoCreatures.proxy.getParticleFX());
        if (densityInt == 0) { return; }
        double var2 = entity.worldObj.rand.nextGaussian() * 0.02D;
        double var4 = entity.worldObj.rand.nextGaussian() * 0.02D;
        double var6 = entity.worldObj.rand.nextGaussian() * 0.02D;
        mc.theWorld.spawnParticle("lava", entity.posX + (double) (entity.worldObj.rand.nextFloat() * entity.width) - (double) entity.width, entity.posY + 0.5D + (double) (entity.worldObj.rand.nextFloat() * entity.height), entity.posZ + (double) (entity.worldObj.rand.nextFloat() * entity.width) - (double) entity.width, var2, var4, var6);

    }

    @Override
    public void VanishFX(MoCEntityHorse entity)
    {
        int densityInt = (MoCreatures.proxy.getParticleFX());
        if (densityInt == 0) { return;
        }

        for (int var6 = 0; var6 < densityInt * 8; ++var6)
        {
            double newPosX = ((float) entity.posX + entity.worldObj.rand.nextFloat());
            double newPosY = 0.7D + ((float) entity.posY + entity.worldObj.rand.nextFloat());
            double newPosZ = ((float) entity.posZ + entity.worldObj.rand.nextFloat());
            int var19 = entity.worldObj.rand.nextInt(2) * 2 - 1;
            double speedY = ((double) entity.worldObj.rand.nextFloat() - 0.5D) * 0.5D;
            double speedX = (double) (entity.worldObj.rand.nextFloat() * 2.0F * (float) var19);
            double speedZ = (double) (entity.worldObj.rand.nextFloat() * 2.0F * (float) var19);

            MoCEntityFXVanish FXVanish = new MoCEntityFXVanish(entity.worldObj, newPosX, newPosY, newPosZ, speedX, speedY, speedZ, entity.colorFX(1, entity.getType()), entity.colorFX(2, entity.getType()), entity.colorFX(3, entity.getType()), false);
            mc.effectRenderer.addEffect(FXVanish);
        }
    }

    @Override
    public void MaterializeFX(MoCEntityHorse entity)
    {
        int densityInt = (MoCreatures.proxy.getParticleFX());
        if (densityInt == 0) { return; }

        for (int var6 = 0; var6 < (densityInt * 50); ++var6)
        {
            double newPosX = ((float) entity.posX + entity.worldObj.rand.nextFloat());
            double newPosY = 0.7D + ((float) entity.posY + entity.worldObj.rand.nextFloat());
            double newPosZ = ((float) entity.posZ + entity.worldObj.rand.nextFloat());
            int var19 = entity.worldObj.rand.nextInt(2) * 2 - 1;
            double speedY = ((double) entity.worldObj.rand.nextFloat() - 0.5D) * 0.5D;
            double speedX = (double) (entity.worldObj.rand.nextFloat() * 2.0F * (float) var19);
            double speedZ = (double) (entity.worldObj.rand.nextFloat() * 2.0F * (float) var19);

            MoCEntityFXVanish FXVanish = new MoCEntityFXVanish(mc.theWorld, newPosX, newPosY, newPosZ, speedX, speedY, speedZ, entity.colorFX(1, entity.getType()), entity.colorFX(2, entity.getType()), entity.colorFX(3, entity.getType()), true);
            mc.effectRenderer.addEffect(FXVanish);
        }
        
    }

    @Override
    public void VacuumFX(MoCEntityGolem entity)
    {
        int densityInt = (MoCreatures.proxy.getParticleFX());
        if (densityInt == 0) { return; }

        for (int var1 = 0; var1 < 2; ++var1)
        {
            double newPosX = entity.posX - (1.5 * Math.cos((MoCTools.realAngle(entity.rotationYaw - 90F)) / 57.29578F));
            double newPosZ = entity.posZ - (1.5 * Math.sin((MoCTools.realAngle(entity.rotationYaw - 90F)) / 57.29578F));
            double newPosY = entity.posY + ((double) entity.height - 0.8D - (double) entity.getAdjustedYOffset() * 1.8);// + (entity.worldObj.rand.nextDouble() * ((double) entity.height - (double) entity.getAdjustedYOffset() * 2));
            //adjustedYOffset from 0 (tallest) to 1.45 (on the ground)
            //height = 4F

            double speedX = (entity.worldObj.rand.nextDouble() - 0.5D) * 4.0D;
            double speedY = -entity.worldObj.rand.nextDouble();
            double speedZ = (entity.worldObj.rand.nextDouble() - 0.5D) * 4.0D;
            MoCEntityFXVacuum FXVacuum = new MoCEntityFXVacuum(mc.theWorld, newPosX, newPosY, newPosZ, speedX, speedY, speedZ, entity.colorFX(1), entity.colorFX(2), entity.colorFX(3), 146);
            mc.effectRenderer.addEffect(FXVacuum);
        }
    }

     
    @Override
    public void hammerFX(EntityPlayer entity)
    {
        int densityInt = (MoCreatures.proxy.getParticleFX());
        if (densityInt == 0) { return; }

        for (int var6 = 0; var6 < (densityInt * 10); ++var6)
        {
            double newPosX = ((float) entity.posX + entity.worldObj.rand.nextFloat());
            double newPosY = 0.3D + ((float) entity.posY + entity.worldObj.rand.nextFloat());
            double newPosZ = ((float) entity.posZ + entity.worldObj.rand.nextFloat());
            int var19 = entity.worldObj.rand.nextInt(2) * 2 - 1;
            double speedY = ((double) entity.worldObj.rand.nextFloat() - 0.5D) * 0.5D;
            double speedX = (double) (entity.worldObj.rand.nextFloat() * 2.0F * (float) var19);
            double speedZ = (double) (entity.worldObj.rand.nextFloat() * 2.0F * (float) var19);

            EntitySpellParticleFX hammerFX = new EntitySpellParticleFX(mc.theWorld, newPosX, newPosY, newPosZ, speedX, speedY, speedZ);
            ((EntitySpellParticleFX)hammerFX).setBaseSpellTextureIndex(144);
            ((EntityFX)hammerFX).setRBGColorF(74F/256F, 145F/256F, 71F/256F);
            mc.effectRenderer.addEffect(hammerFX);
        }
        
    }
    
    
    @Override
    public void teleportFX(EntityPlayer entity)
    {
        int densityInt = (MoCreatures.proxy.getParticleFX());
        if (densityInt == 0) { return; }

        for (int var6 = 0; var6 < (densityInt * 50); ++var6)
        {
            double newPosX = ((float) entity.posX + entity.worldObj.rand.nextFloat());
            double newPosY = 0.7D + ((float) entity.posY + entity.worldObj.rand.nextFloat());
            double newPosZ = ((float) entity.posZ + entity.worldObj.rand.nextFloat());
            int var19 = entity.worldObj.rand.nextInt(2) * 2 - 1;
            double speedY = ((double) entity.worldObj.rand.nextFloat() - 0.5D) * 0.5D;
            double speedX = (double) (entity.worldObj.rand.nextFloat() * 2.0F * (float) var19);
            double speedZ = (double) (entity.worldObj.rand.nextFloat() * 2.0F * (float) var19);

            MoCEntityFXVanish hammerFX = new MoCEntityFXVanish(mc.theWorld, newPosX, newPosY, newPosZ, speedX, speedY, speedZ, 189F/256F, 110F/256F, 229F/256F, true);
            mc.effectRenderer.addEffect(hammerFX);
        }
        
    }
    @Override
    public int getProxyMode()
    {
        return 2;
    }

    public static final List<String> entityTypes = Arrays.asList("CREATURE", "MONSTER", "WATERCREATURE", "AMBIENT");

    public MoCEntityData currentSelectedEntity;

    @Override
    public void ConfigInit(FMLPreInitializationEvent event) {
        super.ConfigInit(event);
    }

    public void cancelReset()
    {
    }
    @Override
    public int getParticleFX()
    {
        return particleFX;
    }

    public boolean getDisplayPetName()
    {
        return displayPetName;
    }

    public boolean getDisplayPetIcons()
    {
        return displayPetIcons;
    }

    public boolean getDisplayPetHealth()
    {
        return displayPetHealth;
    }

    @Override
    public boolean getAnimateTextures()
    {
        return animateTextures;
    }

    @Override
    public void printMessageToPlayer(String msg)
    {
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentTranslation(msg));
    }

    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Entry<T, E> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
}
