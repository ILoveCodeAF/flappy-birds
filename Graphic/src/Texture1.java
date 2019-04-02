/**
 * this = texture + pipeline
 * add new constructor with para: width, height, ByteBuffer
 */

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.*;
//import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

//import java.awt.image.BufferedImage;
import java.awt.*;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15C.glBufferData;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindFragDataLocation;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_load;
import static org.lwjgl.stb.STBImage.stbi_set_flip_vertically_on_load;
//import static org.lwjgl.opengl.GL30.glGenVertexArrays;

//import static org.lwjgl.stb.STBImage.stbi_set_flip_vertically_on_load;

public class Texture1 {
    private int width;
    private int height;
    private int windowWidth;
    private int windowHeight;

    private int vao;//vertex array objects
    private int vbo;//vertex buffer objects
    private int ebo;//element buffer objects
    private int texture;
    private int nVertices;
    private ArrayList<Region> regionList;

    private int vertexShader;
    private int fragmentShader;
    private int shaderProgram;
    private CharSequence vertexSource =
            "#version 150 core\n" +
                    "\n" +
                    "in vec2 position;\n" +
                    "in vec3 color;\n" +
                    "in vec2 texcoord;\n" +
                    "\n" +
                    "out vec3 vertexColor;\n" +
                    "out vec2 textureCoord;\n" +
                    "\n" +
                    "uniform mat4 model;\n" +
                    "uniform mat4 view;\n" +
                    "uniform mat4 projection;\n" +
                    "\n" +
                    "void main() {\n" +
                    "    vertexColor = color;\n" +
                    "    textureCoord = texcoord;\n" +
                    "    mat4 mvp = projection * view * model;\n" +
                    "    gl_Position = mvp * vec4(position, 0.0, 1.0);\n" +
                    "}";
    private CharSequence fragmentSource =
            "#version 150 core\n" +
                    "\n" +
                    "in vec3 vertexColor;\n" +
                    "in vec2 textureCoord;\n" +
                    "\n" +
                    "out vec4 fragColor;\n" +
                    "\n" +
                    "uniform sampler2D texImage;\n" +
                    "\n" +
                    "void main() {\n" +
                    "    vec4 textureColor = texture(texImage, textureCoord);\n" +
                    "    fragColor = vec4(vertexColor, 1.0) * textureColor;\n" +
                    "}";
    private CharSequence vertexSourcev120 = "#version 120\n" +
            "\n" +
            "attribute vec2 position;\n" +
            "attribute vec3 color;\n" +
            "attribute vec2 texcoord;\n" +
            "\n" +
            "varying vec3 vertexColor;\n" +
            "varying vec2 textureCoord;\n" +
            "\n" +
            "uniform mat4 model;\n" +
            "uniform mat4 view;\n" +
            "uniform mat4 projection;\n" +
            "\n" +
            "void main() {\n" +
            "    vertexColor = color;\n" +
            "    textureCoord = texcoord;\n" +
            "    mat4 mvp = projection * view * model;\n" +
            "    gl_Position = mvp * vec4(position, 0.0, 1.0);\n" +
            "}";
    private CharSequence fragmentSourcev120 = "#version 120\n" +
            "\n" +
            "varying vec3 vertexColor;\n" +
            "varying vec2 textureCoord;\n" +
            "\n" +
            "uniform sampler2D texImage;\n" +
            "\n" +
            "void main() {\n" +
            "    vec4 textureColor = texture2D(texImage, textureCoord);\n" +
            "    gl_FragColor = vec4(vertexColor, 1.0) * textureColor;\n" +
            "}";

