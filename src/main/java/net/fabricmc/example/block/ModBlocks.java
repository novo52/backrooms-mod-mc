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
	public static final Identifier WALLPAPER_LINED_ID = new Identifier(modID, "wallpaper_lined");
	public static final Block WALLPAPER_LINED = new Block(FabricBlockSettings
			.of(Material.NETHER_WOOD)
			.strength(4.0f));
	
	public static final Identifier WALLPAPER_DOTTED_ID = new Identifier(modID, "wallpaper_dotted");
	public static final Block WALLPAPER_DOTTED = new Block(FabricBlockSettings
			.of(Material.NETHER_WOOD)
			.strength(4.0f));
	
	public static final Identifier WALLPAPER_SPOTTED_ID = new Identifier(modID, "wallpaper_spotted");
	public static final Block WALLPAPER_SPOTTED = new Block(FabricBlockSettings
			.of(Material.NETHER_WOOD)
			.strength(4.0f));
	
	public static final Identifier WALLPAPER_SYMBOLIC_ID = new Identifier(modID, "wallpaper_symbolic");
	public static final Block WALLPAPER_SYMBOLIC = new Block(FabricBlockSettings
			.of(Material.NETHER_WOOD)
			.strength(4.0f));
	
	public static final Identifier MOLDY_CARPET_ID = new Identifier(modID, "moldy_carpet");
	public static final Block MOLDY_CARPET = new Block(FabricBlockSettings
			.of(Material.WOOL)
			.strength(4.0f));
	
	public static final Identifier CEILING_TILE_ID = new Identifier(modID, "ceiling_tile");
	public static final Block CEILING_TILE = new Block(FabricBlockSettings
			.of(Material.SPONGE)
			.strength(4.0f));
	
	public static final Identifier CEILING_LIGHT_ID = new Identifier(modID, "ceiling_light");
	public static final Block CEILING_LIGHT = new Block(FabricBlockSettings
			.of(Material.GLASS)
			.strength(4.0f)
			.luminance(15));
	
	public static void register() {
		
		BackroomsMod.LOGGER.debug("Registering Blocks");
		
		registerBlock( WALLPAPER_LINED_ID, 		WALLPAPER_LINED	   );
		registerBlock( WALLPAPER_DOTTED_ID, 	WALLPAPER_DOTTED   );
		registerBlock( WALLPAPER_SPOTTED_ID, 	WALLPAPER_SPOTTED  );
		registerBlock( WALLPAPER_SYMBOLIC_ID, 	WALLPAPER_SYMBOLIC );
		registerBlock( MOLDY_CARPET_ID, 		MOLDY_CARPET	   );
		registerBlock( CEILING_TILE_ID, 		CEILING_TILE	   );
		registerBlock( CEILING_LIGHT_ID, 		CEILING_LIGHT	   );
	}
	
	private static void registerBlock(Identifier id, Block block) {
		Registry.register(Registry.BLOCK, id, block);
		Registry.register(Registry.ITEM, id,
				new BlockItem(block, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));
	}
}
