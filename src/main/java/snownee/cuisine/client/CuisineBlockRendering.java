package snownee.cuisine.client;

import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.animation.AnimationTESR;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import snownee.cuisine.Cuisine;
import snownee.cuisine.blocks.CuisineBlocks;
import snownee.cuisine.client.renderer.TESRBarbecueRack;
import snownee.cuisine.client.renderer.TESRChoppingBoard;
import snownee.cuisine.client.renderer.TESRMortar;
import snownee.cuisine.client.renderer.TESRWok;
import snownee.cuisine.tiles.TileBarbecueRack;
import snownee.cuisine.tiles.TileChoppingBoard;
import snownee.cuisine.tiles.TileMill;
import snownee.cuisine.tiles.TileMortar;
import snownee.cuisine.tiles.TileWok;
import snownee.kiwi.client.ModelUtil;

@Mod.EventBusSubscriber(modid = Cuisine.MODID, value = Side.CLIENT)
public final class CuisineBlockRendering
{

    @SubscribeEvent
    public static void onModelRegister(ModelRegistryEvent event)
    {
        ModelUtil.mapFluidModel((BlockFluidBase) CuisineBlocks.SOY_MILK); // Work around. Belows are the same.
        ModelUtil.mapFluidModel((BlockFluidBase) CuisineBlocks.SOY_SAUCE);
        ModelUtil.mapFluidModel((BlockFluidBase) CuisineBlocks.FRUIT_VINEGAR);
        ModelUtil.mapFluidModel((BlockFluidBase) CuisineBlocks.RICE_VINEGAR);
        ModelUtil.mapFluidModel((BlockFluidBase) CuisineBlocks.EDIBLE_OIL);
        ModelUtil.mapFluidModel((BlockFluidBase) CuisineBlocks.SESAME_OIL);

        ClientRegistry.bindTileEntitySpecialRenderer(TileMortar.class, new TESRMortar());
        ClientRegistry.bindTileEntitySpecialRenderer(TileMill.class, new AnimationTESR<>());
        ClientRegistry.bindTileEntitySpecialRenderer(TileChoppingBoard.class, new TESRChoppingBoard());
        ClientRegistry.bindTileEntitySpecialRenderer(TileWok.class, new TESRWok());
        ClientRegistry.bindTileEntitySpecialRenderer(TileBarbecueRack.class, new TESRBarbecueRack());
    }

    @SubscribeEvent
    public static void onBlockColorsInit(ColorHandlerEvent.Block event)
    {
        //        if (!CuisineConfig.GENERAL.disableEssence)
        //        {
        //        BlockColors blockColors = event.getBlockColors();
        //        blockColors.registerBlockColorHandler((state, worldIn, pos, tintIndex) -> {
        //            if (worldIn != null && pos != null)
        //            {
        //                return BiomeColorHelper.getGrassColorAtPos(worldIn, pos);
        //            }
        //            else
        //            {
        //                return ColorizerGrass.getGrassColor(0.5D, 1.0D);
        //            }
        //        }, CuisineRegistry.GARDEN);
    }
    //    }

}
