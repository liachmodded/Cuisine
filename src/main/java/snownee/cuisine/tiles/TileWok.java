package snownee.cuisine.tiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.api.CompositeFood;
import snownee.cuisine.api.CookingStrategy;
import snownee.cuisine.api.CookingStrategyProvider;
import snownee.cuisine.api.CookingVessel;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.CulinarySkillPoint;
import snownee.cuisine.api.Form;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.api.IngredientTrait;
import snownee.cuisine.api.Seasoning;
import snownee.cuisine.api.Spice;
import snownee.cuisine.api.util.SkillUtil;
import snownee.cuisine.blocks.BlockFirePit;
import snownee.cuisine.client.gui.CuisineGUI;
import snownee.cuisine.internal.CuisinePersistenceCenter;
import snownee.cuisine.internal.food.Dish;
import snownee.cuisine.items.ItemIngredient;
import snownee.cuisine.items.ItemSpiceBottle;
import snownee.cuisine.network.PacketCustomEvent;
import snownee.cuisine.util.I18nUtil;
import snownee.kiwi.network.NetworkChannel;

public class TileWok extends TileBase implements CookingVessel, ITickable
{

    public enum Status
    {
        IDLE, WORKING
    }

    static
    {
        NetworkChannel.INSTANCE.register(PacketIncrementalWokUpdate.class);
    }

    private Status status = Status.IDLE;
    private CompositeFood dish;
    private int temperature, water, oil;
    public byte actionCycle = 0;
    transient List<ItemStack> ingredientsForRendering = new ArrayList<>(8);
    transient List<FluidStack> spicesForRendering = new ArrayList<>(8);

    @Override
    public void update()
    {
        if (!world.isRemote && status == Status.WORKING)
        {
            if (temperature < 300 && this.world.rand.nextInt(5) == 0)
            {
                this.temperature += this.world.rand.nextInt(10);
            }
            if (dish != null && this.world.getWorldTime() % 20 == 0)
            {
                this.dish = dish.apply(new Heating(), this);
            }
        }
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState)
    {
        if (oldState.getBlock() != CuisineRegistry.FIRE_PIT || newState.getBlock() != CuisineRegistry.FIRE_PIT)
        {
            return true;
        }
        else
        {
            return oldState.getValue(BlockFirePit.COMPONENT) != newState.getValue(BlockFirePit.COMPONENT);
        }
    }

    public Status getStatus()
    {
        return status;
    }

    @Override
    public int getTemperature()
    {
        return this.temperature;
    }

    @Override
    public int getWaterAmount()
    {
        return this.water;
    }

    @Override
    public int getOilAmount()
    {
        return this.oil;
    }

    public void onActivated(EntityPlayerMP playerIn, EnumHand hand, EnumFacing facing)
    {
        switch (status)
        {
        case IDLE:
        {
            ItemStack heldThing = playerIn.getHeldItem(hand);
            if (heldThing.getItem() instanceof ItemIngredient || heldThing.getItem() instanceof ItemSpiceBottle || (CulinaryHub.API_INSTANCE.isKnownMaterial(heldThing) && CulinaryHub.API_INSTANCE.findMaterial(heldThing).isValidForm(Form.FULL)))
            {
                this.dish = new Dish();
                this.temperature = 0;
                boolean result = cook(playerIn, hand, heldThing, facing);
                if (result)
                {
                    this.status = Status.WORKING;
                }
                else
                {
                    dish = null;
                }
            }
            break;
        }
        case WORKING:
            ItemStack heldThing = playerIn.getHeldItem(hand);
            if (cook(playerIn, hand, heldThing, facing))
            {
                break;
            }
            else if (heldThing.getItem() == Item.getItemFromBlock(CuisineRegistry.PLACED_DISH) && !dish.getIngredients().isEmpty())
            {
                // CulinarySkillPointContainer skill = playerIn.getCapability(CulinaryCapabilities.CULINARY_SKILL, null);
                double modifier = 1.0;
                // if (skill != null)
                // {
                // modifier *= SkillUtil.getPlayerSkillLevel((EntityPlayerMP) playerIn, CuisineSharedSecrets.KEY_SKILL_WOK);
                // SkillUtil.increaseSkillPoint((EntityPlayerMP) playerIn, 1);
                // }
                dish.setQualityBonus(modifier);
                dish.onBeingServed(this, playerIn);
                dish.getOrComputeModelType();

                SkillUtil.increasePoint(playerIn, CulinarySkillPoint.EXPERTISE, (int) (dish.getFoodLevel() * dish.getSaturationModifier()));
                SkillUtil.increasePoint(playerIn, CulinarySkillPoint.PROFICIENCY, 1);

                heldThing.shrink(1);
                playerIn.openGui(Cuisine.getInstance(), CuisineGUI.NAME_FOOD, world, pos.getX(), pos.getY(), pos.getZ());

                break;
            }
            else
            {
                CookingStrategy strategy = determineCookingStrategy(heldThing);
                if (strategy == null)
                {
                    return;
                }
                double limit = dish.getMaxSize();
                if (!SkillUtil.hasPlayerLearnedSkill(playerIn, CulinaryHub.CommonSkills.BIGGER_SIZE))
                {
                    limit *= 0.75;
                }
                if (dish.getSize() > limit)
                {
                    playerIn.sendStatusMessage(new TextComponentTranslation(I18nUtil.getFullKey("gui.wok_size_too_large")), true);
                    return;
                }
                this.dish = dish.apply(strategy, this);
                if (getWorld().rand.nextInt(5) == 0)
                {
                    SkillUtil.increasePoint(playerIn, CulinarySkillPoint.PROFICIENCY, 1);
                }
                NetworkChannel.INSTANCE.sendToDimension(new PacketCustomEvent(3, this.getPos().getX(), this.getPos().getY(), this.getPos().getZ()), this.getWorld().provider.getDimension());
            }
            break;
        }
    }