    private CharSequence vertexSourceAlpha = "#version 150 core\n" +
            "\n" +
            "in vec2 position;\n" +
            "in vec4 color;\n" +
            "in vec2 texcoord;\n" +
            "\n" +
            "out vec4 vertexColor;\n" +
            "out vec2 textureCoord;\n" +
            "\n" +
            "uniform mat4 model;\n" +
            "uniform mat4 view;\n" +
            "uniform mat4 projection;\n" +
            "\n" +
            "void main() {\n" +
            "    vertexColor = color;\n" +
            "    textureCoord = texcoord;\n" +
            "    mat4 mvp = projection * view * model;\n" +
            "    gl_Position = mvp * vec4(position, 0.0, 1.0);\n" +
            "}";
    private CharSequence fragmentSourceAlpha = "#version 150 core\n" +
            "\n" +
            "in vec4 vertexColor;\n" +
            "in vec2 textureCoord;\n" +
            "\n" +
            "out vec4 fragColor;\n" +
            "\n" +
            "uniform sampler2D texImage;\n" +
            "\n" +
            "void main() {\n" +
            "    vec4 textureColor = texture(texImage, textureCoord);\n" +
            "    fragColor = vertexColor * textureColor;\n" +
            "}";
    private Texture1(){
//        this.vao = GL30.glGenVertexArrays();
//        glBindVertexArray(this.vao);
        /* Get width and height of framebuffer */

        try (MemoryStack stack = MemoryStack.stackPush()) {
            long window = GLFW.glfwGetCurrentContext();

            IntBuffer widthBuffer = stack.mallocInt(1);
            IntBuffer heightBuffer = stack.mallocInt(1);

            GLFW.glfwGetFramebufferSize(window, widthBuffer, heightBuffer);
            this.windowWidth = widthBuffer.get();
            this.windowHeight = heightBuffer.get();

            System.out.println("windowWidth = " + this.windowWidth);
            System.out.println("windowHeight = " + this.windowHeight);
        }
    }

    public Texture1(int width, int height, ByteBuffer buffer){
        this();
        this.setTexture1(width, height, buffer);
    }

    public Texture1(String path){
        this();
        ByteBuffer image;
        try(MemoryStack stack = MemoryStack.stackPush()){
            /* Prepare image buffers */
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);

            /* Load image */
            stbi_set_flip_vertically_on_load(true);
            image = stbi_load(path, w, h, comp, 4);

            if (image == null) {
                throw new RuntimeException("Failed to load a texture file!"
                        + System.lineSeparator() + stbi_failure_reason());
            }

            this.setTexture1(w.get(), h.get(), image);
        }
    }

    private void setTexture1(int width, int height, ByteBuffer buffer){
        if(buffer==null){
            throw new RuntimeException("BufferImage is null!");
        }
        /* Get width and height of image */
        this.width = width;
        this.height = height;
        System.out.println("imageWidth = "+this.getWidth());
        System.out.println("imageHeight = "+this.getHeight());

        this.texture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, this.texture);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, this.width, this.height, 0, GL_RGBA, GL20.GL_UNSIGNED_BYTE, buffer);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    public void prepareToRender() {

        glBindTexture(GL_TEXTURE_2D, this.texture);
        this.vao = glGenVertexArrays();
        glBindVertexArray(this.vao);

        /* Get coordinates for centering the texture on screen */
        float x1 = (this.windowWidth - this.getWidth()) / 2f;
        float y1 = (this.windowHeight - this.getHeight()) / 2f;
        float x2 = x1 + this.getWidth();
        float y2 = y1 + this.getHeight();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            /* Vertex data */
            FloatBuffer vertices = stack.mallocFloat(6 * 7);
//            vertices.put(x1).put(y1).put(0f).put(1f).put(1f).put(0f).put(0f);
//            vertices.put(x2).put(y1).put(1f).put(0f).put(1f).put(1f).put(0f);
//            vertices.put(x2).put(y2).put(1f).put(1f).put(0f).put(1f).put(1f);
//            vertices.put(x1).put(y2).put(1f).put(1f).put(1f).put(0f).put(1f);

            vertices.put(x1).put(y1).put(1f).put(1f).put(1f).put(0f).put(0f);
            vertices.put(x2).put(y1).put(1f).put(1f).put(1f).put(1f).put(0f);
            vertices.put(x2).put(y2).put(1f).put(1f).put(1f).put(1f).put(1f);

            vertices.put(x2).put(y2).put(1f).put(1f).put(1f).put(1f).put(1f);
            vertices.put(x1).put(y2).put(1f).put(1f).put(1f).put(0f).put(1f);
            vertices.put(x1).put(y1).put(1f).put(1f).put(1f).put(0f).put(0f);
            vertices.flip();

            this.nVertices = 6;

            /* Generate Vertex Buffer Object */
            this.vbo = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, this.vbo);
            glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
