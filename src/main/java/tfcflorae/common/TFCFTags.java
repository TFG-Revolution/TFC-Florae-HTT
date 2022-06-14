package tfcflorae.common;

import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import net.dries007.tfc.util.Helpers;

public class TFCFTags
{
    public static class Blocks
    {
        private static TagKey<Block> create(String id)
        {
            return TagKey.create(Registry.BLOCK_REGISTRY, Helpers.identifier(id));
        }
    }

    public static class Items
    {
        public static final TagKey<Item> EARTHENWARE_CLAY_KNAPPING = create("earthenware_clay_knapping");
        public static final TagKey<Item> KAOLINITE_CLAY_KNAPPING = create("kaolinite_clay_knapping");
        public static final TagKey<Item> STONEWARE_CLAY_KNAPPING = create("stoneware_clay_knapping");

        private static TagKey<Item> create(String id)
        {
            return TagKey.create(Registry.ITEM_REGISTRY, Helpers.identifier(id));
        }
    } 
}