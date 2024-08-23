import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FrameBuffer {

	//Store all pixel data in this int array.
	//NOTE: 1 int should contain red, green and blue data NOT 3!
	private int[] pixels;
	private int width;
	private int height;
	private int posY = 250; // Hardcoded assuming height is 500px
	private int scrollOffset = 0;
	private boolean scrollEnabled = false;
	private List<int[]> obstacles;

	/**
	 * Initialises the FrameBuffer with the specified visible dimensions.
	 * Allocates memory for the pixel data and initialises the obstacle collisions.
	 *
	 * @param visibleWidth  The width of the visible area.
	 * @param visibleHeight The height of the visible area.
	 */
	public FrameBuffer(int visibleWidth, int visibleHeight) {
		this.width = visibleWidth;
		this.height = visibleHeight;
		this.pixels = new int[width * height];
		obstacles = defineObstacleCollisions();
	}

	/**
	 * Enables side scrolling by expanding the buffer width.
	 * Allocates additional memory for the extended pixel data.
	 */
	public void enableScrolling() {
		this.width = width * 2; // Expand
		this.pixels = new int[width * height];
		this.scrollEnabled = true;

	}

	/**
	 * Plots a single point on the buffer if it is within bounds.
	 * The colour is specified by the red, green, and blue components.
	 *
	 * @param xc The x-coordinate of the point.
	 * @param yc The y-coordinate of the point.
	 * @param r The red component of the colour (0-255).
	 * @param g The green component of the colour (0-255).
	 * @param b The blue component of the colour (0-255).
	 */
	public void point(int xc, int yc, int r, int g, int b) {

		// Check if the point is within the bounds of the buffer
		if (xc >= 0 && xc < width && yc >= 0 && yc < height) {
			// Combine RGB values
			int colour = (r << 16) | (g << 8) | b;
			// Set the pixel in the buffer
			pixels[yc * width + xc] = colour;
		}
	}

	/**
	 * Draws a line using floating point calculations based on the equation y=mx+c.
	 * Handles steep lines by swapping x and y coordinates, ensuring that lines are drawn from left to right.
	 *
	 * @param x1 The starting x-coordinate.
	 * @param y1 The starting y-coordinate.
	 * @param x2 The ending x-coordinate.
	 * @param y2 The ending y-coordinate.
	 * @param r The red component of the colour (0-255).
	 * @param g The green component of the colour (0-255).
	 * @param b The blue component of the colour (0-255).
	 */
	public void lineFloat(int x1, int y1, int x2, int y2, int r, int g, int b) {

		// Calculate what is steep
		boolean isSteep = Math.abs(y2 - y1) > Math.abs(x2 - x1);

		// Check x and y if the line is steep
		if (isSteep) {
			int temp;
			temp = x1; x1 = y1; y1 = temp;
			temp = x2; x2 = y2; y2 = temp;
		}

		// Ensure it always iterates from left to right
		if (x1 > x2) {
			int tempX = x1, tempY = y1;
			x1 = x2; y1 = y2;
			x2 = tempX; y2 = tempY;
		}

		float m = (float) (y2 - y1) / (x2 - x1); // Calculate the slope
		float c = y1 - m * x1; // Calculate y-intercept

		// Iterate over the longer dimension
		for (int x = x1; x <= x2; x++) {
			int y = Math.round(m * x + c);

			// Draw with x and y swapped
			if (isSteep) {
				point(y, x, r, g, b);
			}
			else {
				point(x, y, r, g, b); // Draw normally
			}
		}
	}

	/**
	 * Draws a line using Bresenham's algorithm, ensuring that the line is drawn pixel by pixel.
	 *
	 * @param x1 The starting x-coordinate.
	 * @param y1 The starting y-coordinate.
	 * @param x2 The ending x-coordinate.
	 * @param y2 The ending y-coordinate.
	 * @param r The red component of the colour (0-255).
	 * @param g The green component of the colour (0-255).
	 * @param b The blue component of the colour (0-255).
	 */
	public void line(int x1, int y1, int x2, int y2, int r, int g, int b) {

		int dx = Math.abs(x2 - x1);
		int dy = Math.abs(y2 - y1);
		int x = x1;
		int y = y1;
		int d = dx - dy;
		int stepX, stepY;

		// Determine the direction of the steps
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

	/**
	 * Draws the outline of a polygon by connecting a series of vertices.
	 * The polygon is closed by drawing a line from the last vertex back to the first.
	 *
	 * @param vertices A list of integer arrays where each array contains the x and y coordinates of a vertex.
	 * @param r The red component of the colour (0-255).
	 * @param g The green component of the colour (0-255).
	 * @param b The blue component of the colour (0-255).
	 */
	public void outlinePolygon(List<int[]> vertices, int r, int g, int b) {

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

	/**
	 * Fills a polygon with a specified colour by drawing horizontal lines between the intersections of the polygon's edges.
	 * The polygon is defined by a list of vertices, and the algorithm uses a scan-line approach to fill the area.
	 *
	 * @param vertices A list of integer arrays where each array contains the x and y coordinates of a vertex.
	 * @param r The red component of the colour (0-255).
	 * @param g The green component of the colour (0-255).
	 * @param b The blue component of the colour (0-255).
	 */
	public void fillPolygon(List<int[]> vertices, int r, int g, int b) {

		// Find the bounding box of the polygon
		int minX = vertices.get(0)[0];
		int maxX = vertices.get(0)[0];
		int minY = vertices.get(1)[1];
		int maxY = vertices.get(1)[1];

		// Iterate through the vertices to find the min and max values
		for (int i = 1; i < vertices.size(); i++) {

			int x = vertices.get(i)[0];
			int y = vertices.get(i)[1];

			if (x < minX) {
				minX = x;
			}
			if (x > maxX) {
				maxX = x;
			}
			if (y < minY) {
				minY = y;
			}
			if (y > maxY) {
				maxY = y;
			}
		}

		// Fill the polygon
		for (int y = minY; y <= maxY; y++) {

			List<Integer> intersections = new ArrayList<>();

			// Find the intersections
			for (int i = 0; i < vertices.size(); i++) {

				int[] start = vertices.get(i);
				int[] end = vertices.get((i + 1) % vertices.size());
				int x1 = start[0];
				int y1 = start[1];
				int x2 = end[0];
				int y2 = end[1];

				if (y1 == y2) continue; // Ignore horizontal edges
				if (y >= Math.min(y1, y2) && y < Math.max(y1, y2)) {
					// Calculate the x coordinate of the intersection
					int x = x1 + (y - y1) * (x2 - x1) / (y2 - y1);
					intersections.add(x);
				}
			}
			// Sort the intersections
			Collections.sort(intersections);

			// Fill between pairs of intersections
			for (int j = 0; j < intersections.size(); j += 2) {

				if (j + 1 < intersections.size()) {
					int startX = intersections.get(j);
					int endX = intersections.get(j + 1);

					// Loop through the x coordinates from start to end
					for (int x = startX; x <= endX; x++) {
						// Plot the point
						point(x, y, r, g, b);
					}
				}
			}
		}
	}

	/**
	 * Draws the outline of a circle using the midpoint algorithm.
	 * The circle is drawn by plotting points in all eight octants of the circle.
	 *
	 * @param xc The x-coordinate of the center of the circle.
	 * @param yc The y-coordinate of the center of the circle.
	 * @param radius The radius of the circle.
	 * @param r The red component of the colour (0-255).
	 * @param g The green component of the colour (0-255).
	 * @param b The blue component of the colour (0-255).
	 */
	public void outlineCircle(int xc, int yc, int radius, int r, int g, int b) {

		// Initial points
		int x = 0;
		int y = radius;
		int d = 3 - 2 * radius;

		// Plot the initial points
		plotCirclePoints(xc, yc, x, y, r, g, b);

		// Loop to calculate all points and plot them
		while (y >= x) {

			x++;

			// Update the decision variable and coordinates
			if (d > 0) {
				y--;
				d = d + 4 * (x - y) + 10;
			}
			else {
				d = d + 4 * x + 6;
			}

			// Plot the points
			plotCirclePoints(xc, yc, x, y, r, g, b);
		}
	}

	/**
	 * Plots the points of a circle in all eight octants.
	 * This method is used to reduce the amount of redundant calculations when drawing circles.
	 *
	 * @param xc The x-coordinate of the center of the circle.
	 * @param yc The y-coordinate of the center of the circle.
	 * @param x The x-offset from the center.
	 * @param y The y-offset from the center.
	 * @param r The red component of the colour (0-255).
	 * @param g The green component of the colour (0-255).
	 * @param b The blue component of the colour (0-255).
	 */
	private void plotCirclePoints(int xc, int yc, int x, int y, int r, int g, int b) {
		point(xc + x, yc + y, r, g, b);
		point(xc - x, yc + y, r, g, b);
		point(xc + x, yc - y, r, g, b);
		point(xc - x, yc - y, r, g, b);
		point(xc + y, yc + x, r, g, b);
		point(xc - y, yc + x, r, g, b);
		point(xc + y, yc - x, r, g, b);
		point(xc - y, yc - x, r, g, b);
	}

	/**
	 * Fills a circle using the midpoint algorithm by drawing horizontal lines between the points.
	 * The circle is filled by connecting the points in all octants with lines.
	 *
	 * @param xc The x-coordinate of the center of the circle.
	 * @param yc The y-coordinate of the center of the circle.
	 * @param radius The radius of the circle.
	 * @param r The red component of the colour (0-255).
	 * @param g The green component of the colour (0-255).
	 * @param b The blue component of the colour (0-255).
	 */
	public void fillCircle(int xc, int yc, int radius, int r, int g, int b) {

		// Initial points
		int x = 0;
		int y = radius;
		int d = 3 - 2 * radius;

		while (y >= x) {

			// Draw horizontal lines to fill the circle
			line(xc - x, yc + y, xc + x, yc + y, r, g, b);
			line(xc - x, yc - y, xc + x, yc - y, r, g, b);
			line(xc - y, yc + x, xc + y, yc + x, r, g, b);
			line(xc - y, yc - x, xc + y, yc - x, r, g, b);

			x++;

			// Update the decision variable and coordinates
			if (d > 0) {
				y--;
				d = d + 4 * (x - y) + 10;
			}
			else {
				d = d + 4 * x + 6;
			}
		}
	}

	/**
	 * Inverts the colours of the pixels within a specified rectangular area.
	 * The inversion is performed by subtracting the RGB components from 255.
	 *
	 * @param x1 The x-coordinate of the first corner of the rectangle.
	 * @param y1 The y-coordinate of the first corner of the rectangle.
	 * @param x2 The x-coordinate of the opposite corner of the rectangle.
	 * @param y2 The y-coordinate of the opposite corner of the rectangle.
	 */
	public void invert(int x1, int y1, int x2, int y2) {

		// Calculate the bounding box
		int startX = Math.min(x1, x2);
		int endX = Math.max(x1, x2);
		int startY = Math.min(y1, y2);
		int endY = Math.max(y1, y2);

		// Loop through the specified area
		for (int y = startY; y <= endY; y++) {
			for (int x = startX; x <= endX; x++) {

				int index = y * width + x;
				int p = pixels[index];

				// Extract the RGB components
				int r = (p >> 16) & 0xFF;
				int g = (p >> 8) & 0xFF;
				int b = p & 0xFF;

				// Invert the colour
				int invertedColour = (255 - r) << 16 | (255 - g) << 8 | (255 - b);

				// Update the pixel
				pixels[index] = invertedColour;
			}
		}
	}

	/**
	 * Applies a tint to the entire image by subtracting the tint colour from the pixel's colour components.
	 * The tint operation darkens the image by reducing the intensity of the RGB components.
	 *
	 * @param tintR The amount to subtract from the red component (0-255).
	 * @param tintG The amount to subtract from the green component (0-255).
	 * @param tintB The amount to subtract from the blue component (0-255).
	 */
	public void tint(int tintR, int tintG, int tintB) {

		// Loop through all the pixels
		for (int i = 0; i < pixels.length; i++) {

			int colour = pixels[i];
			int r = (colour >> 16) & 0xFF;
			int g = (colour >> 8) & 0xFF;
			int b = colour & 0xFF;

			// Apply the tint
			r = Math.max(0, r - tintR);
			g = Math.max(0, g - tintG);
			b = Math.max(0, b - tintB);

			// Update the pixels with the tint
			pixels[i] = (r << 16) | (g << 8) | b;
		}
	}

	/**
	 * Applies a blur effect to the entire image by averaging the colours of pixels within a specified radius.
	 * The blur effect smooths the image by blending neighbouring pixels' colours.
	 *
	 * @param radius The radius of the blur effect, determining the size of the area to average.
	 */
	public void blur(int radius) {

		// Store the pixels in a temporary array
		int[] tempPixels = new int[pixels.length];

		// Loop through the pixels to blur
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {

				int avgR = 0, avgG = 0, avgB = 0, count = 0;

				// Average the colour of neighbouring pixels within the radius
				for (int dy = -radius; dy <= radius; dy++) {
					for (int dx = -radius; dx <= radius; dx++) {

						int nx = x + dx;
						int ny = y + dy;

						// Check if the neighbouring pixel is within bounds
						if (nx >= 0 && nx < width && ny >= 0 && ny < height) {

							int colour = pixels[ny * width + nx];
							avgR += (colour >> 16) & 0xFF;
							avgG += (colour >> 8) & 0xFF;
							avgB += colour & 0xFF;
							count++;
						}
					}
				}
				// Calculate the average colour for the current pixel
				avgR /= count;
				avgG /= count;
				avgB /= count;

				// Store the blurred pixel in the temporary array
				tempPixels[y * width + x] = (avgR << 16) | (avgG << 8) | avgB;
			}
		}
		// Replace the original pixels with the blurred pixels
		pixels = tempPixels;
	}

	/**
	 * Gets the red component of the colour at a specific pixel.
	 *
	 * @param xc The x-coordinate of the pixel.
	 * @param yc The y-coordinate of the pixel.
	 * @return The red component of the colour (0-255).
	 */
	public int getRed(int xc, int yc) {
		int colour = pixels[yc * width + xc];
		int red = (colour >> 16) & 0xff;

		return red;
	}

	/**
	 * Gets the green component of the colour at a specific pixel.
	 *
	 * @param xc The x-coordinate of the pixel.
	 * @param yc The y-coordinate of the pixel.
	 * @return The green component of the colour (0-255).
	 */
	public int getGreen(int xc, int yc) {
		int colour = pixels[yc * width + xc];
		int green = (colour >> 8) & 0xff;

		return green;
	}

	/**
	 * Gets the blue component of the colour at a specific pixel.
	 *
	 * @param xc The x-coordinate of the pixel.
	 * @param yc The y-coordinate of the pixel.
	 * @return The blue component of the colour (0-255).
	 */
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

	public int getScrollOffset() {
		return scrollOffset;
	}

	public boolean canScroll() {
		return scrollEnabled;
	}

	/**
	 * Scrolls the view to the left by a fixed amount, moving the character along with it.
	 * Scrolling stops at the left edge of the buffer.
	 */
	public void scrollLeft() {
		// Do not allow scrolling to the negatives
		if (scrollOffset > 0) {
			scrollOffset -= 10;
			drawCharacter(20, 255, 0, 0);
		}
	}

	/**
	 * Scrolls the view to the right by a fixed amount, moving the character along with it.
	 * Scrolling stops at the right edge of the extended buffer.
	 */
	public void scrollRight() {
		// Check that the scroll is within the extended buffer
		if (scrollOffset < width / 2) {
			scrollOffset += 10;
			drawCharacter(20, 255, 0, 0);
		}
	}

	/**
	 * Moves the character up by a fixed amount, ensuring it stays within the top bounds of the screen.
	 */
	public void moveCharacterUp() {
		// Check the top screen bounds
		if (posY - 20 > 0) {
			posY -= 10;
			drawCharacter(20, 255, 0, 0);
		}
	}

	/**
	 * Moves the character down by a fixed amount, ensuring it stays within the bottom bounds of the screen.
	 */
	public void moveCharacterDown() {
		// Check the bottom screen bounds
		if (posY + 60 < height) {
			posY += 10;
			drawCharacter(20, 255, 0, 0);
		}
	}

	/**
	 * Draws the visible area of the buffer onto the provided Graphics context.
	 * This method only draws the portion of the buffer currently visible on screen.
	 *
	 * @param g The Graphics context to draw on.
	 */
	public void drawVisibleArea(Graphics g) {

		// Iterate over the height
		for (int y = 0; y < height; y++) {
			// Only draw the visible width
			for (int x = 0; x < width; x++) {

				int actualX = x + scrollOffset;

				// Only draw if the actual width is within the bounds
				if (actualX >= 0 && actualX < width) {
					int pixel = pixels[y * width + actualX];
					g.setColor(new Color(pixel));
					g.drawRect(x, y, 1, 1); // Draw each pixel
				}
			}
		}
	}

	/**
	 * Draws the character as a filled square on the screen at its current position.
	 * Also checks for collisions with obstacles and whether the player has reached the end of the scrolling area.
	 *
	 * @param size The size of the character square.
	 * @param r The red component of the character's colour.
	 * @param g The green component of the character's colour.
	 * @param b The blue component of the character's colour.
	 */
	public void drawCharacter(int size, int r, int g, int b) {

		// Center of the visible area
		int centerX = width / 4 + scrollOffset;

		// Coordinates for the square
		int topLeftX = centerX - size / 2;
		int topLeftY = posY - size / 2;
		int bottomRightX = centerX + size / 2;
		int bottomRightY = posY + size / 2;

		// Create the vertices list
		List<int[]> vertices = new ArrayList<>();
		vertices.add(new int[] {topLeftX, topLeftY});       // Top-left corner
		vertices.add(new int[] {bottomRightX, topLeftY});    // Top-right corner
		vertices.add(new int[] {bottomRightX, bottomRightY});// Bottom-right corner
		vertices.add(new int[] {topLeftX, bottomRightY});    // Bottom-left corner

		// Draw the filled square using fillPolygon
		fillPolygon(vertices, r, g, b);

		boolean collision = false;

		for (int[] obstacle : obstacles) {
			int obsX = obstacle[0];
			int obsY = obstacle[1];
			int obsWidth = obstacle[2];
			int obsHeight = obstacle[3];

			// Check for the collision
			collision = checkCollision(topLeftX, topLeftY, size, obsX, obsY, obsWidth, obsHeight);
			if (collision) {
				System.out.println("Game Over!");
				scrollEnabled = false;
				break; // Check no more
			}
		}

		// Check if the player has reached the end
		if (centerX >= (width + scrollOffset) / 2) {
			System.out.println("You Win!");
		}
	}

	/**
	 * Defines the positions and sizes of obstacles in the game.
	 * Each obstacle is defined by its top-left corner and its width and height.
	 *
	 * @return A list of integer arrays, each representing an obstacle.
	 */
	private List<int[]> defineObstacleCollisions() {

		List<int[]> obstacles = new ArrayList<>();

		// Define each obstacle's position and size (x, y, width, height)
		obstacles.add(new int[]{300, 200, 50, 250}); // Obstacle 1 Bottom
		obstacles.add(new int[]{300, 0, 50, 150}); // Obstacle 1 Top
		obstacles.add(new int[]{400, 370, 50, 30}); // Obstacle 2 Bottom
		obstacles.add(new int[]{400, 0, 50, 350}); // Obstacle 2 Top
		obstacles.add(new int[]{500, 200, 50, 250}); // Obstacle 3 Bottom
		obstacles.add(new int[]{500, 0, 50, 180}); // Obstacle 3 Top

		return obstacles;
	}

	/**
	 * Checks if the character is colliding with an obstacle.
	 * Collision is detected by comparing the character's position and size with the obstacle's bounds.
	 *
	 * @param charX The x-coordinate of the character's top-left corner.
	 * @param charY The y-coordinate of the character's top-left corner.
	 * @param charSize The size of the character.
	 * @param obsX The x-coordinate of the obstacle's top-left corner.
	 * @param obsY The y-coordinate of the obstacle's top-left corner.
	 * @param obsWidth The width of the obstacle.
	 * @param obsHeight The height of the obstacle.
	 * @return True if there is a collision, false otherwise.
	 */
	public boolean checkCollision(int charX, int charY, int charSize, int obsX, int obsY, int obsWidth, int obsHeight) {
		return (charX < obsX + obsWidth && charX + charSize > obsX &&
				charY < obsY + obsHeight && charY + charSize > obsY);
	}
}
