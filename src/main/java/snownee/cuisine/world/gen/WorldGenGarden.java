package snownee.cuisine.world.gen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeOcean;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import snownee.cuisine.CuisineConfig;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.blocks.BlockCuisineCrops;

@SuppressWarnings("deprecation")
public class WorldGenGarden
{
    public static class DropPool // loot table?
    {
        public static final Block[] INSTANCE = new Block[] { CuisineRegistry.CHINESE_CABBAGE, CuisineRegistry.CORN,
                CuisineRegistry.CUCUMBER, CuisineRegistry.EGGPLANT, CuisineRegistry.GINGER,
                CuisineRegistry.GREEN_PEPPER, CuisineRegistry.LEEK, CuisineRegistry.LETTUCE, CuisineRegistry.ONION,
                CuisineRegistry.PEANUT, CuisineRegistry.RED_PEPPER, CuisineRegistry.SCALLION, CuisineRegistry.SESAME,
                CuisineRegistry.SICHUAN_PEPPER, CuisineRegistry.SOYBEAN, CuisineRegistry.SPINACH,
                CuisineRegistry.TOMATO, CuisineRegistry.TURNIP, Blocks.CARROTS, Blocks.POTATOES, Blocks.WHEAT };

        public static Block draw(Random rand)
        {
            return INSTANCE[rand.nextInt(INSTANCE.length)];
        }
    }

    @SubscribeEvent
    public void decorateEvent(DecorateBiomeEvent.Decorate event)
    {
        World worldIn = event.getWorld();
        if (worldIn.provider.getDimension() == 0 && event.getType() == Decorate.EventType.PUMPKIN)
        {
            Random rand = event.getRand();
            BlockPos position = event.getPos().add(rand.nextInt(16) + 1, 128, rand.nextInt(16) + 1);

            Biome biome = worldIn.getBiome(position);

            if (!biome.canRain() || biome.topBlock.getMaterial() != Material.GRASS || biome instanceof BiomeOcean || rand.nextDouble() > biome.getDefaultTemperature() || rand.nextInt(CuisineConfig.GENERAL.cropsGenRate) > 0)
            {
                return;
            }

            for (IBlockState iblockstate = worldIn.getBlockState(position); (iblockstate.getBlock().isAir(iblockstate, worldIn, position) || iblockstate.getBlock().isLeaves(iblockstate, worldIn, position)) && position.getY() > 0; iblockstate = worldIn.getBlockState(position))
            {
                position = position.down();
            }
            // position = position.up();

            Block plant = DropPool.draw(rand);
            plant(worldIn, position, plant, biome.topBlock.getBlock(), rand);
            plant(worldIn, position.offset(EnumFacing.byHorizontalIndex(rand.nextInt(4))), plant, biome.topBlock.getBlock(), rand);
            plant(worldIn, position.offset(EnumFacing.byHorizontalIndex(rand.nextInt(4))), plant, biome.topBlock.getBlock(), rand);
        }
    }

    private static void plant(World world, BlockPos pos, Block block, Block replacedBlock, Random rand)
    {
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() == replacedBlock)
        {
            world.setBlockState(pos, Blocks.FARMLAND.getDefaultState(), 0);
            if (block instanceof BlockCuisineCrops)
            {
                BlockCuisineCrops blockCuisineCrops = (BlockCuisineCrops) block;
                world.setBlockState(pos.up(), block == CuisineRegistry.CORN ? block.getDefaultState()
                        : blockCuisineCrops.withAge(rand.nextInt(blockCuisineCrops.getMaxAge())), 0);
            }
            else if (block instanceof BlockCrops)
            {
                BlockCrops blockCrops = (BlockCrops) block;
                world.setBlockState(pos.up(), blockCrops.withAge(rand.nextInt(blockCrops.getMaxAge())), 0);
            }
        }
    }

}
