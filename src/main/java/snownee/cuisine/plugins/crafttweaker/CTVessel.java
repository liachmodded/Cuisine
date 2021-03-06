package snownee.cuisine.plugins.crafttweaker;

import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.oredict.IOreDictEntry;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import snownee.cuisine.api.process.Processing;
import snownee.cuisine.api.process.Vessel;
import snownee.kiwi.crafting.input.ProcessingInput;
import snownee.kiwi.util.definition.ItemDefinition;
import snownee.kiwi.util.definition.OreDictDefinition;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.cuisine.Vessel")
@ZenRegister
public class CTVessel
{

    @ZenMethod
    public static void add(IItemStack input, ILiquidStack inputFluid, IItemStack output, ILiquidStack outputFluid, IItemStack extra)
    {
        ItemDefinition actualInput = ItemDefinition.of(CraftTweakerMC.getItemStack(input));
        ItemDefinition actualOutput = ItemDefinition.of(CraftTweakerMC.getItemStack(output));
        Fluid actualInputFluid = CraftTweakerMC.getLiquidStack(inputFluid).getFluid();
        FluidStack actualOutputFluid = CraftTweakerMC.getLiquidStack(outputFluid);
        ItemDefinition actualExtra = ItemDefinition.of(CraftTweakerMC.getItemStack(extra));
        CTSupport.DELAYED_ACTIONS.add(new Addition(actualInput, actualInputFluid, actualExtra, actualOutput, actualOutputFluid));
    }

    @ZenMethod
    public static void add(IItemStack input, ILiquidStack inputFluid, IItemStack output, ILiquidStack outputFluid, IOreDictEntry extra)
    {
        ItemDefinition actualInput = ItemDefinition.of(CraftTweakerMC.getItemStack(input));
        ItemDefinition actualOutput = ItemDefinition.of(CraftTweakerMC.getItemStack(output));
        Fluid actualInputFluid = CraftTweakerMC.getLiquidStack(inputFluid).getFluid();
        FluidStack actualOutputFluid = CraftTweakerMC.getLiquidStack(outputFluid);
        OreDictDefinition actualExtra = CTSupport.fromOreEntry(extra);
        CTSupport.DELAYED_ACTIONS.add(new Addition(actualInput, actualInputFluid, actualExtra, actualOutput, actualOutputFluid));
    }

    @ZenMethod
    public static void add(IOreDictEntry input, ILiquidStack inputFluid, IItemStack output, ILiquidStack outputFluid, IItemStack extra)
    {
        OreDictDefinition actualInput = CTSupport.fromOreEntry(input);
        ItemDefinition actualOutput = ItemDefinition.of(CraftTweakerMC.getItemStack(output));
        Fluid actualInputFluid = CraftTweakerMC.getLiquidStack(inputFluid).getFluid();
        FluidStack actualOutputFluid = CraftTweakerMC.getLiquidStack(outputFluid);
        ItemDefinition actualExtra = ItemDefinition.of(CraftTweakerMC.getItemStack(extra));
        CTSupport.DELAYED_ACTIONS.add(new Addition(actualInput, actualInputFluid, actualExtra, actualOutput, actualOutputFluid));
    }

    @ZenMethod
    public static void add(IOreDictEntry input, ILiquidStack inputFluid, IItemStack output, ILiquidStack outputFluid, IOreDictEntry extra)
    {
        OreDictDefinition actualInput = CTSupport.fromOreEntry(input);
        ItemDefinition actualOutput = ItemDefinition.of(CraftTweakerMC.getItemStack(output));
        Fluid actualInputFluid = CraftTweakerMC.getLiquidStack(inputFluid).getFluid();
        FluidStack actualOutputFluid = CraftTweakerMC.getLiquidStack(outputFluid);
        OreDictDefinition actualExtra = CTSupport.fromOreEntry(extra);
        CTSupport.DELAYED_ACTIONS.add(new Addition(actualInput, actualInputFluid, actualExtra, actualOutput, actualOutputFluid));
    }

    @ZenMethod
    public static void add(IItemStack input, ILiquidStack inputFluid, IItemStack output, ILiquidStack outputFluid)
    {
        ItemDefinition actualInput = ItemDefinition.of(CraftTweakerMC.getItemStack(input));
        ItemDefinition actualOutput = ItemDefinition.of(CraftTweakerMC.getItemStack(output));
        Fluid actualInputFluid = CraftTweakerMC.getLiquidStack(inputFluid).getFluid();
        FluidStack actualOutputFluid = CraftTweakerMC.getLiquidStack(outputFluid);
        CTSupport.DELAYED_ACTIONS.add(new Addition(actualInput, actualInputFluid, ItemDefinition.EMPTY, actualOutput, actualOutputFluid));
    }

    @ZenMethod
    public static void add(IOreDictEntry input, ILiquidStack inputFluid, IItemStack output, ILiquidStack outputFluid)
    {
        OreDictDefinition actualInput = CTSupport.fromOreEntry(input);
        ItemDefinition actualOutput = ItemDefinition.of(CraftTweakerMC.getItemStack(output));
        Fluid actualInputFluid = CraftTweakerMC.getLiquidStack(inputFluid).getFluid();
        FluidStack actualOutputFluid = CraftTweakerMC.getLiquidStack(outputFluid);
        CTSupport.DELAYED_ACTIONS.add(new Addition(actualInput, actualInputFluid, ItemDefinition.EMPTY, actualOutput, actualOutputFluid));
    }

