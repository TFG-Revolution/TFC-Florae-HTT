package tfcflorae.objects.items;

import java.util.EnumMap;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.item.ItemStack;

import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.objects.Powder;
import net.dries007.tfc.objects.items.ItemTFC;
//import net.dries007.tfc.util.OreDictionaryHelper;
import tfcflorae.util.OreDictionaryHelper;
import tfcflorae.objects.PowderTFCF;
import tfcflorae.TFCFlorae;

@SuppressWarnings("WeakerAccess")
@ParametersAreNonnullByDefault
public class ItemPowderTFCF extends ItemTFC
{
    private static final EnumMap<PowderTFCF, ItemPowderTFCF> MAP = new EnumMap<>(PowderTFCF.class);

    public static ItemPowderTFCF get(PowderTFCF powder)
    {
        return MAP.get(powder);
    }

    public static ItemStack get(PowderTFCF powder, int amount)
    {
        return new ItemStack(MAP.get(powder), amount);
    }

    private final PowderTFCF powder;

    public ItemPowderTFCF(PowderTFCF powder)
    {
        this.powder = powder;
        if (MAP.put(powder, this) != null) throw new IllegalStateException("There can only be one.");
        setMaxDamage(0);
        OreDictionaryHelper.register(this, "dust", powder);
    }

    @Nonnull
    @Override
    public Size getSize(ItemStack stack)
    {
        return Size.SMALL;
    }

    @Nonnull
    @Override
    public Weight getWeight(ItemStack stack)
    {
        return Weight.VERY_LIGHT;
    }

    @Nonnull
    public PowderTFCF getPowder()
    {
        return powder;
    }
}