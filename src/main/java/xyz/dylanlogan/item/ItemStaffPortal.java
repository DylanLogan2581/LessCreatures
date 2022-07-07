package xyz.dylanlogan.item;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import xyz.dylanlogan.MoCreatures;

public class ItemStaffPortal extends MoCItem
{
    public ItemStaffPortal(String name)
    {
        super(name);
        maxStackSize = 1;
        setMaxDamage(3);
    }

    private int portalPosX;
    private int portalPosY;
    private int portalPosZ;
    private int portalDimension;

    /**
     * Returns True is the item is renderer in full 3D when hold.
     */
    @Override
    public boolean isFull3D()
    {
        return true;
    }

    /**
     * returns the action that specifies what animation to play when the items
     * is being used
     */
    @Override
    public EnumAction getItemUseAction(ItemStack par1ItemStack)
    {
        return EnumAction.block;
    }

    /**
     * Called whenever this item is equipped and the right mouse button is
     * pressed. Args: itemStack, world, entityPlayer
     */
    @Override
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        par3EntityPlayer.setItemInUse(par1ItemStack, this.getMaxItemUseDuration(par1ItemStack));
        return par1ItemStack;
    }

    public void readFromNBT(NBTTagCompound nbt)
    {
        this.portalPosX = nbt.getInteger("portalPosX");
        this.portalPosY = nbt.getInteger("portalPosY");
        this.portalPosZ = nbt.getInteger("portalPosZ");
        this.portalDimension = nbt.getInteger("portalDimension");
    }

    public void writeToNBT(NBTTagCompound nbt)
    {
        nbt.setInteger("portalPosX", this.portalPosX);
        nbt.setInteger("portalPosY", this.portalPosY);
        nbt.setInteger("portalPosZ", this.portalPosZ);
        nbt.setInteger("portalDimension", this.portalDimension);
    }
}
