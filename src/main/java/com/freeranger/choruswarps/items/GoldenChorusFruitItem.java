package com.freeranger.choruswarps.items;

import com.freeranger.choruswarps.blocks.EnderLinkBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ChorusFruitItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.List;

public class GoldenChorusFruitItem extends ChorusFruitItem {
    public GoldenChorusFruitItem(Properties builder) {
        super(builder);
    }

    @Override
    public boolean isFood() {
        return true;
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        if (this.isFood()) {
            ItemStack itemstack = context.getItem(); //playerIn.getHeldItem(handIn);
            CompoundNBT nbt = itemstack.getTag();
            if(nbt == null) nbt = new CompoundNBT();

            boolean isLinked = nbt.contains("linked_x");

            if (isLinked && !(context.getWorld().getBlockState(context.getPos()).getBlock() instanceof EnderLinkBlock)) {
                context.getPlayer().setActiveHand(context.getHand());

                return ActionResultType.CONSUME;
            } else {
                if (context.getWorld().getBlockState(context.getPos()).getBlock() instanceof EnderLinkBlock) {

                    nbt.putFloat("linked_x", context.getPos().getX());
                    nbt.putFloat("linked_y", context.getPos().getY());
                    nbt.putFloat("linked_z", context.getPos().getZ());

                    nbt.putString("dimension", context.getWorld().getDimensionKey().getLocation().toString());

                    nbt.putBoolean("linked", true);
                    itemstack.setTag(nbt);
                    context.getPlayer().playSound(SoundEvents.ENTITY_CHICKEN_EGG, 2f, 1f);
                }
                if(!nbt.contains("linked_x")){
                    context.getPlayer().playSound(SoundEvents.ENTITY_DROWNED_STEP, 2f, 1f);
                }
                return ActionResultType.FAIL;
            }
        } else {
            return ActionResultType.PASS;
        }
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
        super.addInformation(stack, worldIn, tooltip, flagIn);

        if(tooltip.size() > 1) {
            tooltip.remove(1);
        }

        String dimension;
        String notLinked = new TranslationTextComponent("tooltip.choruswarps.not_linked").getString();
        String linkedTo = new TranslationTextComponent("tooltip.choruswarps.linked_to").getString();
        String overworld = new TranslationTextComponent("tooltip.choruswarps.overworld").getString();
        String nether = new TranslationTextComponent("tooltip.choruswarps.nether").getString();
        String end = new TranslationTextComponent("tooltip.choruswarps.end").getString();
        String abyss = new TranslationTextComponent("tooltip.choruswarps.abyss").getString();
        String undergarden = new TranslationTextComponent("tooltip.choruswarps.undergarden").getString();
        String bumblezone = new TranslationTextComponent("tooltip.choruswarps.bumblezone").getString();
        String ratlantis = new TranslationTextComponent("tooltip.choruswarps.ratlantis").getString();
        String gaia = new TranslationTextComponent("tooltip.choruswarps.gaia").getString();
        String neverdark = new TranslationTextComponent("tooltip.choruswarps.neverdark").getString();
        String terridus = new TranslationTextComponent("tooltip.choruswarps.terridus").getString();
        String invalidDimension = new TranslationTextComponent("tooltip.choruswarps.invalid_dimension").getString();

        if (stack.getTag() != null) {
            if(stack.getTag().contains("linked_x") && stack.getTag().contains("dimension")){
                String dim = stack.getTag().getString("dimension");
                if(dim == "")
                    dimension = invalidDimension;
                else{
                    switch(dim){
                        case "minecraft:overworld":
                            dimension = overworld;
                            break;
                        case "minecraft:the_nether":
                            dimension = nether;
                            break;
                        case "minecraft:the_end":
                            dimension = end;
                            break;
                        case "theabyss:theabyssdim":
                            dimension = abyss;
                            break;
                        case "undergarden:undergarden":
                            dimension = undergarden;
                            break;
                        case "the_bumblezone:the_bumblezone":
                            dimension = bumblezone;
                            break;
                        case "rats:ratlantis":
                            dimension = ratlantis;
                            break;
                        case "gaiadimension:gaia_dimension":
                            dimension = gaia;
                            break;
                        case "neverdark:neverdark_abyss":
                            dimension = neverdark;
                            break;
                        case "terridus:terridus":
                            dimension = terridus;
                            break;
                        default:
                            dimension = dim;
                    }
                }

                tooltip.add(new StringTextComponent(
                        "\u00A7f" + linkedTo + " \u00A7e" +
                                stack.getTag().getFloat("linked_x") + " " +
                                stack.getTag().getFloat("linked_y") + " " +
                                stack.getTag().getFloat("linked_z") +
                                " \u00A7f(\u00A7e" + dimension + "\u00A7f)")
                );
            }else{
                if(tooltip.size() > 1) {
                    tooltip.set(1, new StringTextComponent("\u00A7f"+notLinked));
                }else tooltip.add(new StringTextComponent("\u00A7f"+notLinked));
            }
        }else{
            if(tooltip.size() > 1)
                tooltip.set(1, new StringTextComponent("\u00A7f"+notLinked));
            else tooltip.add(new StringTextComponent("\u00A7f"+notLinked));
        }
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        if(!(entityLiving instanceof ServerPlayerEntity) || stack.getTag()==null){
            return stack;
        }

        ServerPlayerEntity player = (ServerPlayerEntity)entityLiving;

        float targetX = stack.getTag().getFloat("linked_x");
        float targetY = stack.getTag().getFloat("linked_y");
        float targetZ = stack.getTag().getFloat("linked_z");
        BlockPos linkedBlockPos = new BlockPos(targetX, targetY, targetZ);

        String blockDim = stack.getTag().getString("dimension");

        if(!(player.world.getDimensionKey().getLocation().toString().equals(blockDim))) return stack;

        if (!(worldIn.getBlockState(linkedBlockPos).getBlock() instanceof EnderLinkBlock)){
            CompoundNBT nbt = stack.getTag();
            nbt.remove("linked_x");
            nbt.remove("linked_y");
            nbt.remove("linked_z");
            nbt.remove("dimension");
            player.playSound(SoundEvents.ENTITY_DROWNED_STEP, 2f, 1f);
            return stack;
        }

        if (entityLiving.isPassenger()) {
            entityLiving.stopRiding();
        }

        boolean hasTeleported = safeTeleport(player, targetX, targetY, targetZ);

        ((ServerPlayerEntity)entityLiving).getCooldownTracker().setCooldown(this, 80);

        return hasTeleported ? returnIfTeleportSuccessful(entityLiving, worldIn, stack) : stack;
    }

