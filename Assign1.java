import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Assign1 {

    public static void main(String[] args) {

	System.out.println("Starting 2D engine...");
        //Buffer, Canvas and Frame containers.
        FrameBuffer buffer = null;
        MainCanvas canvas = null;
        JFrame frame = null;
        KeyPress keyPress = null;

        //Tools to read command line and text file instructions
        BufferedReader reader = null;
        Scanner scanner;
        String command;
        int lineNumber = 0;
        String trimmedLine;

        //Look at each file
        for (int i = 0; i < args.length; i++) {
            try {

                //Open file and read line
                reader = new BufferedReader(new FileReader(args[i]));
                String line = reader.readLine();

                //Process and read every line
                while (line != null) {
                	
                	//Clean up any extra spaces etc.
                    trimmedLine = line;
                    while (trimmedLine != null && trimmedLine.trim().equals("")) {
                        line = reader.readLine();
                        trimmedLine = line;
                    }
                    lineNumber++;
                    
                    //ignore empty
                    if(line == null){
                        break;
                    }

                    //Filter comments
                    if (line.contains("#")) {
                        String noComments;
                        noComments = line.substring(0, line.indexOf("#"));
                        //if there is text before comment, process it, otherwise ignore the line
                        if (noComments.length() > 0) {
                            line = noComments;
                        } else {
                            line = reader.readLine();
                            continue;
                        }
                    }

                    if (line.contains("(")) {
                        //strip spaces from after opening bracket - ensure scanner reads RGB values together with next()
                        line = line.substring(0, line.indexOf("(")) + line.substring(line.indexOf("("), line.length()).replace(" ", "");
                    }

                    //Store command
                    command = line.substring(0, line.indexOf(" "));

                    //Read all the arguments into a scanner
                    scanner = new Scanner(line.substring(line.indexOf(" "), line.length()));

                    switch (command) {
                        case "INIT":
                            //Create the buffer and canvas
                            int width = scanner.nextInt();
                            int height = scanner.nextInt();
                            buffer = new FrameBuffer(width, height);
                            canvas = new MainCanvas(buffer);

                            //Set up a swing frame
                            frame = new JFrame();
                            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                            frame.setSize(width, height);
                            frame.add(canvas);
                            frame.setTitle("Assign1");

                            // Initialize the KeyPress handler
                            keyPress = new KeyPress(buffer, canvas);
                            frame.addKeyListener(keyPress);

                            frame.setVisible(true);

                            System.out.println("Creating canvas, frame and buffer of size " + width + "x" + height);
                            break;

                        case "POINT":
                            //Get x and y location
                            int xc = scanner.nextInt();
                            int yc = scanner.nextInt();

                            //get the colour
                            String colour = scanner.next();
                            //System.out.println(colour);
                            int[] colours = extractColour(colour);
                            int red = colours[0];
                            int green = colours[1];
                            int blue = colours[2];

                            colours = checkColourRange(red, green, blue);

                            //Draw a point with our point method
                            buffer.point(xc, yc, colours[0], colours[1], colours[2]);
                            canvas.repaint();

                            System.out.println("Drawing point: (" + xc + "," + yc + "R=" + red + " G="+green + " B="+blue + ")");
                            
                            break;
                        case "LINE_FLOAT":

                            //read end point values from next 4 ints
                            int lnflt_x1 = scanner.nextInt();
                            int lnflt_y1 = scanner.nextInt();
                            int lnflt_x2 = scanner.nextInt();
                            int lnflt_y2 = scanner.nextInt();

                            //get the colour
                            colour = scanner.next();
                            colour = colour.replace("(", "").replace(")", "");
                            colours = extractColour(colour);
                            red = colours[0];
                            green = colours[1];
                            blue = colours[2];

                            colours = checkColourRange(red, green, blue);

                            // Call lineFloat method to change pixel array and update canvas
                            buffer.lineFloat(lnflt_x1, lnflt_y1, lnflt_x2, lnflt_y2, colours[0], colours[1], colours[2]);
                            canvas.repaint();

                            break;
                            
                        case "LINE":

                            // Read end point values
                            lnflt_x1 = scanner.nextInt();
                            lnflt_y1 = scanner.nextInt();
                            lnflt_x2 = scanner.nextInt();
                            lnflt_y2 = scanner.nextInt();

                            // Extract the colours
                            colour = scanner.next();
                            colours = extractColour(colour);
                            red = colours[0];
                            green = colours[1];
                            blue = colours[2];

                            colours = checkColourRange(red, green, blue);

                            // Call line method to change pixel array and update canvas
                            buffer.line(lnflt_x1, lnflt_y1, lnflt_x2, lnflt_y2, colours[0], colours[1], colours[2]);
                            canvas.repaint();

                            break;

                        case "OUTLINE_POLYGON":

                            List<int[]> vertices = new ArrayList<>();

                            // Read vertices until the color string
                            while (scanner.hasNextInt()) {
                                int x = scanner.nextInt();
                                int y = scanner.nextInt();
                                vertices.add(new int[]{x, y});
                            }

                            // Extract the colours
                            colour = scanner.next();
                            colours = extractColour(colour);
                            red = colours[0];
                            green = colours[1];
                            blue = colours[2];

                            colours = checkColourRange(red, green, blue);

                            // Call line method to change pixel array and update canvas
                            buffer.outlinePolygon(vertices, colours[0], colours[1], colours[2]);
                            canvas.repaint();

                            break;

                        case "FILL_POLYGON":

                            vertices = new ArrayList<>();

                            // Read vertices until the color string
                            while (scanner.hasNextInt()) {
                                int x = scanner.nextInt();
                                int y = scanner.nextInt();
                                vertices.add(new int[]{x, y});
                            }

                            // Extract the colours
                            colour = scanner.next();
                            colours = extractColour(colour);
                            red = colours[0];
                            green = colours[1];
                            blue = colours[2];

                            colours = checkColourRange(red, green, blue);

                            // Call line method to change pixel array and update canvas
                            buffer.fillPolygon(vertices, colours[0], colours[1], colours[2]);
                            canvas.repaint();

                            break;

                        case "OUTLINE_CIRCLE":

                            int x = scanner.nextInt();
                            int y = scanner.nextInt();
                            int r = scanner.nextInt();

                            // Extract the colours
                            colour = scanner.next();
                            colours = extractColour(colour);
                            red = colours[0];
                            green = colours[1];
                            blue = colours[2];

                            colours = checkColourRange(red, green, blue);

                            // Call line method to change pixel array and update canvas
                            buffer.outlineCircle(x, y, r, colours[0], colours[1], colours[2]);
                            canvas.repaint();

                            break;

                        case "FILL_CIRCLE":

                            x = scanner.nextInt();
                            y = scanner.nextInt();
                            r = scanner.nextInt();

                            // Extract the colours
                            colour = scanner.next();
                            colours = extractColour(colour);
                            red = colours[0];
                            green = colours[1];
                            blue = colours[2];

                            colours = checkColourRange(red, green, blue);

                            // Call line method to change pixel array and update canvas
                            buffer.fillCircle(x, y, r, colours[0], colours[1], colours[2]);
                            canvas.repaint();

                            break;

                        case "INVERT":

                            int x1 = scanner.nextInt();
                            int y1 = scanner.nextInt();
                            int x2 = scanner.nextInt();
                            int y2 = scanner.nextInt();

                            buffer.invert(x1, y1, x2, y2);
                            canvas.repaint();

                        	break;

                        case "TINT":

                            r = scanner.nextInt();
                            int g = scanner.nextInt();
                            int b = scanner.nextInt();

                            buffer.tint(r, g, b);
                            canvas.repaint();

                            break;

                        case "SIDE_SCROLL":

                            // Enable the scrolling (simply 2x buffer size)
                            buffer.enableScrolling();
                            canvas.repaint();

                            break;

                        case "PAUSE":
                            int millis = scanner.nextInt();
                            System.out.println("Pause: " + millis + " milliseconds");
                            try {
                                Thread.sleep(millis);
                            } catch (InterruptedException e) {
                                System.out.println("Problem with sleep...");
                                e.printStackTrace();
                            }
                            break;

                        case "SAVE":
                            String fileName = scanner.next();
                            canvas.save(fileName);
                            System.out.println("Saving file:" + fileName);

                            break;

                        case "LOAD_BMP":

                            String loadFileName = scanner.next();
                            loadFile(loadFileName, buffer, canvas);
                            canvas.repaint();
                            System.out.println("Loading BMP File: " + loadFileName);

                            break;

                        default:
                            System.out.println("Unknown command at line:  " + lineNumber);
                    }
                    line = reader.readLine();

                }

            } catch (FileNotFoundException e) {
                System.out.println("Error: File could not be found: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("Error: Unexpected IO exception encountered");
            }
        }
    }

    public static int[] extractColour(String colour) {

        colour = colour.replace("(", "").replace(")", ""); // Remove the parentheses
        String[] colourTokens = colour.split(","); // Split by comma
        int[] colours = new int[3];
        colours[0] = Integer.parseInt(colourTokens[0]); // Red
        colours[1] = Integer.parseInt(colourTokens[1]); // Green
        colours[2] = Integer.parseInt(colourTokens[2]); // Blue

        return colours;
    }

    public static int[] checkColourRange(int red, int green, int blue) {

        int[] colours = new int[3];

        // Ensure colour values are within the valid range
        colours[0] = Math.min(255, Math.max(0, red));
        colours[1] = Math.min(255, Math.max(0, green));
        colours[2] = Math.min(255, Math.max(0, blue));

        return colours;
    }

    public static void loadFile(String fileName, FrameBuffer buffer, Canvas canvas) {

        try {
            // Load the BMP file
            BufferedImage image = ImageIO.read(new File(fileName));

            // Get the image dimensions
            int width = image.getWidth();
            int height = image.getHeight();

            // Initialise the pixel buffer
            if (buffer.getPixels() == null || width != buffer.getWidth() || height != buffer.getHeight()) {
                buffer = new FrameBuffer(width, height);
            }

            // Draw the entire image directly to the canvas
            Graphics g = canvas.getGraphics();
            g.drawImage(image, 0, 0, null);

            // Access the pixel data manually
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    buffer.getPixels()[y * width + + x] = image.getRGB(x, y);
                }
            }
        }
        catch (IOException e) {
            System.out.println("Error loading BMP file: " + e.getMessage());
        }
    }
}
