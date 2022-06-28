package net.fabricmc.example.world.gen.chunk;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.example.world.biome.ModBiomes;
import net.fabricmc.example.world.gen.BackroomsMazeGenius;
import net.minecraft.block.BlockState;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class BackroomsChunkGenerator extends ChunkGenerator {
	public static final Codec<BackroomsChunkGenerator> CODEC = RecordCodecBuilder.create((instance) ->
			method_41042(instance).and(
							RegistryOps.createRegistryCodec(Registry.BIOME_KEY).forGetter((generator) -> generator.biomeRegistry)
					)
					.apply(instance, instance.stable(BackroomsChunkGenerator::new))
	);
	
	private final Registry<Biome> biomeRegistry;
	
	public BackroomsChunkGenerator(Registry<StructureSet> registry, Registry<Biome> biomeRegistry) {
		super(registry, Optional.empty(), new FixedBiomeSource(biomeRegistry.getOrCreateEntry(ModBiomes.getLevel1())));
		this.biomeRegistry = biomeRegistry;
	}
	
	@Override
	protected Codec<? extends ChunkGenerator> getCodec() {
		return CODEC;
	}
	
	@Override
	public ChunkGenerator withSeed(long seed) {
		return this;
	}
	
	@Override
	public MultiNoiseUtil.MultiNoiseSampler getMultiNoiseSampler() {
		// Mirror what Vanilla does in the debug chunk generator
		return MultiNoiseUtil.method_40443();
	}
	
	@Override
	public void carve(ChunkRegion chunkRegion, long l, BiomeAccess biomeAccess, StructureAccessor structureAccessor, Chunk chunk, GenerationStep.Carver carver) {
	}
	
	@Override
	public void generateFeatures(StructureWorldAccess world, Chunk chunk, StructureAccessor structureAccessor) {
	
	}
	
	@Override
	public void setStructureStarts(DynamicRegistryManager registryManager, StructureAccessor world, Chunk chunk, StructureManager structureManager, long worldSeed) {
	
	}
	
	@Override
	public void buildSurface(ChunkRegion region, StructureAccessor structureAccessor, Chunk chunk) {
	}
	
	public int getSpawnHeight(HeightLimitView world) {
		return 1;
	}
	
	@Override
	public void populateEntities(ChunkRegion region) {
	}
	
	@Override
	public int getWorldHeight() {
		return 16;
	}
	
	@Override
	public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender, StructureAccessor structureAccessor, Chunk chunk) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		Heightmap heightmap = chunk.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
		Heightmap heightmap2 = chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);
		
		
		BlockState floorState = Registry.BLOCK.get(new Identifier("backrooms:moldy_carpet")).getDefaultState();
		int floorHeight = 0;
		
		BlockState ceilTileState = Registry.BLOCK.get(new Identifier("backrooms:ceiling_tile")).getDefaultState();
		BlockState ceilLightState = Registry.BLOCK.get(new Identifier("backrooms:ceiling_light")).getDefaultState();
		int ceilHeight = 5;
		
		BlockState innerRoomState;
		BackroomsMazeGenius bmg = BackroomsMazeGenius.getInstance();
		
		for(int k = 0; k < 16; ++k) {
			for(int l = 0; l < 16; ++l) {
				// Floor
				chunk.setBlockState(mutable.set(k, floorHeight, l), floorState, false);
				heightmap.trackUpdate(k, floorHeight, l, floorState);
				heightmap2.trackUpdate(k, floorHeight, l, floorState);
				
				// Ceiling
				if(k % 4 == 0 && (l & 2) != 0) {
					chunk.setBlockState(mutable.set(k, ceilHeight, l), ceilLightState, false);
				} else {
					chunk.setBlockState(mutable.set(k, ceilHeight, l), ceilTileState, false);
				}
				
				// Walls
				int x = chunk.getPos().getOffsetX(k);
				int z = chunk.getPos().getOffsetZ(l);
				innerRoomState = bmg.getBlockStateAt(new Vec3i(x, 0, z));
				if(!innerRoomState.isAir()) {
					for(int i = 1; i < ceilHeight; i++) {
						chunk.setBlockState(mutable.set(k, i, l), innerRoomState, false);
					}
				}
			}
		}
		
		return CompletableFuture.completedFuture(chunk);
	}
	
	@Override
	public int getSeaLevel() {
		return 0;
	}
	
	@Override
	public int getMinimumY() {
		return 0;
	}
	
	@Override
	public int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world)
	{
		return 1;
	}
	
	@Override
	public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView heightLimitView) {
		return new VerticalBlockSample(heightLimitView.getBottomY(), new BlockState[]{Registry.BLOCK.get(new Identifier("minecraft:red_stained_glass")).getDefaultState()});
	}
	
	@Override
	public void getDebugHudText(List<String> list, BlockPos blockPos) {
	}
}