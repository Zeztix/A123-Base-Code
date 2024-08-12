import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Canvas to use with swing
 */
public class MainCanvas extends Canvas {

	//Our frame buffer that stores all the image data
	private FrameBuffer buffer;
	private BufferedImage image;
	int width;
	int height;

	public MainCanvas(FrameBuffer buffer){
		super();
		this.buffer = buffer;
		this.width = buffer.getWidth();
		this.height = buffer.getHeight();
		this.setSize(buffer.getWidth(), buffer.getHeight());

		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	}

	//Need to override the paint function to use our own frame buffer pixels
	@Override
	public void paint(Graphics g){
		buffer.drawVisibleArea(g);
	}

	//Save function so we can store the output
	public void save(String fileName) {
		try {
			JLabel messageLabel = new JLabel("Would you like to save the visible size or the entire extended buffer?");
			JLabel noteLabel = new JLabel("<html><br><i>Note: Choose extended size if you'd like to see everything (it's also more reliable).</i></html>");

			// Add a popup for the user to choose desired save size
			Object[] options = {"Visible Size", "Extended Buffer Size"};
			Object[] messageContent = {messageLabel, noteLabel};

			int choice = JOptionPane.showOptionDialog(
					null,
					messageContent,

					"Save Option",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null,
					options,
					options[0]
			);

			// NOTE: This can be heavily improved considering the offset, it's just super basic for now.
			// Save only the visible size
			if (choice == JOptionPane.YES_OPTION) {
				image = new BufferedImage(width / 2, height, BufferedImage.TYPE_INT_RGB);
				image.setRGB(0, 0, width / 2, height, this.buffer.getPixels(), 0, width / 2);
			}
			else {
				// Save the entire extended buffer
				image.setRGB(0, 0, width, height, this.buffer.getPixels(), 0, width);
			}

			ImageIO.write(image, "bmp", new File(fileName));
		}
		catch (IOException e) {
			System.out.println("Error: could not write to file " + fileName);
		}
	}
}
