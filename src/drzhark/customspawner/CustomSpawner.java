package drzhark.customspawner;


import static net.minecraftforge.common.ForgeDirection.UP;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockStep;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingData;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.ForgeEventFactory;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import drzhark.customspawner.biomes.BiomeData;
import drzhark.customspawner.biomes.BiomeGroupData;
import drzhark.customspawner.biomes.BiomeModData;
import drzhark.customspawner.command.CommandCMS;
import drzhark.customspawner.configuration.CMSConfigCategory;
import drzhark.customspawner.configuration.CMSConfiguration;
import drzhark.customspawner.configuration.CMSProperty;
import drzhark.customspawner.entity.EntityData;
import drzhark.customspawner.entity.EntityModData;
import drzhark.customspawner.handlers.SpawnTickHandler;
import drzhark.customspawner.registry.StructureRegistry;
import drzhark.customspawner.utils.CMSLog;


@Mod(modid = "CustomSpawner", name = "DrZhark's CustomSpawner", version = "2.4.0.d2")
public final class CustomSpawner {

    @Instance("CustomSpawner")
    public static CustomSpawner INSTANCE;

    public static boolean worldGenCreatureSpawning;
    public static boolean spawnCreatures;
    public static boolean spawnMonsters;
    public static boolean spawnAmbients;
    public static boolean spawnWaterCreatures;
    //public static boolean debugCMS;
    public static boolean checkAmbientLightLevel;
    public static boolean disallowMonsterSpawningDuringDay;
    public static boolean killallUseLightLevel;
    public static boolean killallVillagers;
    public static boolean enforceMaxSpawnLimits;
    public static int despawnLightLevel = 7;
    public static int lightLevel = 7;
    public static int creatureSpawnTickRate;
    public static int monsterSpawnTickRate;
    public static int ambientSpawnTickRate;
    public static int waterSpawnTickRate;
    public static int despawnTickRate;
    public static int entitySpawnRange;
    public boolean useDefaultBiomeGroups;
    public static boolean modifyVanillaSpawns;
    public static boolean despawnVanilla;

    // defaults
    public int frequency = 6;
    public int minGroup = 1;
    public int maxGroup = 2;
    public int maxSpawnInChunk = 3;
    public float strength = 1;
    public static int maxCreatures;
    public static int maxMonsters;
    public static int maxWaterCreatures;
    public static int maxAmbients;
    public static boolean debug;

    public static Map<String, EntityData> entityMap = new HashMap<String, EntityData>();
    private static Map<String, EnumCreatureType> entityTypes = new HashMap<String, EnumCreatureType>();
    public static Map<String, BiomeData> biomeMap = new TreeMap<String, BiomeData>();
    public static Map<String, BiomeGroupData> biomeGroupMap = new TreeMap<String, BiomeGroupData>();
    public static Map<String, BiomeGroupData> defaultBiomeGroupMap = new TreeMap<String, BiomeGroupData>(); // used only for default biome groups
    public static Map<String, EntityModData> entityModMap = new TreeMap<String, EntityModData>();
    public static Map<String, EntityModData> defaultModMap = new TreeMap<String, EntityModData>();
    public static Map<String, BiomeModData> biomeModMap = new TreeMap<String, BiomeModData>();
    public static Map<String, BiomeModData> tagConfigMap = new HashMap<String, BiomeModData>();
    public static Map<String, SpawnListEntry> defaultSpawnListEntryMap = new HashMap<String, SpawnListEntry>();
    public static Map<Integer, Class<? extends EntityLiving>> instaSpawnerMap = new HashMap<Integer, Class<? extends EntityLiving>>();
    public static Map<Class<? extends EntityLiving>, EntityData> classToEntityMapping = new HashMap<Class<? extends EntityLiving>, EntityData>();
    public static Map<String, EntitySpawnType> entitySpawnTypes = new HashMap<String, EntitySpawnType>();
    public static EntitySpawnType LIVINGTYPE_UNDEFINED = new EntitySpawnType(EntitySpawnType.UNDEFINED, 0, 0, 0.0F, false);
    public static EntitySpawnType LIVINGTYPE_CREATURE = new EntitySpawnType(EntitySpawnType.CREATURE, 400, 10, 0.1F, true);
    public static EntitySpawnType LIVINGTYPE_AMBIENT = new EntitySpawnType(EntitySpawnType.AMBIENT, 100, 15);
    public static EntitySpawnType LIVINGTYPE_WATERCREATURE = new EntitySpawnType(EntitySpawnType.WATERCREATURE, 100, 5, Material.water);
    public static EntitySpawnType LIVINGTYPE_MONSTER = new EntitySpawnType(EntitySpawnType.MONSTER, 1, 70);
    public static EntitySpawnType LIVINGTYPE_UNDERGROUND = new EntitySpawnType(EntitySpawnType.UNDERGROUND, 400, 15, 0.0F, false);
    public static StructureRegistry structureData = new StructureRegistry();

    protected static final String MOD_CREATURES_FILE_ROOT = File.separator + "Creatures" + File.separator;
    private static final String MOD_BIOME_FILE_ROOT = File.separator + "Biomes" + File.separator;

    protected static final String CATEGORY_MOC_GENERAL_SETTINGS = "global-settings";
    protected static final String CATEGORY_MOC_CREATURE_GENERAL_SETTINGS = "creature-general-settings";
    protected static final String CATEGORY_MOC_MONSTER_GENERAL_SETTINGS = "monster-general-settings";
    protected static final String CATEGORY_MOC_WATER_CREATURE_GENERAL_SETTINGS = "water-mob-general-settings";
    protected static final String CATEGORY_MOC_AMBIENT_GENERAL_SETTINGS = "ambient-general-settings";
    protected static final String CATEGORY_MOC_UNDEFINED_FREQUENCIES = "undefined-frequencies";
    protected static final String CATEGORY_CUSTOMSPAWNER_SETTINGS = "customspawner-settings";
    //protected static final String CATEGORY_ENTITY_BIOME_SETTINGS = "entity-biome-settings";
    protected static final String CATEGORY_BIOMEGROUP_DEFAULTS = "biomegroup-defaults";
    protected static final String CATEGORY_MOC_ID_SETTINGS = "custom-id-settings";
    private static final String CATEGORY_MOD_MAPPINGS = "mod-mappings";
    private static final String CATEGORY_VANILLA_CREATURE_FREQUENCIES = "vanilla-creature-frequencies";
    private static final String CATEGORY_CREATURES = "Creatures";
    public static final String CATEGORY_ENTITY_CANSPAWN_SETTINGS = "entity-canspawn-settings";
    private static final String CATEGORY_OWNERSHIP_SETTINGS = "ownership-settings";
    private static final String CATEGORY_ENTITY_SPAWN_TYPES = "entity-spawn-types";
    private static final String CATEGORY_WORLD_SETTINGS = "world-settings";
    //public static final String CATEGORY_ENTITY_SPAWN_SETTINGS = "entity-spawn-settings";

    public static CMSConfiguration CMSGlobalConfig;
    public static CMSConfiguration CMSStructureConfig;
    public static CMSConfiguration defaultBiomeGroupConfig;
    public static CMSConfiguration CMSLivingSpawnTypeConfig;
    protected File configFile;

    private static List<BiomeGenBase> biomeList;
    private List<Class> vanillaClassList;
    public static Map<String, ArrayList<BiomeGenBase>> entityDefaultSpawnBiomes = new HashMap<String, ArrayList<BiomeGenBase>>();

    private static HashMap eligibleChunksForSpawning = new HashMap();

    static {
        entitySpawnTypes.put(EntitySpawnType.UNDEFINED, LIVINGTYPE_UNDEFINED);
        entitySpawnTypes.put(EntitySpawnType.CREATURE, LIVINGTYPE_CREATURE);
        entitySpawnTypes.put(EntitySpawnType.AMBIENT, LIVINGTYPE_AMBIENT);
        entitySpawnTypes.put(EntitySpawnType.MONSTER, LIVINGTYPE_MONSTER);
        entitySpawnTypes.put(EntitySpawnType.WATERCREATURE, LIVINGTYPE_WATERCREATURE);
    }
    /**
     * @return the instance
     */
    public static CustomSpawner instance()
    {
        return INSTANCE;
    }

