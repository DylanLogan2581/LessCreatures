package xyz.dylanlogan.client.handlers;

import java.util.EnumSet;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import xyz.dylanlogan.MoCreatures;
import xyz.dylanlogan.client.MoCClientProxy;
import xyz.dylanlogan.entity.IMoCEntity;
import xyz.dylanlogan.network.MoCMessageHandler;
import xyz.dylanlogan.network.message.MoCMessageEntityDive;
import xyz.dylanlogan.network.message.MoCMessageEntityJump;

public class MoCKeyHandler {
    int keyCount;
    //static KeyBinding jumpBinding = new KeyBinding("jumpBind", Keyboard.KEY_F);
    //static KeyBinding jumpBinding = new KeyBinding("MoCreatures Jump", MoCClientProxy.mc.gameSettings.keyBindJump.getKeyCode(), "key.categories.movement");
    static KeyBinding diveBinding = new KeyBinding("MoCreatures Dive", Keyboard.KEY_F, "key.categories.movement");
    static KeyBinding guiBinding = new KeyBinding("MoCreatures GUI", Keyboard.KEY_F6, "key.categories.misc");
    //static KeyBinding dismountBinding = new KeyBinding("MoCreatures Dismount", Keyboard.KEY_F);

    public MoCKeyHandler()
    {
        //the first value is an array of KeyBindings, the second is whether or not the call
        //keyDown should repeat as long as the key is down
        //cpw.mods.fml.client.registry.ClientRegistry.registerKeyBinding(jumpBinding);
        cpw.mods.fml.client.registry.ClientRegistry.registerKeyBinding(diveBinding);
        cpw.mods.fml.client.registry.ClientRegistry.registerKeyBinding(guiBinding);
    }

    
    @SubscribeEvent
    public void onKeyInput(KeyInputEvent event)
    {
        Keyboard.enableRepeatEvents(true); // allow holding down key. Fixes flying
        EntityPlayer ep = MoCClientProxy.mc.thePlayer;
        if (ep == null || ep.ridingEntity == null) return;
        if (FMLClientHandler.instance().getClient().ingameGUI.getChatGUI().getChatOpen()) return; // if chatting return
        boolean kbJump = Keyboard.isKeyDown(MoCClientProxy.mc.gameSettings.keyBindJump.getKeyCode());
        boolean kbDive = Keyboard.isKeyDown(diveBinding.getKeyCode());
        boolean kbGui = Keyboard.isKeyDown(guiBinding.getKeyCode());
        boolean isJumpKeyDown = Keyboard.isKeyDown(MoCClientProxy.mc.gameSettings.keyBindJump.getKeyCode());
        //boolean kbDismount = kb.keyDescription.equals("MoCreatures Dismount");

        /**
         * this avoids double jumping
         */
        if (kbJump && ep != null && ep.ridingEntity != null && ep.ridingEntity instanceof IMoCEntity)
        {
            // keyCount = 0;
            // jump code needs to be executed client/server simultaneously to take
            ((IMoCEntity) ep.ridingEntity).makeEntityJump();
            MoCMessageHandler.INSTANCE.sendToServer(new MoCMessageEntityJump());
        }

        if (kbDive && ep != null && ep.ridingEntity != null && ep.ridingEntity instanceof IMoCEntity)
        {
          //  keyCount = 0;
            // jump code needs to be executed client/server simultaneously to take
            ((IMoCEntity) ep.ridingEntity).makeEntityDive();
            MoCMessageHandler.INSTANCE.sendToServer(new MoCMessageEntityDive());
        }
    }
}
