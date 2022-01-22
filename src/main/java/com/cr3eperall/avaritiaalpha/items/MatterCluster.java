package com.cr3eperall.avaritiaalpha.items;

import codechicken.lib.util.ItemUtils;
import com.cr3eperall.avaritiaalpha.AvaritiaAlpha;
import com.cr3eperall.avaritiaalpha.items.tools.InfinityMaterial;
import com.cr3eperall.avaritiaalpha.util.ItemStackWrapper;
import com.cr3eperall.avaritiaalpha.util.ToolHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.*;

public class MatterCluster extends Item {

    protected static Random randy = new Random();

    public static final String MAINTAG = "clusteritems";
    public static final String LISTTAG = "items";
    public static final String ITEMTAG = "item";
    public static final String COUNTTAG = "count";
    public static final String MAINCOUNTTAG = "clustersize";

    public static int CAPACITY = 64 * 64;

    public MatterCluster() {
        super(new Properties().maxStackSize(1).rarity(InfinityMaterial.COSMIC_RARITY));
        setRegistryName("matter_cluster");
        addPropertyOverride(new ResourceLocation(AvaritiaAlpha.MOD_ID + ":" + MAINCOUNTTAG), new IItemPropertyGetter() {
            @Override
            public float call(ItemStack stack, @Nullable World world, @Nullable LivingEntity holder) {
                return stack.getTag().getInt(MAINCOUNTTAG);
            }
        });
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (!stack.hasTag() || !stack.getTag().contains(MAINTAG)) {
            return;
        }
        CompoundNBT clustertag = stack.getTag().getCompound(MAINTAG);

        tooltip.add(new StringTextComponent(stack.getTag().getInt(MAINCOUNTTAG) + "/" + CAPACITY + " " + I18n.format("tooltip.matter_cluster.counter")));
        tooltip.add(new StringTextComponent(""));

        if (Screen.hasShiftDown()) {
            ListNBT list = clustertag.getList(LISTTAG, 10);
            for (int i = 0; i < list.size(); i++) {
                CompoundNBT tag = list.getCompound(i);
                ItemStack countstack = ItemStack.read(tag.getCompound(ITEMTAG));
                int count = tag.getInt(COUNTTAG);

                tooltip.add(countstack.getDisplayName().applyTextStyle(countstack.getItem().getRarity(countstack).color).appendSibling(new StringTextComponent(" x " + count).applyTextStyle(TextFormatting.GRAY)));
            }
        } else {
            tooltip.add(new StringTextComponent(I18n.format("tooltip.matter_cluster.desc")).applyTextStyle(TextFormatting.DARK_GRAY));
            tooltip.add( new StringTextComponent(I18n.format("tooltip.matter_cluster.desc2")).applyTextStyles(TextFormatting.DARK_GRAY,TextFormatting.ITALIC));
        }
    }

    public static List<ItemStack> makeClusters(Set<ItemStack> input) {
        Map<ItemStackWrapper, Integer> items = ToolHelper.collateMatterCluster(input);
        List<ItemStack> clusters = new ArrayList<>();
        List<Map.Entry<ItemStackWrapper, Integer>> itemlist = new ArrayList<>();
        itemlist.addAll(items.entrySet());

        int currentTotal = 0;
        Map<ItemStackWrapper, Integer> currentItems = new HashMap<>();

        while (!itemlist.isEmpty()) {
            Map.Entry<ItemStackWrapper, Integer> e = itemlist.get(0);
            ItemStackWrapper wrap = e.getKey();
            int wrapcount = e.getValue();

            int count = Math.min(CAPACITY - currentTotal, wrapcount);

            if (!currentItems.containsKey(e.getKey())) {
                currentItems.put(wrap, count);
            } else {
                currentItems.put(wrap, currentItems.get(wrap) + count);
            }
            currentTotal += count;

            e.setValue(wrapcount - count);
            if (e.getValue() == 0) {
                itemlist.remove(0);
            }

            if (currentTotal == CAPACITY) {
                ItemStack cluster = makeCluster(currentItems);

                clusters.add(cluster);

                currentTotal = 0;
                currentItems = new HashMap<>();
            }
        }

        if (currentTotal > 0) {
            ItemStack cluster = makeCluster(currentItems);

            clusters.add(cluster);
        }

        return clusters;
    }