    public void initializeCMS()
    {
        biomeList = new ArrayList<BiomeGenBase>();
        try
        {
            for (BiomeGenBase biomegenbase : BiomeGenBase.biomeList)
            {
                if (biomegenbase == null)
                {
                    continue;
                }

                biomeList.add(biomegenbase);
                for (EntitySpawnType entitySpawnType : entitySpawnTypes.values())
                {
                    entitySpawnType.getLivingSpawnList().put(biomegenbase.biomeID, new ArrayList<SpawnListEntry>());
                }
            }

            vanillaClassList = new ArrayList<Class>();
            vanillaClassList.add(EntityChicken.class);
            vanillaClassList.add(EntityCow.class);
            vanillaClassList.add(EntityPig.class);
            vanillaClassList.add(EntitySheep.class);
            vanillaClassList.add(EntityWolf.class);
            vanillaClassList.add(EntitySquid.class);
            vanillaClassList.add(EntityOcelot.class);
            vanillaClassList.add(EntityBat.class);
            vanillaClassList.add(EntityHorse.class);
            clearLists();
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    @EventHandler
    public void load(FMLInitializationEvent event)
    {
        TickRegistry.registerTickHandler(new SpawnTickHandler(), Side.SERVER);
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        CMSLog.initLog();
        CMSGlobalConfig = new CMSConfiguration(new File(event.getSuggestedConfigurationFile().getParent(), "CustomSpawner" + File.separator + "Global.cfg"));
        CMSStructureConfig = new CMSConfiguration(new File(event.getSuggestedConfigurationFile().getParent(), "CustomSpawner" + File.separator + "Structures.cfg"));
        CMSLivingSpawnTypeConfig = new CMSConfiguration(new File(event.getSuggestedConfigurationFile().getParent(), "CustomSpawner" + File.separator + "LivingSpawnTypes.cfg"));
        defaultBiomeGroupConfig = new CMSConfiguration(new File(event.getSuggestedConfigurationFile().getParent(), "CustomSpawner" + File.separator + "DefaultEntityBiomeGroups.cfg"));
        configFile = event.getSuggestedConfigurationFile();
        CMSGlobalConfig.load();
        defaultBiomeGroupConfig.load();
        CMSStructureConfig.load();
        CMSLivingSpawnTypeConfig.load();
        registerLivingSpawnTypes();
        genModConfiguration();
        this.readConfigValues();
        MinecraftForge.TERRAIN_GEN_BUS.register(new EventHooks()); // register our event subscriptions
        MinecraftForge.EVENT_BUS.register(new EventHooks());
        if (debug) CMSLog.logger.info("Initializing CustomSpawner Config File at " + event.getSuggestedConfigurationFile().getParent() + "CMSGlobal.cfg");
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        BiomeDictionary.registerAllBiomes();
        initializeCMS();
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
        // SAFEST POINT TO COPY VANILLA SPAWN LISTS //
        List<BiomeGenBase> biomeList = new ArrayList<BiomeGenBase>();
        for (int j = 0; j < BiomeGenBase.biomeList.length; j++)
        {
            if (BiomeGenBase.biomeList[j] != null)
            {
                biomeList.add(BiomeGenBase.biomeList[j]);
            }
        }
        if (biomeList.size() > 0)
        {
            BiomeGenBase[] allBiomes = new BiomeGenBase[biomeList.size()];
            allBiomes = biomeList.toArray(allBiomes);
            copyVanillaSpawnLists(allBiomes);
        }
        initializeBiomes();
        initializeEntities();
        updateSettings(); // refresh settings
        event.registerServerCommand(new CommandCMS());
    }

    public void updateSettings()
    {
        readConfigValues();
        // make sure latest config data is loaded, fixes issue where mocreatures don't spawn on initial load
        CMSGlobalConfig.load();
        CMSStructureConfig.load();
        populateSpawns();
    }

    /**
     * Populates spawn lists
     */
    public void populateSpawns()
    {
        if (debug) CMSLog.logger.info("Populating spawns...");

        if (debug) CMSLog.logger.info("Scanning mod configs for entities...");
        for (Map.Entry<String, EntityModData> modEntry : entityModMap.entrySet())
        {
            if (modEntry.getKey().equalsIgnoreCase("vanilla") && !modifyVanillaSpawns)
                continue;
            ArrayList<Map<String, EntityData>> entityList = new ArrayList();

            for (EntitySpawnType entitySpawnType : entitySpawnTypes.values())
            {
                if (entitySpawnType.getLivingSpawnTypeName().equalsIgnoreCase("UNDEFINED") || modEntry.getValue().getSpawnListFromType(entitySpawnType) == null)
                {
                    continue;
                }
                //System.out.println("entitySpawnTypes size = " + entitySpawnTypes.size());
                //System.out.println("entitySpawnType = " + entitySpawnType.getLivingSpawnTypeName());
                for (Map.Entry<String, EntityData> entityEntry : modEntry.getValue().getSpawnListFromType(entitySpawnType).entrySet())
                {
                    EntityData entityData = entityEntry.getValue();
                    //System.out.println("Found entity " + entityEntry.getValue().getEntityName() + " spawnType = " + entityData.getLivingSpawnType() + ", biomes size = " + entityData.getSpawnBiomes().size());
                    if (entityData.getSpawnBiomes() != null && entityData.getSpawnBiomes().size() > 0 && entityData.getLivingSpawnType() != null)
                    {
                        BiomeGenBase[] biomesToSpawn = new BiomeGenBase[entityData.getSpawnBiomes().size()];
                        biomesToSpawn = entityData.getSpawnBiomes().toArray(biomesToSpawn);
                        if (debug) CMSLog.logger.info(entityData.getEntityName()  +" canSpawn = " + entityData.getCanSpawn());
                        if (entityData.getCanSpawn())
                        {
                            instance().AddCustomSpawn(entityData.getEntityClass(), entityData.getFrequency(), entityData.getMinSpawn(), entityData.getMaxSpawn(), entityData.getLivingSpawnType(), biomesToSpawn);
                            if (debug) CMSLog.logger.info("Added " + entityData.getEntityClass() + " to CustomSpawner spawn lists");
                            //System.out.println("Added " + entityData.getEntityClass() + " to CustomSpawner spawn lists");
                        }
                        //otherwise the Forge spawnlist remains populated with duplicated entries on CMS
                        removeAllBiomeSpawns(entityData, true); // If we add a entity to CMS, we MUST remove it from ALL biomes on vanilla to avoid massive spawning in missing biomes
                        if (debug) CMSLog.logger.info("Removed " + entityData.getEntityClass() + " from Vanilla spawn lists");
                        //System.out.println("Removed " + entityData.getEntityClass() + " from Vanilla spawn lists");
                    }
                    else
                    {
                        if (debug) CMSLog.logger.info("Skipping " + entityData.getEntityClass() + " spawn!!");
                    }
                    // handle entity removals
                    /*if (entityData != null && entityData.getFrequency() <= 0 &&
                       (entityData.getType() == null // undefined
                    || ((maxAmbients == 0 || !spawnAmbients) && entityData.getType() == EnumCreatureType.ambient) 
                    || ((maxCreatures == 0 || !spawnCreatures) && entityData.getType() == EnumCreatureType.creature)
                    || ((maxMonsters == 0 || !spawnMonsters) && entityData.getType() == EnumCreatureType.monster)
                    || ((maxWaterCreatures == 0 || !spawnWaterCreatures) && entityData.getType() == EnumCreatureType.waterCreature)))
                    {
                        // remove from all biomes
                        if (debug) CMSLog.logger.info("Entity " + entityData.getEntityClass() + " is not allowed to spawn due to configuration settings. Please check your confirm your settings if this is a mistake.");
                        removeAllBiomeSpawns(entityData, false);
                    }*/
                }
            }
        }
    }

    public void removeAllBiomeSpawns(EntityData entityData, boolean vanillaOnly)
    {
        BiomeGenBase[] allBiomes = new BiomeGenBase[biomeMap.size()];
        List<BiomeGenBase> biomeList = new ArrayList<BiomeGenBase>();
        for (int j = 0; j < BiomeGenBase.biomeList.length; j++)
        {
            if (BiomeGenBase.biomeList[j] != null)
            {
                biomeList.add(BiomeGenBase.biomeList[j]);
            }
        }
        if (biomeList.size() > 0)
        {
            if (debug) CMSLog.logger.info("Removing entity " + entityData.getEntityClass() + " with type " + entityData.getType() + " from all biome spawnlists.");
            allBiomes = biomeList.toArray(allBiomes);
            if (!vanillaOnly)
            {
                //System.out.println("Removing entity " + entityData.getEntityName() + " from CMS spawn lists");
                RemoveCustomSpawn(entityData.getEntityClass(), entityData.getLivingSpawnType(), allBiomes);
            }
            // handle undefined types
            EntityRegistry.removeSpawn(entityData.getEntityClass(), EnumCreatureType.creature, allBiomes);
            EntityRegistry.removeSpawn(entityData.getEntityClass(), EnumCreatureType.waterCreature, allBiomes);
            EntityRegistry.removeSpawn(entityData.getEntityClass(), EnumCreatureType.monster, allBiomes);
            EntityRegistry.removeSpawn(entityData.getEntityClass(), EnumCreatureType.ambient, allBiomes);
        }
    }

    public void resetAllData()
    {
        entityModMap.clear();
        tagConfigMap.clear();
        biomeModMap.clear();
        biomeGroupMap.clear();
        instaSpawnerMap.clear();
        classToEntityMapping.clear();
        genModConfiguration();
        this.readConfigValues();
    }
    public void genModConfiguration()
    {
        entityTypes.put("UNDEFINED", null);
        entityTypes.put("CREATURE", EnumCreatureType.creature);
        entityTypes.put("MONSTER", EnumCreatureType.monster);
        entityTypes.put("WATERCREATURE", EnumCreatureType.waterCreature);
        entityTypes.put("AMBIENT", EnumCreatureType.ambient);

        defaultModMap.put("drzhark", new EntityModData("drzhark", "MOC", new CMSConfiguration(new File(CMSGlobalConfig.file.getParent(), MOD_CREATURES_FILE_ROOT + "MoCreatures.cfg"))));
        defaultModMap.put("biomesop", new EntityModData("biomesop", "BOP", new CMSConfiguration(new File(CMSGlobalConfig.file.getParent(), MOD_CREATURES_FILE_ROOT + "BiomesOPlenty.cfg"))));
        defaultModMap.put("ted80", new EntityModData("ted80", "BWG", new CMSConfiguration(new File(CMSGlobalConfig.file.getParent(), MOD_CREATURES_FILE_ROOT + "BWG.cfg"))));
        defaultModMap.put("extrabiomes", new EntityModData("extrabiomes", "XL", new CMSConfiguration(new File(CMSGlobalConfig.file.getParent(), MOD_CREATURES_FILE_ROOT + "ExtraBiomesXL.cfg"))));
        defaultModMap.put("twilightforest", new EntityModData("twilightforest", "TF", new CMSConfiguration(new File(CMSGlobalConfig.file.getParent(), MOD_CREATURES_FILE_ROOT + "TwilightForest.cfg"))));
        defaultModMap.put("gaia", new EntityModData("gaia", "GAIA", new CMSConfiguration(new File(CMSGlobalConfig.file.getParent(), MOD_CREATURES_FILE_ROOT + "GrimoireOfGaia.cfg"))));
        defaultModMap.put("atomicstryker", new EntityModData("atomicstryker", "IM", new CMSConfiguration(new File(CMSGlobalConfig.file.getParent(), MOD_CREATURES_FILE_ROOT + "InfernalMobs.cfg"))));
        defaultModMap.put("arsmagica", new EntityModData("arsmagica", "ARS", new CMSConfiguration(new File(CMSGlobalConfig.file.getParent(), MOD_CREATURES_FILE_ROOT + "ArsMagica.cfg"))));
        defaultModMap.put("projectzulu", new EntityModData("projectzulu", "PZ", new CMSConfiguration(new File(CMSGlobalConfig.file.getParent(), MOD_CREATURES_FILE_ROOT + "ProjectZulu.cfg"))));
        defaultModMap.put("thaumcraft", new EntityModData("thaumcraft", "TC", new CMSConfiguration(new File(CMSGlobalConfig.file.getParent(), MOD_CREATURES_FILE_ROOT + "Thaumcraft.cfg"))));
        defaultModMap.put("vanilla", new EntityModData("vanilla", "MC", new CMSConfiguration(new File(CMSGlobalConfig.file.getParent(), MOD_CREATURES_FILE_ROOT + "Vanilla.cfg"))));
        defaultModMap.put("highlands", new EntityModData("highlands", "HL", new CMSConfiguration(new File(CMSGlobalConfig.file.getParent(), MOD_CREATURES_FILE_ROOT + "Highlands.cfg"))));
        defaultModMap.put("undefined", new EntityModData("undefined", "U", new CMSConfiguration(new File(CMSGlobalConfig.file.getParent(), MOD_CREATURES_FILE_ROOT + "Undefined.cfg"))));
        defaultModMap.put("tinker", new EntityModData("tinker", "TC", new CMSConfiguration(new File(CMSGlobalConfig.file.getParent(), MOD_CREATURES_FILE_ROOT + "TinkerConstruct.cfg"))));
        defaultModMap.put("atum", new EntityModData("atum", "ATUM", new CMSConfiguration(new File(CMSGlobalConfig.file.getParent(), MOD_CREATURES_FILE_ROOT + "Atum.cfg"))));
        defaultModMap.put("advancedglowstone", new EntityModData("advancedglowstone", "AC", new CMSConfiguration(new File(CMSGlobalConfig.file.getParent(), MOD_CREATURES_FILE_ROOT + "AngryCreatures.cfg"))));
        defaultModMap.put("minefantasy", new EntityModData("minefantasy", "MF", new CMSConfiguration(new File(CMSGlobalConfig.file.getParent(), MOD_CREATURES_FILE_ROOT + "MineFantasy.cfg"))));
        defaultModMap.put("primitivemobs", new EntityModData("primitivemobs", "PM", new CMSConfiguration(new File(CMSGlobalConfig.file.getParent(), MOD_CREATURES_FILE_ROOT + "PrimitiveMobs.cfg"))));
        defaultModMap.put("atmosmobs", new EntityModData("atmosmobs", "ATMOS", new CMSConfiguration(new File(CMSGlobalConfig.file.getParent(), MOD_CREATURES_FILE_ROOT + "AtmosMobs.cfg"))));
        defaultModMap.put("farlanders", new EntityModData("farlanders", "FL", new CMSConfiguration(new File(CMSGlobalConfig.file.getParent(), MOD_CREATURES_FILE_ROOT + "Farlanders.cfg"))));

        CMSConfigCategory modMapCat = CMSGlobalConfig.getCategory(CATEGORY_MOD_MAPPINGS);

        // generate default key mappings
        for (Map.Entry<String, EntityModData> modEntry : defaultModMap.entrySet())
        {
            List<String> values = new ArrayList(Arrays.asList(modEntry.getValue().getModTag(), modEntry.getValue().getModConfig().getFileName()));
            modMapCat.put(modEntry.getKey(), new CMSProperty(modEntry.getKey(), values, CMSProperty.Type.STRING));
            biomeModMap.put(modEntry.getKey(), new BiomeModData(modEntry.getKey(), modEntry.getValue().getModTag(), new CMSConfiguration(new File(CMSGlobalConfig.file.getParent(), MOD_BIOME_FILE_ROOT + (modEntry.getValue().getModConfig().getFileName() + ".cfg")))));
            entityModMap.put(modEntry.getKey(), new EntityModData(modEntry.getKey(), modEntry.getValue().getModTag(), new CMSConfiguration(new File(CMSGlobalConfig.file.getParent(), MOD_CREATURES_FILE_ROOT + (modEntry.getValue().getModConfig().getFileName() + ".cfg")))));
            if (debug) CMSLog.logger.info("Added Mod Entity Mapping " + modEntry.getKey() + " to file " + (modEntry.getValue().getModConfig().getFileName() + ".cfg"));
            tagConfigMap.put(modEntry.getValue().getModTag(), biomeModMap.get(modEntry.getKey()));
            if (debug) CMSLog.logger.info("Added Mod Biome Mapping " + modEntry.getKey() + " with tag " + modEntry.getValue().getModTag() + " to file " + (modEntry.getValue().getModConfig().getFileName() + ".cfg"));
        }
        // handle custom tag to config mappings
        for (Map.Entry<String, CMSProperty> propEntry : modMapCat.entrySet())
        {
            CMSProperty prop = propEntry.getValue();
            if (prop != null && !biomeModMap.containsKey(propEntry.getKey()))
            {
                if (prop.valueList.size() == 2)
                {
                    biomeModMap.put(propEntry.getKey(), new BiomeModData(propEntry.getKey(), prop.valueList.get(0), new CMSConfiguration(new File(CMSGlobalConfig.file.getParent(), MOD_BIOME_FILE_ROOT + prop.valueList.get(1)))));
                    tagConfigMap.put(propEntry.getKey(), biomeModMap.get(propEntry.getKey()));
                    if (debug) CMSLog.logger.info("Added Custom Mod Biome Mapping " + propEntry.getKey() + " with tag " + prop.valueList.get(0) + " to file " + prop.valueList.get(1));
                    entityModMap.put(propEntry.getKey(), new EntityModData(propEntry.getKey(), prop.valueList.get(0), new CMSConfiguration(new File(CMSGlobalConfig.file.getParent(), MOD_CREATURES_FILE_ROOT + prop.valueList.get(1)))));
                    if (debug) CMSLog.logger.info("Added Custom Mod Entity Mapping " + propEntry.getKey() + " with tag " + prop.valueList.get(0) + " to file " + prop.valueList.get(1));
                }
            }
        }

        CMSGlobalConfig.save();
    }

    public void registerLivingSpawnTypes()
    {
        // check config for new values
        for (EntitySpawnType entitySpawnType : entitySpawnTypes.values())
        {
            if (entitySpawnType.getLivingSpawnTypeName().equalsIgnoreCase("UNDEFINED"))
                continue;
            if (CMSLivingSpawnTypeConfig.hasCategory(entitySpawnType.getLivingSpawnTypeName().toLowerCase()))
            {
                System.out.println("HAS CATEGORY " + entitySpawnType.getLivingSpawnTypeName());
                CMSConfigCategory cat = CMSLivingSpawnTypeConfig.getCategory(entitySpawnType.getLivingSpawnTypeName().toLowerCase());
                for (Map.Entry<String, CMSProperty> propEntry : cat.getValues().entrySet())
                {
                    System.out.println("Found property " + propEntry.getKey());
                    if (propEntry.getKey().equalsIgnoreCase("ChunkGenSpawnChance"))
                    {
                        entitySpawnType.setChunkSpawnChance(Float.parseFloat(propEntry.getValue().value));
                    }
                    else if (propEntry.getKey().equalsIgnoreCase("SpawnTickRate"))
                    {
                        entitySpawnType.setSpawnTickRate(Integer.parseInt(propEntry.getValue().value));
                    }
                    else if (propEntry.getKey().equalsIgnoreCase("SpawnCap"))
                    {
                        entitySpawnType.setSpawnCap(Integer.parseInt(propEntry.getValue().value));
                    }
                    else if (propEntry.getKey().equalsIgnoreCase("ShouldSeeSky") && !propEntry.getValue().value.equalsIgnoreCase("UNDEFINED"))
                    {
                        entitySpawnType.setShouldSeeSky(Boolean.parseBoolean(propEntry.getValue().value));
                    }
                }
            }
            else
            {
                CMSConfigCategory configCat = CMSLivingSpawnTypeConfig.getCategory(entitySpawnType.getLivingSpawnTypeName().toLowerCase());
                configCat.put("SpawnTickRate", new CMSProperty("SpawnTickRate", Integer.toString(entitySpawnType.getSpawnTickRate()), CMSProperty.Type.INTEGER));
                configCat.put("SpawnCap", new CMSProperty("SpawnCap", Integer.toString(entitySpawnType.getSpawnCap()), CMSProperty.Type.INTEGER));
                configCat.put("ChunkGenSpawnChance", new CMSProperty("ChunkGenSpawnChance",Float.toString(entitySpawnType.getChunkSpawnChance()), CMSProperty.Type.DOUBLE));
                configCat.put("ShouldSeeSky", new CMSProperty("ShouldSeeSky", entitySpawnType.getShouldSeeSky() == null ? "UNDEFINED" : Boolean.toString(entitySpawnType.getShouldSeeSky()), CMSProperty.Type.STRING));
            }
        }
        CMSLivingSpawnTypeConfig.save();
        // register custom handlers
    }

    public void initializeEntities()
    {
        for (Object obj : EntityList.classToStringMapping.entrySet())
        {
            Map.Entry entry = (Map.Entry)obj;
            Class clazz = null;
            String entityName = "";
            clazz = (Class)entry.getKey();
            EntityLiving entityliving = null;
            EntityData entityData = registerEntity(clazz);
        }
        CMSGlobalConfig.save();
    }

    public EntityData registerEntity(Class<? extends EntityLiving> clazz)
    {
        EntityLiving entityliving = null;
        EntityData entityData = null;
        try
        {
            if (debug) CMSLog.logger.info("Attempting to register Entity from class " + clazz + "...");
            entityliving = (EntityLiving) clazz.getConstructor(new Class[] { World.class }).newInstance(new Object[] { DimensionManager.getWorld(0) });
        }
        catch (Exception exception)
        {
            if (debug) CMSLog.logger.info(clazz + " is not a valid Entity for registration, Skipping...");
            return null;
        } 

        String entityName = entityliving.getEntityName();

        if (debug) CMSLog.logger.info("Starting registration for "  + entityName);
        if (entityName.contains("."))
        {
            if ((entityName.indexOf(".") + 1) < entityName.length())
                entityName = entityName.substring(entityName.indexOf(".") + 1, entityName.length());
        }
        entityName = entityName.replaceAll("[^A-Za-z0-9]", ""); // remove all non-digits/alphanumeric

        if (clazz != null && EntityLiving.class.isAssignableFrom(clazz))
        {
            if (debug) CMSLog.logger.info("Attempting to find a valid type for " + entityName + "...");
            EnumCreatureType creatureType = null;

            // temp fix for MoCreature ambients
            if (((EntityAnimal.class.isAssignableFrom(clazz) && !entityliving.isCreatureType(EnumCreatureType.ambient, true)) || entityliving.isCreatureType(EnumCreatureType.creature, true))) //&& !(MoCEntityAmbient.class.isAssignableFrom(clazz)))
            {
                creatureType = EnumCreatureType.creature;
                entityData = new EntityData(clazz, entityliving.entityId, creatureType, entityName);
            }
            else if (((IMob.class.isAssignableFrom(clazz) || IRangedAttackMob.class.isAssignableFrom(clazz)) && (clazz != EntityMob.class)) || entityliving.isCreatureType(EnumCreatureType.monster, true))
            {
                creatureType = EnumCreatureType.monster;
                entityData = new EntityData(clazz, entityliving.entityId, creatureType, entityName);
            }
            else if (EntityAmbientCreature.class.isAssignableFrom(clazz)|| entityliving.isCreatureType(EnumCreatureType.ambient, true)) //|| MoCEntityAmbient.class.isAssignableFrom(clazz))
            {
                creatureType = EnumCreatureType.ambient;
                entityData = new EntityData(clazz, entityliving.entityId, creatureType, entityName);
            }
            else if (EntityWaterMob.class.isAssignableFrom(clazz) || entityliving.isCreatureType(EnumCreatureType.waterCreature, true))
            {
                creatureType = EnumCreatureType.waterCreature;
                entityData = new EntityData(clazz, entityliving.entityId, creatureType, entityName);
            }
            else if (clazz != EntityLiving.class && clazz != EntityMob.class)//&& clazz != MoCEntityFishBowl.class && clazz != MoCEntityLitterBox.class && clazz != MoCEntityEgg.class && clazz != MoCEntityKittyBed.class)
            {
                entityData = new EntityData(clazz, entityliving.entityId, creatureType, entityName);
                entityData.setCanSpawn(false); // dont allow undefined types to spawn
            }
            else if (entityData == null)
            {
                if (debug) CMSLog.logger.info("Could not find a valid type for Entity " + entityName + " with class " + clazz + ", skipping...");
                return null;
            }
            if (debug) CMSLog.logger.info("Detected type as " + creatureType.name().toUpperCase() + ".");
            entityData.setType(creatureType);
            entityData.setLivingSpawnType(creatureType); // set default type
            entityData.setEntityName(entityName);
            entityData.setEntityID(entityliving.entityId);
            entityData.setMaxInChunk(entityliving.getMaxSpawnedInChunk());
            if (debug) CMSLog.logger.info("Added " + ((creatureType == null) ? "UNDEFINED" : creatureType.toString().toUpperCase()) + " " + clazz + " with name " + entityName);
            // handle frequencies
            // assign config for type of mod
            String entityClass = clazz.getName().toLowerCase();
            CMSConfiguration entityConfig = entityModMap.get("undefined").getModConfig();

            boolean undefined = true;
            if (debug) CMSLog.logger.info("Attempting to detect mod for Entity " + entityName + "...");

            if (entityClass.contains("net.minecraft.entity")|| entityClass.toString().length() <= 3) // vanilla
            {
                EntityModData modData = entityModMap.get("vanilla");
                if (debug) CMSLog.logger.info("Matched mod Vanilla to " + entityName);
                entityData.setEntityMod(modData);
                entityConfig = modData.getModConfig();
                entityData.setEntityConfig(entityConfig);
                processEntityConfig(entityData);
                if (!modData.addCreature(entityData))
                {
                    entityData = modData.getCreature(entityData.getLivingSpawnType(), entityName);
                    entityData.setEntityID(entityliving.entityId);
                    entityData.setEntityMod(modData);
                }
                undefined = false;
            }
            else { // custom
                for (Map.Entry<String, EntityModData> modEntry : entityModMap.entrySet())
                {
                    if (entityClass.contains(modEntry.getKey()))
                    {
                        // Found match, use config
                        EntityModData modData = modEntry.getValue();
                        //System.out.println("Matched mod " + modEntry.getKey() + " to " + entityClass);
                        if (debug) CMSLog.logger.info("Matched mod " + modEntry.getKey() + " to " + entityClass);
                        entityData.setEntityMod(modData);
                        entityConfig = modData.getModConfig();
                        entityData.setEntityConfig(entityConfig);
                        processEntityConfig(entityData);
                        if (!modData.addCreature(entityData))
                        {
                            entityData = modData.getCreature(entityData.getLivingSpawnType(), entityName);
                            entityData.setEntityID(entityliving.entityId);
                            entityData.setEntityMod(modData);
                        }
                        undefined = false;
                        break;
                    }
                }
            }
            if (undefined)
            {
                if (debug) CMSLog.logger.info("Detected Undefined Entity Class " + entityClass + ". You must add a mapping for this class in MoCGlobal.cfg.");
                String tokens[] = entityClass.split("\\.");

                // no mapping for class in config so lets generate one
                if (tokens.length >= 2)
                {
                    String modKey = tokens[0];
                    if (modKey.equalsIgnoreCase("mod") || modKey.equalsIgnoreCase("mods") || modKey.equalsIgnoreCase("mob") || modKey.equalsIgnoreCase("mobs") || modKey.equalsIgnoreCase("com") || modKey.equalsIgnoreCase("coms") || modKey.equalsIgnoreCase("net"))
                        modKey = tokens[1];
                    else if (entityClass.contains("net.minecraft") && tokens.length > 2)
                        modKey = tokens[2];
                    String configName = modKey + ".cfg";

                    biomeModMap.put(modKey, new BiomeModData(modKey, modKey, new CMSConfiguration(new File(CMSGlobalConfig.file.getParent(), MOD_BIOME_FILE_ROOT + configName))));
                    if (debug) CMSLog.logger.info("AddedAutomatic Mod Biome Mapping " + modKey + " with tag " + modKey + " to file " + configName);
                    entityModMap.put(modKey, new EntityModData(modKey, modKey, new CMSConfiguration(new File(CMSGlobalConfig.file.getParent(), MOD_CREATURES_FILE_ROOT + configName))));
                    if (debug) CMSLog.logger.info("Added Automatic Mod Entity Mapping " + modKey + " to file " + configName);
                    tagConfigMap.put(modKey, biomeModMap.get(modKey));
                    CMSConfigCategory modMapCat = CMSGlobalConfig.getCategory(CATEGORY_MOD_MAPPINGS);
                    modMapCat.put(modKey, new CMSProperty(modKey, new ArrayList(Arrays.asList(modKey.toUpperCase(), configName)), CMSProperty.Type.STRING, "automatically generated"));

                    EntityModData modData = entityModMap.get(modKey);
                    if (debug) CMSLog.logger.info("Matched mod " + modKey + " to " + entityClass);
                    entityData.setEntityMod(modData);
                    entityConfig = modData.getModConfig();
                    entityData.setEntityConfig(entityConfig);
                    processEntityConfig(entityData);
                    if (!modData.addCreature(entityData))
                    {
                        entityData = modData.getCreature(entityData.getLivingSpawnType(), entityName);
                        entityData.setEntityID(entityliving.entityId);
                        entityData.setEntityMod(modData);
                    }
                }
                else 
                {
                    if (debug) CMSLog.logger.info("Could not generate an automatic mod mapping for entity " + entityClass);
                    return null;
                }
            }
            classToEntityMapping.put(clazz, entityData); // store for structure use
            entityMap.put(entityData.getEntityMod().getModTag() + "|" + entityData.getEntityName(), entityData); // used for fast lookups
            instaSpawnerMap.put(entityliving.entityId, entityliving.getClass()); // used for instaspawner
            return entityData;
        }
        return null;
    }

    public void processEntityConfig(EntityData entityData)
    {
        String entityName = entityData.getEntityName();
        CMSConfigCategory entityCategory = null;
        if (!entityData.getEntityConfig().hasCategory(entityName))//getCategory(CATEGORY_ENTITY_SPAWN_SETTINGS).containsKey(entityName))
        {
            // generate default entity settings
            entityCategory = entityData.getEntityConfig().getCategory(entityName);
            entityCategory.put("type", new CMSProperty("type", entityData.getLivingSpawnType().getLivingSpawnTypeName().toUpperCase(), CMSProperty.Type.STRING));
            entityCategory.put("canspawn", new CMSProperty("canspawn", Boolean.toString(entityData.getCanSpawn()), CMSProperty.Type.BOOLEAN));
            entityCategory.put("frequency", new CMSProperty("frequency", Integer.toString(entityData.getFrequency()), CMSProperty.Type.INTEGER));
            entityCategory.put("min", new CMSProperty("min", Integer.toString(entityData.getMinSpawn()), CMSProperty.Type.INTEGER));
            entityCategory.put("max", new CMSProperty("max", Integer.toString(entityData.getMaxSpawn()), CMSProperty.Type.INTEGER));
            entityCategory.put("maxchunk", new CMSProperty("maxchunk", Integer.toString(entityData.getMaxInChunk()), CMSProperty.Type.INTEGER));
            entityCategory.put("minY", new CMSProperty("minY", Integer.toString(entityData.getMinY()), CMSProperty.Type.INTEGER));
            entityCategory.put("maxY", new CMSProperty("maxY", Integer.toString(entityData.getMaxY()), CMSProperty.Type.INTEGER));
            entityCategory.put("spawnblockblacklist", new CMSProperty("spawnblockblacklist", entityData.getSpawnBlockBlacklist(), CMSProperty.Type.STRING));
            //entityData.getEntityConfig().get(CATEGORY_ENTITY_SPAWN_SETTINGS, entityName, new ArrayList(Arrays.asList( entityData.getLivingSpawnType().getLivingSpawnTypeName(), new Integer(defaultSpawnListEntryMap.get(clazz.getName()) == null ? frequency : defaultSpawnListEntryMap.get(clazz.getName()).itemWeight).toString(), new Integer(defaultSpawnListEntryMap.get(clazz.getName()) == null ? minGroup : defaultSpawnListEntryMap.get(clazz.getName()).minGroupCount).toString(), new Integer(defaultSpawnListEntryMap.get(clazz.getName()) == null ? maxGroup : defaultSpawnListEntryMap.get(clazz.getName()).maxGroupCount).toString(), new Integer(entityData.getMaxInChunk()).toString())));
        }
        else 
        {
            entityCategory = entityData.getEntityConfig().getCategory(entityName);
            for (Map.Entry<String, CMSProperty> propEntry : entityCategory.getValues().entrySet())
            {
                    // handle entity config
                    CMSProperty property = propEntry.getValue();
                    if (property != null)
                    {
                        if (propEntry.getKey().equalsIgnoreCase("type"))
                        {
                            entityData.setLivingSpawnType(entitySpawnTypes.get(property.value.toUpperCase()));
                        }
                        else if (propEntry.getKey().equalsIgnoreCase("canspawn"))
                        {
                            entityData.setCanSpawn(Boolean.parseBoolean(property.value));
                            if (entityData.getLivingSpawnType().getSpawnCap() == 0 || !entityData.getLivingSpawnType().allowSpawning())
                            {
                                entityData.setCanSpawn(false);
                            }
                            if (entityData.getLivingSpawnType().getLivingSpawnTypeName().equalsIgnoreCase("UNDEFINED") || entityData.getFrequency() <= 0 || entityData.getMaxSpawn() <= 0 || entityData.getMaxInChunk() <= 0)
                            {
                                entityData.setCanSpawn(false);
                            }
                        }
                        else if (propEntry.getKey().equalsIgnoreCase("frequency"))
                        {
                            entityData.setFrequency(Integer.parseInt(property.value));
                        }
                        else if (propEntry.getKey().equalsIgnoreCase("min"))
                        {
                            entityData.setMinSpawn(Integer.parseInt(property.value));
                        }
                        else if (propEntry.getKey().equalsIgnoreCase("max"))
                        {
                            entityData.setMaxSpawn(Integer.parseInt(property.value));
                        }
                        else if (propEntry.getKey().equalsIgnoreCase("maxchunk"))
                        {
                            entityData.setMaxInChunk(Integer.parseInt(property.value));
                        }
                        else if (propEntry.getKey().equalsIgnoreCase("spawnblockblacklist") && property.valueList != null)
                        {
                            for (String bannedBlock : property.valueList)
                            {
                                // check for single id
                                try
                                {
                                    int blockID = Integer.parseInt(bannedBlock.substring(0, bannedBlock.indexOf("-")));
                                    int blockMeta = Integer.parseInt(bannedBlock.substring(bannedBlock.indexOf("-"), bannedBlock.length()));
                                    String block = Integer.toString(blockID) + "-" + Integer.toString(blockMeta);
                                    entityData.addSpawnBlockToBanlist(block);
                                    if (debug) CMSLog.logger.info("Added spawnblock " + bannedBlock + " to blacklist.");
                                }
                                catch (Throwable e)
                                {
                                    if (debug) CMSLog.logger.info("Failed to blacklist spawnblock " + bannedBlock + ", invalid format. Format needs to be id-meta");
                                }
                            }
                        }
                    }
                    //entityData.getEntityConfig().save();
                    /*else if (debug)
                    {
                        CMSLog.logger.severe("Detected invalid frequency value for entity " + entityName);
                    }*/
                    //entityData.setEntityName(entityName);
            }
        }
        CMSGlobalConfig.save();
        // Add spawnable biomes for each entity
        if (entityCategory.containsKey("biomegroups"))// && entityBiomeCategory.get(entityName).valueList != null)
        {
            System.out.println("FOUND CATEGORY " + entityName);
            //CMSConfigCategory entityBiomeCategory = entityData.getEntityConfig().getCategory(entityName);
            List<String> biomeGroups = entityCategory.get("biomegroups").valueList;
            for (int i = 0; i < biomeGroups.size(); i++)
            {
                String biomeGroupName = biomeGroups.get(i);
                List<BiomeGenBase> spawnBiomes = new ArrayList<BiomeGenBase>();
                // check default entity biome config
                if (defaultBiomeGroupConfig.getCategory(CATEGORY_BIOMEGROUP_DEFAULTS).containsKey(entityData.getEntityMod().getModTag() + "_" + biomeGroupName))
                {
                    biomeGroupName = entityData.getEntityMod().getModTag() + "_" + biomeGroups.get(i);
                    //System.out.println("FOUND ENTITY DEFAULT BIOME GROUP " + biomeGroupName);
                    CMSProperty biomeProps = defaultBiomeGroupConfig.getCategory(CATEGORY_BIOMEGROUP_DEFAULTS).get(biomeGroupName);
                    for (int j = 0; j < biomeProps.valueList.size(); j++)
                    {
                        List<String> biomeParts = parseName(biomeProps.valueList.get(j));
                        BiomeModData biomeModData = tagConfigMap.get(biomeParts.get(0));
                        if (biomeModData != null)
                        {
                            spawnBiomes.add(biomeModData.getBiome(biomeProps.valueList.get(j)));
                            entityData.addSpawnBiome(biomeModData.getBiome(biomeProps.valueList.get(j)));
                        }
                    }
                    entityData.addBiomeGroupSpawnMap(biomeGroupName, spawnBiomes);
                }
                else // search for group in biome mod configs
                {
                    //System.out.println("Did not find biomegroup " + biomeGroupName + " in default config");
                    for (Map.Entry<String, BiomeModData> modEntry : biomeModMap.entrySet())
                    {
                        BiomeModData biomeModData = modEntry.getValue();
                        CMSConfigCategory cat = biomeModData.getModConfig().getCategory("biomegroups");
                        if (cat.containsKey(biomeGroupName))
                        {
                            CMSProperty biomeProps = cat.get(biomeGroupName);
                            for (int j = 0; j < biomeProps.valueList.size(); j++)
                            {
                                //System.out.println("found biome " + biomeProps.valueList.get(j) + " in config " + biomeModData.getBiomeModKey());
                                if (spawnBiomes.contains(biomeModData.getBiome(biomeModData.getModTag() + "|" + biomeProps.valueList.get(j))))
                                    continue;
                                BiomeGenBase biome = biomeModData.getBiome(biomeModData.getModTag() + "|" + biomeProps.valueList.get(j));
                                if (biome == null)
                                {
                                    continue;
                                }
                                spawnBiomes.add(biome);
                                entityData.addSpawnBiome(biome);
                            }
                        }
                    }
                    entityData.addBiomeGroupSpawnMap(biomeGroupName, spawnBiomes);
                }
            }
            entityData.setBiomeGroups(biomeGroups);
        }
        else // populate empty list so GUI can add to it
        {
            ArrayList<String> biomes = new ArrayList<String>();
            List<BiomeGenBase> spawnBiomes = new ArrayList<BiomeGenBase>();
            ArrayList<BiomeGenBase> entryBiomes = entityDefaultSpawnBiomes.get(entityData.getEntityClass().getName());
            if (entryBiomes != null)
            {
                for (int i = 0; i < entryBiomes.size(); i++)
                {
                    for (Map.Entry<String, BiomeModData> modEntry : biomeModMap.entrySet())
                    {
                        BiomeModData biomeModData = modEntry.getValue();
                        if (biomeModData.hasBiome(entryBiomes.get(i)))
                        {
                            biomes.add(biomeModData.getModTag() + "|" + entryBiomes.get(i).biomeName);
                            spawnBiomes.add(entryBiomes.get(i));
                        }
                    }
                }
            }
            entityData.setSpawnBiomes(spawnBiomes);
            CMSProperty prop = new CMSProperty(entityData.getEntityMod().getModTag() + "_" + entityName.toUpperCase() + "_DEFAULT", biomes, CMSProperty.Type.STRING);
            defaultBiomeGroupConfig.getCategory(CATEGORY_BIOMEGROUP_DEFAULTS).put(entityData.getEntityMod().getModTag() + "_" + entityName.toUpperCase() + "_DEFAULT", prop);
            //biomeModMap.put("entitydefaults", new MoCBiomeModData("entitydefaults", "entitydefaults", entityDefaultSpawnConfig));
            entityCategory.put("biomegroups", new CMSProperty("biomegroups", new ArrayList(Arrays.asList(entityName.toUpperCase() + "_DEFAULT")), CMSProperty.Type.STRING));
            entityData.setBiomeGroups(prop.valueList);
            biomeGroupMap.put(entityData.getEntityMod().getModTag() + "_" + entityName.toUpperCase() + "_DEFAULT", new BiomeGroupData(entityData.getEntityMod().getModTag() + "_" + entityName.toUpperCase() + "_DEFAULT", biomes));
        }
        // entity config comments
       // entityData.getEntityConfig().addCustomCategoryComment(CATEGORY_ENTITY_SPAWN_SETTINGS, "S:Name <Type:Frequency:MinSpawn:MaxSpawn:MaxSpawnInChunk>");
        //entityData.getEntityConfig().addCustomCategoryComment(CATEGORY_ENTITY_CANSPAWN_SETTINGS, "Set to false to disable entity spawns");
        entityData.getEntityConfig().save();
        defaultBiomeGroupConfig.save();
    }

    public void initializeBiomes()
    {
        biomeMap.clear();
        // initialize moc biome groups
        // populate default groups
        // copy map so we can alter main one
        for (BiomeDictionary.Type type : BiomeDictionary.Type.values())
        {
            for (BiomeGenBase biome : BiomeDictionary.getBiomesForType(type))
            {
                String biomeName = biome.biomeName;
                String biomeClass = biome.getClass().getName();
                BiomeData biomeData = new BiomeData(biome);
                biomeData.setType(type);
                if (debug) CMSLog.logger.info("Detected Biome " + biomeName + " with class " + biomeClass + " with biomeID " + biome.biomeID + " with type " + type.name());
                boolean found = false;
                BiomeModData biomeModData = null;
                for (Map.Entry<String, BiomeModData> modEntry : biomeModMap.entrySet())
                {
                    if (biomeClass.contains(modEntry.getKey()))
                    {
                        // Found match, use config
                        //System.out.println("Matched mod " + modEntry.getKey() + " to " + biomeClass);
                        BiomeModData modData = modEntry.getValue();
                        biomeData.setTag(modData.getModTag()); // needed for undefined
                        biomeData.setDefined((true));
                        if (!modData.getBiomes().contains(modData.getModTag() + "|" + biomeData.getBiomeName()))
                        {
                            modData.addBiome(biomeData);
                        }

                        biomeMap.put(modData.getModTag() + "|" + biomeName, biomeData);
                        found = true;
                        break;
                    }
                    else if ((biomeClass.contains("net.minecraft") || biomeClass.length() <= 3) && modEntry.getKey().equalsIgnoreCase("vanilla")) // special case for vanilla
                    {
                        if (debug) CMSLog.logger.info("Matched mod " + modEntry.getKey() + " to " + biomeClass);
                        BiomeModData modData = modEntry.getValue();
                        biomeData.setTag(modData.getModTag());
                        biomeData.setDefined((true));
                        if (!modData.getBiomes().contains(modData.getModTag() + "|" + biomeData.getBiomeName()))
                        {
                            modData.addBiome(biomeData);
                        }

                        biomeMap.put(modData.getModTag() + "|" + biomeName, biomeData);
                        found = true;
                        break;
                    }
                }
                if (!found)
                {
                    if (debug) CMSLog.logger.info("Detected Undefined Biome Class " + biomeClass + ". Generating automatic mapping for this class in CMSGlobal.cfg ...");
                    String tokens[] = biomeClass.split("\\.");

                    // no mapping for class in config so lets generate one
                    if (tokens.length >= 2)
                    {
                        String modKey = tokens[0];
                        if (modKey.equalsIgnoreCase("mod") || modKey.equalsIgnoreCase("mods") || modKey.equalsIgnoreCase("mob") || modKey.equalsIgnoreCase("mobs") || modKey.equalsIgnoreCase("com") || modKey.equalsIgnoreCase("coms") || modKey.equalsIgnoreCase("net"))
                            modKey = tokens[1];
                        else if (biomeClass.contains("net.minecraft") && tokens.length > 2)
                            modKey = tokens[2];
                        String configName = modKey + ".cfg";

                        BiomeModData modData =  new BiomeModData(modKey, modKey, new CMSConfiguration(new File(CMSGlobalConfig.file.getParent(), MOD_BIOME_FILE_ROOT + configName)));
                        biomeModMap.put(modKey, modData);
                        if (debug) CMSLog.logger.info("Added Automatic Mod Biome Mapping " + modKey + " with tag " + modKey + " to file " + configName);
                        tagConfigMap.put(modKey, modData);
                        CMSConfigCategory modMapCat = CMSGlobalConfig.getCategory(CATEGORY_MOD_MAPPINGS);
                        modMapCat.put(modKey, new CMSProperty(modKey, new ArrayList(Arrays.asList(modKey.toUpperCase(), configName)), CMSProperty.Type.STRING, "automatically generated"));
                        biomeData.setTag(modData.getModTag());
                        biomeData.setDefined((true));
                        if (!modData.getBiomes().contains(modData.getModTag() + "|" + biomeData.getBiomeName()))
                        {
                            modData.addBiome(biomeData);
                        }
                        biomeMap.put(modData.getModTag() + "|" + biomeName, biomeData);
                    }
                }
            }
        }
        // save configs
        for (Map.Entry<String, BiomeModData> modEntry : biomeModMap.entrySet())
        {
            for (BiomeDictionary.Type type : BiomeDictionary.Type.values())
            {
                BiomeModData modData = modEntry.getValue();
                CMSProperty prop = modData.getModConfig().get("biomegroups", "biomegroups");
                if (prop!= null && prop.valueList != null)
                {
                    prop.valueList = modData.getBiomes();
                }
                else {
                    modData.getModConfig().get("biomegroups", type.name(), modData.getBiomesForType(type));
                }
                modData.getModConfig().save();
            }
        }
        
        CMSGlobalConfig.addCustomCategoryComment("mod-mappings", "Mod Biome Mappings\n" + 
                "You may change tag values but do NOT change the default keys since they are used to generate our defaults.\n" +
                "For example, 'twilightforest=TL:TwilightForest.cfg' may be changed to 'twilightforest=TWL:TWL.cfg' but may NOT be changed to 'twilight=TWL:TWL.cfg'");
        // update tags in our defaults if necessary
        CMSGlobalConfig.save();
        initDefaultGroups();
       // mocBiomeConfig.save();
    }

    public void initDefaultGroups()
    {
        // scan all biome mods for biome dictionary groups
        for (BiomeDictionary.Type type : BiomeDictionary.Type.values())
        {
            List<String> biomes = new ArrayList<String>();
            for (BiomeGenBase biome : BiomeDictionary.getBiomesForType(type))
            {
                for (Map.Entry<String, BiomeModData> modEntry : biomeModMap.entrySet())
                {
                    BiomeModData biomeModData = modEntry.getValue();
                   // System.out.println("Checking biome mod " + biomeModData.getBiomeModKey());
                    if (biomeModData.hasBiome(biome))
                    {
                        biomes.add(biomeModData.getModTag() + "|" + biome.biomeName);
                        break;
                    }
                }
            }
            CMSProperty prop = new CMSProperty(type.name(), biomes, CMSProperty.Type.STRING);
            if (!biomes.isEmpty())
            {
                if (biomeGroupMap.containsKey(type.name()))
                    biomeGroupMap.remove(type.name());
                biomeGroupMap.put(type.name(), new BiomeGroupData(type.name(), biomes));
                Collections.sort(biomes); // sort biome groups for GUI
                prop.valueList = biomes; // blood - make sure to link our newly generated list to the configuration list for direct modification later
                if (debug) CMSLog.logger.info("Successfully added Biome Group " + type.name());
            }
            defaultBiomeGroupConfig.getCategory(CATEGORY_BIOMEGROUP_DEFAULTS).put(type.name(), prop);
            defaultBiomeGroupConfig.save();
        }
        // add any biomes that we may have missed to biome group UNDEFINED
        if (debug) CMSLog.logger.info("Scanning biomeMap for biomes not found in MoCBiomeGroups.cfg ...");
        BiomeModData biomeModData = biomeModMap.get("undefined");
        for (Map.Entry<String, BiomeData> biomeEntry : biomeMap.entrySet())
        {
            BiomeData biomeData = biomeEntry.getValue();
            if (!biomeData.isDefined())
            {
                biomeModData.addBiome(biomeData);
                if (debug) CMSLog.logger.warning("Biome " + biomeData.getBiomeName() + " was NOT DEFINED, Added biome to UNDEFINED BIOME GROUP");
            }
            else if (debug) CMSLog.logger.info("Biome " + biomeData.getBiomeName() + " was DEFINED");
        }
        if (biomeModData.getBiomes().size() > 0)
        {
            CMSConfigCategory cat = biomeModData.getModConfig().getCategory("biomegroups");
            for (CMSProperty prop : cat.values())
            {
                if (prop != null)
                {
                    prop.valueList = biomeModData.getBiomes();
                }
            }
            biomeModData.getModConfig().save();
        }
    }

    public List<String> parseName(String biomeConfigEntry)
    {
        String tag = biomeConfigEntry.substring(0, biomeConfigEntry.indexOf('|'));
        String biomeName = biomeConfigEntry.substring(biomeConfigEntry.indexOf('|') + 1, biomeConfigEntry.length());
        List<String> biomeParts = new ArrayList();
        biomeParts.add(tag);
        biomeParts.add(biomeName);
        return biomeParts;
    }

    protected static ChunkPosition getRandomSpawningPointInChunk(World worldObj, int par1, int par2)
    {
        Chunk chunk = worldObj.getChunkFromChunkCoords(par1, par2);
        int i = par1 * 16 + worldObj.rand.nextInt(16);
        int j = worldObj.rand.nextInt(chunk == null ? worldObj.getActualHeight() : chunk.getTopFilledSegment() + 16 - 1);
        int k = par2 * 16 + worldObj.rand.nextInt(16);
        return new ChunkPosition(i, j, k);
    }

    public void clearLists()
    {
        for (int i = 0; i < biomeList.size(); i++)
        {
            for (EntitySpawnType entitySpawnType : entitySpawnTypes.values())
            {
                entitySpawnType.getLivingSpawnList().get(biomeList.get(i).biomeID).clear();
            }
        }
    }

    // Spigot start - get entity count only from chunks being processed in theChunkProviderServer
    public static final int getEntityCount(WorldServer server, EntitySpawnType entitySpawnType)
    {
        int i = 0;
        Iterator iterator = eligibleChunksForSpawning.keySet().iterator();
        while (iterator.hasNext())
        {
            ChunkCoordIntPair chunkcoordintpair1 = (ChunkCoordIntPair)iterator.next();

            if (server.theChunkProviderServer.chunkExists(chunkcoordintpair1.chunkXPos, chunkcoordintpair1.chunkZPos))
            {
                for (List<Entity> entitySlice : server.getChunkFromChunkCoords(chunkcoordintpair1.chunkXPos, chunkcoordintpair1.chunkZPos).entityLists)
                {
                    for (Entity entity : entitySlice)
                    {
                        /*if (entity.isCreatureType(type, true))
                        {
                            ++i;
                        }*/
                        EntityData entityData = classToEntityMapping.get(entity.getClass());
                        if (entityData != null && entityData.getLivingSpawnType() == entitySpawnType)
                        {
                            ++i;
                        }
                    }
                }
            }
        }
        return i;
    }
    // Spigot end

    /**
     * New customSpawner
     * 
     * 
     */
    public final int doCustomSpawning(WorldServer worldObj, EntitySpawnType entitySpawnType, int mobSpawnRange, int lightLevel, boolean checkAmbientLightLevel, boolean enforceMaxSpawnLimits)
    {
        eligibleChunksForSpawning.clear();
        int countTotal;
        int var6;

        byte spawnRadius = 8;
        for (countTotal = 0; countTotal < worldObj.playerEntities.size(); ++countTotal)
        {
            EntityPlayer entityplayer = (EntityPlayer) worldObj.playerEntities.get(countTotal);
            int var5 = MathHelper.floor_double(entityplayer.posX / 16.0D);
            var6 = MathHelper.floor_double(entityplayer.posZ / 16.0D);
           // spawnRadius = 8;
            spawnRadius = (byte) mobSpawnRange;
            // Spigot Start

            spawnRadius = (spawnRadius > FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().getViewDistance() ) ? (byte) FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().getViewDistance() : spawnRadius;
            spawnRadius = ( spawnRadius > 8 ) ? 8 : spawnRadius;
            // Spigot End

            for (int var8 = -spawnRadius; var8 <= spawnRadius; ++var8)
            {
                for (int var9 = -spawnRadius; var9 <= spawnRadius; ++var9)
                {
                    boolean var10 = var8 == -spawnRadius || var8 == spawnRadius || var9 == -spawnRadius || var9 == spawnRadius;
                    ChunkCoordIntPair var11 = new ChunkCoordIntPair(var8 + var5, var9 + var6);

                    if (!var10)
                    {
                        eligibleChunksForSpawning.put(var11, Boolean.valueOf(false));
                    }
                    else if (!eligibleChunksForSpawning.containsKey(var11))
                    {
                        eligibleChunksForSpawning.put(var11, Boolean.valueOf(true));
                    }
                }
            }
        }

        countTotal = 0;
        ChunkCoordinates chunkcoordspawn = worldObj.getSpawnPoint();
        EnumCreatureType[] enumcreaturevalues = EnumCreatureType.values();
        var6 = enumcreaturevalues.length;
        int limit = 0;

        limit = entitySpawnType.getSpawnCap();
        //System.out.println("livingSpawn name = " + entitySpawnType.getLivingSpawnTypeName());
        //System.out.println("limit = " + limit);

        if (limit == 0)
        {
            return 0;
        }

        int mobcnt = 0;

        //modified to allow custom creature counts instead of vanillas
        //System.out.println("countEntity = " + worldObj.countEntities(enumcreaturetype, true) + ", max = " + (getMax(enumcreaturetype) * eligibleChunksForSpawning.size() / 256));
        //System.out.println("getEntityCount = " + getEntityCount(worldObj, enumcreaturetype));
        //System.out.println("entitycount = " + getEntityCount(worldObj, entitySpawnType) + ", max = " + getMax(entitySpawnType));
        if ((mobcnt = getEntityCount(worldObj, entitySpawnType)) <= getMax(entitySpawnType) * eligibleChunksForSpawning.size() / 256)
        {
            Iterator iterator = eligibleChunksForSpawning.keySet().iterator();
            ArrayList<ChunkCoordIntPair> tmp = new ArrayList(eligibleChunksForSpawning.keySet());
            Collections.shuffle(tmp);
            iterator = tmp.iterator();
            int moblimit = (limit * eligibleChunksForSpawning.size() / 256) - mobcnt + 1;
            //System.out.println("moblimit = " + moblimit);
            label108: while (iterator.hasNext() && moblimit > 0)
            {
                ChunkCoordIntPair chunkcoordintpair = (ChunkCoordIntPair) iterator.next();
                if (eligibleChunksForSpawning.get(chunkcoordintpair) != null && !((Boolean) eligibleChunksForSpawning.get(chunkcoordintpair)).booleanValue()) // blood - added null check to avoid crashing during SSP spawning
                {
                    ChunkPosition chunkpos = getRandomSpawningPointInChunk(worldObj, chunkcoordintpair.chunkXPos, chunkcoordintpair.chunkZPos);
                    int posX = chunkpos.x;
                    int posY = chunkpos.y;
                    int posZ = chunkpos.z;
                    //System.out.println("entitySpawnType material = " + entitySpawnType.getLivingMaterial());

                    if (!worldObj.isBlockNormalCube(posX, posY, posZ) && worldObj.getBlockMaterial(posX, posY, posZ) == entitySpawnType.getLivingMaterial())
                    {
                        //System.out.println("entitySpawnType material = " + entitySpawnType.getLivingMaterial());
                        int spawnedMob = 0;
                        int spawnCount = 0;

                        while (spawnCount < 3)
                        {
                            int tempX = posX;
                            int tempY = posY;
                            int tempZ = posZ;
                            byte var20 = 6;
                            SpawnListEntry spawnlistentry = null;
                            int spawnAttempt = 0;

                            while (true)
                            {
                                if (spawnAttempt < 4)

                                {
                                    label101:
                                    {
                                        tempX += worldObj.rand.nextInt(var20) - worldObj.rand.nextInt(var20);
                                        tempY += worldObj.rand.nextInt(1) - worldObj.rand.nextInt(1);
                                        tempZ += worldObj.rand.nextInt(var20) - worldObj.rand.nextInt(var20);

                                        //if(canCreatureTypeSpawnAtLocation(enumcreaturetype, worldObj, tempPosX, tempPosY, tempPosZ))
                                        if (canCreatureTypeSpawnAtLocation(entitySpawnType, worldObj, tempX, tempY, tempZ))
                                        {
                                            float spawnX = (float) tempX + 0.5F;
                                            float spawnY = (float) tempY;
                                            float spawnZ = (float) tempZ + 0.5F;
                                            if (worldObj.getClosestPlayer((double) spawnX, (double) spawnY, (double) spawnZ, 24.0D) == null)
                                            {
                                                float var26 = spawnX - (float) chunkcoordspawn.posX;
                                                float var27 = spawnY - (float) chunkcoordspawn.posY;
                                                float var28 = spawnZ - (float) chunkcoordspawn.posZ;
                                                float spawnDist = var26 * var26 + var27 * var27 + var28 * var28;
                                                if (spawnDist >= 576.0F)
                                                {
                                                    if (spawnlistentry == null)
                                                    {
                                                        //this is where it has to be changed to include the custom list
                                                        //spawnlistentry = worldObj.getRandomMob(enumcreaturetype, tempX, tempY, tempZ);
                                                        spawnlistentry = getRandomCustomMob(worldObj, entitySpawnType, tempX, tempY, tempZ);
                                                        if (spawnlistentry == null)
                                                        {
                                                            break label101;
                                                        }
                                                    }

                                                    EntityLiving entityliving;
                                                    EntityData entityData;

                                                    try
                                                    {
                                                        entityliving = (EntityLiving) spawnlistentry.entityClass.getConstructor(new Class[] { World.class }).newInstance(new Object[] { worldObj });
                                                    }
                                                    catch (Exception exception)
                                                    {
                                                        exception.printStackTrace();
                                                        return countTotal;
                                                    }
                                                    entityData = classToEntityMapping.get(spawnlistentry.entityClass);
                                                    //System.out.println("attempting to spawn " + entityData.getEntityName());
                                                    // spawn checks
                                                    entityliving.setLocationAndAngles((double) spawnX, (double) spawnY, (double) spawnZ, worldObj.rand.nextFloat() * 360.0F, 0.0F);
                                                    Result canSpawn = ForgeEventFactory.canEntitySpawn(entityliving, worldObj, spawnX, spawnY, spawnZ);
                                                    //System.out.println("canSpawn = " + canSpawn);
                                                    if (/*isValidLightLevel(entityliving, worldObj, lightLevel, checkAmbientLightLevel) && */ (canSpawn == Result.ALLOW || (canSpawn == Result.DEFAULT && entityliving.getCanSpawnHere())))
                                                    {
                                                        ++spawnedMob;
                                                        //System.out.println("SPAWNED ENTITY " + entityliving);
                                                        worldObj.spawnEntityInWorld(entityliving);
                                                        creatureSpecificInit(entityliving, worldObj, spawnX, spawnY, spawnZ);
                                                        // changed check from maxSpawnedInChunk to maxGroupCount.
                                                       // System.out.println("spawned " + entityliving + ", spawnedMob = " + spawnedMob + ", maxGroupCount = " + spawnlistentry.maxGroupCount);
                                                        if (debug) CMSLog.logger.info("[" + entityData.getLivingSpawnType().getLivingSpawnTypeName().toUpperCase() + " TICKHANDLER:" + spawnedMob + "]:[spawned " + entityliving.getEntityName() + " at " + spawnX + ", " + spawnY + ", " + spawnZ + " with " + entityData.getLivingSpawnType().getLivingSpawnTypeName().toUpperCase() + ":" + spawnlistentry.itemWeight + ":" + spawnlistentry.minGroupCount + ":" + spawnlistentry.maxGroupCount + ":" + ForgeEventFactory.getMaxSpawnPackSize(entityliving) + " in biome " + worldObj.getBiomeGenForCoords((chunkcoordintpair.chunkXPos * 16) + 16, (chunkcoordintpair.chunkZPos * 16) + 16).biomeName + "]:[spawns left in limit " + moblimit + "]:[enforceMaxSpawnLimits:" + enforceMaxSpawnLimits + "]");

                                                        if (spawnedMob >= ForgeEventFactory.getMaxSpawnPackSize(entityliving))
                                                        {
                                                            continue label108;
                                                        }
                                                        if (enforceMaxSpawnLimits)
                                                        {
                                                            moblimit--;
                                                           // System.out.println("moblimit now = " + moblimit);
                                                            if (moblimit <= 0)  // If we're past limit, stop spawn
                                                            {
                                                                continue label108;
                                                            }
                                                        }
                                                    }
                                                    else
                                                    {
                                                        //if (verboseConsole) CMSLog.logger.info("unable to spawn " + entityliving + " at coords " + var26 + ", " + var27 + ", " + var28);
                                                    }
                                                    countTotal += spawnedMob;
                                                }
                                            }
                                        }

                                        ++spawnAttempt;
                                        continue;
                                    }
                                }

                                ++spawnCount;
                                break;
                            }
                        }
                    }
                }
            }
        }

        return countTotal;
    }

    public static void RegisterCustomEntity(String tag, EntityData data)
    {
        entityMap.put(tag + "|" + data.getEntityName(), data);
    }

    public void AddCustomSpawn(Class class1, int i, int max, EntitySpawnType entitySpawnType)
    {
        AddCustomSpawn(class1, i, 4, max, entitySpawnType, null);
    }

    public void AddCustomSpawn(Class class1, int i, EntitySpawnType entitySpawnType)
    {
        AddCustomSpawn(class1, i, 4, 5, entitySpawnType, null);
    }

    public void AddCustomSpawn(Class class1, int i, int max, EntitySpawnType entitySpawnType, BiomeGenBase abiomegenbase[])
    {
        AddCustomSpawn(class1, i, 4, max, entitySpawnType, abiomegenbase);
    }

    public void AddCustomSpawn(Class class1, int i, EntitySpawnType entitySpawnType, BiomeGenBase abiomegenbase[])
    {
        AddCustomSpawn(class1, i, 4, 5, entitySpawnType, abiomegenbase);
    }

    //this one adds spawn where biome is not specified
    public void AddCustomSpawn(Class class1, int i, int j, int k, EntitySpawnType entitySpawnType)
    {
        AddCustomSpawn(class1, i, j, k, entitySpawnType, null);
    }

    public void AddCustomSpawn(Class class1, int i, int j, int k, EntitySpawnType entitySpawnType, BiomeGenBase abiomegenbase[])
    {
        if (debug) CMSLog.logger.info("AddCustomSpawn class " + class1 + ", with " + i + ":" + j + ":" + k + "  type = " + entitySpawnType);
        if (class1 == null) { throw new IllegalArgumentException("entityClass cannot be null"); }
        if (entitySpawnType == null) { throw new IllegalArgumentException("spawnList cannot be null"); }
        if (abiomegenbase == null)
        {
            abiomegenbase = new BiomeGenBase[biomeList.size()];
            abiomegenbase = biomeList.toArray(abiomegenbase);
        }

        for (BiomeGenBase biome : abiomegenbase)
        {
            if (biome == null)
                continue;
            ArrayList<SpawnListEntry> spawnList = entitySpawnType.getBiomeSpawnList(biome.biomeID);
            if (spawnList != null)
            {
                boolean flag = false;
                // check to see if spawn entry already exists
                for (SpawnListEntry spawnEntry : spawnList)
                {
                    if (spawnEntry.entityClass == class1)
                    {
                        spawnEntry.itemWeight = i;
                        spawnEntry.minGroupCount = j;
                        spawnEntry.maxGroupCount = k;
                        flag = true;
                        break;
                    }
                }

                if (!flag)
                {
                    //System.out.println("added spawnlist entry " + class1 + " for biome " + biome.biomeName);
                    spawnList.add(new SpawnListEntry(class1, i, j, k));
                }
            }
        }

    }

    public void RemoveCustomSpawn(Class class1, EntitySpawnType entitySpawnType)
    {
        RemoveCustomSpawn(class1, entitySpawnType, null);
    }

    public void RemoveCustomSpawn(Class class1, EntitySpawnType entitySpawnType, BiomeGenBase abiomegenbase[])
    {
        if (class1 == null) { throw new IllegalArgumentException("entityClass cannot be null"); }
        //if (enumcreaturetype == null) { throw new IllegalArgumentException("spawnList cannot be null"); }
        if (abiomegenbase == null)
        {
            abiomegenbase = new BiomeGenBase[biomeList.size()];
            abiomegenbase = biomeList.toArray(abiomegenbase);
        }

        for (BiomeGenBase biome : abiomegenbase)
        {
            ArrayList<SpawnListEntry> spawnList = entitySpawnType.getBiomeSpawnList(biome.biomeID);

            if (spawnList != null)
            {
                Iterator iterator = spawnList.iterator();
                while (iterator.hasNext())
                {
                    SpawnListEntry spawnEntry = (SpawnListEntry)iterator.next();
                    if (spawnEntry.entityClass == class1)
                    {
                        iterator.remove();
                        break;
                    }
                }
            }
        }
    }

    public void copyVanillaSpawnLists(BiomeGenBase abiomegenbase[])
    {
        if (abiomegenbase == null)
        {
            abiomegenbase = new BiomeGenBase[biomeList.size()];
            abiomegenbase = biomeList.toArray(abiomegenbase);
        }
       // System.out.println("abiomegenbase size = " + abiomegenbase.length);
        for (BiomeGenBase biome : abiomegenbase)
        {
           // System.out.println("Checking biome " + element);
            for (EntitySpawnType entitySpawnType : entitySpawnTypes.values())
            {
                if (entitySpawnType.getEnumCreatureType() == null)
                    continue;
                ArrayList<SpawnListEntry> spawnList = entitySpawnType.getBiomeSpawnList(biome.biomeID);
                if (spawnList != null)
                {
                       // System.out.println("element.getSpawnableList for type " + enumType + " = " + element.getSpawnableList(enumType));
                    for (Iterator iterator = biome.getSpawnableList(entitySpawnType.getEnumCreatureType()).iterator(); iterator.hasNext();)
                    {
                        if (iterator != null)
                        {
                            SpawnListEntry spawnlistentry = (SpawnListEntry) iterator.next();
                            if (BiomeDictionary.isBiomeRegistered(biome))
                            {
                                if (entityDefaultSpawnBiomes.containsKey(spawnlistentry.entityClass.getName()))
                                {
                                    if (!entityDefaultSpawnBiomes.get(spawnlistentry.entityClass.getName()).contains(biome))
                                    {
                                        //System.out.println("Adding new biome " + biome.biomeName + " for existing entity " + spawnlistentry.entityClass);
                                        entityDefaultSpawnBiomes.get(spawnlistentry.entityClass.getName()).add(biome);
                                    }
                                }
                                else
                                {
                                    ArrayList<BiomeGenBase> biomes = new ArrayList<BiomeGenBase>();
                                    biomes.add(biome);
                                    //System.out.println("2Adding new biome " + biome.biomeName + " for entity " + spawnlistentry.entityClass);
                                    entityDefaultSpawnBiomes.put(spawnlistentry.entityClass.getName(), biomes);
                                    if (!defaultSpawnListEntryMap.containsKey(spawnlistentry.entityClass.getName()))
                                    {
                                        defaultSpawnListEntryMap.put(spawnlistentry.entityClass.getName(), spawnlistentry);
                                    }
                                }
                            }
                            iterator.remove();
                        }
                    }
                }
            }
        }
    }

    public void updateSpawnListEntry(Class<? extends EntityLiving> clazz, EntitySpawnType entitySpawnType, int freq, int min, int max)
    {
        for (int i = 0; i < biomeList.size(); i++)
        {
            ArrayList<SpawnListEntry> spawnList = entitySpawnType.getBiomeSpawnList(biomeList.get(i).biomeID);
            for (SpawnListEntry spawnEntry : spawnList)
            {
                if (spawnEntry.entityClass == clazz)
                {
                    if (debug) CMSLog.logger.info("updateSpawnListEntry " + clazz + " to " + freq + ":" + min + ":" + max + " in biome " + biomeList.get(i).biomeName);
                    spawnEntry.itemWeight = freq;
                    spawnEntry.minGroupCount = min;
                    spawnEntry.maxGroupCount = max;
                }
            }
        }
    }

    public void updateSpawnListBiomes(Class<? extends EntityLiving> clazz, EntitySpawnType entitySpawnType, int freq, int min, int max, List<BiomeGenBase> biomes)
    {
        if (biomes != null)
        {
            if (debug) CMSLog.logger.info("updateSpawnListBiomes for clazz " + clazz + " with " + freq + ":" + min + ":" + max + " in " + biomes);
            RemoveCustomSpawn(clazz, entitySpawnType, null);
            BiomeGenBase[] allBiomes = new BiomeGenBase[biomes.size()];
            AddCustomSpawn(clazz, freq, min, max, entitySpawnType, biomes.toArray(allBiomes));
        }
    }

    public ArrayList<SpawnListEntry> getCustomBiomeSpawnList(BiomeGenBase biome)
    {
        return entitySpawnTypes.get(EntitySpawnType.CREATURE).getBiomeSpawnList(biome.biomeID);
    }

    public int getMax(EntitySpawnType entitySpawnType)
    {
        return entitySpawnType.getSpawnCap();
    }

    public boolean isVerboseConsole() 
    {
        return debug;
    }

    public void setVerboseConsole(boolean flag) 
    {
        this.debug = flag;
    }

    /**
     * Returns whether or not the specified creature type can spawn at the specified location.
     */
    public static boolean canCreatureTypeSpawnAtLocation(EntitySpawnType entitySpawnType, World par1World, int par2, int par3, int par4)
    {
        if (entitySpawnType.getLivingMaterial() == Material.water)
        {
            return par1World.getBlockMaterial(par2, par3, par4).isLiquid() && par1World.getBlockMaterial(par2, par3 - 1, par4).isLiquid() && !par1World.isBlockNormalCube(par2, par3 + 1, par4);
        }
        else if (!par1World.doesBlockHaveSolidTopSurface(par2, par3 - 1, par4))
        {
            return false;
        }
        else
        {
            int l = par1World.getBlockId(par2, par3 - 1, par4);
            boolean spawnBlock;
            if (entitySpawnType.getEnumCreatureType() != null)
                spawnBlock = Block.blocksList[l].canCreatureSpawn(entitySpawnType.getEnumCreatureType(), par1World, par2, par3, par4);
            else spawnBlock = (Block.blocksList[l] != null && canCreatureSpawn(Block.blocksList[l], par1World, par2, par3, par4));
            return spawnBlock && l != Block.bedrock.blockID && !par1World.isBlockNormalCube(par2, par3, par4) && !par1World.getBlockMaterial(par2, par3, par4).isLiquid() && !par1World.isBlockNormalCube(par2, par3 + 1, par4);
        }
    }

    public static boolean canCreatureSpawn(Block block, World world, int x, int y, int z)
    {
        int meta = world.getBlockMetadata(x, y, z);
        if (block instanceof BlockStep)
        {
            return (((meta & 8) == 8) || block.isOpaqueCube());
        }
        else if (block instanceof BlockStairs)
        {
            return ((meta & 4) != 0);
        }
        return block.isBlockSolidOnSide(world, x, y, z, UP);
    }

    protected final int entityDespawnCheck(WorldServer worldObj, EntityLiving entityliving)
    {
        return entityDespawnCheck(worldObj, entityliving, 7);
    }

    //New DesPawner stuff
    protected final static int entityDespawnCheck(WorldServer worldObj, EntityLiving entityliving, int despawnLightLevel)
    {
        if (entityliving instanceof EntityWolf && ((EntityWolf) entityliving).isTamed()) { return 0; }
        if (entityliving instanceof EntityOcelot && ((EntityOcelot) entityliving).isTamed()) { return 0; }
        if (!isValidDespawnLightLevel(entityliving, worldObj, despawnLightLevel)) { return 0; }

        EntityPlayer entityplayer = worldObj.getClosestPlayerToEntity(entityliving, -1D);
        if (entityplayer != null) //entityliving.canDespawn() && 
        {
            double d = ((Entity) (entityplayer)).posX - entityliving.posX;
            double d1 = ((Entity) (entityplayer)).posY - entityliving.posY;
            double d2 = ((Entity) (entityplayer)).posZ - entityliving.posZ;
            double d3 = d * d + d1 * d1 + d2 * d2;
            if (d3 > 16384D)
            {
                entityliving.setDead();
                return 1;
            }
            if (entityliving.getAge() > 600 && worldObj.rand.nextInt(800) == 0)
            {
                if (d3 < 1024D)
                {
                    //entityliving.attackEntityFrom(DamageSource.generic, 0);
                }
                else
                {
                    entityliving.setDead();
                    return 1;
                }
            }
        }
        return 0;
    }

   public final int countEntities(Class class1, World worldObj)
    {
        int i = 0;
        for (int j = 0; j < worldObj.loadedEntityList.size(); j++)
        {
            Entity entity = (Entity) worldObj.loadedEntityList.get(j);
            if (class1.isAssignableFrom(entity.getClass()))
            {
                i++;
            }
        }

        return i;
    }

    public final static int despawnVanillaAnimals(WorldServer worldObj, int despawnLightLevel)
    {
        int count = 0;
        for (int j = 0; j < worldObj.loadedEntityList.size(); j++)
        {
            Entity entity = (Entity) worldObj.loadedEntityList.get(j);
            if (!(entity instanceof EntityLiving))
            {
                continue;
            }
            if ((entity instanceof EntityHorse || entity instanceof EntityCow || entity instanceof EntitySheep || entity instanceof EntityPig || entity instanceof EntityOcelot || entity instanceof EntityChicken || entity instanceof EntitySquid || entity instanceof EntityWolf || entity instanceof EntityMooshroom))
            {
                count += entityDespawnCheck(worldObj, (EntityLiving) entity, despawnLightLevel);
            }
        }
        return count;
    }

    public final int despawnMob(WorldServer worldObj)
    {
        List<Class> myNullList = null;
        return despawnMob(worldObj, myNullList);
    }

    public final int despawnMob(WorldServer worldObj, Class... classList)
    {
        List<Class> myList = new ArrayList();
        for (Class element : classList)
        {
            myList.add(element);
        }
        return despawnMob(worldObj, myList);
    }

    public final int despawnMob(WorldServer worldObj, List<Class> classList)
    {
        int count = 0;
        if (classList == null)
        {
            classList = vanillaClassList;
        }

        for (int j = 0; j < worldObj.loadedEntityList.size(); j++)
        {
            Entity entity = (Entity) worldObj.loadedEntityList.get(j);
            if (!(entity instanceof EntityLiving))
            {
                continue;
            }
            for (Iterator iterator = classList.iterator(); iterator.hasNext();)
            {
                if (iterator != null)
                {
                    Class class2 = (Class) iterator.next();
                    if (class2 == entity.getClass())
                    {
                        count += entityDespawnCheck(worldObj, (EntityLiving) entity);
                    }
                }
            }

        }
        return count;
    }

    public final int despawnMobWithMinimum(WorldServer worldObj, Class class1, int minimum)
    {
        int killedcount = 0;
        int mobcount = countEntities(class1, worldObj);
        for (int j = 0; j < worldObj.loadedEntityList.size(); j++)
        {
            if ((mobcount - killedcount) <= minimum)
            {
                worldObj.updateEntities();
                return killedcount;
            }
            Entity entity = (Entity) worldObj.loadedEntityList.get(j);
            if (!(entity instanceof EntityLiving))
            {
                continue;
            }
            if (class1 == entity.getClass())
            {
                killedcount += entityDespawnCheck(worldObj, (EntityLiving) entity);
            }
        }
        worldObj.updateEntities();
        return killedcount;

    }

    /**
     * Gets a random custom mob for spawning based on XYZ coordinates
     */
    public SpawnListEntry getRandomCustomMob(World worldObj, EntitySpawnType entitySpawnType, int pX, int pY, int pZ)
    {
        List list = getPossibleCustomCreatures(worldObj, entitySpawnType, pX, pY, pZ);
        if (list == null || list.isEmpty())
        {
            return null;
        }
        else
        {
            return (SpawnListEntry) WeightedRandom.getRandomItem(worldObj.rand, list);
        }
    }

    /**
     * Returns a list of creatures of the specified type (mob/aquatic/animal)
     * that can spawn at the given XYZ location, based on biomes.
     */
    public List getPossibleCustomCreatures(World worldObj, EntitySpawnType entitySpawnType, int pX, int pY, int pZ)
    {
        BiomeGenBase biomegenbase = worldObj.getBiomeGenForCoords(pX, pZ);
        if (biomegenbase == null)
        {
            return null;
        }
        else
        {
            //System.out.println("getting biomelist for biome " + biomegenbase.biomeName + ", size = " + entitySpawnType.getBiomeSpawnList(biomegenbase.biomeID).size());
            return entitySpawnType.getBiomeSpawnList(biomegenbase.biomeID);
        }
    }

    public static boolean isValidLightLevel(Entity entity, WorldServer worldObj, int lightLevel, boolean checkAmbientLightLevel)
    {
        if (entity.isCreatureType(EnumCreatureType.monster, false)) // ignore monsters since monsters should be checking ValidLightLevel
            return true;
        else if (entity.isCreatureType(EnumCreatureType.ambient, false) && !checkAmbientLightLevel)
        {
            return true;
        }
        else if (!entity.isCreatureType(EnumCreatureType.creature, false))
        {
            return true;
        }
        int x = MathHelper.floor_double(entity.posX);
        int y = MathHelper.floor_double(entity.boundingBox.minY);
        int z = MathHelper.floor_double(entity.posZ);
        int i = 0;
        if (y >= 0)
        {
            if (y >= 256)
            {
                y = 255;
            }
            i = getBlockLightValue(worldObj.getChunkFromChunkCoords(x >> 4, z >> 4), x & 15, y, z & 15);
        }
        if (i > lightLevel)
        {
            if (debug) CMSLog.logger.info("Denied spawn! for " + entity.getEntityName() + ". LightLevel over threshold of " + lightLevel + " in dimension " + worldObj.provider.dimensionId + " at coords " + x + ", " + y + ", " + z);
        }
        return i <= lightLevel;
    }
    
    protected static boolean isValidDespawnLightLevel(Entity entity, WorldServer worldObj, int despawnLightLevel)
    {
        int x = MathHelper.floor_double(entity.posX);
        int y = MathHelper.floor_double(entity.boundingBox.minY);
        int z = MathHelper.floor_double(entity.posZ);
        int i = 0;
        if (y >= 0)
        {
            if (y >= 256)
            {
                y = 255;
            }
            i = getBlockLightValue(worldObj.getChunkFromChunkCoords(x >> 4, z >> 4), x & 15, y, z & 15);
        }
        if (i > despawnLightLevel)
        {
            if (debug) CMSLog.logger.info("Denied despawn! for vanilla " + entity.getEntityName() + ". LightLevel over threshold of " + despawnLightLevel + " in dimension " + worldObj.provider.dimensionId + " at coords " + x + ", " + y + ", " + z);
        }
        /*else {
            if (verboseConsole) CMSLog.logger.info("Valid LightLevel " + i + " found. Proceeding to despawn vanilla " + entity.getEntityName() + " at " + x + ", " + y + ", " + z);
        }*/
        return i <= despawnLightLevel;
    }

    /**
     * Gets the amount of light on a block without taking into account sunlight
     */
    public static int getBlockLightValue(Chunk chunk, int x, int y, int z)
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

    /**
     * determines if a skeleton spawns on a spider, and if a sheep is a different color
     */
    private static EntityLivingData creatureSpecificInit(EntityLiving par0EntityLiving, World par1World, float par2, float par3, float par4)
    {
        EntityLivingData entitylivingdata = null;
        if (!ForgeEventFactory.doSpecialSpawn(par0EntityLiving, par1World, par2, par3, par4))
        {
            entitylivingdata = par0EntityLiving.onSpawnWithEgg(entitylivingdata);
        }
        return entitylivingdata;
    }

    /**
     * Called during chunk generation to spawn initial creatures.
     */
    public static void performWorldGenSpawning(World worldObj, BiomeGenBase par1BiomeGenBase, int par2, int par3, int par4, int par5, Random par6Random, List customSpawnList, boolean worldGenCreatureSpawning)
    {
        if (!customSpawnList.isEmpty() && worldGenCreatureSpawning)
        {
            while (par6Random.nextFloat() < par1BiomeGenBase.getSpawningChance())
            {
                //this is where it has to be changed to include the custom list
                //spawnlistentry = worldObj.getRandomMob(enumcreaturetype, tempX, tempY, tempZ);
                SpawnListEntry spawnlistentry = (SpawnListEntry)WeightedRandom.getRandomItem(worldObj.rand, customSpawnList);
                if (spawnlistentry != null)
                {
                    int i1 = spawnlistentry.minGroupCount + par6Random.nextInt(1 + spawnlistentry.maxGroupCount - spawnlistentry.minGroupCount);
                    int j1 = par2 + par6Random.nextInt(par4);
                    int k1 = par3 + par6Random.nextInt(par5);
                    int l1 = j1;
                    int i2 = k1;
                    EntityData entityData = classToEntityMapping.get(spawnlistentry.entityClass);

                    for (int j2 = 0; j2 < i1; ++j2)
                    {
                        boolean flag = false;
    
                        for (int k2 = 0; !flag && k2 < 4; ++k2)
                        {
                            int l2 = worldObj.getTopSolidOrLiquidBlock(j1, k1);
    
                            //System.out.println("canCreatureTypeSpawnAtLocation = " + canCreatureTypeSpawnAtLocation(entityData.getLivingSpawnType(), worldObj, j1, l2, k1));
                            if (canCreatureTypeSpawnAtLocation(entityData.getLivingSpawnType(), worldObj, j1, l2, k1))
                            {
                                float f = (float)j1 + 0.5F;
                                float f1 = (float)l2;
                                float f2 = (float)k1 + 0.5F;
                                EntityLiving entityliving;
    
                                try
                                {
                                    entityliving = (EntityLiving)spawnlistentry.entityClass.getConstructor(new Class[] {World.class}).newInstance(new Object[] {worldObj});
                                }
                                catch (Exception exception)
                                {
                                    exception.printStackTrace();
                                    continue;
                                }
    
                                entityliving.setLocationAndAngles((double)f, (double)f1, (double)f2, par6Random.nextFloat() * 360.0F, 0.0F);
                                Result canSpawn = ForgeEventFactory.canEntitySpawn(entityliving, worldObj, f, f1, f2);
                                if (canSpawn == Result.ALLOW || (canSpawn == Result.DEFAULT && entityliving.getCanSpawnHere()))
                                {
                                    worldObj.spawnEntityInWorld(entityliving);
                                    //System.out.println("[WorldGen spawned " + entityliving.getEntityName() + " at " + f + ", " + f1 + ", " + f2 + " with CREATURE:" + spawnlistentry.itemWeight + ":" + spawnlistentry.minGroupCount + ":" + spawnlistentry.maxGroupCount + ":" + ForgeEventFactory.getMaxSpawnPackSize(entityliving) + " in biome " + par1BiomeGenBase.biomeName + "]");
                                    if (debug) CMSLog.logger.info("[WorldGen spawned " + entityliving.getEntityName() + " at " + f + ", " + f1 + ", " + f2 + " with CREATURE:" + spawnlistentry.itemWeight + ":" + spawnlistentry.minGroupCount + ":" + spawnlistentry.maxGroupCount + ":" + ForgeEventFactory.getMaxSpawnPackSize(entityliving) + " in biome " + par1BiomeGenBase.biomeName + "]");
                                    creatureSpecificInit(entityliving, worldObj, f, f1, f2);
                                    flag = true;
                                }
                            }
    
                            j1 += par6Random.nextInt(5) - par6Random.nextInt(5);
    
                            for (k1 += par6Random.nextInt(5) - par6Random.nextInt(5); j1 < par2 || j1 >= par2 + par4 || k1 < par3 || k1 >= par3 + par4; k1 = i2 + par6Random.nextInt(5) - par6Random.nextInt(5))
                            {
                                j1 = l1 + par6Random.nextInt(5) - par6Random.nextInt(5);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Reads values from file
     */
    public static void readConfigValues() 
    {
        // general
        worldGenCreatureSpawning = CMSGlobalConfig.get(CATEGORY_CUSTOMSPAWNER_SETTINGS, "worldGenCreatureSpawning", true, "Allows spawns during world chunk generation.").getBoolean(true);
        maxMonsters = CMSGlobalConfig.get(CATEGORY_CUSTOMSPAWNER_SETTINGS, "maxMonsters", 150, "Max amount of monster.").getInt();
        maxCreatures = CMSGlobalConfig.get(CATEGORY_CUSTOMSPAWNER_SETTINGS, "maxCreatures", 75, "Max amount of animals.").getInt();
        maxAmbients = CMSGlobalConfig.get(CATEGORY_CUSTOMSPAWNER_SETTINGS, "maxAmbients", 10, "Max amount of ambients.").getInt();
        maxWaterCreatures = CMSGlobalConfig.get(CATEGORY_CUSTOMSPAWNER_SETTINGS, "maxWaterCreatures", 15, "Max amount of watercreatures.").getInt();
        creatureSpawnTickRate = CMSGlobalConfig.get(CATEGORY_CUSTOMSPAWNER_SETTINGS, "creatureSpawnTickRate", 200, "The amount of ticks it takes to spawn animals. A tick rate of 100 would cause Custom Spawner to spawn animals every 5 seconds. Raise this value if you want spawning to occur less. Note: 20 ticks takes about 1 second.").getInt();
        monsterSpawnTickRate = CMSGlobalConfig.get(CATEGORY_CUSTOMSPAWNER_SETTINGS, "monsterSpawnTickRate", 20, "The amount of ticks it takes to spawn mobs. A tick rate of 100 would cause Custom Spawner to spawn mobs every 5 seconds. Raise this value if you want spawning to occur less. Note: 20 ticks takes about 1 second.").getInt();
        ambientSpawnTickRate = CMSGlobalConfig.get(CATEGORY_CUSTOMSPAWNER_SETTINGS, "ambientSpawnTickRate", 200, "The amount of ticks it takes to spawn ambients. A tick rate of 100 would cause Custom Spawner to spawn ambients every 5 seconds. Raise this value if you want spawning to occur less. Note: 20 ticks takes about 1 second.").getInt();
        waterSpawnTickRate = CMSGlobalConfig.get(CATEGORY_CUSTOMSPAWNER_SETTINGS, "waterSpawnTickRate", 200, "The amount of ticks it takes to spawn water creatures. A tick rate of 100 would cause Custom Spawner to spawn water creatures every 5 seconds. Raise this value if you want spawning to occur less. Note: 20 ticks takes about 1 second.").getInt();
        despawnTickRate = CMSGlobalConfig.get(CATEGORY_CUSTOMSPAWNER_SETTINGS, "despawnTickRate", 111, "The amount of ticks it takes to despawn vanilla creatures. Requires despawnVanilla to be enabled. Note: 20 ticks takes about 1 second.").getInt();
        entitySpawnRange = CMSGlobalConfig.get(CATEGORY_CUSTOMSPAWNER_SETTINGS, "entitySpawnRange", 8, "Max entity spawn distance from the player (chunks aren't loaded). Note: This value must be equal or greater than view-distance.").getInt();
        spawnCreatures = CMSGlobalConfig.get(CATEGORY_CUSTOMSPAWNER_SETTINGS, "spawnCreatures", true, "Allow creatures to spawn. Turn off to disable all creature entity types.").getBoolean(true);
        spawnMonsters = CMSGlobalConfig.get(CATEGORY_CUSTOMSPAWNER_SETTINGS, "spawnMonsters", true, "Allow monsters to spawn. Turn off to disable all monster entity types.").getBoolean(true);
        spawnWaterCreatures = CMSGlobalConfig.get(CATEGORY_CUSTOMSPAWNER_SETTINGS, "spawnWaterCreatures", true, "Allow watercreatures to spawn. Turn off to disable all watercreature entity types.").getBoolean(true);
        spawnAmbients = CMSGlobalConfig.get(CATEGORY_CUSTOMSPAWNER_SETTINGS, "spawnAmbients", true, "Allow ambients to spawn. Turn off to disable all ambient entity types.").getBoolean(true);
        lightLevel = CMSGlobalConfig.get(CATEGORY_CUSTOMSPAWNER_SETTINGS, "lightLevel", 2, "The light level threshold used to determine whether or not to spawn a creature.").getInt();
        despawnLightLevel = CMSGlobalConfig.get(CATEGORY_CUSTOMSPAWNER_SETTINGS, "despawnLightLevel", 3, "The light level threshold used to determine whether or not to despawn a creature.").getInt();
        checkAmbientLightLevel = CMSGlobalConfig.get(CATEGORY_CUSTOMSPAWNER_SETTINGS, "checkAmbientLightLevel", false, "Turns on check for lightLevel for Ambient creature spawns.").getBoolean(false);
        killallUseLightLevel = CMSGlobalConfig.get(CATEGORY_CUSTOMSPAWNER_SETTINGS, "killallUseLightLevel", true, "Turns on check for lightLevel before killing an entity during a killall. If entity is under lightLevel threshold, it will be killed.").getBoolean(false);
        disallowMonsterSpawningDuringDay = CMSGlobalConfig.get(CATEGORY_CUSTOMSPAWNER_SETTINGS, "disallowMonsterSpawningDuringDay", false, "Prevents monsters from spawning anywhere during the day. Note: this will affect underground spawns as well.").getBoolean(false);
        enforceMaxSpawnLimits = CMSGlobalConfig.get(CATEGORY_CUSTOMSPAWNER_SETTINGS, "enforceMaxSpawnLimits", false, "If enabled, all spawns will stop when max spawn limits have been reached for type.").getBoolean(false);
        debug = CMSGlobalConfig.get(CATEGORY_CUSTOMSPAWNER_SETTINGS, "debug", false, "Turns on CustomMobSpawner debug logging.").getBoolean(false);
        killallVillagers = CMSGlobalConfig.get(CATEGORY_CUSTOMSPAWNER_SETTINGS, "killAllVillagers", false).getBoolean(false);
        modifyVanillaSpawns = CMSGlobalConfig.get(CATEGORY_CUSTOMSPAWNER_SETTINGS, "ModifyVanillaSpawns", true, "Forces Custom Spawner to handle vanilla spawns otherwise the default vanilla spawner is used.").getBoolean(true);
        despawnVanilla = CMSGlobalConfig.get(CATEGORY_CUSTOMSPAWNER_SETTINGS, "DespawnVanilla", true, "Allows Custom Spawner to despawn vanilla every despawnTickRate. This helps prevent vanilla from overwhelming custom spawns.").getBoolean(true);
        CMSGlobalConfig.save();
    }
}
