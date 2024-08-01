/**
 * Frame buffer class skeleton
 */

public class FrameBuffer {

	//Store all pixel data in this int array.
	//NOTE: 1 int should contain red, green and blue data NOT 3!
	private int[] pixels;
	private int width;
	private int height;

	//Set up memory for pixel data
	public FrameBuffer(int width, int height) {
		this.width = width;
		this.height = height;
		this.pixels = new int[width * height];
	}

	//A start on the point function. NOTE this is not complete!
	public void point(int xc, int yc, int r, int g, int b) {
		// TODO: Add point into array
		// Check if the point is within the bounds of the buffer
		if (xc >= 0 || xc < width && yc >= 0 || yc < height) {
			// Combine RGB values
			int colour = (r << 16) | (g << 8) | b;
			// Set the pixel in the buffer
			pixels[yc * width + xc] = colour;
		}
	}

	// Implement other drawing functions here...

	public void lineFloat(int x1, int y1, int x2, int y2, int r, int g, int b) {

		// Check if line is vertical
		if (x1 == x2) {
			if (y1 > y2) {
				int temp = y1;
				y1 = y2;
				y2 = temp;
			}
			for (int y = y1; y <= y2; y++) {
				point(x1, y, r, g, b); // Plot vertical line
			}
			return;
		}

		// Ensure it always iterates from left to right
		if (x1 > x2) {
			int tempX = x1, tempY = y1;
			x1 = x2; y1 = y2;
			x2 = tempX; y2 = tempY;
		}

		float m = (y2 - y1) / (x2 - x1); // Calculate the slope
		float c = y1 - m * x1; // Calculate y-intercept

		for (int x = x1; x <= x2; x++) {
			int y = Math.round(m * x + c);
			point(x, y, r, g, b);
		}
	}

	// Definitions for the getRed, getGreen and getBlue functions. NOTE these are not complete!
	public int getRed(int xc, int yc) {
		// TODO: Implement these functions using bitwise operations and masking to retrieve individual colour components
		return 0;
	}

	public int getGreen(int xc, int yc) {
		return 0;
	}

	public int getBlue(int xc, int yc) {
		return 0;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int[] getPixels() {
		return pixels;
	}
}
