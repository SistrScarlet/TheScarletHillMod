package com.sistr.scarlethill.item;

import com.sistr.scarlethill.block.tile.IHasWizardBlock;
import com.sistr.scarlethill.setup.ModSetup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CreativeWrenchItem extends Item implements ILeftClickable {
    private BlockPos wizardBlockPos = null;

    public CreativeWrenchItem() {
        super(new Properties()
                .maxStackSize(1)
                .maxDamage(0)
                .group(ModSetup.ITEM_GROUP));
    }

    /*

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        CompoundNBT nbt = stack.getOrCreateTag();
        String type = nbt.getString("EntityType");
        if (!type.equals("")) {
            String translationKey = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(type)).getTranslationKey();
            tooltip.add(new TranslationTextComponent(translationKey).applyTextStyle(TextFormatting.GRAY));
        }
        if (nbt.contains("BlockPos", 9)) {
            ListNBT markerNBT = nbt.getList("BlockPos", 11);
            for (int i = 0; i < markerNBT.size(); i++) {
                int[] getPos = markerNBT.getIntArray(i);
                tooltip.add(new StringTextComponent((new BlockPos(getPos[0], getPos[1], getPos[2])).toString()).applyTextStyle(TextFormatting.GRAY));
            }
        }
    }

    @Override
    public void onLeftClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if (playerIn.isShiftKeyDown()) {
            playerIn.getHeldItem(handIn).setTag(new CompoundNBT());
        }
    }

    @Override
    public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.getOrCreateTag().putString("EntityType", target.getType().getRegistryName().toString());
        if (attacker instanceof PlayerEntity) {
            attacker.sendMessage(new StringTextComponent("Get entity type : " + stack.getOrCreateTag().getString("EntityType")));
        }
        return true;
    }

    @Override
    public boolean onBlockRightClick(World worldIn, PlayerEntity playerIn, Hand handIn, BlockPos pos) {
        ItemStack stack = playerIn.getHeldItem(handIn);

        CompoundNBT nbt = stack.getOrCreateTag();
        List<BlockPos> posList = Lists.newArrayList();

        //ポジションリストにクリック地点を加える
        posList.add(pos);

        //ポジションリストにアイテムのNBTのポジションを加える
        if (nbt.contains("BlockPos", 9)) {
            ListNBT getPosNBT = nbt.getList("BlockPos", 11);
            for (int i = 0; i < getPosNBT.size(); i++) {
                int[] getPos = getPosNBT.getIntArray(i);
                posList.add(new BlockPos(getPos[0], getPos[1], getPos[2]));
            }
        }

        //ポジションリストをポジションnbtに変換
        ListNBT setPosNBT = new ListNBT();
        for (BlockPos setPos : posList) {
            int[] posArray = new int[]{setPos.getX(), setPos.getY(), setPos.getZ()};
            setPosNBT.add(new IntArrayNBT(posArray));
        }

        //アイテムのnbtに上書き
        nbt.put("BlockPos", setPosNBT);

        stack.setTag(nbt);

        if (worldIn.isRemote) {
            playerIn.sendMessage(new StringTextComponent("Get block position : " + pos.toString()));
        }

        return true;
    }

     */

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        PlayerEntity player = context.getPlayer();
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof IHasWizardBlock) {
            ((IHasWizardBlock) tile).startWizard(player);
            this.wizardBlockPos = pos;
        } else if (this.wizardBlockPos != null) {
            TileEntity wizard = world.getTileEntity(this.wizardBlockPos);
            if (wizard instanceof IHasWizardBlock) {
                ((IHasWizardBlock) wizard).receiveDate(player, pos);
            } else {
                this.wizardBlockPos = null;
            }
        }
        return ActionResultType.SUCCESS;
    }


    @Override
    public void onLeftClick(World world, PlayerEntity player, Hand hand) {
        if (player.isSneaking()) {
            TileEntity wizard = world.getTileEntity(this.wizardBlockPos);
            if (wizard instanceof IHasWizardBlock) {
                ((IHasWizardBlock) wizard).receiveDate(player, true);
            }
        }
    }

}
