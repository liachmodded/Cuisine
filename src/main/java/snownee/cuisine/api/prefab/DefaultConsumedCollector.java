package snownee.cuisine.api.prefab;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.api.CompositeFood;
import snownee.cuisine.api.EffectCollector;
import snownee.cuisine.api.EffectType;

public class DefaultConsumedCollector implements EffectCollector
{
    private final Map<Potion, PotionEffectInfo> mapPotions = new HashMap<>();

    @Override
    public void apply(CompositeFood food, EntityPlayer player)
    {
        boolean resistance = player.isPotionActive(CuisineRegistry.EFFECT_RESISTANCE);

        int maxDuration = 0;
        for (Entry<Potion, PotionEffectInfo> entry : mapPotions.entrySet())
        {
            if (!resistance || entry.getKey().isBadEffect())
            {
                PotionEffectInfo info = entry.getValue();
                player.addPotionEffect(new PotionEffect(entry.getKey(), info.duration, info.amplifier, false, info.showParticles));
                if (info.duration > maxDuration)
                {
                    maxDuration = info.duration;
                }
            }
        }
        if (maxDuration > 0)
        {
            player.addPotionEffect(new PotionEffect(CuisineRegistry.EFFECT_RESISTANCE, maxDuration * 2, 0, true, false));
        }

    }

    @Override
    public <T> void addEffect(EffectType<T> type, T effect)
    {
        if (type == DefaultTypes.POTION)
        {
            PotionEffect potionEffect = (PotionEffect) effect;
            if (mapPotions.containsKey(potionEffect.getPotion()))
            {
                PotionEffectInfo info = mapPotions.get(potionEffect.getPotion());
                if (potionEffect.getAmplifier() > info.amplifier)
                {
                    info.duration = (int) (info.duration * Math.pow(2, potionEffect.getAmplifier() - info.amplifier));
                    info.amplifier = potionEffect.getAmplifier();
                }
                if (!potionEffect.doesShowParticles())
                {
                    info.showParticles = false;
                }
                info.duration += (int) (potionEffect.getDuration() * Math.pow(2, info.amplifier - potionEffect.getAmplifier()));
            }
            else
            {
                PotionEffectInfo info = new PotionEffectInfo(potionEffect);
                mapPotions.put(potionEffect.getPotion(), info);
            }
        }
        else
        {
            Cuisine.logger.error("Try to add an uncaught effect: ", effect);
        }
    }

    @Override
    public <T> void clear(EffectType<T> type)
    {
        if (type == DefaultTypes.POTION)
        {
            mapPotions.clear();
        }
        else
        {
            Cuisine.logger.error("Try to clear an uncaught type: ", type);
        }
    }

    public static class PotionEffectInfo
    {
        public int amplifier;
        public int duration;
        public boolean showParticles;

        public PotionEffectInfo(PotionEffect effect)
        {
            this.amplifier = effect.getAmplifier();
            this.duration = effect.getDuration();
            this.showParticles = effect.doesShowParticles();
        }
    }
}
