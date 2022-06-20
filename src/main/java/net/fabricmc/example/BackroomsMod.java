package net.fabricmc.example;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BackroomsMod implements ModInitializer {

	public static final Logger LOGGER = LoggerFactory.getLogger("backrooms");
	
	// Custom blocks
	public static final Block WALLPAPER = new Block(FabricBlockSettings.of(Material.NETHER_WOOD).strength(4.0f));
	public static final Block MOLDY_CARPET = new Block(FabricBlockSettings.of(Material.WOOL).strength(4.0f));
	public static final Block CEILING_TILE = new Block(FabricBlockSettings.of(Material.SPONGE).strength(4.0f));
	public static final Block CEILING_LIGHT = new Block(FabricBlockSettings.of(Material.GLASS).strength(4.0f).luminance(15));

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");
		Registry.register(Registry.BLOCK, new Identifier("backrooms", "wallpaper"), WALLPAPER);
		Registry.register(Registry.ITEM, new Identifier("backrooms", "wallpaper"), new BlockItem(WALLPAPER, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));
		Registry.register(Registry.BLOCK, new Identifier("backrooms", "moldy_carpet"), MOLDY_CARPET);
		Registry.register(Registry.ITEM, new Identifier("backrooms", "moldy_carpet"), new BlockItem(MOLDY_CARPET, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));
		Registry.register(Registry.BLOCK, new Identifier("backrooms", "ceiling_tile"), CEILING_TILE);
		Registry.register(Registry.ITEM, new Identifier("backrooms", "ceiling_tile"), new BlockItem(CEILING_TILE, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));
		Registry.register(Registry.BLOCK, new Identifier("backrooms", "ceiling_light"), CEILING_LIGHT);
		Registry.register(Registry.ITEM, new Identifier("backrooms", "ceiling_light"), new BlockItem(CEILING_LIGHT, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));
	}
}
