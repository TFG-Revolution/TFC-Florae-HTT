package tfcflorae.world.feature.tree.cypress;

import java.util.Random;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.material.Material;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.util.EnvironmentHelpers;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.TFCChunkGenerator;
import net.dries007.tfc.world.biome.BiomeExtension;
import net.dries007.tfc.world.biome.TFCBiomes;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.feature.tree.TreeFeature;
import net.dries007.tfc.world.feature.tree.TreeHelpers;

import tfcflorae.TFCFlorae;
import tfcflorae.common.blocks.TFCFBlocks;
import tfcflorae.common.blocks.wood.TFCFWood;
import tfcflorae.world.feature.tree.DynamicTreeConfig;

public class CypressTreeFeature extends TreeFeature<DynamicTreeConfig>
{
    public CypressTreeFeature(Codec<DynamicTreeConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<DynamicTreeConfig> context)
    {
        final WorldGenLevel level = context.level();
        final BlockPos pos = context.origin();
        final BlockState state = level.getBlockState(pos.below());
        final Random random = context.random();
        final DynamicTreeConfig config = context.config();

        final ChunkPos chunkPos = new ChunkPos(pos);
        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos().set(pos);

        final StructurePlaceSettings settings = TreeHelpers.getPlacementSettings(level, chunkPos, random);
        final Biome biome = level.getBiome(pos).value();
        final BiomeExtension variants = TFCBiomes.getExtensionOrThrow(level, biome);
        final int seaLevel = TFCChunkGenerator.SEA_LEVEL_Y;

        if (TreeHelpers.isValidLocation(level, pos, settings, config.placement()) || TreeHelpers.isValidGround(level, pos, settings, config.placement()))
        {
            //if (isLake(variants) || isRiver(variants) || isLow(variants))
            //{
                config.trunk().ifPresent(trunk -> {
                    final int height = TreeHelpers.placeTrunk(level, mutablePos, random, settings, trunk);
                    mutablePos.move(0, height, 0);
                });

                final BlockState cypressTreeLog = config.logState();
                final BlockState cypressTreeLeaves = config.leavesState();
                final BlockState cypressTreeWood = config.woodState();

                final int chance = random.nextInt(3);
                if (chance == 0)
                {
                    TFCFlorae.LOGGER.info("generating tree 1 at XZ " + pos.getX() + ", " + pos.getZ());
                    buildCypressVariant1(config, level, random, mutablePos, pos, cypressTreeLog, cypressTreeLeaves, cypressTreeWood);
                    return true;
                }
                else if (chance == 1)
                {
                    TFCFlorae.LOGGER.info("generating tree 2 at XZ " + pos.getX() + ", " + pos.getZ());
                    buildCypressVariant2(config, level, random, mutablePos, pos, cypressTreeLog, cypressTreeLeaves, cypressTreeWood);
                    return true;
                }
                else
                {
                    TFCFlorae.LOGGER.info("generating tree 3 at XZ " + pos.getX() + ", " + pos.getZ());
                    buildCypressVariant3(config, level, random, mutablePos, pos, cypressTreeLog, cypressTreeLeaves, cypressTreeWood);
                    return true;
                }
            //}
        }
        return false;
    }

