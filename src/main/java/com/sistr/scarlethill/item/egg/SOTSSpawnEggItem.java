package com.sistr.scarlethill.item.egg;

import com.sistr.scarlethill.setup.Registration;
import net.minecraft.entity.EntityType;

public class SOTSSpawnEggItem extends ScarletSpawnEggItem {

    @Override
    public EntityType<?> getSummonEntity() {
        return Registration.SOTS_BODY_BOSS.get();
    }

}
