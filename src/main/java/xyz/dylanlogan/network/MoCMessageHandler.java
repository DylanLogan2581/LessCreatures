package xyz.dylanlogan.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import xyz.dylanlogan.MoCreatures;
import xyz.dylanlogan.network.message.MoCMessageAnimation;
import xyz.dylanlogan.network.message.MoCMessageAppear;
import xyz.dylanlogan.network.message.MoCMessageAttachedEntity;
import xyz.dylanlogan.network.message.MoCMessageEntityDive;
import xyz.dylanlogan.network.message.MoCMessageEntityJump;
import xyz.dylanlogan.network.message.MoCMessageExplode;
import xyz.dylanlogan.network.message.MoCMessageHealth;
import xyz.dylanlogan.network.message.MoCMessageHeart;
import xyz.dylanlogan.network.message.MoCMessageInstaSpawn;
import xyz.dylanlogan.network.message.MoCMessageShuffle;
import xyz.dylanlogan.network.message.MoCMessageTwoBytes;
import xyz.dylanlogan.network.message.MoCMessageUpdatePetName;
import xyz.dylanlogan.network.message.MoCMessageVanish;

public class MoCMessageHandler
{
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("MoCreatures");

    public static void init()
    {
        INSTANCE.registerMessage(MoCMessageAnimation.class, MoCMessageAnimation.class, 0, Side.CLIENT);
        INSTANCE.registerMessage(MoCMessageAppear.class, MoCMessageAppear.class, 1, Side.CLIENT);
        INSTANCE.registerMessage(MoCMessageAttachedEntity.class, MoCMessageAttachedEntity.class, 2, Side.CLIENT);
        INSTANCE.registerMessage(MoCMessageEntityDive.class, MoCMessageEntityDive.class, 3, Side.SERVER);
        INSTANCE.registerMessage(MoCMessageEntityJump.class, MoCMessageEntityJump.class, 4, Side.SERVER);
        INSTANCE.registerMessage(MoCMessageExplode.class, MoCMessageExplode.class, 5, Side.CLIENT);
        INSTANCE.registerMessage(MoCMessageHealth.class, MoCMessageHealth.class, 6, Side.CLIENT);
        INSTANCE.registerMessage(MoCMessageHeart.class, MoCMessageHeart.class, 7, Side.CLIENT);
        INSTANCE.registerMessage(MoCMessageInstaSpawn.class, MoCMessageInstaSpawn.class, 8, Side.SERVER);
        INSTANCE.registerMessage(MoCMessageUpdatePetName.class, MoCMessageUpdatePetName.class, 10, Side.SERVER);
        INSTANCE.registerMessage(MoCMessageShuffle.class, MoCMessageShuffle.class, 11, Side.CLIENT);
        INSTANCE.registerMessage(MoCMessageTwoBytes.class, MoCMessageTwoBytes.class, 12, Side.CLIENT);
        INSTANCE.registerMessage(MoCMessageVanish.class, MoCMessageVanish.class, 13, Side.CLIENT);
    }
}