    public ItemStack serveDishAndReset()
    {
        if (status == Status.IDLE || dish == null)
        {
            return ItemStack.EMPTY;
        }

        ItemStack stack = this.dish.makeItemStack();

        this.dish = null;
        this.status = Status.IDLE;
        this.ingredientsForRendering.clear();
        this.spicesForRendering.clear();
        NetworkChannel.INSTANCE.sendToAll(new PacketIncrementalWokUpdate(this.getPos(), ItemStack.EMPTY));

        return stack;
    }

    private boolean cook(EntityPlayerMP playerIn, EnumHand hand, ItemStack heldThing, EnumFacing facing)
    {
        if (heldThing.getItem() instanceof ItemSpiceBottle)
        {
            Spice spice = CuisineRegistry.SPICE_BOTTLE.getSpice(heldThing);
            if (spice != null)
            {
                if (CuisineRegistry.SPICE_BOTTLE.hasFluid(heldThing))
                {
                    FluidStack fluidStack = CuisineRegistry.SPICE_BOTTLE.getFluidHandler(heldThing).drain(1, false);
                    for (FluidStack content : spicesForRendering)
                    {
                        if (content.isFluidEqual(fluidStack))
                        {
                            content.amount += fluidStack.amount;
                            fluidStack = null;
                            break;
                        }
                    }
                    if (fluidStack != null)
                    {
                        spicesForRendering.add(fluidStack);
                    }
                }
                CuisineRegistry.SPICE_BOTTLE.consume(heldThing, 1);
                Seasoning seasoning = new Seasoning(spice);
                this.dish.flavorWith(seasoning, this);
                return true;
            }
            else
            {
                return false;
            }
        }
        else if (heldThing.getItem() == CuisineRegistry.INGREDIENT || CulinaryHub.API_INSTANCE.isKnownMaterial(heldThing))
        {
            Ingredient ingredient = null;
            if (heldThing.getItem() == CuisineRegistry.INGREDIENT)
            {
                NBTTagCompound data = heldThing.getTagCompound();
                if (data != null)
                {
                    ingredient = CuisinePersistenceCenter.deserializeIngredient(data);
                }
            }
            else
            {
                ingredient = Ingredient.make(heldThing, 1);
            }

            if (ingredient == null)
            {
                return false;
            }

            if ((!SkillUtil.hasPlayerLearnedSkill(playerIn, CulinaryHub.CommonSkills.BIGGER_SIZE) && dish.getSize() + ingredient.getSize() >= dish.getMaxSize() * 0.75) || !dish.canAdd(ingredient))
            {
                playerIn.sendStatusMessage(new TextComponentTranslation(I18nUtil.getFullKey("gui.cannot_add_more")), true);
                return false;
            }

            this.dish.addIngredient(ingredient);
            ItemStack newStack = heldThing.splitStack(1);
            this.ingredientsForRendering.add(newStack);
            NetworkChannel.INSTANCE.sendToAll(new PacketIncrementalWokUpdate(this.getPos(), newStack));
            return true;
        }

        return false;
    }