    public static ItemStack makeCluster(Map<ItemStackWrapper, Integer> input) {
        ItemStack cluster = new ItemStack(ModItems.MATTERCLUSTER);
        int total = 0;
        for (int num : input.values()) {
            total += num;
        }
        setClusterData(cluster, input, total);
        return cluster;
    }

    public static Map<ItemStackWrapper, Integer> getClusterData(ItemStack cluster) {
        if (!cluster.hasTag() || !cluster.getTag().contains(MAINTAG)) {
            return null;
        }
        CompoundNBT tag = cluster.getTag().getCompound(MAINTAG);
        ListNBT list = tag.getList(LISTTAG, 10);
        Map<ItemStackWrapper, Integer> data = new HashMap<>();

        for (int i = 0; i < list.size(); i++) {
            CompoundNBT entry = list.getCompound(i);
            ItemStackWrapper wrap = new ItemStackWrapper(ItemStack.read(entry.getCompound(ITEMTAG)));
            int count = entry.getInt(COUNTTAG);
            data.put(wrap, count);
        }
        return data;
    }

    public static int getClusterSize(ItemStack cluster) {
        if (!(cluster.getItem() instanceof MatterCluster)) {
            return 0;
        }
        return cluster.getTag().getInt(MAINCOUNTTAG);
    }

    public static void setClusterData(ItemStack stack, Map<ItemStackWrapper, Integer> data, int count) {
        if (!stack.hasTag()) {
            stack.setTag(new CompoundNBT());
        }

        CompoundNBT clustertag = new CompoundNBT();
        ListNBT list = new ListNBT();

        for (Map.Entry<ItemStackWrapper, Integer> entry : data.entrySet()) {
            CompoundNBT itemtag = new CompoundNBT();
            itemtag.put(ITEMTAG, entry.getKey().stack.write(new CompoundNBT()));
            itemtag.putInt(COUNTTAG, entry.getValue());
            list.add(itemtag);
        }
        clustertag.put(LISTTAG, list);
        stack.getTag().putInt(MAINCOUNTTAG, count);
        stack.getTag().put(MAINTAG, clustertag);
    }

    public static void mergeClusters(ItemStack donor, ItemStack recipient) {
        int donorcount = getClusterSize(donor);
        int recipientcount = getClusterSize(recipient);

        if (donorcount == 0 || donorcount == CAPACITY || recipientcount == CAPACITY) {
            return;
        }

        Map<ItemStackWrapper, Integer> donordata = getClusterData(donor);
        Map<ItemStackWrapper, Integer> recipientdata = getClusterData(recipient);
        List<Map.Entry<ItemStackWrapper, Integer>> datalist = new ArrayList<>();
        datalist.addAll(donordata.entrySet());

        while (recipientcount < CAPACITY && donorcount > 0) {
            Map.Entry<ItemStackWrapper, Integer> e = datalist.get(0);
            ItemStackWrapper wrap = e.getKey();
            int wrapcount = e.getValue();

            int count = Math.min(CAPACITY - recipientcount, wrapcount);

            if (!recipientdata.containsKey(wrap)) {
                recipientdata.put(wrap, count);
            } else {
                recipientdata.put(wrap, recipientdata.get(wrap) + count);
            }

            donorcount -= count;
            recipientcount += count;

            if (wrapcount - count > 0) {
                e.setValue(wrapcount - count);
            } else {
                donordata.remove(wrap);
                datalist.remove(0);
            }
        }
        setClusterData(recipient, recipientdata, recipientcount);

        if (donorcount > 0) {
            setClusterData(donor, donordata, donorcount);
        } else {
            donor.setTag(null);
            donor.setCount(0);
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
            List<ItemStack> drops = ToolHelper.collateMatterClusterContents(MatterCluster.getClusterData(stack));

            for (ItemStack drop : drops) {
                ItemUtils.dropItem(world, player.getPosition(), drop);
            }
        }

        stack.setCount(0);
        return new ActionResult<>(ActionResultType.SUCCESS, stack);
    }

}
