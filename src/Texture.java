import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.*;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

//import java.awt.image.BufferedImage;
//import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

//import static org.lwjgl.stb.STBImage.stbi_set_flip_vertically_on_load;

public class Texture {
    private int width;
    private int height;

    private int vao;//vertex array objects
    private int vbo;//vertex buffer objects
    private int ebo;//element buffer objects
    private int texture;

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

    private Texture(){
//        this.vao = GL30.glGenVertexArrays();
//        GL30.glBindVertexArray(this.vao);
    }
    public Texture(String path){
        ByteBuffer image;
        try(MemoryStack stack = MemoryStack.stackPush()){
            /* Prepare image buffers */
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);

            /* Load image */
            STBImage.stbi_set_flip_vertically_on_load(true);
            image = STBImage.stbi_load(path, w, h, comp, 4);

            if (image == null) {
                throw new RuntimeException("Failed to load a texture file!"
                        + System.lineSeparator() + STBImage.stbi_failure_reason());
            }

            /* Get width and height of image */
            this.width = w.get();
            this.height = h.get();
            System.out.println("imageWidth = "+this.getWidth());
            System.out.println("imageHeight = "+this.getHeight());

            this.texture = GL11.glGenTextures();
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.texture);

            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL13.GL_CLAMP_TO_BORDER);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL13.GL_CLAMP_TO_BORDER);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, this.width, this.height, 0, GL11.GL_RGBA, GL20.GL_UNSIGNED_BYTE, image);

