package tfcflorae.objects.blocks.FruitWood;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.BlockButtonWood;
import net.minecraft.block.SoundType;
import net.minecraft.init.Blocks;

import tfcflorae.util.OreDictionaryHelper;

public class BlockFruitButton extends BlockButtonWood
{
    public BlockFruitButton()
    {
        setHardness(0.5F);
        setSoundType(SoundType.WOOD);
        OreDictionaryHelper.register(this, "button", "button_wood", "button_fruit", "button_wood_fruit");
        Blocks.FIRE.setFireInfo(this, 5, 20);
    }
}