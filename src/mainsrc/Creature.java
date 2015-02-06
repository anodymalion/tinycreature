package mainsrc;

import java.util.Random;

public class Creature {
	public int x, y, target,  maxspeed = 3, maxturn = 8, foodpt, aggr, energy, maxenergy, timeboost;
	public double angle, speed, turnspeed;
	public long starttime, cooldown, coolstart;
	public static int creatSize = 36;
	public double vector[];
	public boolean boost, cool;
	
	public Creature(){
		initializeC();
	}
	public void initializeC(){
		Random rand = new Random();
		x = rand.nextInt(Field.WIDTH - creatSize - 10) + 30;
		y = rand.nextInt(Field.HEIGHT - creatSize - 10) + 30;
		target = -1;
		foodpt = 0;
		angle = 0;
		vector = new double[2];
		vector[0] = 0;
		vector[1] = -1;
		boost = false;
		cool = false;
		timeboost = rand.nextInt(1000) + 1700;
		starttime = 0;
		cooldown = rand.nextInt(1000) + 4000;
		coolstart = 0;
		maxenergy = rand.nextInt(10) + 50;
		energy = maxenergy;
		aggr = rand.nextInt(10);
		speed = rand.nextDouble()*maxspeed + 3;
		turnspeed = rand.nextDouble()*maxturn + 5;
	}
}