//            GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        }
    }
    public Texture(int width, int height, ByteBuffer data){
        if(data == null ){
            throw new RuntimeException("no data to load!");
        }
        this.width = width;
        this.height = height;
        System.out.println("imageWidth = "+this.getWidth());
        System.out.println("imageHeight = "+this.getHeight());

        this.texture = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.texture);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL13.GL_CLAMP_TO_BORDER);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL13.GL_CLAMP_TO_BORDER);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, this.width, this.height, 0, GL11.GL_RGBA, GL20.GL_UNSIGNED_BYTE, data);
    }

    public void prepareToRender(){
        /* Get width and height of framebuffer */
        int windowWidth;
        int windowHeight;

        try(MemoryStack stack = MemoryStack.stackPush()){
            long window = GLFW.glfwGetCurrentContext();

            IntBuffer widthBuffer = stack.mallocInt(1);
            IntBuffer heightBuffer = stack.mallocInt(1);

            GLFW.glfwGetFramebufferSize(window, widthBuffer, heightBuffer);
            windowWidth = widthBuffer.get();
            windowHeight = heightBuffer.get();

            System.out.println("windowWidth = "+windowWidth);
            System.out.println("windowHeight = "+windowHeight);
        }

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.texture);

        /* Generate Vertex Array Object */
        this.vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(this.vao);

        /* Get coordinates for centering the texture on screen */
        float x1 = (windowWidth - this.getWidth()) / 2f;
        float y1 = (windowHeight - this.getHeight()) / 2f;
        float x2 = x1 + this.getWidth();
        float y2 = y1 + this.getHeight();

        try(MemoryStack stack = MemoryStack.stackPush()){
            /* Vertex data */
            FloatBuffer vertices = stack.mallocFloat(4*7);
            vertices.put(x1).put(y1).put(1f).put(1f).put(1f).put(0f).put(0f);
            vertices.put(x2).put(y1).put(1f).put(1f).put(1f).put(1f).put(0f);
            vertices.put(x2).put(y2).put(1f).put(1f).put(1f).put(1f).put(1f);
            vertices.put(x1).put(y2).put(1f).put(1f).put(1f).put(0f).put(1f);
            vertices.flip();

            /* Generate Vertex Buffer Object */
            this.vbo = GL15.glGenBuffers();
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.vbo);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_STATIC_DRAW);

            /* Element data */
            IntBuffer elements = stack.mallocInt(2 * 3);
            elements.put(0).put(1).put(2);
            elements.put(2).put(3).put(0);
            elements.flip();

            /* Generate Element Buffer Object */
            this.ebo = GL15.glGenBuffers();
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, this.ebo);
            GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, elements, GL15.GL_STATIC_DRAW);
        }

        /* Load shaders */
        this.vertexShader = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        GL20.glShaderSource(this.vertexShader, this.vertexSource);
        GL20.glCompileShader(this.vertexShader);

        int statusVertexShader = GL20.glGetShaderi(this.vertexShader, GL20.GL_COMPILE_STATUS);
        if (statusVertexShader != GL20.GL_TRUE) {
            throw new RuntimeException(GL20.glGetShaderInfoLog(this.vertexShader));
        }

        this.fragmentShader = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
        GL20.glShaderSource(this.fragmentShader, this.fragmentSource);
        GL20.glCompileShader(this.fragmentShader);

        int statusFragmentShader = GL20.glGetShaderi(this.fragmentShader, GL20.GL_COMPILE_STATUS);
        if (statusFragmentShader != GL20.GL_TRUE) {
            throw new RuntimeException(GL20.glGetShaderInfoLog(this.fragmentShader));
        }

        /* Create shader program */
        this.shaderProgram = GL20.glCreateProgram();
        GL20.glAttachShader(this.shaderProgram, this.vertexShader);
        GL20.glAttachShader(this.shaderProgram, this.fragmentShader);
        GL30.glBindFragDataLocation(this.shaderProgram, 0, "fragColor");
        GL20.glLinkProgram(this.shaderProgram);

        int statusShaderProgram = GL20.glGetProgrami(this.shaderProgram, GL20.GL_LINK_STATUS);
        if (statusShaderProgram != GL15.GL_TRUE) {
            throw new RuntimeException(GL20.glGetProgramInfoLog(this.shaderProgram));
        }
        GL20.glUseProgram(this.shaderProgram);

        this.specifyVertexAttributes();

        /* Set texture uniform */
        int uniTex = GL20.glGetUniformLocation(this.shaderProgram, "texImage");
        GL20.glUniform1i(uniTex, 0);

        /* Set model matrix to identity matrix */
        Matrix4f model = new Matrix4f();
        int uniModel = GL20.glGetUniformLocation(this.shaderProgram, "model");
        try(MemoryStack stack = MemoryStack.stackPush()){
            FloatBuffer buffer = stack.mallocFloat(4*4);
            model.toBuffer(buffer);
            GL20.glUniformMatrix4fv(uniModel, false, buffer);
        }

        /* Set view matrix to identity matrix */
        Matrix4f view = new Matrix4f();
        int uniView = GL20.glGetUniformLocation(this.shaderProgram, "view");
        try(MemoryStack stack = MemoryStack.stackPush()){
            FloatBuffer buffer = stack.mallocFloat(4*4);
            view.toBuffer(buffer);
            GL20.glUniformMatrix4fv(uniView, false, buffer);
        }

        /* Set projection matrix to an orthographic projection */
        Matrix4f projection = Matrix4f.orthographic(0f, windowWidth, 0f, windowHeight, -1f, 1f);
        int uniProjection = GL20.glGetUniformLocation(this.shaderProgram, "projection");
        try(MemoryStack stack = MemoryStack.stackPush()){
            FloatBuffer buffer = stack.mallocFloat(4*4);
            projection.toBuffer(buffer);
            GL20.glUniformMatrix4fv(uniProjection, false, buffer);
        }
    }

    private void bind(){
//        GL30.glBindVertexArray(this.vao);
//        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.vbo);
//        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, this.ebo);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.texture);
    }
    public void render(){
        //clear screen
//        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

        GL30.glBindVertexArray(this.vao);
//        GL11.glEnable(GL11.GL_TEXTURE_2D);
//        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.vbo);
//        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, this.ebo);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.texture);
//        this.specifyVertexAttributes();
        GL20.glUseProgram(this.shaderProgram);

        GL11.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_INT, 0);
    }

    private void specifyVertexAttributes() {
        /* Specify Vertex Pointer */
        int posAttrib = GL20.glGetAttribLocation(this.shaderProgram, "position");
        GL20.glEnableVertexAttribArray(posAttrib);
        GL20.glVertexAttribPointer(posAttrib, 2, GL20.GL_FLOAT, false,7 * Float.BYTES, 0);

        /* Specify Color Pointer */
        int colAttrib = GL20.glGetAttribLocation(this.shaderProgram, "color");
        GL20.glEnableVertexAttribArray(colAttrib);
        GL20.glVertexAttribPointer(colAttrib, 3, GL20.GL_FLOAT, false, 7 * Float.BYTES, 2 * Float.BYTES);

        /* Specify Texture Pointer */
        int texAttrib = GL20.glGetAttribLocation(this.shaderProgram, "texcoord");
        GL20.glEnableVertexAttribArray(texAttrib);
        GL20.glVertexAttribPointer(texAttrib, 2, GL20.GL_FLOAT, false, 7 * Float.BYTES, 5 * Float.BYTES);
    }

    public void dispose(){
        GL30.glDeleteVertexArrays(this.vao);
        GL15.glDeleteBuffers(this.vbo);
        GL15.glDeleteBuffers(this.ebo);
        GL20.glDeleteShader(this.vertexShader);
        GL20.glDeleteShader(this.fragmentShader);
        GL20.glDeleteProgram(this.shaderProgram);
        GL11.glDeleteTextures(this.texture);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}