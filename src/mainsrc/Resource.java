package mainsrc;

import java.util.Random;
import java.util.ArrayList;

public class Resource {
	public int x, y, resSize = 10;
	public ArrayList<Creature> seekers = new ArrayList<Creature>();
	
	public Resource(){
		initializeG();
		//ArrayList<Creature> seekers = new ArrayList<Creature>();
	}
	private void initializeG(){
		Random rand = new Random();
		x = rand.nextInt(Field.WIDTH - resSize - 50) + 30;
		y = rand.nextInt(Field.HEIGHT - resSize - 50) + 30;
	}
	public int numSeekers(){
		return seekers.size();
	}
	public int highestAgg(){
		int high = 0;
		int index = -1;
		for(int i = 0; i < seekers.size(); ++i){
			if(seekers.get(i).aggr > high){
				index = i;
				high = seekers.get(i).aggr;
			}
		}
		return index;
	}
}
