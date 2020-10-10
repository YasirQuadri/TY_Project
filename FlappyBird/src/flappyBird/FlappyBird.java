package flappyBird;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.Timer;

public class FlappyBird implements ActionListener,MouseListener,KeyListener
{
	
	public static FlappyBird flappyBird;
	
	public final int Width= 800 , Height= 800;
	
	public Renderer renderer;
	
	public Rectangle bird;
	
	public int ticks,yMotion,score,highScore = 0;
	
	public boolean gameOver,started;
	
	public ArrayList<Rectangle>  columns;
	 
	public Random rand;
	
	public FlappyBird()
	{
		JFrame jframe= new JFrame();
		
		Timer timer = new Timer(20, this);
		 	
		renderer = new Renderer();
		rand = new Random();
		 
		jframe.add(renderer);
		jframe.setTitle("Flappy Bird");
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.setSize(Width, Height);
		jframe.addMouseListener(this);
		jframe.addKeyListener(this);
		jframe.setResizable(false);
		jframe.setVisible(true);
		
		
		bird= new Rectangle(Width/2 -10 , Height/2 -10 ,20,20);
		columns = new ArrayList<Rectangle>();
		
		addColumn(true);
		addColumn(true);
		addColumn(true);
		addColumn(true);
		
		timer.start();
	}
	
	public void addColumn(boolean start)
	{
		int space  = 300;
		int width  = 100;
		int height = 50 + rand.nextInt(300);
		
		if(start)
		{
			columns.add(new Rectangle(Width + width + columns.size() *300,Height - height -120 ,width,height));
			columns.add(new Rectangle(Width + width + (columns.size() -1) *300, 0,width,Height - height - space));
		}
		
		else
		{
			columns.add(new Rectangle(columns.get(columns.size() -1).x + 600,Height - height -120 ,width,height));
			columns.add(new Rectangle(columns.get(columns.size() -1).x, 0,width,Height - height - space));
		}
	}
	
	public void paintColumn(Graphics g, Rectangle column)
	{
		g.setColor(Color.green.darker());
		g.fillRect(column.x, column.y, column.width, column.height);
	}
	
	public void jump()
	{
		if(gameOver)
		{
			bird= new Rectangle(Width/2 -10 , Height/2 -10 ,20,20);
			columns.clear();
			yMotion =0;
			score = 0;
			
			addColumn(true);
			addColumn(true);
			addColumn(true);
			addColumn(true);
			
			gameOver = false;
		}
		
		if(!started)
		{
			started = true;
		}
		
		else if(!gameOver)
		{
			if(yMotion>0)
			{
				yMotion=0;
			}
			
			yMotion -= 10;
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		int speed =6;
		ticks++;
		
/*	
	 	int difficulty = 1;
	 	int speedAdd = 5;
		if(score>=difficulty)
		{
			difficulty +=1;
			speed ++;
			System.out.println("dif" + difficulty);
			System.out.println("speedadd" + speed);
		}
*/		
		if(started)
		{	
			for(int i=0; i< columns.size();i++)
			{
				Rectangle column = columns.get(i);
				column.x -=speed;
				
			 	if (gameOver)
				{
					column.x +=speed;
				}
				
			}
			
			if(ticks % 2 ==0 && yMotion < 15)
			{
				yMotion += 2;
			}
			
			for(int i=0; i< columns.size();i++)
			{
				Rectangle column = columns.get(i);
				
				if(column.x + column.width <0)
				{
					columns.remove(column);
					
					if(column.y ==0)
					{
						addColumn(false);
					}
				}
				
			}
			
			bird.y += yMotion;
			
			for (Rectangle column : columns)
			{
				if (column.y ==0 && bird.x + bird.width / 2 > column.x + column.width / 2 - 10 && bird.x + bird.width / 2 < column.x + column.width / 2 + 10 )
				{
					if(!gameOver)
						score++;
				}
				
				if(column.intersects(bird))
				{
					gameOver = true;
					
					if(bird.x <= column.x)
					{	
						bird.x = column.x - bird.width;
					}
					else
					{
						if(column.y !=0)
						{
							bird.y = column.y - bird.height;
							
						}
						else if (bird.y < column.height)
						{
							bird.y = column.height;
						}
					}
				}
			}
			
			if (bird.y>Height-120 || bird.y<0 || bird.y == 0)
			{
				gameOver = true;
			}
			
			if(bird.y +yMotion >= Height-120)
			{
				bird.y = Height - 120 - bird.height ;
				gameOver = true;
			}
		}
		highScore();
		renderer.repaint();
	}
	
	public void repaint(Graphics g) 
	{
		g.setColor(Color.cyan);
		g.fillRect(0, 0, Width, Height);
		
		g.setColor(Color.orange);
		g.fillRect(0, Height - 120, Width, 150);

		g.setColor(Color.green);
		g.fillRect(0, Height - 120, Width, 20);

		g.setColor(Color.red);
		g.fillRect(bird.x, bird.y, bird.width, bird.height);
		
		for(Rectangle column : columns)
		{
			paintColumn(g, column);
		}
		
		g.setColor(Color.white);
		g.setFont(new Font("Arial" , 1 , 100));
		
		if(!started)
		{
			g.drawString("Click To Start!", 40, Height/2 -50);
		}
				
		if(gameOver)
		{
			g.drawString("Game Over!", 100, Height/2 -50);
		}
		
		if(!gameOver && started)
		{
			g.drawString(String.valueOf(score), Width / 2 -25, 100);
		}
		
		g.setColor(Color.yellow);
		g.setFont(new Font("Arial" , 1 , 25));
		
		if(!gameOver && started)
		{	
			g.drawString("High Score" , 650 , 20);
			g.drawString(String.valueOf(highScore),700 , 50);
		}

		g.setFont(new Font("Arial" , 1 , 50));
		if(gameOver)
		{
			g.drawString("High Score ", Width/2 - 180, Height/2 + 50 );
			g.drawString(String.valueOf(highScore), Width/2 +110 , Height/2 +50);
		}
		
	}
	
	public void highScore()
	{
		
		if(score > highScore)
		{
			highScore = score;
		}
	}
	
	public static void main (String[] args)
	{
		flappyBird = new FlappyBird();
	}

	@Override
	public void mouseClicked(MouseEvent e) 
	{
		jump();
	}

	@Override
	public void keyReleased(KeyEvent e) 
	{
		if(e.getKeyCode() == KeyEvent.VK_SPACE)
		{
			jump();
		}
	}
	
	@Override
	public void mousePressed(MouseEvent e) 
	{	
	}

	@Override
	public void mouseReleased(MouseEvent e) 
	{
	}

	@Override
	public void mouseEntered(MouseEvent e) 
	{
	}

	@Override
	public void mouseExited(MouseEvent e) 
	{
	}

	@Override
	public void keyTyped(KeyEvent e) 
	{
	}

	@Override
	public void keyPressed(KeyEvent e) 
	{
	}

}
