/*
 * v.0
 * perfect sight
 * instant turning
 * no collision between creatures
 * 
 * v.1
 * delayed turning
 * 
 * v.2 (ongoing)
 * aggression
 * speed bursts
 * energy (?)
 * 
 * Notes to Update: 
 * when a new resource appears, reevaluate your choice
 */

package mainsrc;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

//import java.awt.Toolkit;
import java.awt.Image;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import java.util.ArrayList;
import java.util.Random;

import java.net.URL;

@SuppressWarnings("serial")
public class Field extends JPanel implements Runnable{
	
	//field info
	public static final int WIDTH = MainFunction.WIDTH;
	public static final int HEIGHT = MainFunction.HEIGHT;
	
	//thread info
	private Thread thread;
	private final int DELAY = 50;
	
	//image info
	private Image creature;
	private Image resource;
	private Image cboost;
	
	//resource info
	public int numRes = 10;
	public int resSize = 10;
	ArrayList<Resource> resList = new ArrayList<Resource>();
	
	//creature info
	public int numCr = 5;
	public int creatSize = 36;
	ArrayList<Creature> crList = new ArrayList<Creature>();
	
	public Field(){
		
		//USE THESE for running code
		URL urlc = this.getClass().getResource("images/creature-default.png");
		URL urlr = this.getClass().getResource("images/resource.png");
		URL urlb = this.getClass().getResource("images/creature-boost.png");
		
		//USE THESE for exporting .jar file
		//URL urlc = this.getClass().getResource("/images/creature-default.png");
		//URL urlr = this.getClass().getResource("/images/resource.png");
		//URL urlb = this.getClass().getResource("/images/creature-boost.png");
		
		//creature = this.getToolkit().getImage(urlc); 
		//"images/..."
		ImageIcon iic = new ImageIcon(urlc);
		creature = iic.getImage();
		
		//resource = this.getToolkit().getImage(urlr);
		//ImageIcon iir = new ImageIcon(MainFunction.class.getResource("resource.png"));
		ImageIcon iir = new ImageIcon(urlr);
		resource = iir.getImage();
		
		//cboost = this.getToolkit().getImage(urlb);
		//this.getClass().getResource
		//ImageIcon iib = new ImageIcon(MainFunction.class.getResource("creature-boost.png"));
		ImageIcon iib = new ImageIcon(urlb);
		cboost = iib.getImage();
		
		setBackground(Color.white);
		setFocusable(true);
		initGame();
	}
	
	public void initGame(){
		int numberRes = this.numRes;
		int numberCr = this.numCr;
		for(int i = 0; i < numberCr; ++i){
			spawnCr();
		}
		for(int i = 0; i < numberRes; ++i){
			spawnRes();
		}	
	}
	
	public void addNotify(){
		super.addNotify();
		thread = new Thread(this);
		thread.start();
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);

