package xyz.dylanlogan;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.common.DimensionManager;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.world.WorldEvent;
import xyz.dylanlogan.utils.MoCLog;

public class MoCEventHooks {

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event)
    {
        // if overworld has been deleted or unloaded, reset our flag
        if (event.world.provider.dimensionId == 0)
        {
            MoCreatures.proxy.worldInitDone = false;
        }
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event)
    {
        if (DimensionManager.getWorld(0) != null && !MoCreatures.proxy.worldInitDone) // if overworld has loaded, use its mapstorage
        {
            MoCreatures.proxy.worldInitDone = true;
        }
        // make sure doMobSpawning is on if CMS is not installed
        GameRules gameRule = event.world.getGameRules();
        if (gameRule != null && !MoCreatures.isCustomSpawnerLoaded) {
            gameRule.setOrCreateGameRule("doMobSpawning", "true");
        }
    }

    @SubscribeEvent
    public void onLivingDeathEvent(LivingDeathEvent event) 
    {
    }

    // used for Despawner
    @SubscribeEvent
    public void onLivingDespawn(LivingSpawnEvent.AllowDespawn event)
    {
        if (MoCreatures.proxy.forceDespawns && !MoCreatures.isCustomSpawnerLoaded)
        {
            // try to guess what we should ignore
            // Monsters
            if ((IMob.class.isAssignableFrom(event.entityLiving.getClass()) || IRangedAttackMob.class.isAssignableFrom(event.entityLiving.getClass())) || event.entityLiving.isCreatureType(EnumCreatureType.monster, false))
            {
                return;
            }
            // Farm animals
            if (event.entityLiving instanceof EntitySheep || event.entityLiving instanceof EntityPig || event.entityLiving instanceof EntityCow || event.entityLiving instanceof EntityChicken)
            {
                // check lightlevel
                if (isValidDespawnLightLevel(event.entity, event.world, MoCreatures.proxy.minDespawnLightLevel, MoCreatures.proxy.maxDespawnLightLevel))
                {
                    return;
                }
            }
            // Others
            NBTTagCompound nbt = new NBTTagCompound();
            event.entityLiving.writeToNBT(nbt);
            if (nbt != null)
            {
            }
            // Deny Rest
            if (event.entityLiving.getAge() > 600)
            {
                event.setResult(Result.ALLOW);
            }

            if (MoCreatures.proxy.debug) 
            {
                int x = MathHelper.floor_double(event.entity.posX);
                int y = MathHelper.floor_double(event.entity.boundingBox.minY);
                int z = MathHelper.floor_double(event.entity.posZ);
                MoCLog.logger.info("Forced Despawn of entity " + event.entityLiving + " at " + x + ", " + y + ", " + z + ". To prevent forced despawns, use /moc forceDespawns false.");
            }
        }
    }

    private boolean isValidDespawnLightLevel(Entity entity, World worldObj, int minDespawnLightLevel, int maxDespawnLightLevel)
    {
        int x = MathHelper.floor_double(entity.posX);
        int y = MathHelper.floor_double(entity.boundingBox.minY);
        int z = MathHelper.floor_double(entity.posZ);
        int blockLightLevel = 0;
        if (y >= 0)
        {
            if (y >= 256)
            {
                y = 255;
            }
        }
        if (blockLightLevel < minDespawnLightLevel && maxDespawnLightLevel != -1)
        {
            return false;
        }
        else if (blockLightLevel > maxDespawnLightLevel && maxDespawnLightLevel != -1)
        {
            return false;
        }
        return true;
    }

    /**
     * Gets the amount of light on a block without taking into account sunlight
     */
    private int getBlockLightValue(Chunk chunk, int x, int y, int z)
    {
        ExtendedBlockStorage extendedblockstorage = chunk.getBlockStorageArray()[y >> 4];

        if (extendedblockstorage == null)
        {
            return 0;
        }
        else
        {
            return extendedblockstorage.getExtBlocklightValue(x, y & 15, z);
        }
    }
}
