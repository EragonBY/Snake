import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        new game();
    }
}
class game implements ActionListener{

    public static int scale=40;
    public static int size=15;
    int speed=100;
    int liveTime=5000;

    private static Field fl;
    private static int time;
    public  static Timer timer;
    public static int key;

    final static LinkedList<Point> snake = new LinkedList<>();
    final static LinkedList<Point> apple =new LinkedList<>();
    LinkedList<Integer> appleLive= new LinkedList<>();

    public game(){
        snake.addFirst(new Point(size/2,size/2));
        spawnApple();
        spawnApple();
        spawnApple();
        fl= new Field();

        timer = new Timer(speed, this);
        timer.start();
        time = 0;
    }

    public void step(Point delta){
        snake.addFirst(new Point(snake.getFirst().x+delta.x,snake.getFirst().y+delta.y));
        snake.removeLast();
        fl.drawMove(snake,apple);
        isDead();
        isEaten();
    }

    public  void isDead(){
        if(snake.lastIndexOf(snake.getFirst())!=0||
                snake.getFirst().x<0 ||
                snake.getFirst().x>=size||
                snake.getFirst().y<0 ||
                snake.getFirst().y >=size){

            timer.stop();
            fl.frame.setTitle("Snake Time: " + time / 1000 + " Score: " + snake.size() + " DEAD");
        }

    }

    public  void isEaten(){
        if(apple.contains(snake.getFirst())){
            respawnApple(apple.indexOf(snake.getFirst()));
            snake.addLast(snake.getLast());
        }
    }

    public void spawnApple(){
        Point pnt;
        do pnt=randomPoint();
        while(snake.contains(pnt)||apple.indexOf(pnt)!=-1);
        apple.addFirst(pnt);
        appleLive.addFirst(time);
    }

    public  void respawnApple(int ind){
        Point pnt;
        do pnt=randomPoint();
        while (snake.contains(pnt)||apple.indexOf(pnt)!=-1);
        apple.set(ind,pnt);
        appleLive.set(ind,time);
    }

    public static Point randomPoint(){
        int x;int y;
        Random rand = new Random();
        x=rand.nextInt(size);
        y=rand.nextInt(size);
        return new Point(x,y);
    }


    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        step(Field.checkDirection());
        time += speed;
        fl.frame.setTitle("Snake Time: " + time / 1000 + " Score: " + snake.size());
        for(int i=0;i<apple.size();i++)
            if (appleLive.get(i)+liveTime<time)
                respawnApple(i);
    }
    public static void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT ||
                e.getKeyCode() == KeyEvent.VK_RIGHT ||
                e.getKeyCode() == KeyEvent.VK_UP ||
                e.getKeyCode() == KeyEvent.VK_DOWN
        )
            key = e.getKeyCode();
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (timer.isRunning()) {
                timer.stop();
                fl.frame.setTitle("Snake Time: " + time / 1000 + " Score: " + snake.size() + " PAUSE");
            } else timer.start();
        }
    }
}

class Field  extends JPanel{

    public  JFrame frame;

    enum Direction {left, right, up, down}
    private static Direction direction;
    private static int x=0;
    private static int y=1;
    public Field() {

        frame = new JFrame("Snake");
        frame.getContentPane().setBackground(Color.WHITE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.remove(this);
        frame.setResizable(false);
        frame.getContentPane().setLayout(null);
        frame.setSize(game.size * game.scale + 16, game.size * game.scale + 39);
        frame.setLocationRelativeTo(null);
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                game.keyPressed(e);
            }
        });
        direction = Direction.down;
    //    frame.getContentPane().add(new Draw(game.snake.getFirst(), 1));
        frame.setVisible(true);
    }



    public static Point checkDirection() {

        switch (game.key) {
            case KeyEvent.VK_LEFT:
                if (direction != Direction.right) {
                    x = -1;
                    y = 0;
                    direction = Direction.left;
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (direction != Direction.left) {
                    x = 1;
                    y = 0;
                    direction = Direction.right;
                }
                break;
            case KeyEvent.VK_UP:
                if (direction != Direction.down) {
                    x = 0;
                    y = -1;
                    direction = Direction.up;
                }
                break;
            case KeyEvent.VK_DOWN:
                if (direction != Direction.up) {
                    x = 0;
                    y = 1;
                    direction = Direction.down;
                }
                break;
        }
        return new Point(x,y);
    }

    public  void drawMove(LinkedList<Point> snake,LinkedList<Point> apple){
        for(int i= 0;i<game.size;i++)
            for (int j = 0; j < game.size; j++)
                if (snake.contains(new Point(j,i)))
                {
                    if ( frame.getContentPane().getComponentAt(new Point(j*game.scale, i*game.scale)) !=frame.getContentPane())
                        frame.getContentPane().remove(frame.getContentPane().getComponentAt(new Point(j*game.scale, i*game.scale)));
                    if (snake.indexOf(new Point(j, i)) == 0){
                        frame.getContentPane().add(new Draw(new Point(j, i), 1));
                    }
                    else  frame.getContentPane().add(new Draw(new Point(j, i), 2));
                }
              /*
              //for gif head
              { //frame.getContentPane().remove(frame.getContentPane().getComponentAt(new Point(j*game.scale, i*game.scale)));
                   if (snake.indexOf(new Point(j, i)) == 0) {
                       if ( frame.getContentPane().getComponentAt(new Point(j*game.scale, i*game.scale)) !=frame.getContentPane())
                       frame.getContentPane().remove(frame.getContentPane().getComponentAt(new Point(j*game.scale, i*game.scale)));
                       frame.getContentPane().getComponentAt(snake.get(1).x * game.scale, snake.get(1).y * game.scale).setLocation(new Point(j * game.scale, i * game.scale));
                   }
                   else if(!snake.getLast().equals(new Point(j, i))) {
                           frame.getContentPane().add(new Draw(new Point(j, i), 2));
                   }
                   if(snake.size()>2)
                    frame.getContentPane().remove(frame.getContentPane().getComponentAt(snake.getLast().x*game.scale,snake.getLast().y*game.scale));
                }*/
                else if (apple.contains(new Point(j,i)))
                { if(frame.getContentPane().getComponentAt(new Point(j*game.scale, i*game.scale)) ==frame.getContentPane())
                    frame.getContentPane().add(new Draw(new Point(j,i),3));
                }
                else if ( frame.getContentPane().getComponentAt(new Point(j*game.scale, i*game.scale)) !=frame.getContentPane())
                    frame.getContentPane().remove(frame.getContentPane().getComponentAt(new Point(j * game.scale, i * game.scale)));


        frame.getContentPane().repaint();
}


}
class Draw extends JLabel{
    public Draw(Point pnt,int num){
        ImageIcon imageIcon;
        switch (num) {
            case 1:
                imageIcon = new ImageIcon(getClass().getResource("res/drav.jpg")); break;
            case 2:
                 imageIcon = new ImageIcon(getClass().getResource("res/Spinning_Axe.png"));break;
            case 3:
                 imageIcon = new ImageIcon(getClass().getResource("res/new_axe.gif"));break;
            default: imageIcon = new ImageIcon(getClass().getResource("res/default.png")); break;
        }
        Image image = imageIcon.getImage(); // transform it
        Image newImg = image.getScaledInstance(game.scale, game.scale, Image.SCALE_REPLICATE); // scale it the smooth way
        setIcon(new ImageIcon(newImg));
        setSize(game.scale, game.scale);
        setLocation(pnt.x*game.scale,pnt.y*game.scale);
    }
}





