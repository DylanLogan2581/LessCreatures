package xyz.dylanlogan.block;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import xyz.dylanlogan.MoCreatures;

public class MultiItemBlock extends ItemBlock {

    public MultiItemBlock(Block block) 
    {
        super(block);
        setHasSubtypes(true);
        //setItemName("multiBlock"); //TODO
        this.setUnlocalizedName("multiBlock");
    }

    @Override
    public int getMetadata (int damageValue) {
        return damageValue;
    }

    @Override
    public String getUnlocalizedName(ItemStack itemstack) {
        return getUnlocalizedName() + "." + MoCreatures.multiBlockNames.get(itemstack.getItemDamage());
    }
}