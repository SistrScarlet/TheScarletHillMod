package com.sistr.scarlethill.item.egg;

import com.sistr.scarlethill.setup.Registration;
import net.minecraft.entity.EntityType;

public class MoltenSlimeSpawnEggItem extends ScarletSpawnEggItem {

    @Override
    public EntityType<?> getSummonEntity() {
        return Registration.MOLTEN_SLIME_MOB.get();
    }

}
