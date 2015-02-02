package mainsrc;

import java.util.Random;

public class Creature {
	public int x, y, target,  maxspeed = 3, maxturn = 8, foodpt, aggr;
	public double angle, speed, turnspeed;
	public static int creatSize = 36;
	public double vector[];
	
	public Creature(){
		int x, y;
		double speed;
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
		aggr = rand.nextInt(10);
		speed = rand.nextDouble()*maxspeed + 3;
		turnspeed = rand.nextDouble()*maxturn + 5;
	}
	
}
