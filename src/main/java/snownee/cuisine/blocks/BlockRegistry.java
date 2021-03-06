package snownee.cuisine.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.tiles.TileBarbecueRack;
import snownee.cuisine.tiles.TileChoppingBoard;
import snownee.cuisine.tiles.TileDish;
import snownee.cuisine.tiles.TileJar;
import snownee.cuisine.tiles.TileMill;
import snownee.cuisine.tiles.TileMortar;
import snownee.cuisine.tiles.TileWok;

@Mod.EventBusSubscriber(modid = Cuisine.MODID)
public final class BlockRegistry
{

    @SubscribeEvent
    public static void onBlockRegister(RegistryEvent.Register<Block> event)
    {
        GameRegistry.registerTileEntity(TileMortar.class, new ResourceLocation(Cuisine.MODID, "mortar"));
        GameRegistry.registerTileEntity(TileMill.class, new ResourceLocation(Cuisine.MODID, "mill"));
        GameRegistry.registerTileEntity(TileChoppingBoard.class, new ResourceLocation(Cuisine.MODID, "chopping_board"));
        GameRegistry.registerTileEntity(TileJar.class, new ResourceLocation(Cuisine.MODID, "jar"));
        GameRegistry.registerTileEntity(TileWok.class, new ResourceLocation(Cuisine.MODID, "wok"));
        GameRegistry.registerTileEntity(TileBarbecueRack.class, new ResourceLocation(Cuisine.MODID, "barbecue_rack"));
        GameRegistry.registerTileEntity(TileDish.class, new ResourceLocation(Cuisine.MODID, "placed_dish"));

        CuisineFluids.registerFluids(event);
    }

    @SubscribeEvent
    public static void onCropsGrowPost(BlockEvent.CropGrowEvent event)
    {
        IBlockState state = event.getState();
        if (state.getBlock() instanceof BlockCorn && ((BlockCorn) state.getBlock()).getAge(state, event.getWorld(), event.getPos()) > 1)
        {
            event.getWorld().setBlockState(event.getPos().up(), ((BlockCorn) state.getBlock()).withAge(8));
        }
    }

    @SubscribeEvent
    public static void onChoppingBoardClick(PlayerInteractEvent.LeftClickBlock event)
    {
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        IBlockState state = world.getBlockState(pos);
        if (event.isCanceled() || !event.getEntityPlayer().isCreative() || state.getBlock() != CuisineRegistry.CHOPPING_BOARD)
        {
            return;
        }
        ItemStack stack = event.getItemStack();
        if (stack.getItem() == CuisineRegistry.KITCHEN_KNIFE || stack.getItem().getToolClasses(stack).contains("axe"))
        {
            event.setCanceled(true);
            state.getBlock().onBlockClicked(world, pos, event.getEntityPlayer());
        }
    }

}