    @ZenMethod
    public static void remove(IItemStack input, ILiquidStack inputFluid, IItemStack extra)
    {
        ItemDefinition actualInput = ItemDefinition.of(CraftTweakerMC.getItemStack(input));
        Fluid actualInputFluid = CraftTweakerMC.getLiquidStack(inputFluid).getFluid();
        ItemDefinition actualExtra = ItemDefinition.of(CraftTweakerMC.getItemStack(extra));
        CTSupport.DELAYED_ACTIONS.add(new Removal(actualInput, actualInputFluid, actualExtra));
    }

    @ZenMethod
    public static void remove(IItemStack input, ILiquidStack inputFluid, IOreDictEntry extra)
    {
        ItemDefinition actualInput = ItemDefinition.of(CraftTweakerMC.getItemStack(input));
        Fluid actualInputFluid = CraftTweakerMC.getLiquidStack(inputFluid).getFluid();
        OreDictDefinition actualExtra = CTSupport.fromOreEntry(extra);
        CTSupport.DELAYED_ACTIONS.add(new Removal(actualInput, actualInputFluid, actualExtra));
    }

    @ZenMethod
    public static void remove(IOreDictEntry input, ILiquidStack inputFluid, IItemStack extra)
    {
        OreDictDefinition actualInput = CTSupport.fromOreEntry(input);
        Fluid actualInputFluid = CraftTweakerMC.getLiquidStack(inputFluid).getFluid();
        ItemDefinition actualExtra = ItemDefinition.of(CraftTweakerMC.getItemStack(extra));
        CTSupport.DELAYED_ACTIONS.add(new Removal(actualInput, actualInputFluid, actualExtra));
    }

    @ZenMethod
    public static void remove(IOreDictEntry input, ILiquidStack inputFluid, IOreDictEntry extra)
    {
        OreDictDefinition actualInput = CTSupport.fromOreEntry(input);
        Fluid actualInputFluid = CraftTweakerMC.getLiquidStack(inputFluid).getFluid();
        OreDictDefinition actualExtra = CTSupport.fromOreEntry(extra);
        CTSupport.DELAYED_ACTIONS.add(new Removal(actualInput, actualInputFluid, actualExtra));
    }

    @ZenMethod
    public static void remove(IItemStack input, ILiquidStack inputFluid)
    {
        ItemDefinition actualInput = ItemDefinition.of(CraftTweakerMC.getItemStack(input));
        Fluid actualInputFluid = CraftTweakerMC.getLiquidStack(inputFluid).getFluid();
        CTSupport.DELAYED_ACTIONS.add(new Removal(actualInput, actualInputFluid, ItemDefinition.EMPTY));
    }

    @ZenMethod
    public static void remove(IOreDictEntry input, ILiquidStack inputFluid)
    {
        OreDictDefinition actualInput = CTSupport.fromOreEntry(input);
        Fluid actualInputFluid = CraftTweakerMC.getLiquidStack(inputFluid).getFluid();
        CTSupport.DELAYED_ACTIONS.add(new Removal(actualInput, actualInputFluid, ItemDefinition.EMPTY));
    }

    @ZenMethod
    public static void removeAll()
    {
        CTSupport.DELAYED_ACTIONS.add(new RemoveAll());
    }

    private static final class Addition implements IAction
    {

        private final ProcessingInput actualInput;
        private final Fluid actualInputFluid;
        private final ProcessingInput actualExtra;
        private final ItemDefinition actualOutput;
        private final FluidStack actualOutputFluid;

        Addition(ProcessingInput actualInput, Fluid actualInputFluid, ProcessingInput actualExtra, ItemDefinition actualOutput, FluidStack actualOutputFluid)
        {
            this.actualInput = actualInput;
            this.actualInputFluid = actualInputFluid;
            this.actualExtra = actualExtra;
            this.actualOutput = actualOutput;
            this.actualOutputFluid = actualOutputFluid;
        }

        @Override
        public void apply()
        {
            Processing.VESSEL.add(new Vessel(actualInput, actualInputFluid, actualOutput, actualOutputFluid, actualExtra));
        }

        @Override
        public String describe()
        {
            return String.format("Add Cuisine Vessel recipe: %s + %s + %s -> %s + %s", actualInput, actualInputFluid, actualExtra, actualOutput, actualOutputFluid);
        }
    }

    private static final class Removal implements IAction
    {
        private final ProcessingInput actualInput;
        private final Fluid actualInputFluid;
        private final ProcessingInput actualExtra;

        Removal(ProcessingInput actualInput, Fluid actualInputFluid, ProcessingInput actualExtra)
        {
            this.actualInput = actualInput;
            this.actualInputFluid = actualInputFluid;
            this.actualExtra = actualExtra;
        }

        @Override
        public void apply()
        {
            Processing.VESSEL.remove(new Vessel(actualInput, actualInputFluid, ItemDefinition.EMPTY, null, actualExtra));
        }

        @Override
        public String describe()
        {
            return String.format("Remove all Cuisine Vessel recipes that has input of %s, %s and %s", actualInput, actualInputFluid, actualExtra);
        }
    }

    private static final class RemoveAll implements IAction
    {

        @Override
        public void apply()
        {
            Processing.VESSEL.removeAll();
        }

        @Override
        public String describe()
        {
            return "Removing all Cuisine vessel recipes";
        }
    }

}