		g.setColor(Color.BLACK);
		for(int i = 0; i < resList.size(); ++i){
			drawRes(g, resList.get(i));
		}
		for(int i = 0; i < crList.size(); ++i){
			drawCreature(g, crList.get(i));
		}
		g.dispose();
	}
	
	public void spawnRes(){
		Resource res = new Resource();
		resList.add(res);
	}
	
	public void spawnCr(){
		Creature cr = new Creature();
		crList.add(cr);
	}
	
	public void checkRes(){
		for(int i = 0; i < crList.size(); ++i){
			Creature thisC = crList.get(i);
			int indx = thisC.target;
			if(indx == -1){
				thisC.target = calcClosest(thisC);
				resList.get(thisC.target).seekers.add(thisC);
				continue;
			}
			Resource thisR = resList.get(indx);
			if(thisR.x-resSize/2 <= thisC.x+creatSize/2 && thisC.x <= thisR.x+resSize/2 &&
					thisR.y-resSize/2 <= thisC.y && thisC.y <= thisR.y+resSize/2){
				//thisC.foodpt++; //if keeping track of # food eaten
				thisC.energy += 9;
				if(thisC.energy > thisC.maxenergy)
					thisC.energy = thisC.maxenergy;
				
				Resource newres = new Resource();
				resList.set(indx, newres);
				for(int j = 0; j < crList.size(); ++j){
					if(crList.get(j).target == indx)
						crList.get(j).target = -1;
				}		
			}
		}
	}
	
	public void drawCreature(Graphics g, Creature cr){
		//part 0 - assume it has perfect sight and instant turning
		Graphics2D g2d = (Graphics2D) g;
		g2d.rotate(Math.toRadians(cr.angle), cr.x+creatSize/2, cr.y);
		if(cr.boost)
			g2d.drawImage(cboost, cr.x, cr.y, this);
		else
			g2d.drawImage(creature, cr.x, cr.y, this);
		g2d.rotate(0-Math.toRadians(cr.angle), cr.x+creatSize/2, cr.y);
	}
	
	public void drawRes(Graphics g, Resource res){
		g.drawImage(resource, res.x, res.y, this);
	}
	
	private void cycle(){
		double desired, xtemp, ytemp, speed, factor;
		for(int i = 0; i < crList.size(); ++i){
			Creature thisC = crList.get(i);
			
			if(thisC.target == -1){ //get target if creature has none
				thisC.target = calcClosest(thisC);
				resList.get(thisC.target).seekers.add(thisC);
				//resList.get(thisC.target);
			}
			
			
			if(resList.get(thisC.target).numSeekers() > 1 && !thisC.boost 
					&& thisC.energy >= 6 && !thisC.cool){ //start boost
				//System.out.println("BOOST");
				thisC.boost = true;
				//thisC.energy -= 20;
				thisC.starttime = System.currentTimeMillis();
			}
			else if(thisC.cool && ((System.currentTimeMillis() - thisC.coolstart) > thisC.cooldown || thisC.energy == 0)){
				thisC.cool = false;
			}
			
			if(thisC.boost && (System.currentTimeMillis() - thisC.starttime) < thisC.timeboost){
				Random rand = new Random();
				factor = 1.2 + rand.nextDouble();
			}
			else if(thisC.boost && !thisC.cool){
				thisC.coolstart = System.currentTimeMillis();
				thisC.cool = true;
				thisC.boost = false;
				factor = 1;
			}
			else{
				factor = 1;
			}
			
			//calculate next position on field
			desired = (180 - Math.toDegrees(calcAngle(thisC, resList.get(thisC.target))))%360;
			double diff = (desired - thisC.angle)%360;
			double diff2 = diff;
			if(Math.abs(diff) <= thisC.turnspeed + 1){
				thisC.angle = (int)desired % 360;
			}
			else if(diff < 180 && diff > 0 || diff < -180){
				thisC.angle = (int)(thisC.angle + thisC.turnspeed)%360;
				diff = thisC.turnspeed * factor;
			}
			else{
				thisC.angle = (int)(thisC.angle - thisC.turnspeed)%360;
				diff = 0 - thisC.turnspeed * factor;
			}
			
			if(Math.abs(diff2) < 20 || Math.abs(diff2) > 340)
				speed = thisC.speed * factor; //can erase later
			else if(Math.abs(diff2) < 45 || Math.abs(diff2) > 315)
				speed = (thisC.speed / 2) * factor + 1;
			else
				speed = 0;
				
			calcVector(thisC, resList.get(thisC.target), diff, diff2); //calculate new vector for movement
			double hypot = Math.hypot(thisC.vector[0], thisC.vector[1]);			
			xtemp = (thisC.vector[0] / hypot) * speed;
			ytemp = (thisC.vector[1] / hypot) * speed;
			thisC.x += xtemp;
			thisC.y += ytemp;
		}	
	}
	
	private double calcAngle(Creature cr, Resource res){
		int b2 = (resSize/2 + res.y-cr.y);
		int b3 = (resSize/2 + res.x-cr.x-(creatSize)/2);
		return Math.atan2(b3, b2);
	}
	
	private void calcVector(Creature cr, Resource res, double angl, double diff){ 
		int angle = (int) angl;
		
		if(diff <= cr.turnspeed + 1){
			cr.vector[0] = (resSize/2 + res.x - cr.x - creatSize/2);
			cr.vector[1] = (resSize/2 + res.y - cr.y);
		}
		else{
			//System.out.println(cr.vector[0] + ", " + cr.vector[1]);
			double v0 = cr.vector[0];
			double v1 = cr.vector[1];
			cr.vector[0] = v0 * Math.cos(Math.toRadians(angle)) - v1 * Math.sin(Math.toRadians(angle));
			cr.vector[1] = v0 * Math.sin(Math.toRadians(angle)) + v1 * Math.cos(Math.toRadians(angle));
			
			if(cr.angle > 0 && cr.angle <= 180){
				cr.vector[0] = Math.abs(cr.vector[0]);
			}
			else{
				cr.vector[0] = 0 - Math.abs(cr.vector[0]);
			}
			if(cr.angle > 90 && cr.angle <= 270){
				cr.vector[1] = Math.abs(cr.vector[1]);
			}
			else{
				cr.vector[1] = 0 - Math.abs(cr.vector[1]);
			}
		}
	}
	
	//alternate method for calculating movement vector - simply move toward closest food
	/*private void calcVector2(Creature cr, Resource res, double diff){
		double b2 = (res.y-cr.y);
		double b3 = (res.x-cr.x-(creatSize)/2);
		cr.vector[0] = b3;
		cr.vector[1] = b2;
	}*/
	
	//calculate the closest resource to cr
	private int calcClosest(Creature cr){
		int closest = 0;
		double distance = Math.hypot((double)Field.WIDTH, (double)Field.HEIGHT);
		for(int i = 0; i < resList.size(); ++i){
			Resource res = resList.get(i);
			double xVal = cr.x - res.x;
			double yVal = cr.y - res.y;
			if(Math.hypot(xVal, yVal) < distance){
				distance = Math.hypot(xVal,  yVal);
				closest = i;
			}
		}
		return closest;
	}
	
	public void run(){
		long initTime, energyTime, timeDiff, energyDiff, sleep;
		initTime = System.currentTimeMillis();
		energyTime = initTime;
		while(true){
			cycle();
			checkRes();
			repaint();
			timeDiff = System.currentTimeMillis() - initTime;
			energyDiff = System.currentTimeMillis() - energyTime; 
			sleep = DELAY - timeDiff;
			if(sleep < 0){
				sleep = 2;
			}
			try{
				Thread.sleep(sleep);
			}catch(InterruptedException e){
				System.out.println("Interrupted " + e.getMessage());
			}
			initTime = System.currentTimeMillis();
			if(energyDiff >= 500){
				for(int i = 0; i < crList.size(); ++i){
					int eng = crList.get(i).energy;
					if(eng > 0 && !crList.get(i).boost)
						crList.get(i).energy = eng - 1; //-1
					else if(crList.get(i).boost){
						if(eng > 4)
							crList.get(i).energy = eng - 2;
						else
							crList.get(i).energy = 0;
					}
					else{
						crList.get(i).energy = 0;
					}
				}
				energyTime = System.currentTimeMillis();
			}
		}
	}
}