    private void buildCypressVariant1(DynamicTreeConfig config, WorldGenLevel level, Random random, BlockPos.MutableBlockPos mutablePos, BlockPos pos, BlockState cypressTreeLog, BlockState cypressTreeLeaves, BlockState cypressTreeWood)
    {
        int cypressHeight = config.minHeight() + random.nextInt(config.placement().height());

        BlockPos.MutableBlockPos mainmutable = mutablePos;
        BlockPos.MutableBlockPos mainmutable2 = mutablePos.set(pos.relative(Direction.NORTH));
        BlockPos.MutableBlockPos mainmutable3 = mutablePos.set(pos.relative(Direction.SOUTH));
        BlockPos.MutableBlockPos mainmutable4 = mutablePos.set(pos.relative(Direction.WEST));
        BlockPos.MutableBlockPos mainmutable5 = mutablePos.set(pos.relative(Direction.EAST));

        if (pos.getY() + cypressHeight + 1 < level.getMaxBuildHeight())
        {
            mainmutable.immutable();
            mainmutable2.immutable();
            mainmutable3.immutable();
            mainmutable4.immutable();
            mainmutable5.immutable();

            for (int buildTrunk = 0; buildTrunk <= cypressHeight; buildTrunk++)
            {
                placeTrunk(pos, random, level, mainmutable, cypressTreeWood);
                placeTrunk(pos, random, level, mainmutable2, cypressTreeWood);
                placeTrunk(pos, random, level, mainmutable3, cypressTreeWood);
                placeTrunk(pos, random, level, mainmutable4, cypressTreeWood);
                placeTrunk(pos, random, level, mainmutable5, cypressTreeWood);
                mainmutable.move(Direction.UP);
                mainmutable2.move(Direction.UP);
                mainmutable3.move(Direction.UP);
                mainmutable4.move(Direction.UP);
                mainmutable5.move(Direction.UP);
            }
            mainmutable.set(pos);

            //Roots
            BlockPos.MutableBlockPos rootMutable = new BlockPos.MutableBlockPos().set(mainmutable.offset(-4, 0, 0));
            BlockPos.MutableBlockPos rootMutable2 = new BlockPos.MutableBlockPos().set(mainmutable.offset(0, 0, 4));
            BlockPos.MutableBlockPos rootMutable3 = new BlockPos.MutableBlockPos().set(mainmutable.offset(0, 0, -4));
            BlockPos.MutableBlockPos rootMutable4 = new BlockPos.MutableBlockPos().set(mainmutable.offset(4, 0, 0));
            BlockPos.MutableBlockPos rootMutable5 = new BlockPos.MutableBlockPos().set(mainmutable.offset(-4, 0, -4));
            BlockPos.MutableBlockPos rootMutable6 = new BlockPos.MutableBlockPos().set(mainmutable.offset(4, 0, -4));
            BlockPos.MutableBlockPos rootMutable7 = new BlockPos.MutableBlockPos().set(mainmutable.offset(4, 0, 4));
            BlockPos.MutableBlockPos rootMutable8 = new BlockPos.MutableBlockPos().set(mainmutable.offset(-4, 0, 4));

            for (int buildRoot = 0; buildRoot <= 5; buildRoot++)
            {
                for (Direction direction : Direction.Plane.HORIZONTAL)
                {
                    placeBranch(pos, random, level, rootMutable.relative(direction), cypressTreeLog);
                    placeBranch(pos, random, level, rootMutable2.relative(direction), cypressTreeLog);
                    placeBranch(pos, random, level, rootMutable3.relative(direction), cypressTreeLog);
                    placeBranch(pos, random, level, rootMutable4.relative(direction), cypressTreeLog);
                }

                placeBranch(pos, random, level, rootMutable5, cypressTreeLog);
                placeBranch(pos, random, level, rootMutable6, cypressTreeLog);
                placeBranch(pos, random, level, rootMutable7, cypressTreeLog);
                placeBranch(pos, random, level, rootMutable8, cypressTreeLog);

                rootMutable.move(Direction.DOWN);
                rootMutable2.move(Direction.DOWN);
                rootMutable3.move(Direction.DOWN);
                rootMutable4.move(Direction.DOWN);
                rootMutable5.move(Direction.DOWN);
                rootMutable6.move(Direction.DOWN);
                rootMutable7.move(Direction.DOWN);
                rootMutable8.move(Direction.DOWN);
            }

            //Stump
            placeBranch(pos, random, level, mainmutable.set(pos).move(0, 1, -5), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-4, 1, -4), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-1, 1, -4), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(0, 1, -4), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(1, 1, -4), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(4, 1, -4), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-3, 1, -3), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(0, 1, -3), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(3, 1, -3), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-4, 1, -1), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(4, 1, -1), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-5, 1, 0), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-4, 1, 0), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-3, 1, 0), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(3, 1, 0), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(4, 1, 0), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(5, 1, 0), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-4, 1, 1), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(4, 1, 1), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-3, 1, 3), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(0, 1, 3), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(3, 1, 3), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-4, 1, 4), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-1, 1, 4), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(0, 1, 4), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(1, 1, 4), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(4, 1, 4), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(0, 1, 5), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(0, 2, -4), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-3, 2, -3), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(3, 2, -3), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-4, 2, 0), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(4, 2, 0), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-3, 2, 3), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(3, 2, 3), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(0, 2, 4), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(0, 3, -4), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-3, 3, -3), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-2, 3, -3), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(0, 3, -3), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(2, 3, -3), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(3, 3, -3), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-3, 3, -2), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(3, 3, -2), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-4, 3, 0), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-3, 3, 0), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(3, 3, 0), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(4, 3, 0), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-3, 3, 2), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(3, 3, 2), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-3, 3, 3), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-2, 3, 3), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(0, 3, 3), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(2, 3, 3), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(3, 3, 3), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(0, 3, 4), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(0, 4, -3), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-2, 4, -2), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(2, 4, -2), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-3, 4, 0), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(3, 4, 0), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-1, 4, 1), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(1, 4, 1), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-2, 4, 2), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(2, 4, 2), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(0, 4, 3), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(0, 5, -3), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-1, 5, -2), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(0, 5, -2), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(1, 5, -2), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-2, 5, -1), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-1, 5, -1), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(1, 5, -1), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(2, 5, -1), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-3, 5, 0), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-2, 5, 0), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(2, 5, 0), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(3, 5, 0), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-2, 5, 1), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-1, 5, 1), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(1, 5, 1), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(2, 5, 1), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-1, 5, 2), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(0, 5, 2), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(1, 5, 2), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(0, 5, 3), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(0, 6, -3), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(0, 6, -2), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-1, 6, -1), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(1, 6, -1), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-3, 6, 0), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-2, 6, 0), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(2, 6, 0), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(3, 6, 0), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-1, 6, 1), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(1, 6, 1), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(0, 6, 2), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(0, 6, 3), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(0, 7, -3), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(0, 7, -2), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-1, 7, -1), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(1, 7, -1), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-2, 7, 0), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(2, 7, 0), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-1, 7, 1), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(1, 7, 1), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(0, 7, 2), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(0, 7, 3), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(0, 8, -2), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-1, 8, -1), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(1, 8, -1), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-2, 8, 0), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(2, 8, 0), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-1, 8, 1), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(1, 8, 1), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(0, 8, 2), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(0, 9, -2), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-2, 9, 0), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(2, 9, 0), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(0, 9, 2), cypressTreeLog);

            //Top Branches
            placeBranch(pos, random, level, mainmutable.set(pos).move(0, cypressHeight - 3, -2), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-2, cypressHeight - 3, 0), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(2, cypressHeight - 3, 0), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(0, cypressHeight - 3, 2), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(0, cypressHeight - 2, -3), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(0, cypressHeight - 2, -2), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-3, cypressHeight - 2, 0), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-2, cypressHeight - 2, 0), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(2, cypressHeight - 2, 0), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(3, cypressHeight - 2, 0), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(0, cypressHeight - 2, 2), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(0, cypressHeight - 2, 3), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(0, cypressHeight - 1, -2), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-2, cypressHeight - 1, 0), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(2, cypressHeight - 1, 0), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(0, cypressHeight - 1, 2), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(1, cypressHeight, -7), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(1, cypressHeight, -6), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-3, cypressHeight, -5), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(0, cypressHeight, -5), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(2, cypressHeight, -5), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-2, cypressHeight, -4), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-1, cypressHeight, -4), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(0, cypressHeight, -4), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(3, cypressHeight, -4), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(4, cypressHeight, -4), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-5, cypressHeight, -3), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(0, cypressHeight, -3), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(2, cypressHeight, -3), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(3, cypressHeight, -3), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-4, cypressHeight, -2), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(0, cypressHeight, -2), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(8, cypressHeight, -2), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-4, cypressHeight, -1), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(6, cypressHeight, -1), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(7, cypressHeight, -1), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-5, cypressHeight, 0), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-4, cypressHeight, 0), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-3, cypressHeight, 0), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-2, cypressHeight, 0), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(2, cypressHeight, 0), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(3, cypressHeight, 0), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(4, cypressHeight, 0), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(5, cypressHeight, 0), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-2, cypressHeight, 1), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-3, cypressHeight, 2), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(0, cypressHeight, 2), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-4, cypressHeight, 3), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(0, cypressHeight, 3), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(3, cypressHeight, 3), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(4, cypressHeight, 3), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(-4, cypressHeight, 4), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(0, cypressHeight, 4), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(2, cypressHeight, 4), cypressTreeLog);
            placeBranch(pos, random, level, mainmutable.set(pos).move(1, cypressHeight, 5), cypressTreeLog);

            //Stump Leaves
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-5, 2, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-5, 2, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-4, 2, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-6, 2, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-5, 2, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-4, 2, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-8, 2, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-7, 2, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-6, 2, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-5, 2, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-7, 2, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-6, 2, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-5, 2, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-4, 2, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-5, 2, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(2, 2, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(3, 2, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(4, 2, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-5, 2, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(1, 2, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(2, 2, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(4, 2, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(5, 2, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(2, 2, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(3, 2, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(4, 2, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(5, 2, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(6, 2, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(3, 2, 5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(4, 2, 5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(5, 2, 5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(3, 2, 6), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(0, 3, -8), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-1, 3, -7), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(0, 3, -7), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(1, 3, -7), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-1, 3, -6), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(0, 3, -6), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(1, 3, -6), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(2, 3, -6), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-1, 3, -5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(0, 3, -5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(1, 3, -5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(2, 3, -5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(3, 3, -5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-2, 3, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-1, 3, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(1, 3, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(2, 3, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(3, 3, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-1, 3, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(1, 3, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-5, 3, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-6, 3, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-5, 3, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-6, 3, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-5, 3, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-5, 3, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(4, 3, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(3, 3, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(4, 3, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(5, 3, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(4, 3, 5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(0, 4, -6), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(0, 4, -5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(1, 4, -5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-1, 4, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(0, 4, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(1, 4, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(2, 4, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(1, 4, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-5, 4, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(1, 5, -4), cypressTreeLeaves);


            placeLeaves(pos, random, level, mainmutable.set(pos).move(0, cypressHeight - 3, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(3, cypressHeight - 3, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(2, cypressHeight - 3, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(0, cypressHeight - 2, -6), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(0, cypressHeight - 2, -5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(1, cypressHeight - 2, -5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-1, cypressHeight - 2, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(0, cypressHeight - 2, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(1, cypressHeight - 2, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-1, cypressHeight - 2, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(1, cypressHeight - 2, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(2, cypressHeight - 2, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(3, cypressHeight - 2, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(4, cypressHeight - 2, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-4, cypressHeight - 2, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-3, cypressHeight - 2, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-2, cypressHeight - 2, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-1, cypressHeight - 2, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(1, cypressHeight - 2, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(2, cypressHeight - 2, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-2, cypressHeight - 2, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-1, cypressHeight - 2, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(0, cypressHeight - 2, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(2, cypressHeight - 2, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(3, cypressHeight - 2, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-5, cypressHeight - 2, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-4, cypressHeight - 2, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(4, cypressHeight - 2, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(5, cypressHeight - 2, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-4, cypressHeight - 2, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-3, cypressHeight - 2, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-2, cypressHeight - 2, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(1, cypressHeight - 2, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(2, cypressHeight - 2, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(3, cypressHeight - 2, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(4, cypressHeight - 2, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-5, cypressHeight - 2, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-4, cypressHeight - 2, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-3, cypressHeight - 2, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-1, cypressHeight - 2, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(1, cypressHeight - 2, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(2, cypressHeight - 2, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(3, cypressHeight - 2, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(4, cypressHeight - 2, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-4, cypressHeight - 2, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-3, cypressHeight - 2, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-1, cypressHeight - 2, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(1, cypressHeight - 2, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(2, cypressHeight - 2, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(3, cypressHeight - 2, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-4, cypressHeight - 2, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(1, cypressHeight - 2, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(2, cypressHeight - 2, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(2, cypressHeight - 2, 5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(0, cypressHeight - 1, -8), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(1, cypressHeight - 1, -8), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(2, cypressHeight - 1, -8), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-3, cypressHeight - 1, -7), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-2, cypressHeight - 1, -7), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-1, cypressHeight - 1, -7), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(0, cypressHeight - 1, -7), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(1, cypressHeight - 1, -7), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(2, cypressHeight - 1, -7), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(3, cypressHeight - 1, -7), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-5, cypressHeight - 1, -6), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-4, cypressHeight - 1, -6), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-3, cypressHeight - 1, -6), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-2, cypressHeight - 1, -6), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-1, cypressHeight - 1, -6), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(0, cypressHeight - 1, -6), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(1, cypressHeight - 1, -6), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(2, cypressHeight - 1, -6), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(3, cypressHeight - 1, -6), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(4, cypressHeight - 1, -6), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(5, cypressHeight - 1, -6), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-6, cypressHeight - 1, -5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-5, cypressHeight - 1, -5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-4, cypressHeight - 1, -5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-3, cypressHeight - 1, -5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-2, cypressHeight - 1, -5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-1, cypressHeight - 1, -5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(0, cypressHeight - 1, -5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(1, cypressHeight - 1, -5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(2, cypressHeight - 1, -5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(3, cypressHeight - 1, -5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(4, cypressHeight - 1, -5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(5, cypressHeight - 1, -5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(6, cypressHeight - 1, -5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-7, cypressHeight - 1, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-6, cypressHeight - 1, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-5, cypressHeight - 1, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-4, cypressHeight - 1, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-3, cypressHeight - 1, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-2, cypressHeight - 1, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-1, cypressHeight - 1, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(0, cypressHeight - 1, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(1, cypressHeight - 1, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(2, cypressHeight - 1, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(3, cypressHeight - 1, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(4, cypressHeight - 1, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(5, cypressHeight - 1, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(6, cypressHeight - 1, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(7, cypressHeight - 1, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(8, cypressHeight - 1, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-7, cypressHeight - 1, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-6, cypressHeight - 1, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-5, cypressHeight - 1, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-4, cypressHeight - 1, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-3, cypressHeight - 1, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-2, cypressHeight - 1, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-1, cypressHeight - 1, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(0, cypressHeight - 1, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(1, cypressHeight - 1, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(2, cypressHeight - 1, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(3, cypressHeight - 1, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(4, cypressHeight - 1, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(5, cypressHeight - 1, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(6, cypressHeight - 1, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(7, cypressHeight - 1, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(8, cypressHeight - 1, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(9, cypressHeight - 1, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-7, cypressHeight - 1, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-6, cypressHeight - 1, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-5, cypressHeight - 1, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-4, cypressHeight - 1, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-3, cypressHeight - 1, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-2, cypressHeight - 1, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-1, cypressHeight - 1, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(1, cypressHeight - 1, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(2, cypressHeight - 1, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(3, cypressHeight - 1, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(4, cypressHeight - 1, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(5, cypressHeight - 1, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(6, cypressHeight - 1, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(7, cypressHeight - 1, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(8, cypressHeight - 1, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(9, cypressHeight - 1, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(10, cypressHeight - 1, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-8, cypressHeight - 1, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-7, cypressHeight - 1, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-6, cypressHeight - 1, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-5, cypressHeight - 1, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-4, cypressHeight - 1, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-3, cypressHeight - 1, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-2, cypressHeight - 1, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-1, cypressHeight - 1, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(0, cypressHeight - 1, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(1, cypressHeight - 1, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(2, cypressHeight - 1, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(3, cypressHeight - 1, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(4, cypressHeight - 1, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(5, cypressHeight - 1, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(6, cypressHeight - 1, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(7, cypressHeight - 1, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(8, cypressHeight - 1, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(9, cypressHeight - 1, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(10, cypressHeight - 1, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(11, cypressHeight - 1, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(12, cypressHeight - 1, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-9, cypressHeight - 1, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-8, cypressHeight - 1, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-7, cypressHeight - 1, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-6, cypressHeight - 1, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-5, cypressHeight - 1, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-4, cypressHeight - 1, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-3, cypressHeight - 1, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-1, cypressHeight - 1, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(1, cypressHeight - 1, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(3, cypressHeight - 1, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(4, cypressHeight - 1, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(5, cypressHeight - 1, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(6, cypressHeight - 1, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(7, cypressHeight - 1, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(8, cypressHeight - 1, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(9, cypressHeight - 1, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(10, cypressHeight - 1, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(11, cypressHeight - 1, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-8, cypressHeight - 1, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-7, cypressHeight - 1, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-6, cypressHeight - 1, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-5, cypressHeight - 1, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-4, cypressHeight - 1, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-3, cypressHeight - 1, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-2, cypressHeight - 1, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-1, cypressHeight - 1, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(0, cypressHeight - 1, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(1, cypressHeight - 1, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(2, cypressHeight - 1, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(3, cypressHeight - 1, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(4, cypressHeight - 1, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(5, cypressHeight - 1, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(6, cypressHeight - 1, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(7, cypressHeight - 1, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(8, cypressHeight - 1, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(9, cypressHeight - 1, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-7, cypressHeight - 1, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-6, cypressHeight - 1, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-5, cypressHeight - 1, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-4, cypressHeight - 1, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-3, cypressHeight - 1, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-2, cypressHeight - 1, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-1, cypressHeight - 1, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(1, cypressHeight - 1, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(2, cypressHeight - 1, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(3, cypressHeight - 1, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(4, cypressHeight - 1, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(5, cypressHeight - 1, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(6, cypressHeight - 1, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(7, cypressHeight - 1, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(8, cypressHeight - 1, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(9, cypressHeight - 1, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-7, cypressHeight - 1, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-6, cypressHeight - 1, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-5, cypressHeight - 1, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-4, cypressHeight - 1, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-3, cypressHeight - 1, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-2, cypressHeight - 1, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-1, cypressHeight - 1, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(0, cypressHeight - 1, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(1, cypressHeight - 1, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(2, cypressHeight - 1, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(3, cypressHeight - 1, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(4, cypressHeight - 1, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(5, cypressHeight - 1, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(6, cypressHeight - 1, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(7, cypressHeight - 1, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(8, cypressHeight - 1, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-6, cypressHeight - 1, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-5, cypressHeight - 1, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-4, cypressHeight - 1, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-3, cypressHeight - 1, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-2, cypressHeight - 1, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-1, cypressHeight - 1, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(0, cypressHeight - 1, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(1, cypressHeight - 1, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(2, cypressHeight - 1, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(3, cypressHeight - 1, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(4, cypressHeight - 1, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(5, cypressHeight - 1, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(6, cypressHeight - 1, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-6, cypressHeight - 1, 5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-5, cypressHeight - 1, 5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-4, cypressHeight - 1, 5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-3, cypressHeight - 1, 5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-2, cypressHeight - 1, 5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-1, cypressHeight - 1, 5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(0, cypressHeight - 1, 5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(1, cypressHeight - 1, 5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(2, cypressHeight - 1, 5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(3, cypressHeight - 1, 5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(4, cypressHeight - 1, 5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-5, cypressHeight - 1, 6), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-4, cypressHeight - 1, 6), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-3, cypressHeight - 1, 6), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(0, cypressHeight - 1, 6), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(1, cypressHeight - 1, 6), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(2, cypressHeight - 1, 6), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(3, cypressHeight - 1, 6), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-4, cypressHeight - 1, 7), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-3, cypressHeight - 1, 7), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-4, cypressHeight - 1, 8), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(1, cypressHeight, -8), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(0, cypressHeight, -7), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(2, cypressHeight, -7), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-4, cypressHeight, -6), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-3, cypressHeight, -6), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-2, cypressHeight, -6), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-1, cypressHeight, -6), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(0, cypressHeight, -6), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(2, cypressHeight, -6), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(3, cypressHeight, -6), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-5, cypressHeight, -5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-4, cypressHeight, -5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-2, cypressHeight, -5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-1, cypressHeight, -5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(1, cypressHeight, -5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(3, cypressHeight, -5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(4, cypressHeight, -5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(5, cypressHeight, -5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-5, cypressHeight, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-4, cypressHeight, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-3, cypressHeight, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(1, cypressHeight, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(2, cypressHeight, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(5, cypressHeight, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(6, cypressHeight, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-6, cypressHeight, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-4, cypressHeight, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-3, cypressHeight, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-2, cypressHeight, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-1, cypressHeight, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(1, cypressHeight, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(4, cypressHeight, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(5, cypressHeight, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(6, cypressHeight, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(7, cypressHeight, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(8, cypressHeight, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-6, cypressHeight, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-5, cypressHeight, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-3, cypressHeight, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-2, cypressHeight, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-1, cypressHeight, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(1, cypressHeight, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(2, cypressHeight, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(3, cypressHeight, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(4, cypressHeight, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(5, cypressHeight, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(6, cypressHeight, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(7, cypressHeight, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(9, cypressHeight, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-6, cypressHeight, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-5, cypressHeight, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-3, cypressHeight, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-2, cypressHeight, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-1, cypressHeight, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(1, cypressHeight, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(2, cypressHeight, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(3, cypressHeight, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(4, cypressHeight, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(5, cypressHeight, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(8, cypressHeight, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(9, cypressHeight, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(10, cypressHeight, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-8, cypressHeight, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-7, cypressHeight, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-6, cypressHeight, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(6, cypressHeight, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(7, cypressHeight, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(8, cypressHeight, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(9, cypressHeight, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-7, cypressHeight, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-6, cypressHeight, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-5, cypressHeight, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-4, cypressHeight, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-3, cypressHeight, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-1, cypressHeight, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(1, cypressHeight, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(2, cypressHeight, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(3, cypressHeight, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(4, cypressHeight, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(5, cypressHeight, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(6, cypressHeight, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(7, cypressHeight, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(8, cypressHeight, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-7, cypressHeight, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-6, cypressHeight, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-5, cypressHeight, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-4, cypressHeight, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-2, cypressHeight, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-1, cypressHeight, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(1, cypressHeight, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(2, cypressHeight, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(3, cypressHeight, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(4, cypressHeight, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(5, cypressHeight, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(6, cypressHeight, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(7, cypressHeight, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-6, cypressHeight, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-5, cypressHeight, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-3, cypressHeight, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-2, cypressHeight, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-1, cypressHeight, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(1, cypressHeight, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(2, cypressHeight, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(5, cypressHeight, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(6, cypressHeight, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-5, cypressHeight, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-3, cypressHeight, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-2, cypressHeight, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-1, cypressHeight, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(1, cypressHeight, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(3, cypressHeight, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(4, cypressHeight, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(5, cypressHeight, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-5, cypressHeight, 5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-4, cypressHeight, 5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-3, cypressHeight, 5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-1, cypressHeight, 5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(0, cypressHeight, 5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(2, cypressHeight, 5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(3, cypressHeight, 5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-4, cypressHeight, 6), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(1, cypressHeight, 6), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(1, cypressHeight + 1, -7), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(1, cypressHeight + 1, -6), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-4, cypressHeight + 1, -5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-3, cypressHeight + 1, -5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-2, cypressHeight + 1, -5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(0, cypressHeight + 1, -5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(1, cypressHeight + 1, -5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(2, cypressHeight + 1, -5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(3, cypressHeight + 1, -5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-4, cypressHeight + 1, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-3, cypressHeight + 1, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-2, cypressHeight + 1, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-1, cypressHeight + 1, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(0, cypressHeight + 1, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(1, cypressHeight + 1, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(2, cypressHeight + 1, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(3, cypressHeight + 1, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(4, cypressHeight + 1, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(5, cypressHeight + 1, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-5, cypressHeight + 1, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-4, cypressHeight + 1, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-3, cypressHeight + 1, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-2, cypressHeight + 1, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-1, cypressHeight + 1, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(0, cypressHeight + 1, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(1, cypressHeight + 1, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(2, cypressHeight + 1, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(3, cypressHeight + 1, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(4, cypressHeight + 1, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(5, cypressHeight + 1, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(6, cypressHeight + 1, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-5, cypressHeight + 1, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-4, cypressHeight + 1, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-3, cypressHeight + 1, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-2, cypressHeight + 1, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-1, cypressHeight + 1, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(0, cypressHeight + 1, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(1, cypressHeight + 1, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(2, cypressHeight + 1, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(3, cypressHeight + 1, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(4, cypressHeight + 1, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(5, cypressHeight + 1, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(6, cypressHeight + 1, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(7, cypressHeight + 1, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(8, cypressHeight + 1, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-5, cypressHeight + 1, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-4, cypressHeight + 1, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-3, cypressHeight + 1, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-2, cypressHeight + 1, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-1, cypressHeight + 1, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(0, cypressHeight + 1, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(1, cypressHeight + 1, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(2, cypressHeight + 1, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(3, cypressHeight + 1, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(4, cypressHeight + 1, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(5, cypressHeight + 1, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(6, cypressHeight + 1, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(7, cypressHeight + 1, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(8, cypressHeight + 1, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-6, cypressHeight + 1, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-5, cypressHeight + 1, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-4, cypressHeight + 1, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-3, cypressHeight + 1, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-2, cypressHeight + 1, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-1, cypressHeight + 1, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(0, cypressHeight + 1, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(1, cypressHeight + 1, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(2, cypressHeight + 1, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(3, cypressHeight + 1, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(4, cypressHeight + 1, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(5, cypressHeight + 1, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(6, cypressHeight + 1, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(7, cypressHeight + 1, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-6, cypressHeight + 1, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-5, cypressHeight + 1, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-4, cypressHeight + 1, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-3, cypressHeight + 1, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-2, cypressHeight + 1, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-1, cypressHeight + 1, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(0, cypressHeight + 1, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(1, cypressHeight + 1, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(2, cypressHeight + 1, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(3, cypressHeight + 1, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(4, cypressHeight + 1, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(5, cypressHeight + 1, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(6, cypressHeight + 1, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-6, cypressHeight + 1, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-5, cypressHeight + 1, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-4, cypressHeight + 1, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-3, cypressHeight + 1, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-2, cypressHeight + 1, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-1, cypressHeight + 1, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(0, cypressHeight + 1, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(1, cypressHeight + 1, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(2, cypressHeight + 1, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(3, cypressHeight + 1, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(4, cypressHeight + 1, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(5, cypressHeight + 1, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(6, cypressHeight + 1, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-5, cypressHeight + 1, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-4, cypressHeight + 1, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-3, cypressHeight + 1, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-2, cypressHeight + 1, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-1, cypressHeight + 1, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(0, cypressHeight + 1, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(1, cypressHeight + 1, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(2, cypressHeight + 1, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(3, cypressHeight + 1, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(4, cypressHeight + 1, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(5, cypressHeight + 1, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-4, cypressHeight + 1, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-3, cypressHeight + 1, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(-1, cypressHeight + 1, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(0, cypressHeight + 1, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(1, cypressHeight + 1, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(2, cypressHeight + 1, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(3, cypressHeight + 1, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(4, cypressHeight + 1, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mainmutable.set(pos).move(1, cypressHeight + 1, 5), cypressTreeLeaves);
        }
    }

    private void buildCypressVariant2(DynamicTreeConfig config, WorldGenLevel level, Random random, BlockPos.MutableBlockPos mutablePos, BlockPos pos, BlockState cypressTreeLog, BlockState cypressTreeLeaves, BlockState cypressTreeWood)
    {
        int cypressHeight = config.minHeight() + random.nextInt(config.placement().height());

        if (pos.getY() + cypressHeight + 1 < level.getMaxBuildHeight())
        {
            mutablePos.set(pos).move(0, 0, -5).immutable();
            mutablePos.set(pos).move(-1, 0, -4).immutable();
            mutablePos.set(pos).move(1, 0, -4).immutable();
            mutablePos.set(pos).move(-4, 0, -1).immutable();
            mutablePos.set(pos).move(0, 0, -1).immutable();
            mutablePos.set(pos).move(4, 0, -1).immutable();
            mutablePos.set(pos).move(-5, 0, 0).immutable();
            mutablePos.set(pos).move(-1, 0, 0).immutable();
            mutablePos.set(pos).move(1, 0, 0).immutable();
            mutablePos.set(pos).move(5, 0, 0).immutable();
            mutablePos.set(pos).move(-4, 0, 1).immutable();
            mutablePos.set(pos).move(0, 0, 1).immutable();
            mutablePos.set(pos).move(4, 0, 1).immutable();
            mutablePos.set(pos).move(-1, 0, 4).immutable();
            mutablePos.set(pos).move(1, 0, 4).immutable();
            mutablePos.set(pos).move(0, 0, 5);
            mutablePos.set(pos);

            for (int buildTrunk = 0; buildTrunk <= cypressHeight; buildTrunk++)
            {
                placeTrunk(pos, random, level, mutablePos, cypressTreeWood);

                mutablePos.move(Direction.UP);
            }
            mutablePos.set(pos);

            //Stump
            placeBranch(pos, random, level, mutablePos.set(pos).move(0, 0, -5), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(-1, 0, -4), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(1, 0, -4), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(-4, 0, -1), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(0, 0, -1), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(4, 0, -1), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(-5, 0, 0), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(-1, 0, 0), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(1, 0, 0), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(5, 0, 0), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(-4, 0, 1), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(0, 0, 1), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(4, 0, 1), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(-1, 0, 4), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(1, 0, 4), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(0, 0, 5), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(0, 1, -4), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(-4, 1, 0), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(-1, 1, 0), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(1, 1, 0), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(4, 1, 0), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(0, 1, 4), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(0, 2, -3), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(-3, 2, 0), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(3, 2, 0), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(0, 2, 3), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(0, 3, -2), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(0, 3, -1), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(-2, 3, 0), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(-1, 3, 0), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(1, 3, 0), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(2, 3, 0), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(0, 3, 1), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(0, 3, 2), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(0, 4, -1), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(-1, 4, 0), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(1, 4, 0), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(0, 4, 1), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(-1, 5, 0), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(1, 5, 0), cypressTreeLog);

            //Branches
            placeBranch(pos, random, level, mutablePos.set(pos).move(1, cypressHeight - 7, -1), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight - 7, 1), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(1, cypressHeight - 6, -1), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight - 6, 2), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(2, cypressHeight - 5, -2), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(0, cypressHeight - 1, -2), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(0, cypressHeight - 1, -1), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(1, cypressHeight, -3), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight, 0), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight, 0), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight, 1), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(1, cypressHeight, 1), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(2, cypressHeight, 1), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(1, cypressHeight, 2), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight + 1, -1), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(3, cypressHeight + 1, 0), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(-4, cypressHeight + 1, 2), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(2, cypressHeight + 1, 3), cypressTreeLog);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-4, cypressHeight - 7, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight - 7, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight - 7, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight - 7, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-5, cypressHeight - 7, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-4, cypressHeight - 7, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight - 7, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight - 7, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight - 7, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-5, cypressHeight - 7, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-4, cypressHeight - 7, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight - 7, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight - 7, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight - 7, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight - 7, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-5, cypressHeight - 7, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-4, cypressHeight - 7, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight - 7, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight - 7, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight - 7, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight - 7, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight - 7, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-5, cypressHeight - 7, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-4, cypressHeight - 7, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight - 7, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight - 7, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight - 7, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight - 7, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight - 7, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-5, cypressHeight - 7, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-4, cypressHeight - 7, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight - 7, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight - 7, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight - 7, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight - 7, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight - 7, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-4, cypressHeight - 7, 5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight - 7, 5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight - 7, 5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight - 7, 5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight - 7, 5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight - 6, -5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight - 6, -5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight - 6, -5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, cypressHeight - 6, -5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(4, cypressHeight - 6, -5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight - 6, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight - 6, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight - 6, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight - 6, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, cypressHeight - 6, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(4, cypressHeight - 6, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(5, cypressHeight - 6, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight - 6, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight - 6, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight - 6, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight - 6, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, cypressHeight - 6, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(4, cypressHeight - 6, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(5, cypressHeight - 6, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight - 6, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight - 6, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight - 6, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight - 6, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, cypressHeight - 6, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(4, cypressHeight - 6, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(5, cypressHeight - 6, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight - 6, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight - 6, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight - 6, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, cypressHeight - 6, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(4, cypressHeight - 6, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(5, cypressHeight - 6, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight - 6, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight - 6, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight - 6, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight - 6, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight - 6, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, cypressHeight - 6, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(4, cypressHeight - 6, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(5, cypressHeight - 6, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-4, cypressHeight - 6, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight - 6, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight - 6, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight - 6, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight - 6, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight - 6, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight - 6, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, cypressHeight - 6, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(4, cypressHeight - 6, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-4, cypressHeight - 6, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight - 6, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight - 6, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight - 6, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-4, cypressHeight - 6, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight - 6, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight - 6, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight - 6, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight - 6, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight - 6, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight - 6, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight - 6, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight - 5, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight - 5, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, cypressHeight - 5, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight - 5, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight - 5, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight - 5, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, cypressHeight - 5, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(4, cypressHeight - 5, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight - 5, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight - 5, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, cypressHeight - 5, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(4, cypressHeight - 5, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight - 5, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight - 5, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight - 5, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, cypressHeight - 5, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(4, cypressHeight - 5, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight - 5, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight - 5, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, cypressHeight - 5, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight - 5, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight - 5, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight - 5, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight - 5, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight - 5, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight - 5, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight - 4, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight - 4, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight - 4, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, cypressHeight - 4, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight - 4, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight - 4, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight + 1, -6), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight + 1, -6), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight + 1, -6), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight + 1, -6), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight + 1, -6), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight + 1, -5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight + 1, -5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight + 1, -5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight + 1, -5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight + 1, -5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight + 1, -5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, cypressHeight + 1, -5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-5, cypressHeight + 1, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-4, cypressHeight + 1, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight + 1, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight + 1, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight + 1, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight + 1, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight + 1, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight + 1, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, cypressHeight + 1, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(4, cypressHeight + 1, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-6, cypressHeight + 1, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-5, cypressHeight + 1, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-4, cypressHeight + 1, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight + 1, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight + 1, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight + 1, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight + 1, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight + 1, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight + 1, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, cypressHeight + 1, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(4, cypressHeight + 1, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(5, cypressHeight + 1, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-6, cypressHeight + 1, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-5, cypressHeight + 1, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-4, cypressHeight + 1, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight + 1, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight + 1, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight + 1, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight + 1, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight + 1, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight + 1, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, cypressHeight + 1, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(4, cypressHeight + 1, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(5, cypressHeight + 1, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(6, cypressHeight + 1, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-6, cypressHeight + 1, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-5, cypressHeight + 1, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-4, cypressHeight + 1, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight + 1, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight + 1, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight + 1, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight + 1, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight + 1, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, cypressHeight + 1, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(4, cypressHeight + 1, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(5, cypressHeight + 1, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(6, cypressHeight + 1, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-6, cypressHeight + 1, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-5, cypressHeight + 1, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-4, cypressHeight + 1, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight + 1, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight + 1, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight + 1, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight + 1, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight + 1, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight + 1, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(4, cypressHeight + 1, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(5, cypressHeight + 1, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(6, cypressHeight + 1, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-6, cypressHeight + 1, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-5, cypressHeight + 1, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-4, cypressHeight + 1, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight + 1, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight + 1, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight + 1, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight + 1, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight + 1, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight + 1, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, cypressHeight + 1, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(4, cypressHeight + 1, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(5, cypressHeight + 1, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(6, cypressHeight + 1, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-6, cypressHeight + 1, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-5, cypressHeight + 1, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight + 1, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight + 1, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight + 1, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight + 1, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight + 1, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight + 1, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, cypressHeight + 1, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(4, cypressHeight + 1, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(5, cypressHeight + 1, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-5, cypressHeight + 1, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-4, cypressHeight + 1, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight + 1, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight + 1, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight + 1, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight + 1, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight + 1, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, cypressHeight + 1, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(4, cypressHeight + 1, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-5, cypressHeight + 1, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-4, cypressHeight + 1, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight + 1, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight + 1, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight + 1, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight + 1, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight + 1, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight + 1, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, cypressHeight + 1, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-4, cypressHeight + 1, 5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight + 1, 5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight + 1, 5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight + 1, 5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight + 1, 5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight + 1, 5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight + 1, 5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight + 1, 6), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight + 1, 6), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight + 1, 6), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight + 1, 6), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight + 2, -5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight + 2, -5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight + 2, -5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight + 2, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight + 2, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight + 2, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight + 2, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight + 2, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight + 2, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, cypressHeight + 2, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-4, cypressHeight + 2, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight + 2, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight + 2, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight + 2, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight + 2, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight + 2, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight + 2, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, cypressHeight + 2, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-5, cypressHeight + 2, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-4, cypressHeight + 2, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight + 2, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight + 2, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight + 2, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight + 2, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight + 2, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight + 2, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, cypressHeight + 2, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(4, cypressHeight + 2, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-5, cypressHeight + 2, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-4, cypressHeight + 2, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight + 2, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight + 2, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight + 2, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight + 2, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight + 2, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight + 2, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, cypressHeight + 2, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(4, cypressHeight + 2, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(5, cypressHeight + 2, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-5, cypressHeight + 2, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-4, cypressHeight + 2, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight + 2, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight + 2, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight + 2, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight + 2, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight + 2, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight + 2, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, cypressHeight + 2, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(4, cypressHeight + 2, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(5, cypressHeight + 2, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-5, cypressHeight + 2, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-4, cypressHeight + 2, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight + 2, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight + 2, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight + 2, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight + 2, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight + 2, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight + 2, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, cypressHeight + 2, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(4, cypressHeight + 2, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(5, cypressHeight + 2, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-5, cypressHeight + 2, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-4, cypressHeight + 2, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight + 2, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight + 2, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight + 2, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight + 2, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight + 2, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight + 2, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, cypressHeight + 2, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(4, cypressHeight + 2, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-5, cypressHeight + 2, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-4, cypressHeight + 2, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight + 2, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight + 2, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight + 2, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight + 2, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight + 2, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight + 2, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, cypressHeight + 2, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-4, cypressHeight + 2, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight + 2, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight + 2, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight + 2, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight + 2, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight + 2, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight + 2, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight + 2, 5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight + 2, 5), cypressTreeLeaves);
        }
    }

    private void buildCypressVariant3(DynamicTreeConfig config, WorldGenLevel level, Random random, BlockPos.MutableBlockPos mutablePos, BlockPos pos, BlockState cypressTreeLog, BlockState cypressTreeLeaves, BlockState cypressTreeWood)
    {
        int cypressHeight = config.minHeight() + random.nextInt(config.placement().height());

        if (pos.getY() + cypressHeight + 1 < level.getMaxBuildHeight())
        {
            mutablePos.set(pos).move(-1, 0, -4);
            mutablePos.set(pos).move(1, 0, -4).immutable();
            mutablePos.set(pos).move(-4, 0, -1).immutable();
            mutablePos.set(pos).move(4, 0, -1).immutable();
            mutablePos.set(pos).move(-4, 0, 1).immutable();
            mutablePos.set(pos).move(4, 0, 1).immutable();
            mutablePos.set(pos).move(-1, 0, 4).immutable();
            mutablePos.set(pos).move(1, 0, 4).immutable();
            mutablePos.set(pos);

            for (int buildTrunk = 0; buildTrunk <= cypressHeight; buildTrunk++)
            {
                placeTrunk(pos, random, level, mutablePos, cypressTreeWood);

                mutablePos.move(Direction.UP);
            }
            mutablePos.set(pos);
            //Stump
            placeBranch(pos, random, level, mutablePos.set(pos).move(-1, 0, -4), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(1, 0, -4), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(-4, 0, -1), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(4, 0, -1), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(-4, 0, 1), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(4, 0, 1), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(-1, 0, 4), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(1, 0, 4), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(0, 1, -3), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(-3, 1, 0), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(3, 1, 0), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(0, 1, 3), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(0, 2, -2), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(0, 2, -1), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(-2, 2, 0), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(-1, 2, 0), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(1, 2, 0), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(2, 2, 0), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(0, 2, 1), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(2, 2, 1), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(0, 2, 2), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(0, 3, -1), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(-1, 3, 0), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(1, 3, 0), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(0, 3, 1), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(2, 3, 1), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(-2, 4, -1), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(-1, 4, 0), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(1, 4, 0), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(3, 4, 2), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(-2, 5, -1), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(-3, 6, -2), cypressTreeLog);


            //Top Branches
            placeBranch(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight - 5, 1), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(1, cypressHeight - 4, -1), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight - 4, 1), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(1, cypressHeight - 3, -2), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(1, cypressHeight - 3, -1), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight - 3, 2), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(2, cypressHeight - 2, -2), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight, 0), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight, 1), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight + 1, -2), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight + 1, -1), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(1, cypressHeight + 1, 1), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(2, cypressHeight + 1, 1), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight + 1, 2), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight + 2, -3), cypressTreeLog);
            placeBranch(pos, random, level, mutablePos.set(pos).move(3, cypressHeight + 2, 0), cypressTreeLog);

            //Stump Leaves
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, 3, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, 3, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(4, 3, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(5, 3, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, 3, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, 3, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(4, 3, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(5, 3, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(6, 3, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, 3, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, 3, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(4, 3, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(5, 3, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(6, 3, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, 3, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, 3, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, 3, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, 3, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(4, 3, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(5, 3, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(6, 3, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, 3, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, 3, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, 3, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, 3, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(4, 3, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(5, 3, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(6, 3, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, 3, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, 3, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, 3, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, 3, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(4, 3, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(5, 3, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(6, 3, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, 3, 5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, 3, 5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, 3, 5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(4, 3, 5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(5, 3, 5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, 4, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, 4, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(4, 4, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, 4, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, 4, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, 4, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(4, 4, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(5, 4, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, 4, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, 4, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(4, 4, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(5, 4, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, 4, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, 4, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, 4, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(4, 4, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(5, 4, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, 4, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, 4, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(4, 4, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, 5, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-4, 5, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, 5, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, 5, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, 5, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, 5, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, 5, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, 5, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, 5, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(4, 5, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, 5, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-4, 6, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, 6, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, 6, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-5, 6, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-4, 6, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, 6, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, 6, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, 6, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-5, 6, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-4, 6, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, 6, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, 6, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-5, 6, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-4, 6, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, 6, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, 6, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, 6, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-4, 6, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, 6, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, 6, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, 7, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-4, 7, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, 7, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, 7, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, 7, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, 7, -1), cypressTreeLeaves);

            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight - 3, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight - 3, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight - 3, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-4, cypressHeight - 3, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight - 3, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight - 3, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight - 3, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight - 3, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-4, cypressHeight - 3, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight - 3, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight - 3, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight - 3, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-4, cypressHeight - 3, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight - 3, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight - 3, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight - 3, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight - 3, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight - 3, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight - 3, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight - 3, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight - 2, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight - 2, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, cypressHeight - 2, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight - 2, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight - 2, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight - 2, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, cypressHeight - 2, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(4, cypressHeight - 2, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight - 2, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight - 2, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, cypressHeight - 2, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(4, cypressHeight - 2, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight - 2, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight - 2, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight - 2, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, cypressHeight - 2, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(4, cypressHeight - 2, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight - 2, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight - 2, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, cypressHeight - 2, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight - 2, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight - 2, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight - 2, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight - 2, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight - 2, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight - 2, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight - 1, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, cypressHeight - 1, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight - 1, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight - 1, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight - 1, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight - 1, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, cypressHeight - 1, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight + 2, -6), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight + 2, -6), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight + 2, -6), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight + 2, -5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight + 2, -5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight + 2, -5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight + 2, -5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight + 2, -5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-4, cypressHeight + 2, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight + 2, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight + 2, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight + 2, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight + 2, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight + 2, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight + 2, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, cypressHeight + 2, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-4, cypressHeight + 2, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight + 2, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight + 2, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight + 2, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight + 2, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight + 2, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, cypressHeight + 2, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(4, cypressHeight + 2, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-5, cypressHeight + 2, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-4, cypressHeight + 2, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight + 2, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight + 2, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight + 2, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight + 2, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight + 2, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight + 2, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, cypressHeight + 2, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(4, cypressHeight + 2, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(5, cypressHeight + 2, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-6, cypressHeight + 2, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-5, cypressHeight + 2, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-4, cypressHeight + 2, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight + 2, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight + 2, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight + 2, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight + 2, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight + 2, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight + 2, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, cypressHeight + 2, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(4, cypressHeight + 2, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(5, cypressHeight + 2, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-6, cypressHeight + 2, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-5, cypressHeight + 2, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-4, cypressHeight + 2, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight + 2, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight + 2, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight + 2, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight + 2, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight + 2, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight + 2, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(4, cypressHeight + 2, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(5, cypressHeight + 2, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-6, cypressHeight + 2, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-5, cypressHeight + 2, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-4, cypressHeight + 2, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight + 2, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight + 2, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight + 2, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight + 2, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight + 2, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight + 2, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, cypressHeight + 2, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(4, cypressHeight + 2, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(5, cypressHeight + 2, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-5, cypressHeight + 2, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-4, cypressHeight + 2, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight + 2, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight + 2, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight + 2, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight + 2, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight + 2, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight + 2, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, cypressHeight + 2, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(4, cypressHeight + 2, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-4, cypressHeight + 2, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight + 2, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight + 2, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight + 2, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight + 2, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight + 2, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight + 2, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, cypressHeight + 2, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(4, cypressHeight + 2, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight + 2, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight + 2, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight + 2, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight + 2, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight + 2, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight + 2, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, cypressHeight + 2, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(4, cypressHeight + 2, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight + 2, 5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight + 2, 5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight + 2, 5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight + 2, 5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight + 2, 5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, cypressHeight + 2, 5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight + 2, 6), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight + 2, 6), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight + 2, 6), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight + 2, 6), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight + 3, -5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight + 3, -5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight + 3, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight + 3, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight + 3, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight + 3, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight + 3, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight + 3, -4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight + 3, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight + 3, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight + 3, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight + 3, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight + 3, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight + 3, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, cypressHeight + 3, -3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-4, cypressHeight + 3, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight + 3, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight + 3, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight + 3, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight + 3, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight + 3, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight + 3, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, cypressHeight + 3, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(4, cypressHeight + 3, -2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-4, cypressHeight + 3, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight + 3, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight + 3, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight + 3, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight + 3, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight + 3, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight + 3, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, cypressHeight + 3, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(4, cypressHeight + 3, -1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-5, cypressHeight + 3, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-4, cypressHeight + 3, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight + 3, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight + 3, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight + 3, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight + 3, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight + 3, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight + 3, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, cypressHeight + 3, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(4, cypressHeight + 3, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(5, cypressHeight + 3, 0), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-5, cypressHeight + 3, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-4, cypressHeight + 3, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight + 3, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight + 3, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight + 3, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight + 3, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight + 3, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight + 3, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, cypressHeight + 3, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(4, cypressHeight + 3, 1), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-4, cypressHeight + 3, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight + 3, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight + 3, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight + 3, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight + 3, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight + 3, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight + 3, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, cypressHeight + 3, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(4, cypressHeight + 3, 2), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-3, cypressHeight + 3, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight + 3, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight + 3, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight + 3, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight + 3, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight + 3, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, cypressHeight + 3, 3), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-2, cypressHeight + 3, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(-1, cypressHeight + 3, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight + 3, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight + 3, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(2, cypressHeight + 3, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(3, cypressHeight + 3, 4), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(0, cypressHeight + 3, 5), cypressTreeLeaves);
            placeLeaves(pos, random, level, mutablePos.set(pos).move(1, cypressHeight + 3, 5), cypressTreeLeaves);
        }
    }

    public boolean isFreshWater(LevelAccessor level, BlockPos pos)
    {
        if (level != null && level.getBlockState(pos) != null)
        {
            BlockState state = level.getBlockState(pos);
            if (state.getMaterial() != null)
            {
                return Helpers.isBlock(state, Blocks.WATER);
            }
            else
            {
                TFCFlorae.LOGGER.info("TFCFlorae: stateMat is null");
            }
        }
        else
        {
            TFCFlorae.LOGGER.info("TFCFlorae: level or state is null");
            if (level.getBlockState(pos) != null)
            {
                TFCFlorae.LOGGER.info("TFCFlorae: state is null");
            }
        }
        return false;
    }

    public boolean canLogPlaceHere(LevelAccessor level, BlockPos pos)
    {
        if (level != null && level.getBlockState(pos) != null)
        {
            BlockState state = level.getBlockState(pos);
            if (state.getMaterial() != null)
            {
                return state.isAir() || Helpers.isBlock(state, Blocks.WATER) || EnvironmentHelpers.isWorldgenReplaceable(state) || Helpers.isBlock(state.getBlock(), BlockTags.LEAVES);
            }
            else
            {
                TFCFlorae.LOGGER.info("TFCFlorae: stateMat is null");
            }
        }
        else
        {
            TFCFlorae.LOGGER.info("TFCFlorae: level or state is null");
            if (level.getBlockState(pos) != null)
            {
                TFCFlorae.LOGGER.info("TFCFlorae: state is null");
            }
        }
        return false;
    }

    public void placeTrunk(BlockPos startPos, Random random, WorldGenLevel level, BlockPos pos, BlockState state)
    {
        if (canLogPlaceHere(level, pos) || isFreshWater(level, pos))
        {
            this.setFinalBlockState(level, pos, state);
        }
    }

    public void placeBranch(BlockPos startPos, Random random, WorldGenLevel level, BlockPos pos, BlockState state)
    {
        if (canLogPlaceHere(level, pos) || isFreshWater(level, pos))
        {
            this.setFinalBlockState(level, pos, state);
        }
    }

    private void placeLeaves(BlockPos startPos, Random random, WorldGenLevel level, BlockPos pos, BlockState state)
    {
        if (isAir(level, pos))
        {
            this.setFinalBlockState(level, pos, state);
        }
    }

    public final void setFinalBlockState(WorldGenLevel  level, BlockPos pos, BlockState blockState)
    {
        this.setBlockStateWithoutUpdates(level, pos, blockState);
    }

    public void setBlockStateWithoutUpdates(WorldGenLevel  level, BlockPos pos, BlockState blockState)
    {
        level.setBlock(pos, blockState, 18);
    }

    public void setBlockStateWithoutUpdates(WorldGenLevel  level, BlockPos pos, BlockState blockState, int flags)
    {
        level.setBlock(pos, blockState, flags);
    }

    public BlockPos extractOffset(BlockPos startPos, BlockPos pos)
    {
        return new BlockPos(startPos.getX() - pos.getX(), pos.getY(), startPos.getZ() - pos.getZ());
    }

    public static boolean isLake(BiomeExtension biome)
    {
        return biome == TFCBiomes.LAKE || biome == TFCBiomes.OCEANIC_MOUNTAIN_LAKE || biome == TFCBiomes.OLD_MOUNTAIN_LAKE || biome == TFCBiomes.MOUNTAIN_LAKE || biome == TFCBiomes.VOLCANIC_OCEANIC_MOUNTAIN_LAKE || biome == TFCBiomes.VOLCANIC_MOUNTAIN_LAKE || biome == TFCBiomes.PLATEAU_LAKE;
    }

    public static boolean isRiver(BiomeExtension biome)
    {
        return biome == TFCBiomes.RIVER || biome == TFCBiomes.OCEANIC_MOUNTAIN_RIVER || biome == TFCBiomes.OLD_MOUNTAIN_RIVER || biome == TFCBiomes.MOUNTAIN_RIVER || biome == TFCBiomes.VOLCANIC_OCEANIC_MOUNTAIN_RIVER || biome == TFCBiomes.VOLCANIC_MOUNTAIN_RIVER;
    }

    public static boolean isLow(BiomeExtension biome)
    {
        return biome == TFCBiomes.LOW_CANYONS || biome == TFCBiomes.LOWLANDS;
    }
}