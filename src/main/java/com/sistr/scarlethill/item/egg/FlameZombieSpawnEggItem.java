package com.sistr.scarlethill.item.egg;

import com.sistr.scarlethill.setup.Registration;
import net.minecraft.entity.EntityType;

public class FlameZombieSpawnEggItem extends ScarletSpawnEggItem {

    @Override
    public EntityType<?> getSummonEntity() {
        return Registration.FLAME_ZOMBIE_MOB.get();
    }

}
