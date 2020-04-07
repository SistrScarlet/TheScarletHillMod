package com.sistr.scarlethill.item.egg;

import com.sistr.scarlethill.setup.Registration;
import net.minecraft.entity.EntityType;

public class ScarletBearSpawnEggItem extends ScarletSpawnEggItem {

    @Override
    public EntityType<?> getSummonEntity() {
        return Registration.SCARLET_BEAR_BOSS.get();
    }

}