    ItemStack returnIfTeleportSuccessful(LivingEntity entityLiving, World worldIn, ItemStack stack){
        return entityLiving.onFoodEaten(worldIn, stack);
    }

    boolean safeTeleport(PlayerEntity player, float x, float y, float z){
        BlockPos[] positions = new BlockPos[9];
        positions[0] = new BlockPos(x, y+1f, z);
        positions[1] = new BlockPos(x, y+2f, z);
        positions[2] = new BlockPos(x, y, z);
        positions[3] = new BlockPos(x+1f, y, z);
        positions[4] = new BlockPos(x-1f, y, z);
        positions[5] = new BlockPos(x, y, z+1f);
        positions[6] = new BlockPos(x, y, z-1f);
        positions[7] = new BlockPos(x, y-1f, z);
        positions[8] = new BlockPos(x, y-2f, z);
        positions[8] = new BlockPos(x, y-3f, z);


        for(BlockPos pos : positions){
            BlockPos originPos = player.getPosition();

            if(player.attemptTeleport(pos.getX()+.5f, pos.getY(), pos.getZ()+.5f, true)){
                SoundEvent soundevent = SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT;
                player.world.playSound(null, originPos.getX(), originPos.getY(), originPos.getZ(), soundevent, SoundCategory.PLAYERS, 1.0f, 1.0f);
                player.world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), soundevent, SoundCategory.PLAYERS, 1.0f, 1.0f);
                player.playSound(soundevent, 1.0f, 1.0f);
                return true;
            }
        }
        return false;
    }
}