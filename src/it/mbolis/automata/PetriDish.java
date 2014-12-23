package it.mbolis.automata;

import static it.mbolis.automata.ConwayAutomaton.ALIVE;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;

import net.ekroo.gif.GifSequenceWriter;

public class PetriDish<C extends Automaton<C>> {

    private boolean wrap = true;

    private final int width, height;
    private final Class<C> automatonClass;
    private final int neighbors;
    private final C prototype;
    private final C[] prototypeRow;

    private C[][] field;

    private long startupTime, evolveTime, padTime, blitTime;

    @SuppressWarnings("unchecked")
    public PetriDish(int width, int height, Class<C> automatonClass) {

        long start = System.currentTimeMillis();

        this.width = width;
        this.height = height;
        this.automatonClass = automatonClass;

        try {

            prototype = automatonClass.newInstance();
            neighbors = prototype.neighborhood();
            prototypeRow = (C[]) Array.newInstance(automatonClass, width + neighbors * 2);

            if (!wrap) {
                for (int i = 0; i < prototypeRow.length; i++) {
                    prototypeRow[i] = prototype;
                }
            }

            field = (C[][]) Array.newInstance(automatonClass, height + neighbors * 2, width + neighbors * 2);

            for (int y = neighbors; y < height + neighbors; y++) {
                for (int x = neighbors; x < width + neighbors; x++) {
                    field[y][x] = automatonClass.newInstance();
                }
            }

            startupTime = System.currentTimeMillis() - start;

            pad(field, neighbors, wrap);

        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void pad(C[][] field, int padding, boolean wrap) {

        long start = System.currentTimeMillis();

        // pad top
        for (int y = 0; y < padding; y++) {
            field[y] = wrap ? field[height - y] : prototypeRow;
        }

        for (int y = padding; y < height + padding; y++) {

            // pad left
            for (int x = 0; x < padding; x++) {
                field[y][x] = wrap ? field[y][width - x] : prototype;
            }

            // pad right
            for (int x = width + padding; x < field[0].length; x++) {
                field[y][x] = wrap ? field[y][x - width] : prototype;
            }
        }

        // pad bottom
        for (int y = height + padding; y < field.length; y++) {
            field[y] = wrap ? field[y - height] : prototypeRow;
        }

        padTime += System.currentTimeMillis() - start;
    }

    public boolean isWrap() {
        return wrap;
    }

    public void setWrap(boolean wrap) {
        this.wrap = wrap;
    }

    public C cell(int x, int y) {
        if (x < 0 || x >= width) {
            throw new ArrayIndexOutOfBoundsException("x: " + x);
        }
        if (y < 0 || y >= width) {
            throw new ArrayIndexOutOfBoundsException("y: " + y);
        }
        return field[y + neighbors][x + neighbors];
    }

    public void evolve() {

        long start = System.currentTimeMillis();

        int radius = neighbors;
        int size = 1 + radius * 2;

        @SuppressWarnings("unchecked")
        C[][] neighborhood = (C[][]) Array.newInstance(automatonClass, size, size);

        @SuppressWarnings("unchecked")
        C[][] next = (C[][]) Array.newInstance(automatonClass, field.length, field[0].length);

        for (int y = neighbors; y < height + neighbors; y++) {
            for (int x = neighbors; x < width + neighbors; x++) {

                for (int yr = -radius; yr <= radius; yr++) {
                    int yn = y + yr;

                    for (int xr = -radius; xr <= radius; xr++) {
                        int xn = x + xr;

                        neighborhood[radius - yr][radius - xr] = field[yn][xn];
                    }
                }

                next[y][x] = field[y][x].evolve(neighborhood);
            }
        }

        evolveTime += System.currentTimeMillis() - start;

        pad(next, neighbors, wrap);
        field = next;
    }

    public void blit(Graphics2D g) {

        long start = System.currentTimeMillis();

        State protoState = prototype.getState();
        g.setColor(protoState.color);
        g.fillRect(0, 0, width, height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                State state = field[y + neighbors][x + neighbors].getState();
                if (state != protoState) {
                    g.setColor(state.color);
                    g.drawLine(x, y, x, y);
                }
            }
        }

        blitTime += System.currentTimeMillis() - start;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append('+');
        for (int x = 0; x < width; x++) {
            sb.append('-');
        }
        sb.append("+\n");

        for (int y = 0; y < height; y++) {
            sb.append('|');
            for (int x = 0; x < width; x++) {
                sb.append(field[y + neighbors][x + neighbors].getState().icon);
            }
            sb.append("|\n");
        }

        sb.append('+');
        for (int x = 0; x < width; x++) {
            sb.append('-');
        }
        sb.append("+\n");

        return sb.toString();
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {
        PetriDish<ConwayAutomaton> petriDish = new PetriDish<>(800, 600, ConwayAutomaton.class);

        for (int x = 0; x < 800; x++) {
            for (int y = 0; y < 600; y++) {
                if (Math.random() < 0.25) {
                    petriDish.cell(x, y).setState(ALIVE);
                }
            }
        }

        long seqTime = 0;

        try (ImageOutputStream imageOut = new FileImageOutputStream(Paths.get("/tmp/conway.gif").toFile());
                GifSequenceWriter writer = new GifSequenceWriter(imageOut, TYPE_INT_ARGB, 60, false)) {

            BufferedImage buffer = new BufferedImage(800, 600, TYPE_INT_ARGB);
            petriDish.blit(buffer.createGraphics());

            for (int i = 0; i < 800; i++) {
                petriDish.evolve();

                buffer = new BufferedImage(800, 600, TYPE_INT_ARGB);
                petriDish.blit(buffer.createGraphics());

                long start = System.currentTimeMillis();
                
                writer.writeToSequence(buffer);
                
                seqTime += System.currentTimeMillis() - start;
            }

        }

        System.out.println(String.format("startup: %.3fs, pad: %.3fs, evolve: %.3fs, blit: %.3fs, write: %.3f",
                petriDish.startupTime / 1000.0, petriDish.padTime / 1000.0, petriDish.evolveTime / 1000.0,
                petriDish.blitTime / 1000.0, seqTime / 1000.0));
    }
}