//            glBindBuffer(GL_ARRAY_BUFFER, 0);

            /* Element data */
//            IntBuffer elements = stack.mallocInt(2 * 3);
//            elements.put(0).put(1).put(2);
//            elements.put(2).put(3).put(0);
//            elements.flip();
//
//            /* Generate Element Buffer Object */
//            this.ebo = glGenBuffers();
//            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.ebo);
//            glBufferData(GL_ELEMENT_ARRAY_BUFFER, elements, GL_STATIC_DRAW);
//            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        }
    }

    public void prepareAtrib(){
        this.vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(this.vertexShader, this.vertexSource);
        glCompileShader(this.vertexShader);

        int statusVertexShader = glGetShaderi(this.vertexShader, GL_COMPILE_STATUS);
        if (statusVertexShader != GL_TRUE) {
            throw new RuntimeException(glGetShaderInfoLog(this.vertexShader));
        }

        this.fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(this.fragmentShader, this.fragmentSource);
        glCompileShader(this.fragmentShader);

        int statusFragmentShader = glGetShaderi(this.fragmentShader, GL_COMPILE_STATUS);
        if (statusFragmentShader != GL_TRUE) {
            throw new RuntimeException(glGetShaderInfoLog(statusFragmentShader));
        }

        this.shaderProgram = glCreateProgram();
        glAttachShader(this.shaderProgram, this.vertexShader);
        glAttachShader(this.shaderProgram, this.fragmentShader);
        glBindFragDataLocation(this.shaderProgram, 0, "fragColor");
        glLinkProgram(this.shaderProgram);

        int statusShaderProgram = glGetProgrami(this.shaderProgram, GL_LINK_STATUS);
        if (statusShaderProgram != GL_TRUE) {
            throw new RuntimeException(glGetProgramInfoLog(this.shaderProgram));
        }

        glUseProgram(this.shaderProgram);

//        glBindVertexArray(this.vao);
        this.specifyVertexAttributes();

        /* Set texture uniform */
        int uniTex = glGetUniformLocation(this.shaderProgram, "texImage");
        glUniform1i(uniTex, 0);

        /* Set model matrix to identity matrix */
        Matrix4f model = new Matrix4f();
        int uniModel = glGetUniformLocation(this.shaderProgram, "model");
        try(MemoryStack stack = MemoryStack.stackPush()){
            FloatBuffer buffer = stack.mallocFloat(4*4);
            model.toBuffer(buffer);
            glUniformMatrix4fv(uniModel, false, buffer);
        }

        /* Set view matrix to identity matrix */
        Matrix4f view = new Matrix4f();
        int uniView = glGetUniformLocation(this.shaderProgram, "view");
        try(MemoryStack stack = MemoryStack.stackPush()){
            FloatBuffer buffer = stack.mallocFloat(4*4);
            view.toBuffer(buffer);
            glUniformMatrix4fv(uniView, false, buffer);
        }

        /* Set projection matrix to an orthographic projection */
        Matrix4f projection = Matrix4f.orthographic(0f, this.windowWidth, 0f, this.windowHeight, -1f, 1f);
        int uniProjection = glGetUniformLocation(this.shaderProgram, "projection");
        try(MemoryStack stack = MemoryStack.stackPush()){
            FloatBuffer buffer = stack.mallocFloat(4*4);
            projection.toBuffer(buffer);
            glUniformMatrix4fv(uniProjection, false, buffer);
        }
    }

    private void bind(){
//        GL30.glBindVertexArray(this.vao);
//        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.vbo);
//        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, this.ebo);
        glBindTexture(GL_TEXTURE_2D, this.texture);
    }
    public void render(){
        //clear screen
//        glClear(GL_COLOR_BUFFER_BIT);

        glBindVertexArray(this.vao);
//        GL11.glEnable(GL11.GL_TEXTURE_2D);
//        glBindBuffer(GL_ARRAY_BUFFER, this.vbo);
//        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.ebo);
        this.bind();
//        this.specifyVertexAttributes();
        glUseProgram(this.shaderProgram);

        glDrawArrays(GL_TRIANGLES, 0, this.nVertices);
    }

    private void specifyVertexAttributes() {
        /* Specify Vertex Pointer */
        int posAttrib = glGetAttribLocation(this.shaderProgram, "position");
        glEnableVertexAttribArray(posAttrib);
        glVertexAttribPointer(posAttrib, 2, GL_FLOAT, false,7 * Float.BYTES, 0);

        /* Specify Color Pointer */
        int colAttrib = glGetAttribLocation(this.shaderProgram, "color");
        glEnableVertexAttribArray(colAttrib);
        glVertexAttribPointer(colAttrib, 3, GL_FLOAT, false, 7 * Float.BYTES, 2 * Float.BYTES);

        /* Specify Texture Pointer */
        int texAttrib = glGetAttribLocation(this.shaderProgram, "texcoord");
        glEnableVertexAttribArray(texAttrib);
        glVertexAttribPointer(texAttrib, 2, GL_FLOAT, false, 7 * Float.BYTES, 5 * Float.BYTES);
    }

    public void dispose(){
        glDeleteVertexArrays(this.vao);
        glDeleteBuffers(this.vbo);
        glDeleteBuffers(this.ebo);
        glDeleteShader(this.vertexShader);
        glDeleteShader(this.fragmentShader);
        glDeleteProgram(this.shaderProgram);
        glDeleteTextures(this.texture);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void begin(){
        this.regionList = new ArrayList<>();
        this.nVertices = 0;
    }

    /**
     * Draws a texture region with the currently bound texture on specified
     * coordinates.
     *
     * @param x1 Bottom left x position
     * @param y1 Bottom left y position
     * @param x2 Top right x position
     * @param y2 Top right y position
     * @param s1 Bottom left s coordinate
     * @param t1 Bottom left t coordinate
     * @param s2 Top right s coordinate
     * @param t2 Top right t coordinate
     * @param c  The color to use
     */
    public void drawTextureRegion(float x1, float y1, float x2, float y2, float s1, float t1, float s2, float t2, java.awt.Color c){
        this.regionList.add( new Region(x1, y1, x2, y2, s1, t1, s2, t2, c));
    }

    /**
     * Draws a texture region with the currently bound texture on specified
     * coordinates.
     *
     * @param x1 Bottom left x position
     * @param y1 Bottom left y position
     * @param x2 Top right x position
     * @param y2 Top right y position
     * @param s1 Bottom left s coordinate
     * @param t1 Bottom left t coordinate
     * @param s2 Top right s coordinate
     * @param t2 Top right t coordinate
     *
     */
    public void drawTextureRegion(float x1, float y1, float x2, float y2, float s1, float t1, float s2, float t2){
        this.drawTextureRegion(x1, y1, x2, y2, s1, t1, s2, t2, Color.WHITE);
    }

    /**
     * Draws a texture region with the currently bound texture on specified
     * coordinates.
     *
     *
     * @param x         X position of the texture
     * @param y         Y position of the texture
     * @param regX      X position of the texture region
     * @param regY      Y position of the texture region
     * @param regWidth  Width of the texture region
     * @param regHeight Height of the texture region
     */
    public void drawTextureRegion(float x, float y, float regX, float regY, float regWidth, float regHeight) {
        this.drawTextureRegion(x, y, regX, regY, regWidth, regHeight, Color.WHITE);
    }

    /**
     * Draws a texture region with the currently bound texture on specified
     * coordinates.
     *
     *
     * @param x         X position of the texture
     * @param y         Y position of the texture
     * @param regX      X position of the texture region
     * @param regY      Y position of the texture region
     * @param regWidth  Width of the texture region
     * @param regHeight Height of the texture region
     * @param c         The color to use
     */
    public void drawTextureRegion(float x, float y, float regX, float regY, float regWidth, float regHeight, Color c) {
        /* Vertex positions */
        float x1 = x;
        float y1 = y;
        float x2 = x + regWidth;
        float y2 = y + regHeight;

        /* Texture coordinates */
        float s1 = regX / this.getWidth();
        float t1 = regY / this.getHeight();
        float s2 = (regX + regWidth) / this.getWidth();
        float t2 = (regY + regHeight) / this.getHeight();

        this.drawTextureRegion(x1, y1, x2, y2, s1, t1, s2, t2, c);
    }

    public void drawTextureRegion(){
        /* Get coordinates for centering the texture on screen */
        float x1 = (this.windowWidth - this.getWidth()) / 2f;
        float y1 = (this.windowHeight - this.getHeight()) / 2f;
        float x2 = x1 + this.getWidth();
        float y2 = y1 + this.getHeight();

        this.drawTextureRegion(x1, y1, x2, y2, 0f, 0f, 1f, 1f, Color.WHITE);
    }

    public void end(){
        this.nVertices = regionList.size()*6;
//        System.out.println(this.nVertices);
        glBindTexture(GL_TEXTURE_2D, this.texture);
        this.vao = glGenVertexArrays();
        glBindVertexArray(this.vao);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            /* Vertex data */
            FloatBuffer vertices = stack.mallocFloat(this.nVertices * 7);
            float x1, y1, x2, y2, s1, t1, s2, t2, r, g, b, a;
            for(Region region: regionList){
//                System.out.println(region);
//                System.out.println("r ="+region.c.getRed());
                x1 = region.x1;
                y1 = region.y1;
                x2 = region.x2;
                y2 = region.y2;
                s1 = region.s1;
                t1 = region.t1;
                s2 = region.s2;
                t2 = region.t2;
                r = (float)region.c.getRed()/255;
                g = (float)region.c.getGreen()/255;
                b = (float)region.c.getBlue()/255;
                a = (float)region.c.getAlpha()/255;
//
//                // error: 'cause alpha over buffer
//                vertices.put(x1).put(y1).put(r).put(g).put(b).put(a).put(s1).put(t1);
//                vertices.put(x1).put(y2).put(r).put(g).put(b).put(a).put(s1).put(t2);
//                vertices.put(x2).put(y2).put(r).put(g).put(b).put(a).put(s2).put(t2);
//
//                vertices.put(x1).put(y1).put(r).put(g).put(b).put(a).put(s1).put(t1);
//                vertices.put(x2).put(y2).put(r).put(g).put(b).put(a).put(s2).put(t2);
//                vertices.put(x2).put(y1).put(r).put(g).put(b).put(a).put(s2).put(t1);

                vertices.put(x1).put(y1).put(r).put(g).put(b).put(s1).put(t1);
                vertices.put(x1).put(y2).put(r).put(g).put(b).put(s1).put(t2);
                vertices.put(x2).put(y2).put(r).put(g).put(b).put(s2).put(t2);

                vertices.put(x1).put(y1).put(r).put(g).put(b).put(s1).put(t1);
                vertices.put(x2).put(y2).put(r).put(g).put(b).put(s2).put(t2);
                vertices.put(x2).put(y1).put(r).put(g).put(b).put(s2).put(t1);
//                System.out.println("@@!");
            }
            vertices.flip();

            /* Generate Vertex Buffer Object */
            this.vbo = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, this.vbo);
            glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        }
    }
}