    /**
     * Returning a list of ItemStack for rendering purpose.
     *
     * @return View of ingredients that added into this TileWok
     */
    public List<ItemStack> getWokContents()
    {
        return Collections.unmodifiableList(this.ingredientsForRendering);
    }

    @Nullable
    private CookingStrategy determineCookingStrategy(ItemStack heldItem)
    {
        Item item = heldItem.getItem();
        if (item instanceof CookingStrategyProvider)
        {
            return ((CookingStrategyProvider) item).getCookingStrategy(heldItem);
        }
        else
        {
            return null;
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        this.temperature = compound.getInteger("temperature");
        this.status = compound.getBoolean("status") ? Status.WORKING : Status.IDLE;
        if (compound.hasKey("dish", Constants.NBT.TAG_COMPOUND))
        {
            this.dish = CuisinePersistenceCenter.deserialize(compound.getCompoundTag("dish"));
        }
        NBTTagList items = compound.getTagList("rendering", Constants.NBT.TAG_COMPOUND);
        for (NBTBase tag : items)
        {
            if (tag instanceof NBTTagCompound)
            {
                this.ingredientsForRendering.add(new ItemStack((NBTTagCompound) tag));
            }
        }
        NBTTagList spices = compound.getTagList("rendering", Constants.NBT.TAG_COMPOUND);
        for (NBTBase tag : spices)
        {
            if (tag instanceof NBTTagCompound)
            {
                FluidStack fluidStack = FluidStack.loadFluidStackFromNBT((NBTTagCompound) tag);
                if (fluidStack != null)
                {
                    this.spicesForRendering.add(fluidStack);
                }
            }
        }
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound.setInteger("temperature", this.temperature);
        compound.setBoolean("status", this.status == Status.WORKING);
        if (dish != null)
        {
            compound.setTag("dish", CuisinePersistenceCenter.serialize(this.dish));
        }
        NBTTagList items = new NBTTagList();
        for (ItemStack item : this.ingredientsForRendering)
        {
            items.appendTag(item.serializeNBT());
        }
        compound.setTag("rendering", items);
        NBTTagList spices = new NBTTagList();
        for (FluidStack fluid : this.spicesForRendering)
        {
            spices.appendTag(fluid.writeToNBT(new NBTTagCompound()));
        }
        compound.setTag("renderingSpices", spices);
        return super.writeToNBT(compound);
    }

    @Override
    protected void readPacketData(NBTTagCompound data)
    {

    }

    @Nonnull
    @Override
    protected NBTTagCompound writePacketData(NBTTagCompound data)
    {
        return new NBTTagCompound();
    }

    static final class Heating implements CookingStrategy
    {
        Heating()
        {
            // No-op, just restricted access
        }

        // TODO This is just a prototype, we need further refinement

        private int ingredientSize = 0;
        private int initialTemp = -1;
        private int decrement = 0;

        private CompositeFood dish;

        @Override
        public void beginCook(CompositeFood dish)
        {
            this.dish = dish;
            this.ingredientSize = dish.getIngredients().size();
        }

        @Override
        public void preCook(Seasoning seasoning, CookingVessel vessel)
        {
            if (ingredientSize < 1)
            {
                return;
            }
            initialTemp = vessel.getTemperature();
            decrement = initialTemp / ingredientSize;
        }

        @Override
        public void cook(Ingredient ingredient, CookingVessel vessel)
        {
            if (ingredientSize < 1)
            {
                return;
            }
            int increment = Math.max(0, initialTemp / 4);
            ingredient.setHeat(ingredient.getHeat() + increment);
            if (ingredient.getHeat() > 250 && Math.random() < 0.01)
            {
                // Unconditionally remove the undercooked trait, so that
                // we won't see both co-exist together
                ingredient.removeTrait(IngredientTrait.UNDERCOOKED);
                ingredient.addTrait(IngredientTrait.OVERCOOKED);
            }
            initialTemp -= decrement;
        }

        @Override
        public void postCook(CookingVessel vessel)
        {

        }

        @Override
        public void endCook()
        {

        }

        @Override
        public CompositeFood result()
        {
            return dish;
        }
    }
}
