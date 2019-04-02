/**
 * vertexColor vec4
 */
//import org.lwjgl.opengl.GL11;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Renderer1 {
    private int vao;//vertex array objects
    private int vbo;//vertex buffer objects

    private int vertexShader;
    private int fragmentShader;
    private int shaderProgram;
    private CharSequence vertexSource = "#version 150 core\n" +
            "\n" +
            "in vec3 position;\n" +
            "in vec4 color;\n" +
            "\n" +
            "out vec4 vertexColor;\n" +
            "\n" +
            "uniform mat4 model;\n" +
            "uniform mat4 view;\n" +
            "uniform mat4 projection;\n" +
            "\n" +
            "void main() {\n" +
            "    vertexColor = color;\n" +
            "    mat4 mvp = projection * view * model;\n" +
            "    gl_Position = mvp * vec4(position, 1.0);\n" +
            "}";
    private CharSequence fragmentSource = "#version 150 core\n" +
            "\n" +
            "in vec4 vertexColor;\n" +
            "\n" +
            "out vec4 fragColor;\n" +
            "\n" +
            "void main() {\n" +
            "    fragColor = vertexColor;\n" +
            "}";

    private int uniModel;
    private float previousAngle = 0f;
    private float angle = 0f;
    private final float angelPerSecond = 50f;

    public Renderer1(){
        this.vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(this.vao);

        try(MemoryStack stack = MemoryStack.stackPush()){
            FloatBuffer vertices = stack.mallocFloat(3*7);
            vertices.put(-0.6f).put(-0.4f).put(0f).put(1f).put(0f).put(0f).put(0f);
            vertices.put(0.6f).put(-0.4f).put(0f).put(0f).put(1f).put(0f).put(1f);
            vertices.put(0f).put(0.6f).put(0f).put(0f).put(0f).put(1f).put(1f);
            vertices.flip();

            /* Generate Vertex Buffer Object */
            this.vbo = GL15.glGenBuffers();
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.vbo);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_STATIC_DRAW);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        }

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
            throw new RuntimeException(GL20.glGetShaderInfoLog(statusFragmentShader));
        }

        this.shaderProgram = GL20.glCreateProgram();
        GL20.glAttachShader(this.shaderProgram, this.vertexShader);
        GL20.glAttachShader(this.shaderProgram, this.fragmentShader);
        GL30.glBindFragDataLocation(this.shaderProgram, 0, "fragColor");
        GL20.glLinkProgram(this.shaderProgram);
        GL20.glUseProgram(this.shaderProgram);

        int statusShaderProgram = GL20.glGetProgrami(this.shaderProgram, GL20.GL_LINK_STATUS);
        if (statusShaderProgram != GL15.GL_TRUE) {
            throw new RuntimeException(GL20.glGetProgramInfoLog(this.shaderProgram));
        }

        this.specifyVertexAttributes();

        Matrix4f model = new Matrix4f();
        this.uniModel = GL20.glGetUniformLocation(this.shaderProgram, "model");
        try(MemoryStack stack = MemoryStack.stackPush()){
            FloatBuffer buffer = stack.mallocFloat(4*4);
            model.toBuffer(buffer);
            GL20.glUniformMatrix4fv(this.uniModel, false, buffer);
        }

        Matrix4f view = new Matrix4f();
        int uniView = GL20.glGetUniformLocation(shaderProgram, "view");
        try(MemoryStack stack = MemoryStack.stackPush()){
            FloatBuffer buffer = stack.mallocFloat(4*4);
            view.toBuffer(buffer);
            GL20.glUniformMatrix4fv(uniView, false, buffer);
        }

        float ratio;// = 640f / 480f;
        try(MemoryStack stack = MemoryStack.stackPush()){
            long window = GLFW.glfwGetCurrentContext();
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);

            GLFW.glfwGetFramebufferSize(window, width, height);
            ratio = width.get() / (float) height.get();
        }
        Matrix4f projection = Matrix4f.orthographic(-ratio, ratio, -1f, 1f, -1f, 1f);
        int uniProjection = GL20.glGetUniformLocation(shaderProgram, "projection");
        try(MemoryStack stack = MemoryStack.stackPush()){
            FloatBuffer buffer = stack.mallocFloat(4*4);
            projection.toBuffer(buffer);
            GL20.glUniformMatrix4fv(uniProjection, false, buffer);
        }
    }
    //Specify Vertex Attributes
    private void specifyVertexAttributes(){
        //int floatSize = 4;

        int posAttrib = GL20.glGetAttribLocation(this.shaderProgram, "position");
        GL20.glEnableVertexAttribArray(posAttrib);
        GL20.glVertexAttribPointer(posAttrib, 3, GL20.GL_FLOAT, false, 7*Float.BYTES, 0);

        int colAttrib = GL20.glGetAttribLocation(this.shaderProgram, "color");
        GL20.glEnableVertexAttribArray(colAttrib);
        GL20.glVertexAttribPointer(colAttrib, 4, GL20.GL_FLOAT, false, 7*Float.BYTES, 3*Float.BYTES);
    }
    public void render(){
//        GL15.glClear(GL15.GL_COLOR_BUFFER_BIT);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.vbo);
        this.specifyVertexAttributes();
        GL20.glUseProgram(this.shaderProgram);

        GL20.glDrawArrays(GL15.GL_TRIANGLES, 0, 3);

    }
    public void update(float delta) {
        this.previousAngle = this.angle;
        this.angle += delta * this.angelPerSecond;
    }
    public void render(float alpha){
//        GL15.glClear(GL15.GL_COLOR_BUFFER_BIT);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.vbo);
        this.specifyVertexAttributes();
        GL20.glUseProgram(this.shaderProgram);

        float lerpAngle = (1f - alpha) * this.previousAngle + alpha * angle;
        Matrix4f model = Matrix4f.rotate(lerpAngle, 0f, 0f, 1f);
        try(MemoryStack stack = MemoryStack.stackPush()){
            FloatBuffer buffer = stack.mallocFloat(4*4);
            model.toBuffer(buffer);
            GL20.glUniformMatrix4fv(this.uniModel, false, buffer);
        }

        GL20.glDrawArrays(GL15.GL_TRIANGLES, 0, 3);

    }
    public void dispose(){
        GL30.glDeleteVertexArrays(this.vao);
        GL15.glDeleteBuffers(this.vbo);
        GL20.glDeleteShader(this.vertexShader);
        GL20.glDeleteShader(this.fragmentShader);
        GL20.glDeleteProgram(this.shaderProgram);
    }
}

