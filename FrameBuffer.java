import java.util.List;

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

	public void line(int x1, int y1, int x2, int y2, int r, int g, int b) {

		int dx = Math.abs(x2 - x1);
		int dy = Math.abs(y2 - y1);
		int x = x1;
		int y = y1;
		int d = dx - dy;
		int stepX, stepY;

		if (x1 < x2) {
			stepX = 1;
		} else {
			stepX = -1;
		}

		if (y1 < y2) {
			stepY = 1;
		} else {
			stepY = -1;
		}

		// Loop to draw the line
		while (true) {

			// Plot the point
			point(x, y, r, g, b);

			// Check if the line has been completely drawn
			if (x == x2 && y == y2) break;

			int d2 = 2 * d;
			// Adjust the d variable and step in the x direction
			if (d2 > -dy) {
				d -= dy;
				x += stepX;
			}
			// Or adjust the d variable and step in the y direction
			if (d2 < dx) {
				d += dx;
				y += stepY;
			}
		}
	}

	public void outline_polygon(List<int[]> vertices, int r, int g, int b) {

		// Loop through all vertices
		for (int i = 0; i < vertices.size() - 1; i++) {

			int[] start = vertices.get(i);
			int[] end = vertices.get(i + 1);

			// Draw the line from start to end
			line(start[0], start[1], end[0], end[1], r, g, b);
		}

		// Draw line from the last vertex to the first the finish up the polygon
		int[] first = vertices.get(0);
		int[] last = vertices.get(vertices.size() - 1);

		line(last[0], last[1], first[0], first[1], r, g, b);
	}

	// Definitions for the getRed, getGreen and getBlue functions. NOTE these are not complete!
	public int getRed(int xc, int yc) {
		int colour = pixels[yc * width + xc];
		int red = (colour >> 16) & 0xff;

		return red;
	}

	public int getGreen(int xc, int yc) {
		int colour = pixels[yc * width + xc];
		int green = (colour >> 8) & 0xff;

		return green;
	}

	public int getBlue(int xc, int yc) {
		int colour = pixels[yc * width + xc];
		int blue = colour & 0xff;

		return blue;
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
