package com.sistr.scarlethill.item.egg;

import com.sistr.scarlethill.setup.Registration;
import net.minecraft.entity.EntityType;

public class CrimsonianSpawnEggItem extends ScarletSpawnEggItem {

    @Override
    public EntityType<?> getSummonEntity() {
        return Registration.CRIMSONIAN_MOB.get();
    }

}
