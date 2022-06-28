package net.fabricmc.example.block;

import net.fabricmc.example.BackroomsMod;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static net.fabricmc.example.BackroomsMod.modID;

public class ModBlocks
{
	public static final Block WALLPAPER = new Block(FabricBlockSettings
			.of(Material.NETHER_WOOD)
			.strength(4.0f));
	public static final Block MOLDY_CARPET = new Block(FabricBlockSettings
			.of(Material.WOOL)
			.strength(4.0f));
	public static final Block CEILING_TILE = new Block(FabricBlockSettings
			.of(Material.SPONGE)
			.strength(4.0f));
	public static final Block CEILING_LIGHT = new Block(FabricBlockSettings
			.of(Material.GLASS)
			.strength(4.0f)
			.luminance(15));
	
	public static void register() {
		
		BackroomsMod.LOGGER.debug("Registering Blocks");
		
		Registry.register(Registry.BLOCK, new Identifier(modID, "wallpaper"), WALLPAPER);
		Registry.register(Registry.ITEM, new Identifier(modID, "wallpaper"),
				new BlockItem(WALLPAPER, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));
		
		Registry.register(Registry.BLOCK, new Identifier(modID, "moldy_carpet"), MOLDY_CARPET);
		Registry.register(Registry.ITEM, new Identifier(modID, "moldy_carpet"),
				new BlockItem(MOLDY_CARPET, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));
		
		Registry.register(Registry.BLOCK, new Identifier(modID, "ceiling_tile"), CEILING_TILE);
		Registry.register(Registry.ITEM, new Identifier(modID, "ceiling_tile"),
				new BlockItem(CEILING_TILE, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));
		
		Registry.register(Registry.BLOCK, new Identifier(modID, "ceiling_light"), CEILING_LIGHT);
		Registry.register(Registry.ITEM, new Identifier(modID, "ceiling_light"),
				new BlockItem(CEILING_LIGHT, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));
	}
}
