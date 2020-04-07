package com.sistr.scarlethill.datagen;

import com.sistr.scarlethill.ScarletHillMod;
import com.sistr.scarlethill.setup.Registration;
import com.sistr.scarlethill.world.dimension.ModDimensions;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.criterion.*;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScarletHillAdvancements extends BaseAdvancementProvider {
    protected final List<Advancement> advancementTables = new ArrayList<>();

    public ScarletHillAdvancements(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    public void act(DirectoryCache cache) {
        super.act(cache);
        Map<ResourceLocation, Advancement> tables = new HashMap<>();
        advancementTables.forEach((advancement -> tables.put(advancement.getId(), advancement)));
        writeTables(cache, tables);
    }

    @Override
    protected void addTables() {
        Advancement root = Advancement.Builder.builder().withDisplay(Registration.SCARLET_STONE_BLOCK.get(),
                new TranslationTextComponent("advancements.scarlethill.root.title"),
                new TranslationTextComponent("advancements.scarlethill.root.description"),
                new ResourceLocation("scarlethill:textures/gui/advancements/backgrounds/scarlethill.png"),
                FrameType.TASK, false, false, false)
                .withCriterion("entered_scarlethill", ChangeDimensionTrigger.Instance.changedDimensionTo(ModDimensions.SCARLETHILL_TYPE))
                .build(location("root"));
        add(root);
        Advancement open_gate = Advancement.Builder.builder().withParent(root).withDisplay(Registration.SCARLET_PORTAL_BLOCK.get(),
                new TranslationTextComponent("advancements.scarlethill.open_gate.title"),
                new TranslationTextComponent("advancements.scarlethill.open_gate.description"), null,
                FrameType.TASK, true, true, false)
                .withCriterion("entered_scarlethill", ChangeDimensionTrigger.Instance.changedDimensionTo(ModDimensions.SCARLETHILL_TYPE))
                .build(location("open_gate"));
        add(open_gate);
        Advancement hiding_place = Advancement.Builder.builder().withParent(root).withDisplay(Registration.SCARLET_LOG_BLOCK.get(),
                new TranslationTextComponent("advancements.scarlethill.hiding_place.title"),
                new TranslationTextComponent("advancements.scarlethill.hiding_place.description"), null,
                FrameType.TASK, true, true, false)
                .withCriterion("reached_crimsonian_village", PositionTrigger.Instance.forLocation(LocationPredicate.forFeature(Registration.SCARLET_CRIMSONIAN_VILLAGE_STRUCTURE.get())))
                .build(location("hiding_place"));
        add(hiding_place);
        Advancement bear_hunter = Advancement.Builder.builder().withParent(open_gate).withDisplay(Registration.SCARLET_BEAR_CLAW_ITEM.get(),
                new TranslationTextComponent("advancements.scarlethill.bear_hunter.title"),
                new TranslationTextComponent("advancements.scarlethill.bear_hunter.description"), null,
                FrameType.GOAL, true, true, false)
                .withRewards(AdvancementRewards.Builder.experience(50))
                .withCriterion("killed_scarlet_bear", KilledTrigger.Instance.playerKilledEntity(EntityPredicate.Builder.create().type(Registration.SCARLET_BEAR_BOSS.get())))
                .build(location("bear_hunter"));
        add(bear_hunter);
        Advancement molten_mine = Advancement.Builder.builder().withParent(bear_hunter).withDisplay(Registration.SCARLET_SNOW_BLOCK.get(),
                new TranslationTextComponent("advancements.scarlethill.molten_mine.title"),
                new TranslationTextComponent("advancements.scarlethill.molten_mine.description"), null,
                FrameType.TASK, true, true, false)
                .withCriterion("reached_molten_mine", PositionTrigger.Instance.forLocation(LocationPredicate.forFeature(Registration.MOLTEN_MINE_STRUCTURE.get())))
                .build(location("molten_mine"));
        add(molten_mine);
        Advancement crushing = Advancement.Builder.builder().withParent(molten_mine).withDisplay(Registration.SCARLET_GEM_ITEM.get(),
                new TranslationTextComponent("advancements.scarlethill.crushing.title"),
                new TranslationTextComponent("advancements.scarlethill.crushing.description"), null,
                FrameType.GOAL, true, true, false)
                .withCriterion("killed_crushing", KilledTrigger.Instance.playerKilledEntity(EntityPredicate.Builder.create().type(Registration.SOTS_BODY_BOSS.get())))
                .build(location("crushing"));
        add(crushing);
    }

    private void add(Advancement advancement) {
        advancementTables.add(advancement);
    }

    private ResourceLocation location(String key) {
        return new ResourceLocation(ScarletHillMod.MODID, "scarlethill/" + key);
    }

    @Override
    public String getName() {
        return "ScarletHillMod ScarletHillAdvancements";
    }

}
