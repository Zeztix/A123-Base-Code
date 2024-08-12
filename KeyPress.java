import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class KeyPress extends Canvas implements KeyListener {

    private FrameBuffer buffer;
    private Canvas canvas;

    public KeyPress(FrameBuffer buffer, Canvas canvas) {
        this.buffer = buffer;
        this.canvas = canvas;
    }

    @Override
    public void keyPressed(KeyEvent e) {

        // Check the key pressed
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            System.out.println("Moving Left");
        }
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            System.out.println("Moving Right");
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}
