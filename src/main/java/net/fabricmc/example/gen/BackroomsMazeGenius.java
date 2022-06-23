package net.fabricmc.example.gen;

import net.minecraft.util.math.Vec3i;

import java.util.Random;

/*

This class is responsible for the generation of the walls and rooms.
Hallways are bsp tree partition lines and rooms are leaves.
To find the block at a specific pos, a tree lookup will occur and the tree will be generated more if needed.

 */
public class BackroomsMazeGenius
{
	private static BackroomsMazeGenius instance = null;
	private final int worldBorder = 32767;
	private HallwayRoom root;
	private final Random rand;
	private long worldSeed;
	
	private BackroomsMazeGenius()
	{
		this.rand = new Random();
	}
	
	public static BackroomsMazeGenius getInstance()
	{
		if(instance == null) {
			instance = new BackroomsMazeGenius();
		}
		return instance;
	}
	
	public void setWorldSeed(long worldSeed)
	{
		this.worldSeed = worldSeed;
	}
	
	public boolean isFilled(Vec3i pos) {
		Border room = getRoomAt(pos);
		
		// Rooms that are too small to be walled can be hallways
		if(room.getWidth() < 5 || room.getHeight() < 5) return false;
		
		// Rooms get walls and shit
		if(room.x1 == pos.getX() || room.y1 == pos.getZ()) return true;
		
		return false;
	}
	
	public Border getRoomAt(Vec3i pos)
	{
		
		// create root if needed
		if(root == null) {
			root = new HallwayRoom();
			rand.setSeed(worldSeed); // First hallway uses the world seed as random
			root.coord = randomNormal(-worldBorder, worldBorder);
			root.isVertical = rand.nextBoolean();
			root.totalArea = new Border(-worldBorder, -worldBorder, worldBorder, worldBorder);
		}
		
		// start at root and iterate thru the tree, creating partitions if needed
		HallwayRoom currentNode = root;
		
		do {
			if(currentNode.isVertical) {
				if(pos.getX() < currentNode.coord) {
					if(currentNode.back == null) {
						currentNode.back = new HallwayRoom();
						rand.setSeed(worldSeed + currentNode.coord + 5);
						currentNode.back.coord = randomNormal(currentNode.totalArea.y1, currentNode.totalArea.y2);
						currentNode.back.totalArea = new Border(currentNode.totalArea.x1, currentNode.totalArea.y1,
								currentNode.coord, currentNode.totalArea.y2);
					}
					currentNode = currentNode.back;
				}
				else {
					if(currentNode.front == null) {
						currentNode.front = new HallwayRoom();
						rand.setSeed(worldSeed + currentNode.coord + 2);
						// 2 was chosen by dice-roll, guaranteed to be random
						currentNode.front.coord = randomNormal(currentNode.totalArea.y1, currentNode.totalArea.y2);
						currentNode.front.totalArea = new Border(currentNode.coord, currentNode.totalArea.y1,
								currentNode.totalArea.x2, currentNode.totalArea.y2);
					}
					currentNode = currentNode.front;
				}
				currentNode.isVertical = false;
			}
			else {
				if(pos.getZ() < currentNode.coord) {
					if(currentNode.back == null) {
						currentNode.back = new HallwayRoom();
						rand.setSeed(worldSeed + currentNode.coord + 3);
						currentNode.back.coord = randomNormal(currentNode.totalArea.x1, currentNode.totalArea.x2);
						currentNode.back.totalArea = new Border(currentNode.totalArea.x1, currentNode.totalArea.y1,
								currentNode.totalArea.x2, currentNode.coord);
					}
					currentNode = currentNode.back;
				}
				else {
					if(currentNode.front == null) {
						currentNode.front = new HallwayRoom();
						rand.setSeed(worldSeed + currentNode.coord + 8);
						currentNode.front.coord = randomNormal(currentNode.totalArea.x1, currentNode.totalArea.x2);
						currentNode.front.totalArea = new Border(currentNode.totalArea.x1, currentNode.coord,
								currentNode.totalArea.x2, currentNode.totalArea.y2);
					}
					currentNode = currentNode.front;
				}
				currentNode.isVertical = true;
			}
//			float shapeRatio = (float)currentNode.serviceArea.getHeight() / (float)currentNode.serviceArea.getWidth();
		} while((currentNode.totalArea.getWidth() > 100 &&
				currentNode.totalArea.getHeight() > 100) ||
				currentNode.totalArea.getArea() > 50000);
		
		return currentNode.totalArea;
	}
	
	// Use a normal distribution to generate numbers that are more likely to be average (so rooms are more likely to
	// be square-ish rather than very long and skinny)
	private int randomNormal(int min, int max)
	{
		final double wackinessFactor = 0.25; // Lower values make more uniform rooms, higher values make wackier rooms
		
		int difference = max - min;
		int average = (min + max) / 2;
		double gaussian = rand.nextGaussian();
		int scaledGaussian = (int) ((gaussian * difference * 0.15d * wackinessFactor) + average);
		if(scaledGaussian > max || scaledGaussian < min) scaledGaussian = average;
		return scaledGaussian;
	}
	
	// Space partition line
	private static class HallwayRoom
	{
		public boolean isVertical; // if not, its horizontal
		public int coord;
		
		public HallwayRoom front;
		public HallwayRoom back;
		
		public Border totalArea;
	}
	
	// Space partition tree leaf
	private static class Room
	{
		public Border bounds;
		public int verticalHeight;
	}
	
	public static class Border
	{
		public final int x1;
		public final int y1;
		public final int x2;
		public final int y2;
		
		public Border(int x1, int y1, int x2, int y2)
		{
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
		}
		
		public int getArea()
		{
			return Math.abs((x1 - x2) * (y1 - y2));
		}
		
		public int getWidth()
		{
			return Math.abs(x1 - x2);
		}
		
		public int getHeight()
		{
			return Math.abs(y1 - y2);
		}
		
		
	}
	
	
//	private static class Room1 extends Border {
//
//		public final int height;
//
//		public Room1(int x1, int y1, int x2, int y2, int height)
//		{
//			super(x1, y1, x2, y2);
//			this.height = height;
//		}
//	}
//
//
//	private static class PartitionHallway extends Room1 {
//
//		public PartitionHallway(int x1, int y1, int x2, int y2, int height)
//		{
//			super(x1, y1, x2, y2, height);
//		}
//
//		Room1 hallwayRoom;
//
//		PartitionHallway front;
//		PartitionHallway back;
//
//		boolean isVertical;
//	}


}
