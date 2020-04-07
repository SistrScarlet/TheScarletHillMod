package com.sistr.scarlethill.world.biome;

import com.sistr.scarlethill.setup.Registration;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public abstract class AbstractScarletBiome extends Biome {
    protected final int skyColor;
    protected final int grassColor;
    protected final int foliageColor;

    protected AbstractScarletBiome(Builder biomeBuilder, ExtensionBuilder builderE) {
        super(biomeBuilder);
        if (builderE.skyColor != null && builderE.grassColor != null && builderE.foliageColor != null) {
            this.skyColor = builderE.skyColor;
            this.grassColor = builderE.grassColor;
            this.foliageColor = builderE.foliageColor;
        } else {
            throw new IllegalStateException("You are missing parameters to build a proper biome for " + this.getClass().getSimpleName() + "\n" + builderE);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public int getGrassColor(double p_225528_1_, double p_225528_3_) {
        return this.grassColor;
    }

    @OnlyIn(Dist.CLIENT)
    public int getFoliageColor() {
        return this.grassColor;
    }

    @Override
    public int getSkyColor() {
        return this.skyColor;
    }

    @Override
    public Biome getRiver() {
        //if (this == Biomes.SNOWY_TUNDRA) return Biomes.FROZEN_RIVER;
        //if (this == Biomes.MUSHROOM_FIELDS || this == Biomes.MUSHROOM_FIELD_SHORE) return Biomes.MUSHROOM_FIELD_SHORE;
        return Registration.SCARLET_RIVER_BIOME.get();
    }

    public static class ExtensionBuilder extends Builder {
        @Nullable
        private Integer skyColor;
        @Nullable
        private Integer grassColor;
        @Nullable
        private Integer foliageColor;

        public ExtensionBuilder skyColor(int skyColorIn) {
            this.skyColor = skyColorIn;
            return this;
        }

        public ExtensionBuilder grassColor(int grassColorIn) {
            this.grassColor = grassColorIn;
            return this;
        }

        public ExtensionBuilder foliageColor(int foliageColorIn) {
            this.foliageColor = foliageColorIn;
            return this;
        }

    }
}
