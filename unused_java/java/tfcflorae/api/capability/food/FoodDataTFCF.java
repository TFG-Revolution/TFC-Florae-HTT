package tfcflorae.api.capability.food;

import javax.annotation.Nullable;

import net.dries007.tfc.api.capability.food.Nutrient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public class FoodDataTFCF implements INBTSerializable<NBTTagCompound>
{
    // Instances for special vanilla foods (with relation to decay)
    public static final FoodDataTFCF ROTTEN_FLESH = new FoodDataTFCF(0, 0, 0, 0, 0, 0, 0, 0, Float.POSITIVE_INFINITY);
    public static final FoodDataTFCF GOLDEN_APPLE = new FoodDataTFCF(1, 0, 0, 0, 2.5f, 0, 0, 0, 0);
    public static final FoodDataTFCF GOLDEN_CARROT = new FoodDataTFCF(1, 0, 0, 0, 0, 2.5f, 0, 0, 0);
    public static final FoodDataTFCF RAW_EGG = new FoodDataTFCF(1, 0, 0, 0, 0, 0, 0, 0, 1.3f);
    public static final FoodDataTFCF MILK = new FoodDataTFCF(0, 0, 0, 0, 0, 0, 0, 1.0f, 0);

    private final float[] nutrients; // Nutritional values
    private int hunger; // Hunger. In TFC (for now) this is almost always 4
    private float saturation; // Saturation, only provided by some basic foods and meal bonuses
    private float water; // Water, provided by some foods
    private float decayModifier; // Decay modifier - higher = shorter decay
    private boolean buffed; // if this data instance has been buffed externally.

    public FoodDataTFCF()
    {
        this(4, 0, 0, 0, 0, 0, 0, 0, 1);
    }

    public FoodDataTFCF(int hunger, float water, float saturation, float grain, float fruit, float veg, float protein, float dairy, float decayModifier)
    {
        this(hunger, water, saturation, new float[] {grain, fruit, veg, protein, dairy}, decayModifier);
    }

    public FoodDataTFCF(int hunger, float water, float saturation, float[] nutrients, float decayModifier)
    {
        this.hunger = hunger;
        this.water = water;
        this.saturation = saturation;
        this.nutrients = nutrients;
        this.decayModifier = decayModifier;
    }

    public FoodDataTFCF(@Nullable NBTTagCompound nbt)
    {
        this.nutrients = new float[5];
        deserializeNBT(nbt);
    }

    public float[] getNutrients()
    {
        return nutrients;
    }

    public int getHunger()
    {
        return hunger;
    }

    public float getSaturation()
    {
        return saturation;
    }

    public float getWater()
    {
        return water;
    }

    public float getDecayModifier()
    {
        return decayModifier;
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("food", hunger);
        nbt.setFloat("sat", saturation);
        nbt.setFloat("water", water);
        nbt.setFloat("decay", decayModifier);
        nbt.setFloat("grain", nutrients[Nutrient.GRAIN.ordinal()]);
        nbt.setFloat("veg", nutrients[Nutrient.VEGETABLES.ordinal()]);
        nbt.setFloat("fruit", nutrients[Nutrient.FRUIT.ordinal()]);
        nbt.setFloat("meat", nutrients[Nutrient.PROTEIN.ordinal()]);
        nbt.setFloat("dairy", nutrients[Nutrient.DAIRY.ordinal()]);
        nbt.setBoolean("buffed", buffed);
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        if (nbt != null)
        {
            hunger = nbt.getInteger("food");
            saturation = nbt.getFloat("sat");
            water = nbt.getFloat("water");
            decayModifier = nbt.getFloat("decay");
            nutrients[Nutrient.GRAIN.ordinal()] = nbt.getFloat("grain");
            nutrients[Nutrient.VEGETABLES.ordinal()] = nbt.getFloat("veg");
            nutrients[Nutrient.FRUIT.ordinal()] = nbt.getFloat("fruit");
            nutrients[Nutrient.PROTEIN.ordinal()] = nbt.getFloat("meat");
            nutrients[Nutrient.DAIRY.ordinal()] = nbt.getFloat("dairy");
            buffed = nbt.getBoolean("buffed");
        }
    }

    public FoodDataTFCF copy()
    {
        return new FoodDataTFCF(hunger, water, saturation, nutrients, decayModifier);
    }

    public void applyBuff(FoodDataTFCF buff)
    {
        if (!buffed)
        {
            buffed = true;
            for (Nutrient nutrient : Nutrient.values())
            {
                nutrients[nutrient.ordinal()] += buff.nutrients[nutrient.ordinal()];
            }
        }
    }
}